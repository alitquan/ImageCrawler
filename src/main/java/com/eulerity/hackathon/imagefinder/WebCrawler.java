package com.eulerity.hackathon.imagefinder;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.FileReader;
import java.util.ArrayList; 
import java.util.HashSet;

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
    boolean thread;
    Document doc;
    static HashSet <String> links;             // image links
    HashSet <String> extraLinks;        // css, js, relative paths
    HashSet <String> subpages;  
    int create_time= (int) System.currentTimeMillis();

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
        if (links == null) {
            links = new HashSet<String>();
        }
        extraLinks = new HashSet <String> ();
        subpages = new HashSet<String>();
        URI uri = new URI(url);
        domain = uri.getScheme() + "://" + uri.getHost();
        hostname = domain.startsWith("www.") ? domain.substring(4) : domain;  

        // how to create dirs for testing
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




    
    public WebCrawler retSubPageCrawler(String url) {
        try {
            return new WebCrawler(url, true);
        }
        catch (Exception e){
            System.out.println("*************retSubPageCrawler************");
            return null;
        }

    }
    
    
    public void reset() {
        links = new HashSet<>(); 
    }



    @Override
    public void finalize() {
        // if (! thread) return;

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

  


    /** 
     * @param   selector    html-selector that you want to parse for
     * Testing method. Delete later
     */
    public Elements getElements(String selector) {
        return doc.select(selector);
    }


    
    
    public int getLinksLength() {
        return links.size();
    }





    public String [] retURLsAsArrays() {

        String retArr[] = new String [links.size()];
        int i = 0;
        for (String url: links) {
            retArr[i++] = url;
        }
        return retArr;

    }

    public String [] retSubPagesAsArrays() {

        String retArr[] = new String [subpages.size()];
        int i = 0;
        for (String url: subpages) {
            retArr[i++] = url;
        }
        return retArr;
    }


    // replaces illegal url characters in string s
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


    public void run()  {
        if (! thread) { 
            System.out.println("This is not a thread");   
            return;
        }
        synchronized (links) {

            try {
            
                System.out.println("\n\n============THREAD=============");
                System.out.println("\n\nTitle: " + this.getTitle() +"\n\n"); 
                writeJSON();
                getElementsHashed("img", "src");
                getElementsHashed("meta", "content");
                bruteForceLinkSearch();

                subpages.remove(this.url);
                return;
            
            }
            catch (IOException e) {

            }
        }
    }
    




    public void getAllImageURLs() throws IOException {

        writeJSON();
        // could be logos
        System.out.println("==============Image URLS==============");
        getElementsHashed("img", "src");
        getElementsHashed("meta", "content");
        bruteForceLinkSearch();
        System.out.println (links.toString());
        System.out.println("==============MISC==============");
        getElementsHashed("link", "href"); // needs to implement this 
        System.out.println (extraLinks.toString());
        getElementsHashed("a", "href");
        System.out.println ("\n\n=============SUBPAGES===========" + subpages.toString());
        // needs to clean out css and js files, relative pathjs
        
    }
    




    public void bruteForceLinkSearch() {

        String target = "https://",
                        unclean_url,
                        line,
                        cleaned_url;
        String [] cleanURLs = new String []{};

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
                        if (s.contains (target)) {
                            synchronized(links) {
                                cleaned_url = s.substring(s.indexOf(target));
                                if (!links.contains(cleaned_url)) {
                                    links.add(cleaned_url);
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
        Elements elements = doc.select (selector);
        if (elements.isEmpty()) return;
        for (Element e: elements) {
            String _attribute= e.attr(attribute);
            if (selector.equals("img")) { 
                if (_attribute.contains("http") && _attribute.contains(hostname)) {
                    System.out.println(_attribute);
                    if (_attribute.contains(",")) {
                        continue;
                    }
                    _attribute = urlSanitize(_attribute); 
                    synchronized(links) {
                        if (!links.contains(_attribute))
                        links.add(_attribute); 
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
      
            // <meta property="og:image" content= ... > 
            else if (selector.equals("meta")) {
                if (e.attr("property").equals("og:image"))
                    links.add(_attribute);

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
                            //System.out.println(value);
                            if (value.contains("http")){
                                if (value.contains(",")) {
                                    continue;
                                }
                                value = urlSanitize(value); 
                                System.out.println("Legible value: " + value);
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
        // using example 4, see if finding the 'window.' serves as locating line 
        writer.write(getPostDataJSON());

        // cleaning up resources
        writer.flush();
        writer.close();
    }

}
    