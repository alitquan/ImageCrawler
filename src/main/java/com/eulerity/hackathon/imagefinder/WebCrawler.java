package com.eulerity.hackathon.imagefinder;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.FileReader;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

import java.net.URI;
import java.net.URL;

import org.apache.commons.io.filefilter.TrueFileFilter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/*
    Referred to: 
    https://jsoup.org/
*/ 


public class WebCrawler implements Runnable {
    String hostname,domain,url;
    boolean thread;
    Document doc;

    static HashSet <String> links;       // global image links a few false positives;
    static HashSet <String> subPageLinks;// stores subpage links that were randomized

    HashSet <String> extraLinks;         // css, js, misc
    HashSet <String> subpages;           // absolute and relative paths
    HashSet <String> threadLinks;        // container for indiv. thread. Holds URLs

    static int threadLimit = 0;                // max number of images a thread can obtain  
    int create_time= (int) System.currentTimeMillis();  // for creating unique file names

    // resource folders. are cleaned out using pom.xml configuration
    final String resources_path = "resources/", 
                 xml_output     = resources_path + "output" + create_time +".txt",
                json_output    = resources_path + "json_output"+ create_time + ".txt";


    /** 
     * @param   depth   how many subpages that you want to go through 
     * @param   url     URL address where crawler will be initiated
     *                  ideally will be the home page
     * 
     * HTMLunit was actually designed for testing functionality and validity of 
     * javascript code. It throws errors whenever something unoptimal is detected, 
     * with respect to the efficacy of the JavaScript code. 
     */
    public WebCrawler(String _url, boolean is_thread) throws Exception {
        this.url = _url; 
        this.thread = is_thread;

        // no thread can (re) initialize this global 
        if (!this.thread) {
            links = new HashSet<String> ();
            subPageLinks = new HashSet<String>(); 
        }

        //set-up
        threadLinks = new HashSet<String> ();
        extraLinks = new HashSet <String> ();
        subpages = new HashSet<String>();
        URI uri = new URI(url);
        domain = uri.getScheme() + "://" + uri.getHost();
        hostname = domain.startsWith("www.") ? domain.substring(4) : domain;  

        // creating resource folders
        File theDir = new File(resources_path);
        if (!theDir.exists()) {
            theDir.mkdirs();
        }

        
        try {

            // supresses warnings, errors, and exceptions (less clutter in logs)
            java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(java.util.logging.Level.OFF); 

            // creating a web client
            WebClient webclient = new WebClient();
            // supressing unnecessary scripting errors
            webclient.getOptions().setThrowExceptionOnScriptError(false);
            webclient.getOptions().setPrintContentOnFailingStatusCode(false);
            webclient.getOptions().setThrowExceptionOnFailingStatusCode(false);

            //rendering an html page post-javascript
            HtmlPage myPage = webclient.getPage(url);

            // convert html to xml 
            doc = Jsoup.parse(myPage.asXml());

            // write the xml output to a file -- for debugging
            File output = new File(xml_output);
            FileWriter writer = new FileWriter(output);
            writer.write(myPage.asXml());

            // cleaning up resources
            writer.flush();
            writer.close();

            // getting data from the json that is loaded after javascript 

            // closes web client windows
            webclient.closeAllWindows();

        }
        catch (Exception e) {
            // blank --- we don't want unnecessary errors
        }
    }



    // sets the thread limit 
    public void setThreadLimit(int limit) {
        this.threadLimit = limit;
    }

    /**
     * @param url   the url that the thread will crawl
     * @return      the webcrawler, but in thread form
     */
    public WebCrawler retSubPageCrawler(String url) {
        try {
            return new WebCrawler(url, true);
        }
        catch (Exception e){
            System.out.println("*************retSubPageCrawler************");
            return null;
        }

    }
    
    
    /**
     * Will return the appropriate hashset.
     * Threads cannot directly add to the global URL hashset.
     * It can consume too much RAM
     * Instead, URLS that threads crawl will be saved to their own
     * HashSet. A random number will be selected and added to the global
     * hashset.
     * @return      if a thread is running this, the thread's individual URL hashset
     *              otherwise, the global hashset will be returned
     */
    public HashSet <String> getHashSet() {
        if (this.thread) 
            return this.threadLinks;
        else 
            return links;
    }


    // cleanup 
    @Override
    public void finalize() {

        try {      
            File delete = new File(xml_output);
            delete.delete();
            delete = new File(json_output);
            delete.delete();
        }

        catch (Exception e) {
            System.out.println("Failed to delete");
            e.printStackTrace();
        }

    }


    // prints out the file 
    public String getTitle() {
        return doc.title(); 
    } 

    public static boolean isImage( String url) {
        try {
            BufferedImage image = ImageIO.read (new URL(url));

            if (image != null) 
                return true;
            else
                return false;

        }

        catch (Exception e) {
            System.out.println ("\nError with url: " + url);
            return false;
        }
        
    }
  


