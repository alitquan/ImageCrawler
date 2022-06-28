package com.eulerity.hackathon.imagefinder;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.FileReader;
import java.util.HashSet;
import java.util.Random;


import java.net.URI;

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
    boolean thread;                  // true if instance is a thread
    static boolean externalSites,    // true if are external sites allowed  
                   fileOnly;         // true if image formats are allowed (png,jpg,etc)

    Document doc;

    static HashSet <String> globalImageURLs;   // global image urls a few false positives
    static HashSet <String> subPageImageURLs;  // stores subpage urls gathered by threads

    HashSet <String> miscLinks;         // css, js, misc
    HashSet <String> subpageLinks;      // absolute and relative paths
    HashSet <String> threadLinks;       // container for indiv. thread. Holds URLs

    static int threadLimit = 0;                         // max number of images a thread can obtain  
    int create_time= (int) System.currentTimeMillis();  // for creating unique file names

    // resource folders. gets cleaned out using pom.xml configuration
    final String resources_path = "resources/", 
                 xml_output     = resources_path + "output" + create_time +".txt",
                json_output    = resources_path + "json_output"+ create_time + ".txt";




    /** 
     * @param   url         URL address where crawler will be initiated
     *                      ideally will be the home pag
     * @param   is_thread   true if this instance is a thread
     * 
     * Depending on the value of @param is_thread , this Constructor
     * can determine whether or not a given instance is a thread.
     * Setting the class variable @thread to true will allow the 
     * thread to use the run method. The threads use static variables
     * to make sure that the same URL is never recorded. Synchronization
     * helps with this. 
     */
    public WebCrawler(String _url, boolean is_thread) throws Exception {
        this.url = _url; 
        this.thread = is_thread;

        // threads cannot reinitialzie static variables
        if (!this.thread) {
            globalImageURLs = new HashSet<String> ();
            subPageImageURLs = new HashSet<String>(); 
        }

        //set-up
        threadLinks = new HashSet<String> ();
        miscLinks = new HashSet <String> ();
        subpageLinks = new HashSet<String>();
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

            // creating a web client, surpressing its error ()
            WebClient webclient = new WebClient();
            webclient.getOptions().setThrowExceptionOnScriptError(false);
            webclient.getOptions().setPrintContentOnFailingStatusCode(false);
            webclient.getOptions().setThrowExceptionOnFailingStatusCode(false);

            //rendering an html page post-javascript
            HtmlPage myPage = webclient.getPage(url);

            // convert html to xml; write it to a path 
            doc = Jsoup.parse(myPage.asXml());
            File output = new File(xml_output);
            FileWriter writer = new FileWriter(output);
            writer.write(myPage.asXml());
            writer.flush();
            writer.close();

            // closes web client windows
            webclient.closeAllWindows();

        }
        catch (Exception e) {
            // blank --- we don't want unnecessary errors
        }
    }



    /** @limit sets the maximum number of url procurred by a thread */
    public void setThreadLimit(int limit) {
        threadLimit = limit;
    }

    /** if @param bool is true, external urls are allowed  */
    public void setExternal(boolean bool) {
        externalSites = bool;
    }


    /** if @param bool is true, all URLs must include an image foormat */
    public void setMandatoryFormat(boolean bool) {
        fileOnly = bool;
    }


     // prints out the file 
    public String getTitle() {
        return doc.title(); 
    } 

    // useful after thread is done crawling a page 
    public void removeSubPage(String url) {
        this.subpageLinks.remove(url);
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
        return globalImageURLs.size();
    }



    

    
    
    /**
     * Will return the appropriate hashset (global or thread-local)
     * Threads cannot directly add to the global URL hashset.
     * It can consume too much RAM.
     * Instead, URLS that threads crawl will be saved to their own
     * HashSet. A random assortment will be selected and added to the static
     * hashset.
     * @return      if a thread is running this, the thread's individual URL hashset
     *              otherwise, the global hashset will be returned
     * 
     * @see addFoundImages to know see how each Thread's local hashset is eventually
     *                     integrated into the global hashset 
     */
    public HashSet <String> getHashSet() {
        if (this.thread) 
            return this.threadLinks;
        else 
            return globalImageURLs;
    }






    public String [] hashSetToArray( HashSet <String> hs) { 
        String retArr[] = new String [hs.size()];
        int i = 0;
        for (String url: hs) {
            retArr[i++] = url;
        }
        return retArr;
    }

    public String [] retMainURLs() {
        return hashSetToArray(globalImageURLs); 
    }

    public String [] retsubPageURLs() {
         return hashSetToArray(subPageImageURLs);  
    }

    public String [] retSubpageLinks() {
         return hashSetToArray(subpageLinks); 
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
            System.out.println("invalid subpage");
            return null;
        }

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



    /**
     * This can only be run by threads. 
     * It adds a random assortment of image URLS 
     * collected from the subpageLinks that threads 
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
            System.out.println("Random element: " + randomURL);
            
            // add random element to list
            synchronized (subPageImageURLs) { 
                if (! subPageImageURLs.contains(randomURL) )
                    subPageImageURLs.add(randomURL);
            }
                    
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
        getElementsHashed("a", "href");
        bruteSearch(fileOnly, externalSites); 
    } 




    /**
     *  Web Crawlers that use this @run method scour
     *  subpages saved by the WebCrawler that crawled
     *  the original page. 
     * 
     *  They save resources to their own HashSet 
     *  before randomly choosing an assortment and 
     *  adding it to the global HashSet of potential
     *  URLs. 
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
            getElementsHashed("a", "href");
            bruteSearch(true, externalSites);
            addFoundImages();            
            return;
        
        }
        catch (IOException e) {
            System.out.println("Thread run error");
        }
    }
    



    



    public static boolean  hasImageFormat(String url) {
        String _url = url.toLowerCase();
        if (_url.contains("jpg") |
            _url.contains("png") | 
            _url.contains("jpeg")
        ) return true;

        return false; 
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
     * 
     * @param url       an image url that is being filtered out
     *                  because it may not meet requirements
     * @return          yes if it is a desired url
     */
    public boolean legalEntry(String url) {

        if (url.contains("gif"))
            return false;

         // if user only wants image formats 
        if (fileOnly) {
            if (! hasImageFormat(url))
                return false; 
        }
        // if external sites are disallowed, skip external sites 
        if (!externalSites) {
            if (!url.contains(hostname)) 
                return false; 
        }

        return true;
    }
    


    /**
     * 
     * @param imgFormat         true if only certain image formats are allowed
     * @param externalSite      true if external sites are allowed  
     */
    public void bruteSearch(boolean imgFormat, boolean externalSite) {
        String target = "https://",
               line   = "",
               unSanitized,
               sanitized;
        String [] splitLine= new String []{};
        
        // returns or thread-local 
        HashSet <String> hashset = getHashSet();
        BufferedReader reader = null;  

        try {

            reader= new BufferedReader(new FileReader(xml_output));
            
            while ((line = reader.readLine()) != null) { 
                splitLine = line.split(" ");                

                for (String s: splitLine) {

                    if (s.contains(",")) continue; 

                    if (s.contains("https")) {
                         unSanitized = s.substring(s.indexOf("https"));
                         sanitized = urlSanitize(unSanitized); 

                         if (this.subpageLinks.contains(sanitized) || this.miscLinks.contains(sanitized)) {
                             System.out.println("Extra");
                             continue; 
                         }

                        //  System.out.println("\n\nOriginal: " + unSanitized);
                        //  System.out.println("Sanitized: " + sanitized);
                    }
                    else { continue; }
  

                    if (legalEntry(sanitized) == true) {
                        synchronized(globalImageURLs) {
                            if (!globalImageURLs.contains(sanitized)) {
                                hashset.add(sanitized); 
                                System.out.println("ADDED");
                            }      
                        }                            
                    } 

                       
                }
            }
        }
        catch (Exception e) {

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
        HashSet <String> appropriateHashSet = getHashSet();

        if (elements.isEmpty()) return;

        for (Element e: elements) {

            String _attribute= e.attr(attribute);

            // getting image tags
            if (selector.equals("img")) { 

                // determine if it is a link and sanitize
                if (_attribute.contains("http") ) {

                    if (_attribute.contains(",")|
                        _attribute.contains(".gif")) {
                        continue;
                    }
                    _attribute = urlSanitize(_attribute); 
                    
                    // adding it to appropriate hashset
                    synchronized(globalImageURLs) {
                        if (!globalImageURLs.contains(_attribute))

                        // debugging 
                        //System.out.println(_attribute);
                            appropriateHashSet.add(_attribute); 
                    }
                }
                else {
                    miscLinks.add(_attribute);
                }
            }


            // getting subpageLinks --- only relative paths with no php scripts 
            else if (selector.equals("a") ) {
               
                // globalImageURLs with same domain added are counted as subpageLinks
                if (_attribute.contains(domain)) {

                    _attribute = _attribute.substring(_attribute.indexOf("https"));
                    subpageLinks.add(_attribute);
                    System.out.println("a tag= "+ _attribute);
                }
                
                // relative paths are converted to absolute
                else if (!_attribute.contains("http") &
                    _attribute.length() > 1 &&
                    _attribute.charAt(0) == '/' &
                   !_attribute.contains("php?") ){


                        subpageLinks.add(domain+_attribute);
                }
                
            }
      

            // another alternative for defining images
            else if (selector.equals("meta")) {
                if (e.attr("property").equals("og:image"))
                    appropriateHashSet.add(_attribute);

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
            HashSet appropriateHashSet = getHashSet();

            while ((line = reader.readLine()) != null) {  

                // if line cannot possibly contain JSON, skip it
                if (line.length() < target.length()) {
                    continue;
                } 
                
                // return the post-script JSON for easier parsing
                if (line.substring(0, target.length()).equals(target) && line.length() > 5000) {
                    String unescape = line.replaceAll("\\\\"+"\"", "");
                    String [] unescapeSplit = unescape.split(",");
                    
                    for (String group: unescapeSplit) {
                        String [] commaSplit = group.split("\"");
                        for (String member: commaSplit) {

                            retVal+= "\n" + member;
                            if (member.contains("http")) {
                                key = member.substring(member.indexOf("http"));
                                key = urlSanitize(key);
                                System.out.println(key);
                                if (legalEntry(key))
                                    globalImageURLs.add(key.substring(key.indexOf("http")));

                            }
                           

                        }
                            
                       
                        // // breaks up JSON records and stores urls, which contain images
                        // if (s.contains(":") & s.contains("url")) {
                        //     key = s.substring(0, s.indexOf(":"));
                        //     value = s.substring(s.indexOf(":")+1, s.length());
                        //     if (value.contains("http")){
                        //         if (value.contains(",")) {
                        //             continue;
                        //         }
                        //         value = urlSanitize(value); 

                        //         if (legalEntry(value))
                        //             globalImageURLs.add(value.substring(value.indexOf("http")));
                        //     }
                        // }
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
        writer.flush();
        writer.close();
    }

}
    