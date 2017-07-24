//package PageParser;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.io.PrintWriter;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.URL;
import java.net.HttpCookie;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Map;
import java.util.StringJoiner;
import java.net.HttpURLConnection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.Connection;

import java.io.IOException;
import java.io.FileWriter;
import java.util.concurrent.TimeUnit;
import java.sql.*;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.io.BufferedWriter;

public class testBot {

    public static void main(String[] args){
	System.out.println("bla");
	PageHandler pgHand = new PageHandler();
	String loginUrl = "https://www.instagram.com/accounts/login/ajax/";
	String ref = "https://www.instagram.com/accounts/login/";
	String likeUrl = "https://www.instagram.com/web/likes/1174660949266339394/like/";
	String picUrl = "https://www.instagram.com/p/BBNO-jkixZC/?tagged=ghjf";
	try{
	    //pgHand.tofile(pgHand.GetPageContent(logiUrl));
	    pgHand.GetPageContent(ref);
	    String params = "username="+URLEncoder.encode("treemmetries", "UTF-8")+"&"+"password="+URLEncoder.encode("123ABC", "UTF-8");//pgHand.getFormParams(pgHand.GetPageContent(logiUrl));
	    pgHand.sendPost(loginUrl, params, "https://www.instagram.com/p/BBNO-jkixZC/?tagged=ghjf");
	    //pgHand.tofile(pgHand.GetPageContent("https://www.instagram.com/accounts/edit/"), "priv.html");
	    //pgHand.GetPageContent(picUrl);
	    //pgHand.sendPost(likeUrl, "", ref);


	} catch(Exception e){
	    e.printStackTrace();
	}
	//pgHand.sendPost(url, params, ref);
    }

}

class PageHandler{

    private HttpURLConnection conn;
    private List<String> cookies;
    static final String COOKIES_HEADER = "Set-Cookie";
    //private final String USER_AGENT = "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:48.0) Gecko/20100101 Firefox/48.0";//"Mozilla/5.0";
    private final String USER_AGENT = "Mozilla/5.0";
    static java.net.CookieManager msCookieManager = new java.net.CookieManager();


