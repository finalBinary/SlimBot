//package PageParser;
import InstaJsonManager.*;

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
import java.util.HashMap;
import java.io.BufferedWriter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;

public class testBot {


    public static void main(String[] args){
	JsonHandler blu = new JsonHandler();
	TagJson jsn = new TagJson();
	System.out.println("bla");
	PageHandler pgHand = new PageHandler();
	pgHand.silent(true);
	String loginUrl = "https://www.instagram.com/accounts/login/ajax/";
	String ref = "https://www.instagram.com/accounts/login/";
	String likeUrl = "https://www.instagram.com/web/likes/1174660949266339394/like/";
	String picUrl = "https://www.instagram.com/p/";
	String tagUrl = "https://www.instagram.com/explore/tags/l4l/";
	try{
	    //pgHand.tofile(pgHand.GetPageContent(logiUrl));
	    //pgHand.GetPageContent(ref)
	    //pgHand.tofile(pgHand.GetPageContent(tagUrl), "json.txt");
	    //pgHand.getStuff(pgHand.GetPageContent(tagUrl));
	    pgHand.getJson(pgHand.GetPageContent(tagUrl));
	    pgHand.testJson();
	    pgHand.getJsonFromString("{\"text\": \"foo\"}",HashMap.class);
	    //jsn = pgHand.getJsonFromString(pgHand.GetPageContent(tagUrl+"?__a=1"), TagJson.class);
	    //pgHand.getJsonString(pgHand.GetPageContent(tagUrl))
	    jsn = pgHand.getJsonFromString(pgHand.getJsonString(pgHand.GetPageContent(tagUrl)), TagJson.class);
	    System.out.println("Cuuuuuuursor: "+jsn.getCursor());
	    //String strJsn = pgHand.GetPageContent(picUrl+jsn.getSimpleNodes().get(0).getShortcode()+"/?__a=1");
	    PicJson picJson;
	    for(SimpleNode nd : jsn.getSimpleNodes()){
		//String strJsn = pgHand.GetPageContent(picUrl+jsn.getSimpleNodes().get(0).getShortcode()+"/?__a=1");
	    String strJsn = pgHand.GetPageContent(picUrl+nd.getShortcode()+"/?__a=1");
	    picJson = pgHand.getJsonFromString(strJsn, PicJson.class);
	    System.out.println("Picture Comment Count: "+picJson.getCommentCount());
	    System.out.println("Picture Like Count: "+picJson.getLikeCount());
	    }

	    //String params = "username="+URLEncoder.encode("treemmetries", "UTF-8")+"&"+"password="+URLEncoder.encode("123ABC", "UTF-8");//pgHand.getFormParams(pgHand.GetPageContent(logiUrl));
	    //pgHand.sendPost(loginUrl, params, "https://www.instagram.com/p/BBNO-jkixZC/?tagged=ghjf");
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

    private final String queryId_loadMore = "17875800862117404";
    private boolean _silent;

    private HttpURLConnection conn;
    static final String COOKIES_HEADER = "Set-Cookie";
    private final String USER_AGENT = "Mozilla/5.0";
    static java.net.CookieManager msCookieManager = new java.net.CookieManager();

    TagJson picJsn;

    private void printToConsole(String msg){
	if(!_silent) System.out.println(msg);
    }

    public void silent(boolean state){
	_silent = state;
    }

    public String getRedirectedURL(HttpURLConnection connection){
	int resp = 0;
	try{
	    resp = connection.getResponseCode();
	} catch(Exception e){
	    e.printStackTrace();
	}
	if (resp != HttpURLConnection.HTTP_OK & ( resp == HttpURLConnection.HTTP_MOVED_TEMP || resp == HttpURLConnection.HTTP_MOVED_PERM ) ) {
	    return connection.getHeaderField("Location");
	}
	return "";
    }

    public String getJsonString(String html){
	//String buf ="";
	String[] bufArray;
	//HashMap<String, String> map = new HashMap<String, String>();
	Document doc = Jsoup.parse(html);
	Elements scriptElements = doc.getElementsByTag("script");
	for(Element scriptElement : scriptElements){
	    //buf+="---newline--->"+scriptElement.html()+"\n";
	    bufArray = scriptElement.html().split("=");
	    //System.out.println(bufArray[0] +"\n"+ bufArray[1]);
	    if (bufArray[0].equals("window._sharedData ")) return bufArray[1].substring(0, bufArray[1].length() - 1);
	}
	//tofile(buf,"getStuff.txt");
	return "";
    }

    public static <T> T getJsonFromString(String jsonString, Class<T> var){
	GsonBuilder builder = new GsonBuilder();
	return builder.create().fromJson(jsonString, var);
    }

    public <T> SimpleJson getSimpleJsonFromString(String jsonString, Class<T> type){

	/*GsonBuilder builder = new GsonBuilder();
	  SimpleJson bufJson;
	  if(type == TagJson.class){
	  return builder.create().fromJson(jsonString, TagJson.class);
	  } else if (type == ScrollJson.class){
	  return builder.create().fromJson(jsonString, ScrollJson.class);
	  } else {
	  System.out.println("getSimpleJsonFromString: Null");
	  return null;
	  }*/
	return (SimpleJson) getJsonFromString(jsonString, type);
    }

    public void getJsonFromString(String jsonString){
	GsonBuilder builder = new GsonBuilder();
	picJsn = builder.create().fromJson(jsonString, TagJson.class);
	ScrollJson buf = builder.create().fromJson(jsonString, ScrollJson.class);
	for(SimpleNode node : picJsn.getSimpleNodes()){
	    System.out.println("id: "+node.getId());
	    System.out.println("Caption:\n"+Arrays.toString(node.getHashtags().toArray())+"\n");
	    System.out.println("Shortcode: "+node.getShortcode());
	}
    }

    public void getJson(String html){
	getJsonFromString(getJsonString(html));
    }

    public void testJson(){
	try{
	    String buf = GetPageContent("https://www.instagram.com/graphql/query/?query_id=17875800862117404&variables={\"tag_name\":\"wood\",\"first\":3,\"after\":\""+picJsn.getCursor()+"\"}");
	    tofile(buf, "query1.json");
	    //getJsonFromString(buf, ScrollJson.class);
	    /*GsonBuilder builder = new GsonBuilder();
	    //Object o = builder.create().fromJson(jsonString, Object.class);
	    ScrollJson bufJsn = builder.create().fromJson(buf, ScrollJson.class);
	    bufJsn.printStatus();
	    //picJsn.checkStuff();
	    System.out.println("===> ScrollJson: ");*/
	    getJsonFromString(buf, ScrollJson.class);
	    for(SimpleNode node : getSimpleJsonFromString(buf, ScrollJson.class).getSimpleNodes()){
		System.out.println("id: "+node.getId());
		System.out.println("Caption:\n"+Arrays.toString(node.getHashtags().toArray())+"\n");
		System.out.println("Shortcode: "+node.getShortcode());
	    }
	} catch (Exception e){
	    e.printStackTrace();
	}
    }



    public String sendPost(String url, String postParams, String Ref) throws Exception {

	printToConsole("begin of post");
	URL obj = new URL(url);
	conn = (HttpURLConnection) obj.openConnection();
	printToConsole("after conn");

	int responseCode;

	printToConsole("bevor act like browser");

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

	StringJoiner joiner = new StringJoiner(";");// While joining, use ',' or ';' Most of the servers are using ';'
	String token = "";
	if (msCookieManager.getCookieStore().getCookies().size() > 0) {
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

	conn.setDoOutput(true);
	conn.setDoInput(true);

	// Print set RequestProperties
	Map<String, List<String>> map = conn.getRequestProperties();
	for (Map.Entry<String, List<String>> entry : map.entrySet()) {
	    System.out.println(entry.getKey() + " : "+ entry.getValue());
	}

	// Send post request
	printToConsole("befor dataoutputstream");
	DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
	if(postParams!=null){
	    wr.writeBytes(postParams);
	}
	wr.flush();
	wr.close();

	responseCode = conn.getResponseCode();

	printToConsole("\nSending 'POST' request to URL : " + url);
	printToConsole("Post parameters : " + postParams);
	printToConsole("Response Code : " + responseCode);

	// getting page content
	BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	String inputLine;
	StringBuffer response = new StringBuffer();

	while ((inputLine = in.readLine()) != null) {
	    response.append(inputLine);
	}

	in.close();

	// fetching response cookies
	Map<String, List<String>> headerFields = conn.getHeaderFields();
	List<String> cookiesHeader = headerFields.get(COOKIES_HEADER);

	if (cookiesHeader != null) {
	    System.out.println("in fill cookies of post");
	    for (String cookie : cookiesHeader) {
		msCookieManager.getCookieStore().add(null,HttpCookie.parse(cookie).get(0));
		System.out.println("---   cookie: "+ cookie);
	    }
	}

	tofile(response.toString(), "post-respone.html");
	return response.toString();

    }

    public void tofile(String content){
	tofile(content, "bla.html");
    }

    public void tofile(String content, String fileName){
	try(FileWriter fileWriter = new FileWriter( new File("/home/giovanni/Desktop/"+fileName) );
		BufferedWriter writer = new BufferedWriter(fileWriter)
	   ) {
	    writer.write(content);
	    writer.newLine(); //this is not actually needed for html files - can make your code more readable though 
	} catch (IOException ex) {
	    ex.printStackTrace();
	}
    }

    public String GetPageContent(String url) throws Exception {
	System.out.println("\nGetPageContent: "+url);

	URL obj = new URL(url);
	conn = (HttpURLConnection) obj.openConnection();

	conn.setRequestMethod("GET");
	conn.setUseCaches(false);

	// act like a browser
	conn.setRequestProperty("User-Agent", USER_AGENT);
	conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
	conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
	//conn.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
	conn.setRequestProperty("Upgrade-Insecure-Requests","1");

	StringJoiner joiner = new StringJoiner(";");// While joining, use ',' or ';' Most of the servers are using ';'
	String token = "";
	if (msCookieManager.getCookieStore().getCookies().size() > 0) {
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

	// print requestProperties
	Map<String, List<String>> map = conn.getRequestProperties();
	for (Map.Entry<String, List<String>> entry : map.entrySet()) {
	    printToConsole(entry.getKey() + " : "+ entry.getValue());
	}
	printToConsole(conn.getRequestProperty("Host"));

	// checking responseCode
	int responseCode = conn.getResponseCode();
	printToConsole("In GetPageContent after respond code");
	printToConsole("\nSending 'GET' request to URL : " + url);
	printToConsole("Response Code : " + responseCode);

	// getting page content
	BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	String inputLine;
	StringBuffer response = new StringBuffer();

	while ((inputLine = in.readLine()) != null) {
	    response.append(inputLine);
	}
	in.close();

	// Get the response cookies
	Map<String, List<String>> headerFields = conn.getHeaderFields();
	List<String> cookiesHeader = headerFields.get(COOKIES_HEADER);
	if (cookiesHeader != null) {
	    printToConsole("in fill cookies of get");
	    for (String cookie : cookiesHeader) {
		msCookieManager.getCookieStore().add(null,HttpCookie.parse(cookie).get(0));
		printToConsole("---   cookie: "+ cookie);
	    }               
	}

	return response.toString();

    }


}


