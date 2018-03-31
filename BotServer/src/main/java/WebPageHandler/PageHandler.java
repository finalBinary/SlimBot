package WebPageHandler;

//import WebPageHandler.InstaJsonManager.*;
import MyUtilities.PrintToConsole;

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
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Map;
import java.util.StringJoiner;
import java.util.HashSet;
import java.net.HttpURLConnection;
import java.util.Iterator;

/*
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.Connection;
*/

import java.io.IOException;
import java.io.FileWriter;
import java.util.concurrent.TimeUnit;
//import java.sql.*;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.HashMap;
import java.io.BufferedWriter;

/*
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
*/

public class PageHandler{

    private HttpURLConnection conn;
    static final String COOKIES_HEADER = "Set-Cookie";
    private final String USER_AGENT = "Mozilla/5.0";
    static java.net.CookieManager msCookieManager = new java.net.CookieManager();

    public void silent(boolean state){
	PrintToConsole.setSilent(state);
    }

    public String sendPost(String url, String postParams, String Ref) throws Exception {

	PrintToConsole.print("begin of post");
	URL obj = new URL(url);
	conn = (HttpURLConnection) obj.openConnection();
	PrintToConsole.print("after conn");

	int responseCode;

	PrintToConsole.print("--bevor act like browser");

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
	    PrintToConsole.print(entry.getKey() + " : "+ entry.getValue());
	}

	// Send post request
	PrintToConsole.print("befor dataoutputstream");
	DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
	if(postParams!=null){
	    wr.writeBytes(postParams);
	}
	wr.flush();
	wr.close();

	responseCode = conn.getResponseCode();

	PrintToConsole.print("\nSending 'POST' request to URL : " + url);
	PrintToConsole.print("Post parameters : " + postParams);
	PrintToConsole.print("Response Code : " + responseCode);

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
	    PrintToConsole.print("in fill cookies of post");
	    for (String cookie : cookiesHeader) {
		msCookieManager.getCookieStore().add(null,HttpCookie.parse(cookie).get(0));
		PrintToConsole.print("---   cookie: "+ cookie);
	    }
	}

	//tofile(response.toString(), "post-respone.html");
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

    public String GetPageContent(String url) throws SocketException, Exception {
	PrintToConsole.print("\nGetPageContent: "+url);

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
	    PrintToConsole.print(entry.getKey() + " : "+ entry.getValue());
	}
	PrintToConsole.print(conn.getRequestProperty("Host"));

	// checking responseCode
	int responseCode = conn.getResponseCode();
	PrintToConsole.print("In GetPageContent after respond code");
	PrintToConsole.print("\nSending 'GET' request to URL : " + url);
	PrintToConsole.print("Response Code : " + responseCode);

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
	    PrintToConsole.print("in fill cookies of get");
	    for (String cookie : cookiesHeader) {
		msCookieManager.getCookieStore().add(null,HttpCookie.parse(cookie).get(0));
		PrintToConsole.print("---   cookie: "+ cookie);
	    }               
	}

	return response.toString();

    }


}
