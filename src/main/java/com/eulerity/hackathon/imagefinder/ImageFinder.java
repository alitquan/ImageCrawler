package com.eulerity.hackathon.imagefinder;

import java.io.IOException;
import java.io.Reader;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
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
	
	public static String[] mainPageURLs = {};		/// urls of images on main page
	public static String[] subpageLinks = {};		//  links to subpages
	public static String[] subpageImageURL = {};    // urls of images located on subpages 



	@Override
	protected final void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		resp.setContentType("text/json");
		
		java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(java.util.logging.Level.OFF); 
		String path = req.getServletPath();


		// printing out parameter names from the API call 
		Enumeration<String> params = req.getParameterNames(); 
		while(params.hasMoreElements()){
			String paramName = params.nextElement();
			System.out.println("Parameter Name - "+paramName+", Value - "+req.getParameter(paramName));
		}
		
		
		// if request is to crawl subpages, display them and end  
		String renderSubpage = req.getParameter("render_subpage");
		if ( renderSubpage!= null) {
			if (renderSubpage.equals("true"))
				resp.getWriter().print(GSON.toJson(subpageImageURL));
			else
				System.out.println("Need to generate primary page first");
			return; 
		}


		// getting all parameters from request
		String url = req.getParameter("url");
		int maxThreads = Integer.parseInt( req.getParameter("threads") );
		int  perMain = Integer.parseInt( req.getParameter("permain") );
		int  perThread = Integer.parseInt ( req.getParameter("perthread")); 


		WebCrawler crawler;

		System.out.println("Got request of:" + path + " with query param:" + url);

		try {

			// initial crawl will load subpages
			crawler = new WebCrawler(url,false);
			crawler.setThreadLimit(perThread);
			crawler.getAllImageURLs();
			mainPageURLs  = crawler.retMainURLs();
			subpageLinks = crawler.retSubPagesAsArrays();


			// next few lines randomizes which subpages are selected 
			ArrayList<Integer> randomized = new ArrayList<>();
			// first it is an ordered arraylist 
			for (int i = 0; i < subpageLinks.length; i++) {
				randomized.add(i);
			}
			// arraylist is now randomized
			Collections.shuffle(randomized);
			

			ExecutorService executor = Executors.newFixedThreadPool(maxThreads);
			for (int i = 0; i < maxThreads; i++) {
				
				// create a thread to crawl random subpage
				String subpage = subpageLinks[randomized.remove(0)].replaceAll("\"", "");
				Runnable worker = crawler.retSubPageCrawler(subpage);
				crawler.removeSubPage(subpage);
				executor.execute(worker);

			}
		
			// cleaning up 
			executor.shutdown();	
			while (!executor.isTerminated()) {
			}
			System.out.println("\nFinished all threads");
			executor.shutdownNow();


			// updating urls 
			mainPageURLs = crawler.retMainURLs();
			subpageImageURL = crawler.retsubPageURLs();


			// adding 0 to respective field in HTML will print all photos
			// otherwise parameter asks as a limit
			if (perMain != 0) 
				mainPageURLs = Arrays.copyOf(mainPageURLs, perMain);

		}
		catch (Exception e) {
			//e.printStackTrace();
		}

		// display images from the original URL
		resp.getWriter().print(GSON.toJson(mainPageURLs));
		
		
		
	}
}
