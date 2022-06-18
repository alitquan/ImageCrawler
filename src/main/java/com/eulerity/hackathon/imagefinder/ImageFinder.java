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

	public static final String[] quanTest = {
			// hotlinking 
			"https://richardbernabe.com/wp-content/uploads/2021/05/elephant_logo_thin.png",
			"https://i.imgur.com/xpBAKXQ.jpeg",
			"https://upload.wikimedia.org/wikipedia/commons/thumb/7/7f/Stephen_Curry_Shooting_%28cropped%29_%28cropped%29.jpg/800px-Stephen_Curry_Shooting_%28cropped%29_%28cropped%29.jpg"
	};

	/*
	public static void returnLinks(String main_url) throws IOException {
		crawler = new WebCrawler(5, main_url);
		crawler.getElementsHashed(main_url);

	}
	*/
	
	@Override
	protected final void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 
		resp.setContentType("text/json");
		
		/*
		Returns the part of this request's URL that calls the servlet. This path 
		starts with a "/" character and includes either the servlet name or a 
		path to the servlet, but does not include any extra path information 
		or a query string. 
		*/
		String path = req.getServletPath();

		// what does this do?? 
		String url = req.getParameter("url");
		System.out.println("Got request of:" + path + " with query param:" + url);

		// is this the line that prints out the images? 
		//resp.getWriter().print(GSON.toJson(testImages));
		resp.getWriter().print(GSON.toJson(quanTest));
	}
}