    /** 
     * @param   selector    html-selector that you want to parse for
     * Testing method. Delete later
     */
    public Elements getElements(String selector) {
        return doc.select(selector);
    }


    
    // prints length of global HashSet    
    public int getLinksLength() {
        return links.size();
    }




    /**
     * @return      global url HashSet, but in array form
     */
    public String [] retMainURLs() {

        String retArr[] = new String [links.size()];
        int i = 0;
        for (String url: links) {
            retArr[i++] = url;
        }
        return retArr;

    }

    /**
     * @return      global url HashSet, but in array form
     */
    public String [] retsubPageURLs() {

        String retArr[] = new String [subPageLinks.size()];
        int i = 0;
        for (String url: subPageLinks) {
            retArr[i++] = url;
        }
        return retArr;

    }


    /**
     * @return  global subpage url hashset, but in array form
     */
    public String [] retSubPagesAsArrays() {

        String retArr[] = new String [subpages.size()];
        int i = 0;
        for (String url: subpages) {
            retArr[i++] = url;
        }
        return retArr;
    }




    /**
     * @param s url with invalid characters
     * @return  sanitized version of @param s 
     */
    public String urlSanitize(String s) {

        if (s.contains("/>")) {
            s = s.replace("/>","");
        }
        if (s.contains ("\">")){
            s = s.replace ("\">","");
        }
        if (s.contains("\"")) {
            s = s.replace ("\"","");
        }
        if (s.contains("{")) {
            s = s.replace ("{","");
        }
        if (s.contains("}")) {
            s = s.replace ("}","");
        }
        return s; 
    }



    /**
     * This can only be run by threads. 
     * It adds a random assortment of image URLS 
     * collected from the subpages that threads 
     * have been designated to crawl. 
     * 
     * This random assortment is added to the global
     * hashset. This reduces a lot of memory overhead
     * while increasing diversity.
     */
    public void addFoundImages() {
        if (! thread) throw new Error("NOT A THREAD");

    
        int images_allowed = this.threadLimit;

        // to prevent errors 
        if (threadLinks.size() < images_allowed) {
            images_allowed = threadLinks.size(); 
        }

        // convert HashSet to an array
        String [] hashset = threadLinks.toArray(new String[threadLinks.size()]);
  
        // generate a random number
        Random generator;

        for (int i = images_allowed; i > 0; i--) {
            generator = new Random();
        
            int random = generator.nextInt(threadLinks.size());
            String randomURL = hashset[random];
            //Image io = ImageIO.read(new URL(randomURL));

            // get the element at random number index
            System.out.println("Random element: "
                            + randomURL);
            
            // add random element to list
            synchronized (subPageLinks) {
             
                if (! subPageLinks.contains(randomURL) )
                    subPageLinks.add(randomURL);

            }
                    
        }
    

    }




    /**
     *  Thread behavior is different from a normal 
     *  WebCrawler. It does not gather any more URLs.
     * 
     *  Threads also have their own HashSet. They add
     *  image URLs found on their respective subpages.
     *  Using 'addfoundImages()', they add an arbitrary
     *  number of these URLs to the master hashset
     */
    public void run()  {
        if (! thread) { 
            System.out.println("This is not a thread");   
            return;
        }

        try {
        
            System.out.println("\n\n============THREAD=============");
            System.out.println("\n\nTitle: " + this.getTitle() +"\n\n"); 

            writeJSON();
            getElementsHashed("img", "src");
            getElementsHashed("meta", "content");
            bruteForceLinkSearch();
            addFoundImages();

            subpages.remove(this.url);
            return;
        
        }
        catch (IOException e) {
            System.out.println("Thread run error");
        }
    }
    

    /*
        This is run by the initial WebCrawler, 
        which is the one that creates all of the threads.
    */
    public void getAllImageURLs() throws IOException {

        writeJSON(); 
        getElementsHashed("img", "src");
        getElementsHashed("meta", "content");
        bruteForceLinkSearch(); 
        getElementsHashed("a", "href");
        
    }
    


    /**
     * Brute-force parsing of the generated XML file.
     * All image files are added to the appropriate 
     * HashSet
     */

