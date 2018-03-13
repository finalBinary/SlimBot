import WebPageHandler.InstaJsonManager.*;
import WebPageHandler.*;
import MyUtilities.*;
import TimeUtil.*;
import RandomTools.*;
import Bot.*;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.OutputStream;
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
import java.util.HashSet;
import java.net.HttpURLConnection;
import java.util.Iterator;
import java.util.Set;

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
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.net.InetSocketAddress;

public class testBot {


	public static void main(String[] args){
		try{

			BotServer serv = new BotServer();
			serv.start();

		} catch(Exception e){
			e.printStackTrace();
		}
	}

}

class BotServer{
	HttpServer server;
	private final String response = "\nOk";
	private Map<String, Thread> startedThreads;;

	BotServer(){
		startedThreads = new HashMap<String, Thread>();
		try{
			server = HttpServer.create(new InetSocketAddress(8001), 0);
			server.createContext("/add", new addBotHandler());
			server.createContext("/stopserver", new stopServerHandler());
			server.setExecutor(null); // creates a default executor
			System.out.println("\n#####################\n# BotServer Started #\n#####################\n");
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	BotServer(List<Thread> threads){
		this();
		addContext(threads);
	}

	public void start(){
		server.start();
	}

	public void stop(){
		server.stop(1);
	}

	public void addContext(Thread thread){
		try{
			server.createContext("/start/"+thread.getName(), new startBotHandler(thread));
			System.out.println("\n==> Bot: "+thread.getName()+" added to context\n");
		} catch (Exception e){
			e.printStackTrace();
		}

	}


	private void addContext(List<Thread> threads){
		for (Thread thread : threads){
			try{
				server.createContext("/start/"+thread.getName(), new startBotHandler(thread));
			} catch (Exception e){
				e.printStackTrace();
			}
		}
	}

	class stopServerHandler implements HttpHandler{
		public void handle(HttpExchange t) throws IOException {

			for(Thread thread : startedThreads.values()){
				if(threadIsRunning(thread.getName())){
        	                        System.out.println("Stoping bot: "+thread.getName());
                	                ((InstaBot) thread).stopBot();
                	        }
			}

			String resp = "Ok";
                        t.sendResponseHeaders(200, resp.length());
                        OutputStream os = t.getResponseBody();
                        os.write(resp.getBytes());
                        os.close();
			
			server.stop(60);
		}

	}

	class addBotHandler implements HttpHandler{
		public void handle(HttpExchange t) throws IOException {

			Map<String, String> attributes = queryToMap(t.getRequestURI().getQuery());
			InstaBot bot = new InstaBot(attributes.get("username"), attributes.get("password"), Arrays.asList(attributes.get("tags").split("\\s*,\\s*")));
			addContext(bot);

			String resp = "Ok";
			t.sendResponseHeaders(200, resp.length());
                        OutputStream os = t.getResponseBody();
                        os.write(resp.getBytes());
                        os.close();

		}
	}

	class startBotHandler implements HttpHandler {
		private Thread thread;

		startBotHandler(Thread thread){
			this.thread = thread;
		}

		public void handle(HttpExchange t) throws IOException {
			System.out.println("in startBotHandler");
			String resp = "\nBot is alrady running";
			if(!threadIsRunning(thread.getName())){
				thread = new InstaBot((InstaBot) thread);
				thread.start();

                                startedThreads.put(thread.getName(), thread);

				resp = "\nStarted Bot";
			}
			t.sendResponseHeaders(200, resp.length());
			OutputStream os = t.getResponseBody();
			os.write(resp.getBytes());
			os.close();
			System.out.println("###########\nBot start request!\n##########");
		}
	}

	private boolean threadIsRunning(String name){
		Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
		for(Thread thrd : threadSet){
			if(name.equals(thrd.getName())) return true;
		}
		return false;
	}

	public static Map<String, String> queryToMap(String query){
		Map<String, String> result = new HashMap<String, String>();
		for (String param : query.split("&")) {
			String pair[] = param.split("=");
			if (pair.length>1) {
				result.put(pair[0], pair[1]);
			}else{
				result.put(pair[0], "");
			}
		}
		return result;
	}

}
