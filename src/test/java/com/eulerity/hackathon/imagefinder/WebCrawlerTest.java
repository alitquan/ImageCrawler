package com.eulerity.hackathon.imagefinder;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.Before;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;

public class WebCrawlerTest {
    WebCrawler testInstance, testInstance2, test3,test4,test5;

    // using a blog 
    @Before 
    public void setUp() throws Exception{
        testInstance  = new WebCrawler(1, "https://richardbernabe.com/blog/");

        // test for webcrawling -- this one doesn't generate photos but has lots of links
        testInstance2 = new WebCrawler(1, "https://imgur.com/user/bessity/posts"); 
        test3 = new WebCrawler(1, "https://imgur.com/gallery/BrgX9vM");

        // problem is that your method only works for a specific page in imgur
        test4 = new WebCrawler(4, "https://unsplash.com/photos/fIq0tET6llw");
        test5 = new WebCrawler(1, "https://www.istockphoto.com/photos/bangladesh");
    }
    
    @Test
    public void defaultTest(){
       assertTrue(true);
    }
    
    @Test
    public void titleTest() {
        // determined by inspecting element 
        String actualTitle = "Richard Bernabe Photography and Travel Blog | Richard Bernabe";
        Assert.assertEquals(testInstance.getTitle(), actualTitle);
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
         
        HashSet <String> retVal = testInstance2.getElementsHashed("a", "href"); 
        Iterator <String> iterator = retVal.iterator();
        
        while (iterator.hasNext()) {
            System.out.println(iterator.next());
        }

        assertTrue(true);
    }

    //@Test
    public void selectorTest3() {
        System.out.println("Three");
        ArrayList <String> retVal = test3.getElementsHashedAL("link", "href");
       // retVal.addAll(test3.getElementsHashedAL(selector, attr))
        for (String s: retVal) {
            System.out.println(s);
        }
        assertTrue(true);
    }

    //@Test 
    public void jsonFindTest() throws IOException{
        System.out.println("\n\n===============JSON==================");
        test3.writeJSON();
        test3.printPhotoURLs();
        assertTrue(true);
    }

    @Test
    public void urlReturn() throws IOException {
        test5.getAllImageURLs();
        String [] testArr = test5.retURLsAsArrays();
        System.out.println(test5.hostname);
        Assert.assertEquals(testArr.length, test5.getLinksLength());
    }
}
