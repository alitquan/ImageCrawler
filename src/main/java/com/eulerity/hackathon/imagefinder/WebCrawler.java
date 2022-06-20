package com.eulerity.hackathon.imagefinder;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FileReader;
import java.util.ArrayList; 
import java.util.HashSet;

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


public class WebCrawler {
    int depth; 
    Document doc;
    HashSet <String> links;             // image links
    HashSet <String> extraLinks;        // css, js, relative paths 
    ArrayList <String> _links;

    // resource folders. are cleaned out using pom.xml configuration
    final static String resources_path = "resources/", 
                        xml_output     = resources_path + "output.txt",
                        json_output    = resources_path + "json_output.txt";


    /** 
     * @param   depth   how many subpages that you want to go through 
     * @param   url     URL address where crawler will be initiated
     *                  ideally will be the home page
     * 
     * HTMLunit was actually designed for testing functionality and validity of 
     * javascript code. It throws errors whenever something unoptimal is detected, 
     * with respect to the efficacy of the JavaScript code. 
     */
    public WebCrawler(int depth, String url) throws Exception {
        this.depth = depth; 
        links = new HashSet <String> ();
        extraLinks = new HashSet <String> ();
        _links = new ArrayList<String>();

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
            int counter = 0;

            while ((line = reader.readLine()) != null) {  

                // if line cannot possibly contain JSON, skip it
                if (line.length() < target.length()) {
                    // debugging --- remove later
                    System.out.println(++counter);
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
                            links.add(value);
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



    /*
    Gets all elements of a selected selctor via a Hashset
    Need to actually implement 
    */
    public HashSet <String> getElementsHashed (String selector, String attribute) {
        Elements elements = doc.select (selector);
        for (Element e: elements) {
            String _attribute= e.attr(attribute);
            if (selector.equals("img")) { 
                if (_attribute.contains("https:")) {
                    System.out.println(_attribute);
                    links.add(_attribute); 
                }
                else {
                    extraLinks.add(_attribute);
                }
            }
           
        }
        return links; 
    }



    /*
    Gets all elements of a selected selctor
    */
    public ArrayList <String> getElementsHashedAL (String selector, String attr) {
        Elements elements = doc.select (selector);
        ArrayList <String> linksAL = new ArrayList<String>();
        for (Element e: elements) {
            linksAL.add(e.attr(attr));
        }
        return linksAL; 
    }


    public void getAllImageURLs() throws IOException {
        //writeJSON();
        // could be logos
        getElementsHashed("link", "href");
        System.out.println("==============Image URLS==============");
        System.out.println (getElementsHashed("img", "src").toString());
        System.out.println("==============MISC==============");
        System.out.println (extraLinks.toString());
        // needs to clean out css and js files, relative pathjs
    }
    
    public void printPhotoURLs() {
        System.out.println("Printing out photo urls: ");
        for (String s: _links) {
            System.out.println(s);
        }
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

    private static void log(String msg, String... vals) {
        System.out.println(String.format(msg, vals));
    }
    
}
