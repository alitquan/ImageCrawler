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

public class WebCrawlerTest {
    WebCrawler testInstance;

    // using a blog 
    @Before 
    public void setUp() throws IOException{
        testInstance = new WebCrawler(1, "https://richardbernabe.com/blog/");
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
    @Test
    public void selectorTest() {
        Elements retVal = testInstance.getElements("img");

        for (Element e: retVal) {
            System.out.println(1);
            System.out.println(e.attr("src"));
        }
        assertTrue(true);
    }
}
