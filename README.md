# <redacted> Hackathon Challenge
Congratulations on making it to this stage of <redacted>'s interview process! In this folder is a project for a partially built web application whose goal is to crawl a provided URL and pick out the images from it. This README will provide more information about the goals of the project, its structure, and setup and submission instructions.

## ImageFinder Goal
The goal of this task is to perform a web crawl on a URL string provided by the user. From the crawl, you will need to parse out all of the images on that web page and return a JSON array of strings that represent the URLs of all images on the page. [Jsoup](https://jsoup.org/) is a great basic library for crawling and is already included as a maven dependency in this project, however you are welcome to use whatever library you would like.

### Required Functionality
We expect your submission to be able to achieve the following goals:
- Build a web crawler that can find all images on the web page(s) that it crawls.
- Crawl sub-pages to find more images.
- Implement multi-threading so that the crawl can be performed on multiple sub-pages at a time.
- Keep your crawl within the same domain as the input URL.
- Avoid re-crawling any pages that have already been visited.

### Extra Functionality
No individual point below is explicitly required, but we recommend trying to achieve some extra goals as well, such as the following:
- Make your crawler "friendly" - try not to get banned from the site by performing too many crawls.
- Try to detect what images might be considered logos.
- Show off your front-end dev skills with Javascript, HTML, and/or CSS to make the site look more engaging.
- Any other way you feel you can show off your strengths as a developer 😊

**PLEASE do not send us a submission with only a basic JSoup crawl and only a couple lines of code.** This is your chance to prove what you could contribute to our team.

You have one week to work on the submission from the time when you receive it. To submit you assignment, zip up your project (`imagefinder.zip`) and email it back to me. **Please include a list of URLs that you used to test in your submissions.** You should place them in the attached `test-links.txt` file found in the root of this project.

## Running the Project
Here we will detail how to setup and run this project so you may get started, as well as the requirements needed to do so.

### Requirements
Before beginning, make sure you have the following installed and ready to use
- Maven 3.5 or higher
- Java 8
  - Exact version, **NOT** Java 9+ - the build will fail with a newer version of Java

### Setup
To start, open a terminal window and navigate to wherever you unzipped to the root directory `imagefinder`. To build the project, run the command:

>`mvn package`

If all goes well you should see some lines that end with "BUILD SUCCESS". When you build your project, maven should build it in the `target` directory. To clear this, you may run the command:

>`mvn clean`

To run the project, use the following command to start the server:

>`mvn clean test package jetty:run`

You should see a line at the bottom that says "Started Jetty Server". Now, if you enter `localhost:8080` into your browser, you should see the `index.html` welcome page! If all has gone well to this point, you're ready to begin!

