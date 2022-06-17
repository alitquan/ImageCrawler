package com.eulerity.hackathon.imagefinder;
import java.io.IOException;
import java.util.ArrayList; 
import java.util.HashSet;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


/*
    Referred to: 
    https://jsoup.org/
*/ 


public class WebCrawler {
    int depth; 
    //HashSet <String> urls;
    Document doc;

    /** 
     * @param   depth   how many subpages that you want to go through 
     * @param   url     URL address where crawler will be initiated
     *                  ideally will be the home page
     */
    public WebCrawler(int depth, String url) throws IOException {
        this.depth = depth; 
        // urls = new HashSet<String>();
        doc = Jsoup.connect(url).get();
    }

    // prints out the file 
    public String getTitle() {
        return doc.title(); 
    } 

    /** 
     * @param   selector    html-selector that you want to parse for
     */
    public Elements getElements(String selector) {
        return doc.select(selector);
    }
    

    private static void log(String msg, String... vals) {
        System.out.println(String.format(msg, vals));
    }
    
}
