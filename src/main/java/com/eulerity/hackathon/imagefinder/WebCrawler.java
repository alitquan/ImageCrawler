package com.eulerity.hackathon.imagefinder;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.ArrayList; 
import java.util.HashSet;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

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
    HashSet <String> links;
    
    


    /** 
     * @param   depth   how many subpages that you want to go through 
     * @param   url     URL address where crawler will be initiated
     *                  ideally will be the home page
     */
    public WebCrawler(int depth, String url) throws Exception {
        this.depth = depth; 
        links = new HashSet <String> ();
        
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

            // prints out page as xml -- for testing
            //System.out.println(myPage.asXml());

            // write the xml output to a file -- for debugging
            File output = new File("output.txt");
            FileWriter writer = new FileWriter(output);
            writer.write(myPage.asXml());
            writer.flush();
            writer.close();

            // prints out html without tags -- for debugging
            System.out.println(myPage.asText());

            // closes web client windows
            webclient.closeAllWindows();

        }
        catch (Exception e) {

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

    /*
    Gets all elements of a selected selctor via a Hashset
    Need to actually implement 
    */
    public HashSet <String> getElementsHashed (String selector, String attr) {
        Elements elements = doc.select (selector);
        for (Element e: elements) {
            links.add(e.attr(attr));
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
   
    
    

    private static void log(String msg, String... vals) {
        System.out.println(String.format(msg, vals));
    }
    
}
