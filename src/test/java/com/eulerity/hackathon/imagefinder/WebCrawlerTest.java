package com.eulerity.hackathon.imagefinder;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.junit.Assert;
import org.junit.Test;
import org.junit.Before;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import java.util.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class WebCrawlerTest  {
    WebCrawler testInstance, testInstance2, test3,test4,test5,test6;
    protected static final Gson GSON = new GsonBuilder().create();

    // using a blog 
    @Before 
    public void setUp() throws Exception{
       // testInstance  = new WebCrawler(1, "https://richardbernabe.com/blog/");

        // test for webcrawling -- this one doesn't generate photos but has lots of links
       // testInstance2 = new WebCrawler(1, "https://imgur.com/user/bessity/posts"); 
       // test3 = new WebCrawler(1, "https://imgur.com/gallery/BrgX9vM");

        // problem is that your method only works for a specific page in imgur
        // test4 = new WebCrawler("https://unsplash.com/photos/fIq0tET6llw", false);
        // test5 = new WebCrawler("https://www.istockphoto.com/photos/bangladesh",false);
        test6 = new WebCrawler("https://allaboutcats.com/top-cat-blogs", false);


        
        // https://en.wikipedia.org/wiki/Gustavo_Petro
        // https://alltogether.swe.org/about-all-together/
        // https://iamafoodblog.com/ 
        // https://pinchofyum.com/    
    }
    
    // @Test
    // public void parsingTest() throws Exception {
    //     test5 = new WebCrawler("https://allaboutcats.com/top-cat-blogs", false);
    //     System.out.println("\n\n\nTesting XML search (no formatting)");
    //     int length = test5.getLinksLength();
    //     System.out.println("This is the length: " + length);
    //     test5.writeJSON();
    //     length = test5.getLinksLength();
    //     System.out.println("This is the length: " + length);
    //     test5.bruteSearchTwo(false,false);
        
    //     test6 = new WebCrawler("https://allaboutcats.com/top-cat-blogs", false);
    //     System.out.println("\n\n\nTesting XML search ( formatting)");
    //     int lengthFormat = test6.getLinksLength();
    //     System.out.println("This is the length: " + lengthFormat);
    //     test6.writeJSON();
    //     lengthFormat = test6.getLinksLength();
    //     System.out.println("This is the length: " + lengthFormat);
    //     test6.bruteSearchTwo(true,false);

    //     //assertTrue ( lengthFormat > length);

    // }

    @Test
    public void uniqueTest() throws Exception  {
        System.out.println("\n\n\n\n=========Testing URL crawl=========\n\n\n");
        test5 = new WebCrawler("https://allaboutcats.com/top-cat-blogs",false);
        test5.getElementsHashed("img", "src");
        test5.getElementsHashed("meta", "content"); 
        test5.getElementsHashed("a", "href");
        test5.bruteSearchTwo(false, false); 

    } 


 
    //@Test

    // public void urlReturn() throws IOException {
    //     test5.getAllImageURLs();
    //     String [] testArr = test5.retMainURLs();
    //     System.out.println(test5.hostname);
    //     Assert.assertEquals(testArr.length, test5.getLinksLength());
    // }


}