    public String sendPost(String url, String postParams, String Ref) throws Exception {
	boolean redirect = false;

	System.out.println("begin of post");
	URL obj = new URL(url);
	conn = (HttpURLConnection) obj.openConnection();
	System.out.println("after conn");

	int responseCode;
	//postParams = URLEncoder.encode(postParams, "UTF-8");
	//byte[] params = postParams.getBytes( StandardCharsets.UTF_8 );

	/*int responseCode = conn.getResponseCode();

	  if (responseCode != HttpURLConnection.HTTP_OK) {
	  if (responseCode == HttpURLConnection.HTTP_MOVED_TEMP || responseCode == HttpURLConnection.HTTP_MOVED_PERM)
	  redirect = true;
	  }*/

	if(redirect){

	    System.out.println("\n #### redirected #### \n");
	    // get redirect url from "location" header field
	    String newUrl = conn.getHeaderField("Location");

	    // get the cookie if need, for login
	    //String cookies = conn.getHeaderField("Set-Cookie");

	    // open the new connnection again
	    conn = (HttpURLConnection) new URL(newUrl).openConnection();

	}
	System.out.println("bevor act like browser");

	// Acts like a browser

	conn.setUseCaches(false);
	conn.setRequestMethod("POST");
	conn.setRequestProperty("Host", "www.instagram.com");
	conn.setRequestProperty("User-Agent", USER_AGENT);
	conn.setRequestProperty("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
	conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
	conn.setRequestProperty("Accept", "*/*");
	conn.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
	conn.setRequestProperty("X-Instagram-AJAX", "1");
	conn.setRequestProperty("X-Requested-Width", "XMLHttpRequest");

	/*if(this.cookies!=null){
	  for (String cookie : this.cookies) {
	  conn.addRequestProperty("Cookie", cookie.split(";", 1)[0]);
	  System.out.println(cookie.split(";", 1)[0]);
	  }
	  }*/
	StringJoiner joiner = new StringJoiner(";");
	String token = "";
	if (msCookieManager.getCookieStore().getCookies().size() > 0) {
	    // While joining the Cookies, use ',' or ';' as needed. Most of the servers are using ';'
	    for (HttpCookie cookie : msCookieManager.getCookieStore().getCookies() ){
		joiner.add(cookie.toString());
		if(cookie.toString().split("=")[0].equals("csrftoken")){
		    token = cookie.toString().split("=")[1];
		}
	    }
	    joiner.add("ig_vw=1366");
	    joiner.add("ig_pr=1");
	    conn.setRequestProperty("Cookie",joiner.toString());    
	}

	conn.setRequestProperty("X-CSRFToken", token);
	conn.setRequestProperty("Connection", "keep-alive");
	conn.setRequestProperty("Referer", Ref);
	conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
	conn.setRequestProperty("Content-Length", Integer.toString(postParams.length()));

	System.out.println("befor dooutput");
	conn.setDoOutput(true);
	//System.out.println("befor doinput");
	conn.setDoInput(true);

	Map<String, List<String>> map = conn.getRequestProperties();
	for (Map.Entry<String, List<String>> entry : map.entrySet()) {
	    System.out.println(entry.getKey() + " : "+ entry.getValue());
	}
	System.out.println(conn.getRequestProperty("Host"));

	// Send post request
	System.out.println("befor dataoutputstream");
	DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
	if(postParams!=null){
		wr.writeBytes(postParams);
	}
	wr.flush();
	wr.close();
	System.out.println("after dadaouputstream");

	responseCode = conn.getResponseCode();

	System.out.println("\nSending 'POST' request to URL : " + url);
	System.out.println("Post parameters : " + postParams);
	System.out.println("Response Code : " + responseCode);

	System.out.println("befor getInputstream");
	BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	System.out.println("after getInputstream");
	String inputLine;
	StringBuffer response = new StringBuffer();

	while ((inputLine = in.readLine()) != null) {
	    response.append(inputLine);
	}

	in.close();

	Map<String, List<String>> headerFields = conn.getHeaderFields();
	List<String> cookiesHeader = headerFields.get(COOKIES_HEADER);

	if (cookiesHeader != null) {
	    System.out.println("in fill cookies of post");
	    msCookieManager.getCookieStore().removeAll();
	    for (String cookie : cookiesHeader) {
		msCookieManager.getCookieStore().add(null,HttpCookie.parse(cookie).get(0));
		System.out.println("---   cookie: "+ cookie);
	    }
	}

	Thread.sleep(5*1000);
	System.out.println("end of post");
	//String bufPage = GetPageContent(url);
	//System.out.println(bufPage);
	tofile(response.toString());
	return response.toString();

    }

    public void tofile(String content){
	tofile(content, "bla.html");
    }

    public void tofile(String content, String fileName){
	FileWriter fileWriter = null;
	try {
	    File newTextFile = new File("/home/giovanni/Desktop/"+fileName);
	    //BufferedWriter writer =  new BufferedWriter(fWriter);
	    fileWriter = new FileWriter(newTextFile);
	    BufferedWriter writer;// =  new BufferedWriter(fileWriter);
	    //fileWriter.write(content);
	    //fileWriter.close();
	    writer = new BufferedWriter(fileWriter);
	    writer.write(content);
	    writer.newLine(); //this is not actually needed for html files - can make your code more readable though 
	    writer.close();
	} catch (IOException ex) {
	    //Logger.getLogger(WriteStringToFile.class.getName()).log(Level.SEVERE, null, ex);
	} finally {
	    try {
		fileWriter.close();
	    } catch (IOException ex) {
		//Logger.getLogger(WriteStringToFile.class.getName()).log(Level.SEVERE, null, ex);
	    }
	}
    }

    public String GetPageContent(String url) throws Exception {
	System.out.println("GetPageContent: "+url);
	boolean redirect = false;

	URL obj = new URL(url);
	conn = (HttpURLConnection) obj.openConnection();

	conn.setRequestMethod("GET");
	conn.setUseCaches(false);

	// act like a browser
	conn.setRequestProperty("User-Agent", USER_AGENT);
	conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
	conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
	conn.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
	conn.setRequestProperty("Upgrade-Insecure-Requests","1");

	/*if (cookies != null) {
	  for (String cookie : this.cookies) {
	  conn.addRequestProperty("Cookie", cookie.split(";", 1)[0]);
	  }
	  }*/

	StringJoiner joiner = new StringJoiner(";");
	String token = "";
	if (msCookieManager.getCookieStore().getCookies().size() > 0) {
	    // While joining the Cookies, use ',' or ';' as needed. Most of the servers are using ';'
	    for (HttpCookie cookie : msCookieManager.getCookieStore().getCookies() ){
		joiner.add(cookie.toString());
		if(cookie.toString().split("=")[0].equals("csrftoken")){
		    token = cookie.toString().split("=")[1];
		}
	    }
	    joiner.add("ig_vw=1366");
	    joiner.add("ig_pr=1");
	    conn.setRequestProperty("Cookie",joiner.toString());
	}

	Map<String, List<String>> map = conn.getRequestProperties();
	for (Map.Entry<String, List<String>> entry : map.entrySet()) {
	    System.out.println(entry.getKey() + " : "+ entry.getValue());
	}
	System.out.println(conn.getRequestProperty("Host"));

	int responseCode = conn.getResponseCode();
	System.out.println("In GetPageContent after respond code");
	System.out.println("\nSending 'GET' request to URL : " + url);
	System.out.println("Response Code : " + responseCode);

	if (responseCode != HttpURLConnection.HTTP_OK) {
	    if (responseCode == HttpURLConnection.HTTP_MOVED_TEMP || responseCode == HttpURLConnection.HTTP_MOVED_PERM)
		redirect = true;
	}

	if(redirect){

	    System.out.println("\n #### redirected #### \n");
	    // get redirect url from "location" header field
	    String newUrl = conn.getHeaderField("Location");

	    // get the cookie if need, for login
	    String cookies = conn.getHeaderField("Set-Cookie");

	    // open the new connnection again
	    conn = (HttpURLConnection) new URL(newUrl).openConnection();

	    conn.setRequestMethod("GET");
	    conn.setUseCaches(false);

	    // act like a browser
	    conn.setRequestProperty("Cookie", cookies);
	    conn.setRequestProperty("User-Agent", USER_AGENT);
	    conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
	    conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
	}

	/*Map<String, List<String>> map = conn.getHeaderFields();
	  for (Map.Entry<String, List<String>> entry : map.entrySet()) {
	  System.out.println("Key : " + entry.getKey() +
	  " ,Value : " + entry.getValue());
	  }*/

	BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	String inputLine;
	StringBuffer response = new StringBuffer();

	while ((inputLine = in.readLine()) != null) {
	    response.append(inputLine);
	}
	in.close();

	// Get the response cookies
	//setCookies(conn.getHeaderFields().get("Set-Cookie"));
	Map<String, List<String>> headerFields = conn.getHeaderFields();
	List<String> cookiesHeader = headerFields.get(COOKIES_HEADER);

	if (cookiesHeader != null) {
	    msCookieManager.getCookieStore().removeAll();
	    System.out.println("in fill cookies of get");
	    for (String cookie : cookiesHeader) {
		msCookieManager.getCookieStore().add(null,HttpCookie.parse(cookie).get(0));
		System.out.println("---   cookie: "+ cookie);
	    }               
	}


	return response.toString();

    }

    public String stripMp4Link(String html){

	Document doc = Jsoup.parse(html);
	Elements scriptElements = doc.getElementsByTag("script");
	//System.out.println("scripts : "+scriptElements.size());
	String[] lines;
	for(Element el : scriptElements){

	    lines = el.html().split("\t");
	    //System.out.println(lines.length);

	    for(String line : lines){

		if(line.contains("file:")){
		    String[] buf = line.split("\"");
		    if(buf.length >= 3){
			return buf[1];
		    }	
		}
	    }
	}
	System.out.println("last return");
	return "";
    }

    public String getMp4Link(String link){ //argument is serienstrem.to link to desired video
	System.out.println("String getMp4Link(String link)");
	try{
	    String streamcloudLink = "https://serienstream.to"+getStreamcloudLink(GetPageContent(link)); //get link to streamcloud

	    String params = getFormParams(GetPageContent(streamcloudLink)); //get post parameters

	    System.out.println("sleeping........");
	    Thread.sleep(15*1000); // wait for streamcloud countdown

	    return stripMp4Link(sendPost(streamcloudLink, params, "http://streamcloud.eu"));
	} catch(Exception e) {
	    return null;
	}
    }

    public String getMp4Link(String serie, String season, String episode){ //argument is serienstrem.to link to desired video
	System.out.println("String getMp4Link(String serie, String season, String episode)");
	String link = serienstreamLink(serie, season, episode);

	try{
	    String streamcloudLink = "https://serienstream.to"+getStreamcloudLink(GetPageContent(link)); //get link to streamcloud

	    String params = getFormParams(GetPageContent(streamcloudLink)); //get post parameters

	    System.out.println("sleeping........");
	    Thread.sleep(15*1000); // wait for streamcloud countdown

	    return stripMp4Link(sendPost(streamcloudLink, params, "http://streamcloud.eu"));
	} catch(Exception e) {
	    return null;
	}
    }

    public String getFormParams(String html)
	throws UnsupportedEncodingException {
	    String username = "treemmetries";
	    String password = "123ABC";

	    System.out.println("-Extracting form's data...");
	    //System.out.println(html);

	    Document doc = Jsoup.parse(html);

	    // Google form id
	    //System.out.println("bevor loginform");
	    //Element loginform = (doc.getElementsByClass("proform")).first();
	    //System.out.println("loginform.text(): "+loginform.text());
	    Elements inputElements = doc.getElementsByTag("input");//loginform.getElementsByTag("input");
	    List<String> paramList = new ArrayList<String>();

	    for (Element inputElement : inputElements) {
		String key = inputElement.attr("name");
		String value = inputElement.attr("value");

		if (key.equals("username")) value = username;
		else if (key.equals("password")) value = password;

		paramList.add(key + "=" + URLEncoder.encode(value, "UTF-8"));
	    }

	    // build parameters list
	    StringBuilder result = new StringBuilder();
	    for (String param : paramList) {
		if (result.length() == 0) {
		    result.append(param);
		} else {
		    result.append("&" + param);
		}
	    }
	    System.out.print("Result    :    "+result.toString());
	    return result.toString();
	}

    public String getStreamcloudLink(String html) throws UnsupportedEncodingException {

	System.out.println("getting links...");

	Document doc = Jsoup.parse(html);
	System.out.println("after parse");
	// Google form id

	Elements links = doc.getElementsByTag("a");
	for (Element link : links) {

	    //System.out.println("bevor if, href = "+link.text());

	    if(!link.getElementsByClass("Streamcloud").isEmpty()){
		String linkHref = link.attr("href");
		//System.out.println("in if, href = "+linkHref);
		return linkHref;
	    }
	}


	return "";
    }

    public void getStreamcloudMp4(){
	try{
	    Connection.Response loginForm = Jsoup.connect("http://streamcloud.eu/r5xmndo62sym/.html")
		.method(Connection.Method.GET)
		.execute();

	    Document document = Jsoup.connect("http://streamcloud.eu/r5xmndo62sym/.html")
		.data("cookieexists", "false")
		//.data("username", "32007702")
		.data("imhuman", "Watch video now")
		.cookies(loginForm.cookies())
		.post();
	    System.out.println(document);
	} catch(Exception exc){
	    System.out.println(exc.getMessage());
	}    
    }

    public  String serienstreamLink(String serie, String season, String episode){

	return "https://serienstream.to/serie/stream/"+serie+"/staffel-"+season+"/episode-"+episode;

    }

    public ArrayList<String> getStreamSeasons(String html) throws UnsupportedEncodingException {

	ArrayList<String>  seasons = new ArrayList<String>();

	System.out.println("getting seasons...");

	Document doc = Jsoup.parse(html);
	System.out.println("after parse");

	Element streamSection = doc.getElementById("stream");
	Elements links = streamSection.getElementsByTag("a");

	String[] buf;

	for (Element link : links) {

	    buf = link.attr("title").split(" ");
	    //System.out.println("#"+buf[0]+"#");

	    if(buf.length >= 2 && buf.length <= 2){
		//System.out.println("in first if");
		if(buf[0].equals("Staffel")){
		    //System.out.println(buf[0]+" "+buf[1]);
		    seasons.add(buf[1]);
		}

	    }
	}
	return seasons;

    }


    public ArrayList<String> getStreamEpisodes(String html) throws UnsupportedEncodingException {

	ArrayList<String>  episodes = new ArrayList<String>();

	System.out.println("geting episodes...");

	Document doc = Jsoup.parse(html);
	System.out.println("after parse");

	Element streamSection = doc.getElementById("stream");
	Elements links = streamSection.getElementsByTag("a");

	String[] buf;

	for (Element link : links) {

	    buf = link.attr("title").split(" ");
	    //System.out.println(buf[0]);

	    if(buf.length >=4 ){
		//System.out.println(buf[2]);

		if(buf[2].equals("Episode")){
		    //System.out.println(buf[2]+" "+buf[3]);
		    episodes.add(buf[3]);
		}

	    }
	}

	return episodes;
    }


    public void setCookies(List<String> cookies) {
	this.cookies = cookies;
    }

    public class logger{

	private String EPISODE;
	private String SEASON;

	public logger(String season, String episode){
	    EPISODE = episode;
	    SEASON = season;
	}

	public String getEpisode(){
	    return EPISODE;
	}

	public String getSeason(){
	    return SEASON;
	}

	public String inkrementEpisode(){
	    EPISODE = Integer.toString(Integer.parseInt(EPISODE) + 1);
	    return EPISODE;
	}

	public String dekrementEpisode(){
	    EPISODE = Integer.toString(Integer.parseInt(EPISODE) - 1);
	    return EPISODE;
	}

	public String inkrementSeason(){
	    SEASON = Integer.toString(Integer.parseInt(SEASON) + 1);
	    return SEASON;
	}

	public String dekrementSeason(){
	    SEASON = Integer.toString(Integer.parseInt(SEASON) - 1);
	    return SEASON;
	}

    }

}


