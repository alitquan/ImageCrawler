package com.eulerity.hackathon.imagefinder;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// annotating the servlet; defining attributes and routing 
@WebServlet(
    name = "ImageFinder",
    urlPatterns = {"/main"}
)
public class ImageFinder extends HttpServlet{
	private static final long serialVersionUID = 1L;

	// object for serializing Java objects to JSON 
	protected static final Gson GSON = new GsonBuilder().create();

	protected static WebCrawler crawler;

	//This is just a test array
	public static final String[] testImages = {
			"https://images.pexels.com/photos/545063/pexels-photo-545063.jpeg?auto=compress&format=tiny",
			"https://images.pexels.com/photos/464664/pexels-photo-464664.jpeg?auto=compress&format=tiny",
			"https://images.pexels.com/photos/406014/pexels-photo-406014.jpeg?auto=compress&format=tiny",
			"https://images.pexels.com/photos/1108099/pexels-photo-1108099.jpeg?auto=compress&format=tiny"
    };
	
	public static String[] imageLinks = {};
	public static String[] subpageLinks = {};



	@Override
	protected final void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		resp.setContentType("text/json");
		
		java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(java.util.logging.Level.OFF); 
		String path = req.getServletPath();

		String url = req.getParameter("url");
		WebCrawler crawler;

		int maxThreads = 2;
		System.out.println("Got request of:" + path + " with query param:" + url);

		try {
			crawler = new WebCrawler(url,false);
			crawler.getAllImageURLs();
			imageLinks  = crawler.retURLsAsArrays();
			subpageLinks = crawler.retSubPagesAsArrays();


			// array of ordered numbers  
			ArrayList<Integer> randomized = new ArrayList<>();

			for (int i = 0; i < subpageLinks.length; i++) {
				randomized.add(i);
			}
			
			// array is now randomized
			Collections.shuffle(randomized);
			
			ExecutorService executor = Executors.newFixedThreadPool(maxThreads);

			for (int i = 0; i < maxThreads; i++) {
				
				// create a thread using randomized subpage
				String subpage = subpageLinks[randomized.remove(0)].replaceAll("\"", "");
				Runnable worker = crawler.retSubPageCrawler(subpage);
				
				executor.execute(worker);
			}
		
			executor.shutdown();
			
			while (!executor.isTerminated()) {
			}

			System.out.println("\nFinished all threads");
			executor.shutdownNow();
			imageLinks = crawler.retURLsAsArrays();

		}
		catch (Exception e) {
			//e.printStackTrace();
		}

		resp.getWriter().print(GSON.toJson(imageLinks));
		
	}
}
