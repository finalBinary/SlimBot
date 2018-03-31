package BotREST;

import MySQLHandler.*;

import java.util.Map;
import java.util.HashMap;
import java.util.Scanner;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.Headers;

import java.net.InetSocketAddress;

import java.io.OutputStream;
import java.io.IOException;
import java.io.InputStream;


public class BotREST {
	private HttpServer server;

	public BotREST(){
		try{
			server = HttpServer.create(new InetSocketAddress(7000), 0);
			server.createContext("/data", new dataHandler());
			server.createContext("/botnames", new getBotNamesHandler());
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public void start(){
		server.start();
	}


	class dataHandler implements HttpHandler{
		private MySQLHandler sqlHandler;

		@Override
		public void handle(HttpExchange t) throws IOException {
			System.out.println("REST in dataHandler");
			String resp = "not ok";
			Map<String, String> attributes = null;
			String query = t.getRequestURI().getQuery();

			
			Headers headers = t.getResponseHeaders();
			System.out.println(t.getRequestMethod().toUpperCase());
			
			if(t.getRequestMethod().toUpperCase().equals("GET")){
				resp = getData(query);
			} else if(t.getRequestMethod().toUpperCase().equals("POST")){
				saveData(query, t.getRequestBody());
				resp = "ok";
			}

			//Headers headers = t.getResponseHeaders();
			headers.add("Access-Control-Allow-Origin", "http://localhost:5000");
			headers.add("Access-Control-Allow-Headers", "Cache-Control, Pragma, Origin, Authorization, Content-Type, X-Requested-With");
			headers.add("Access-Control-Allow-Methods", "GET, PUT, POST");
			headers.add("Access-Control-Allow-Credentials", "true");
			headers.add("Content-Type", "application/json");
			t.sendResponseHeaders(200, resp.length());

                        OutputStream os = t.getResponseBody();
                        os.write(resp.getBytes());
                        os.close();
		}

		private String getData(String query){
			String data;
			Map<String, String> attributes = null;

			System.out.println("getData");
			System.out.println(query);

				
			MySQLHandler sqlHandler = new MySQLHandler("localhost","InstaBotDB","BotClient","123");
			System.out.println("after new MySqlHandler");
				
			if(query != null && (attributes = queryToMap(query)) != null){
			        System.out.println("in if of datahandler");
			        data = sqlHandler.getEntry("InstaBotLog", attributes.get("user"));
			} else {
			        System.out.println("in else of datahandler");
			        data = sqlHandler.getEntry("InstaBotLog");
			}
        	
			sqlHandler.closeConnection();
			return data;
		}

		private void saveData(String query, String body){
			Map<String, String> attributes = null;
			
			System.out.println("saveData");
			System.out.println(query);

			if(body != null && query != null && (attributes = queryToMap(query)) != null){
				//String stats = client.getEndStats(bot);
				
				System.out.println(body);
	                	sqlHandler = new MySQLHandler("localhost","InstaBotDB","BotClient","123");
        	        	sqlHandler.addEntry("InstaBotLog", attributes.get("username"), body);
                		sqlHandler.closeConnection();
			}
		}

		private void saveData(String query, InputStream is){
			saveData(query, convertStreamToString(is));
		}
	}
        	
	class getBotNamesHandler implements HttpHandler{
		//private MySQLHandler sqlHandler;

		@Override
		public void handle(HttpExchange t) throws IOException {
			System.out.println("dataServer in handler");
			String resp = "not ok";
			Map<String, String> attributes = null;

			/*sqlHandler = new MySQLHandler("localhost","InstaBotDB","BotClient","123");
			System.out.println("after new MySqlHandler");
			System.out.println("in if of datahandler");
			resp = sqlHandler.getBotNames("InstaBotLog");
			sqlHandler.closeConnection();*/

			resp = getBotNames();

			Headers headers = t.getResponseHeaders();
			headers.add("Access-Control-Allow-Origin", "http://localhost:5000");
			headers.add("Access-Control-Allow-Headers", "Cache-Control, Pragma, Origin, Authorization, Content-Type, X-Requested-With");
			headers.add("Access-Control-Allow-Methods", "GET, PUT, POST");
			headers.add("Access-Control-Allow-Credentials", "true");
			headers.add("Content-Type", "application/json");
			t.sendResponseHeaders(200, resp.length());
                        OutputStream os = t.getResponseBody();
                        os.write(resp.getBytes());
                        os.close();
		}

		private String getBotNames(){
			String botNames;
			MySQLHandler sqlHandler = new MySQLHandler("localhost","InstaBotDB","BotClient","123");
                        System.out.println("after new MySqlHandler");
                        System.out.println("in if of datahandler");
                        botNames = sqlHandler.getBotNames("InstaBotLog");
                        sqlHandler.closeConnection();
			return botNames;
		}
	}

	public static Map<String, String> queryToMap(String query){
		System.out.println("in queryToMap");
		Map<String, String> result = new HashMap<String, String>();
		System.out.println("after new map");
		for (String param : query.split("&")) {
			System.out.println("param:"+param);
			String pair[] = param.split("=");
			if (pair.length>1) {
				result.put(pair[0], pair[1]);
			}else{
				result.put(pair[0], "");
			}
		}
		System.out.println("returning: "+result);
		return result;
	}

	static String convertStreamToString(InputStream iS){
		Scanner s = new Scanner(iS).useDelimiter("\\A");
		return s.hasNext() ? s.next() : "";
	}
}

/*class SaveBotStats extends Thread {
        Botlet bot;
        Client client;
        MySQLHandler sqlHandler;

        SaveBotStats(Botlet bot, Client client){
                System.out.println(bot.getName()+": new SaveBotStats created");
                this.bot = bot;
                this.client = client;
        }

        @Override
        public void run(){
                System.out.println("!!! "+bot.getName()+" SaveBotStats Caled !!!");
                String stats = client.getEndStats(bot);
                sqlHandler = new MySQLHandler("localhost","InstaBotDB","BotClient","123");
                sqlHandler.addEntry("InstaBotLog", bot.getName(), stats);
                sqlHandler.closeConnection();
        }
}*/