    public void bruteForceLinkSearch() {

        String target = "https://",
                        unclean_url,
                        line,
                        cleaned_url;
        String [] cleanURLs = new String []{};
        
        // will determine whether to save it to global links or links for the thread
        HashSet <String> hashset = getHashSet();
        BufferedReader reader = null;  

        try {

            reader= new BufferedReader(new FileReader(xml_output));

            while ((line = reader.readLine()) != null) { 
                
                // looks for links that are not referred to by the <a> tag
                if(line.contains(target) & !subpages.contains(line)) {

                    // gets the whole line starting from "https://"
                    unclean_url= line.substring(line.indexOf(target));

                    // seperating URLs from attributes, text, and/or clutter
                    cleanURLs  = unclean_url.split(" ");

                    // sanitizing malformed URLs
                    for (String s: cleanURLs) {

                        if (s.contains(",")) { 
                            continue; 
                        }

                        s = urlSanitize(s);
                    
                        if (! s.contains(hostname)) {
                            continue;
                        }

                        // ensuring that resulting string is still URL
                        if (s.contains (target)) {
                            synchronized(links) {
                                cleaned_url = s.substring(s.indexOf(target));
                                if (!links.contains(cleaned_url)) {
                                    hashset.add(cleaned_url); // varies depending on whether or not is thread
                                }      
                            }                            
                        }

                    }

                }
            }
        } 
        catch (FileNotFoundException e) {
            e.printStackTrace();
        } 
        catch (IOException e) {
            e.printStackTrace();
        } 
        finally {
            try { 
                reader.close(); 
            }
            catch (IOException e) {
                e.getStackTrace();
            }   
        }


    }






    /*
    Gets all elements of a selected selctor via a Hashset
    Need to actually implement 
    */
    public void getElementsHashed (String selector, String attribute) {
        // getting the correct tag
        Elements elements = doc.select (selector);

        // will get appropriate hashSet depending on if this is a thread
        HashSet <String> hashset = getHashSet();

        if (elements.isEmpty()) return;

        for (Element e: elements) {

            String _attribute= e.attr(attribute);

            // getting image tags
            if (selector.equals("img")) { 

                // determine if it is a link and sanitize
                if (_attribute.contains("http") ) {

                    if (_attribute.contains(",")) {
                        continue;
                    }
                    _attribute = urlSanitize(_attribute); 

                    // adding it to appropriate hashset
                    synchronized(links) {
                        if (!links.contains(_attribute))

                        // debugging 
                        //System.out.println(_attribute);
                        hashset.add(_attribute); 
                    }
                }
                else {
                    extraLinks.add(_attribute);
                }
            }


            // getting subpages --- only relative paths with no php scripts 
            else if (selector.equals("a") ) {
               
                // links with same domain added are counted as subpages
                if (_attribute.contains(domain)) {
                    subpages.add(_attribute);
                }
                
                // relative paths are converted to absolute
                else if (!_attribute.contains("http") &
                    _attribute.length() > 1 &&
                    _attribute.charAt(0) == '/' &
                   !_attribute.contains("php?") ){


                        subpages.add(domain+_attribute);
                }
                
            }
      

            // another alternative for defining images
            else if (selector.equals("meta")) {
                if (e.attr("property").equals("og:image"))
                    hashset.add(_attribute);

            }
        }
    } 


   




    /**
     * @param target        the string that you are looking for
     *                      it should always be the same thing. Will have to test
     * @return              the String that contains all the JSON to be parsed
     * 
     * this is used because JSOUP, while it can be useful,  only loads the DOM prior 
     * to the loading of the script. After all the JS is run on a dynamic webpage,
     * the data is stored as a JSON file
     */
    public String getPostDataJSON () {
        String target = "window."; // commonly indicates json length
        BufferedReader reader = null;  
        try {

            reader= new BufferedReader(new FileReader(xml_output));
            String line, key, value,retVal="";

            while ((line = reader.readLine()) != null) {  

                // if line cannot possibly contain JSON, skip it
                if (line.length() < target.length()) {
                    continue;
                } 
                
                // return the post-script JSON for easier parsing
                if (line.substring(0, target.length()).equals(target) && line.length() > 5000) {
                    String unescape = line.replaceAll("\\\\"+"\"", "");
                    String [] unescapeSplit = unescape.split(",");
                    for (String s: unescapeSplit) {
                        retVal+= "\n" + s;
                       
                        // breaks up JSON records and stores urls, which contain images
                        if (s.contains(":") & s.contains("url")) {
                            key = s.substring(0, s.indexOf(":"));
                            value = s.substring(s.indexOf(":")+1, s.length());
                            if (value.contains("http")){
                                if (value.contains(",")) {
                                    continue;
                                }
                                value = urlSanitize(value); 
                                links.add(value.substring(value.indexOf("http")));
                            }
                        }
                    }
                }
            }
            return retVal;
  

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try { 
                reader.close(); 
            }
            catch (IOException e) {
                e.getStackTrace();
            }   
        }

        // check the path specified by global variable xml_output for this error
        return ("Target String was not detected"); 
    }



    

    public void writeJSON() throws IOException {
        // write the xml output to a file -- for debugging
        File output = new File(json_output);
        FileWriter writer = new FileWriter(output);

        // moves the json portion of the text to the resource directory
        writer.write(getPostDataJSON());

        // cleaning up resources
        writer.flush();
        writer.close();
    }

}
    