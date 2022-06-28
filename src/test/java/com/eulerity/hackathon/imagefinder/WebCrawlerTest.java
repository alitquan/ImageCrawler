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
    WebCrawler testInstance, testInstance2, test3,test4,test5;
    protected static final Gson GSON = new GsonBuilder().create();

    // using a blog 
    @Before 
    public void setUp() throws Exception{
       // testInstance  = new WebCrawler(1, "https://richardbernabe.com/blog/");

        // test for webcrawling -- this one doesn't generate photos but has lots of links
       // testInstance2 = new WebCrawler(1, "https://imgur.com/user/bessity/posts"); 
       // test3 = new WebCrawler(1, "https://imgur.com/gallery/BrgX9vM");

        // problem is that your method only works for a specific page in imgur
        test4 = new WebCrawler("https://unsplash.com/photos/fIq0tET6llw", false);
        test5 = new WebCrawler("https://www.istockphoto.com/photos/bangladesh",false);


        
        // https://en.wikipedia.org/wiki/Gustavo_Petro
        // https://alltogether.swe.org/about-all-together/
        // https://iamafoodblog.com/ 
        // https://pinchofyum.com/    
    }
    
    //@Test
    public void defaultTest(){
       assertTrue(true);
    }
    
    //@Test
    public void titleTest() {
        // determined by inspecting element 
        String actualTitle = "Richard Bernabe Photography and Travel Blog | Richard Bernabe";
        //Assert.assertEquals(testInstance.getTitle(), actualTitle);
        assertTrue(true);
    }

    // need to create a valid asserion here 
    //@Test
    public void selectorTest() {
        Elements retVal = testInstance.getElements("img");

        for (Element e: retVal) {
            System.out.println(1);
            System.out.println(e.attr("src"));
        }
        assertTrue(true);
    }

    //@Test 
    public void selectorTest2() {
        /* 
        HashSet <String> retVal = testInstance2.getElementsHashed("a", "href"); 
        Iterator <String> iterator = retVal.iterator();
        
        while (iterator.hasNext()) {
            System.out.println(iterator.next());
        }
        */
        assertTrue(true);
    }

    //@Test
    public void selectorTest3() {

        /*
        System.out.println("Three");
        HashSet <String> retVal = test3.getElementsHashed("link", "href");
       // retVal.addAll(test3.getElementsHashedAL(selector, attr))
        for (String s: retVal) {
            System.out.println(s);
        }
        */
        assertTrue(true);
    }

    //@Test 
    public void jsonFindTest() throws IOException{
        System.out.println("\n\n===============JSON==================");
        //test3.writeJSON();
        assertTrue(true);
    }

    //@Test
    public void urlReturn() throws IOException {
        test5.getAllImageURLs();
        String [] testArr = test5.retMainURLs();
        System.out.println(test5.hostname);
        Assert.assertEquals(testArr.length, test5.getLinksLength());
    }

    //@Test
    public void validImageTest() {
        System.out.println("\n\n\nUsing isImage");
        // url one 
        boolean testOne   = WebCrawler.isImage("https://media.istockphoto.com/photos/proud-of-what-theyve-made-picture-id923271526?k=20&m=923271526&s=612x612&w=0&h=eTMfqqYNbwzTy4cvmBSYHNaadeqx2KQaiuUVflDG8Wc=");
        boolean testTwo   = WebCrawler.isImage("https://media.istockphoto.com/photos/happy-indian-family-picture-id909851658?k=20&m=909851658&s=612x612&w=0&h=3PlKHLvEHre57KVeunohIH_464fAYgySYP2AKn6fklQ="); 
        boolean testThree = WebCrawler.isImage("https://wwww.istockphoto.com/photos/bangladesh");
        boolean testFour  = WebCrawler.isImage("https://www.istockphoto.com/tr/vekt%C3%B6r/g%C3%BCney-asya-b%C3%B6lgesi-g%C3%BCney-asya-daki-%C3%BClkelerin-haritas%C4%B1-vekt%C3%B6r-illustration-gm1162454114-318860997");
        // url two
        boolean test4 = WebCrawler.isImage("https://images.unsplash.com/profile-1637252443353-99d83ca548d2image?auto=format&fit=crop&w=32&h=32&q=60&crop=faces&bg=fff");
        boolean test5 = WebCrawler.isImage("https://www.instagram.com/reismaatjes/");
        boolean test6 = WebCrawler.isImage("http://almostthatfamous.com");
        boolean test7 = WebCrawler.isImage("https://www.instagram.com/screen_post");
        boolean test8 = WebCrawler.isImage("https://images.unsplash.com/photo-1507608616759-54f48f0af0ee?ixid=MnwxMjA3fDB8MXxzZWFyY2h8MTN8fHJhbmRvbXxlbnwwfHx8fDE2NTYzNDg5Mzg&ixlib=rb-1.2.1");
        

        System.out.println (testOne);
        System.out.println (testTwo);
        System.out.println (testThree);
        System.out.println (testFour);
        System.out.println(test4);
        System.out.println(test5);
        System.out.println(test6);
        System.out.println(test7);
        System.out.println(test8);
    }

    //@Test
    public void jsonTest() {
        System.out.println("\n\n\nUsing GSON");
        String test1 = GSON.toJson("http://www.robinooode.com"); 
        String test2 = GSON.toJson("https://images.unsplash.com/photo-1515168746408-0f924dbb5c39?ixid=MnwxMjA3fDB8MXxjb2xsZWN0aW9ufDIwfDY3NTYwODh8fHx8fDJ8fDE2NTYzNzI4Njc&ixlib=rb-1.2.1"); 
        
        System.out.println(test1); 
        System.out.println(test2);

    }
}
