package com.eulerity.hackathon.imagefinder;
import static org.junit.Assert.assertTrue;
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
    WebCrawler testInstance, testInstance2, test3;

    // using a blog 
    @Before 
    public void setUp() throws Exception{
        testInstance  = new WebCrawler(1, "https://richardbernabe.com/blog/");
        testInstance2 = new WebCrawler(1, "https://imgur.com/user/bessity/posts"); 
        test3 = new WebCrawler(1, "https://imgur.com/gallery/BrgX9vM");
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
        ArrayList <String> retVal = test3.getElementsHashedAL("img", "src");
        for (String s: retVal) {
            System.out.println(s);
        }
        assertTrue(true);
    }
}
