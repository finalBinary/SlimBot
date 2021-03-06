import MySQLHandler.*;
import BotREST.*;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.OutputStreamWriter;
import java.io.BufferedWriter;
import java.io.IOException;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.InetAddress;
import java.net.Socket;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean; 
import java.util.function.Supplier;
import java.util.function.Consumer;
import java.util.concurrent.CompletableFuture;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.Headers;
import java.net.InetSocketAddress;

public class BotClient{

	public static void main(String[] args){
		AtomicBoolean stopFlag = new AtomicBoolean(false);

		Client client = new Client("http://192.168.178.12","8000");

		List<Botlet> bots = new ArrayList<Botlet>();

		//bots.add( new Botlet("geserelumioso", "235711", "8001", "l4l,wood,f4f,followme") );
		//bots.add( new Botlet("lisicamp", "235711Profile", "8002", "l4l,chanel,fashion,nature,teacher,teacherlife,studying,halifax,canada,oregon,universityoforegon") );
		bots.add( new Botlet("geserelumioso", "235711", "8003", "l4l,wood,f4f,followme") );

		//bots.add( new Botlet("treemmetries", "ABC123", "8003", "l4l,wood,hiking,nature,wandern,mountains,tree,trees,woods,forest") );

		//ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(bots.size());

		/*for(Botlet bot : bots){
			(new SingleBotRun2(bot, client, executor)).startBot();
		}

		for(Botlet bot : bots){
			client.addBot(bot);
		}*/


		ScheduledThreadPoolExecutor mainExecutor = new ScheduledThreadPoolExecutor(1);
		ScheduledThreadPoolExecutor saveBotStatsExecutor = new ScheduledThreadPoolExecutor(bots.size());
		ScheduledThreadPoolExecutor singleBotExecutor = new ScheduledThreadPoolExecutor(bots.size());

		( new TerminalInput(bots, client, mainExecutor, stopFlag) ).start();
		//DataServer dataServer = new DataServer();
		BotREST botRest= new BotREST();
		botRest.start();
		//dataServer.start();

		System.out.println("Starting mainExecutor");
		mainExecutor.scheduleAtFixedRate(new RunBotHerd(bots, client, singleBotExecutor, saveBotStatsExecutor), 0, 12, TimeUnit.HOURS);
	}
}

class RunBotHerd implements Runnable{
	private List<Botlet> bots;
	private Client client;
	private ScheduledThreadPoolExecutor saveBotStatsExecutor;
	private ScheduledThreadPoolExecutor singleBotExecutor;
	private int runtime = 0;

	RunBotHerd(List<Botlet> bots, Client client, ScheduledThreadPoolExecutor singleBotExecutor, ScheduledThreadPoolExecutor saveBotStatsExecutor){
		this.bots = bots;
		this.client = client;
		this.saveBotStatsExecutor = saveBotStatsExecutor;
		this.singleBotExecutor = singleBotExecutor;
	}

	@Override
	public void run(){
		for(Botlet bot : bots){
			runtime = (new SingleBotRun2(bot, client, saveBotStatsExecutor)).startBot();
			try{
				Thread.sleep(runtime * 1000 + (30*60*1000) );//wait for bot to end +30min and then start next bot
			}catch(Exception e){
				e.printStackTrace();
				System.out.println("######### BotClient stoped #######");
				break;
			}
		}
	}
}

class SingleBotRun2 {
	Botlet bot;
	Client client;
	private ScheduledThreadPoolExecutor executor;

	SingleBotRun2(Botlet bot, Client client, ScheduledThreadPoolExecutor executor){
		this.bot = bot;
		this.client = client;
		this.executor = executor;
	}



	public int startBot() {
		client.startBot2(bot);
		CompletableFuture future = new CompletableFuture();
		try{
			future.supplyAsync(new suplyRuntime(client, bot)).thenAccept(new consumeRuntime(bot, client, executor)).get();//waitig for bot
			System.out.println("#### Successfully started bot: "+bot.getName()+"and corresponding saveBotStats");
			return Integer.parseInt(client.getRuntime(bot));
		}catch(Exception e){
			e.printStackTrace();
			return 0;
		}
	}
}

class suplyRuntime implements Supplier<String>{
	Client client;
	Botlet bot;
	String runtime = null;

	suplyRuntime(Client client, Botlet bot){
		this.client = client;
		this.bot = bot;
	}

	@Override
	public String get(){
		while(runtime == null){
			try{
				runtime = client.getRuntime(bot);
				Thread.sleep(1000);
			} catch(Exception e){
				System.out.println("######## Exception in suplyRuntime.get()");
				//e.printStackTrace();
			}
		}
		return runtime;
	}
}

class consumeRuntime implements Consumer<String>{
	private Botlet bot;
	private ScheduledThreadPoolExecutor executor;
	private Client client;

	consumeRuntime(Botlet bot, Client client, ScheduledThreadPoolExecutor executor){
		this.bot = bot;
		this.executor = executor;
		this.client = client;
	}

	@Override
	public void accept(String runtime){
		System.out.println("########## Runtime is: "+runtime+" ##########");
		executor.schedule(new SaveBotStats(bot, client), Integer.parseInt(runtime), TimeUnit.SECONDS);
	}
}

/*class SingleBotRun implements Runnable{

	private Client client;
	private ScheduledThreadPoolExecutor executor;
	private ScheduledThreadPoolExecutor startExecutor;
	private final List<Botlet> bots;
	private AtomicBoolean stopFlag;
	private FritzBox fritz;

	SingleBotRun(List<Botlet> bots, Client client, AtomicBoolean stopFlag){
		this.bots = bots;
		this.client = client;
		this.stopFlag = stopFlag;
		//fritz = new FritzBox();
	}

	public void run(){
		executor = new ScheduledThreadPoolExecutor(this.bots.size());
		startExecutor = new ScheduledThreadPoolExecutor(this.bots.size());

		int bufTime = 20;
		int firstTime = 0;
		for(Botlet bot : bots){

			startExecutor.schedule( new Thread(){
				@Override
				public void run(){
					//client.addBot(bot);
					//System.out.println("after add");
					(new FritzBox()).refreshIp();
					System.out.println("after getNewIp");
					try{
						Thread.sleep(1000);
					}catch(Exception e){
						e.printStackTrace();
					}
					client.startBot(bot);
				}
			}, bufTime, TimeUnit.SECONDS);

			try{
				Thread.sleep(10000);
			} catch(Exception e){
				e.printStackTrace();
			}
			if(bufTime == 20){
				bufTime = client.getRuntimeFromHerder(bot);
				firstTime = bufTime;
			} else{
				bufTime = bufTime + firstTime;
			}
			executor.schedule(new SaveBotStats(bot, client), bufTime, TimeUnit.SECONDS);
			bufTime = bufTime + 60*10;
		}

		executor.shutdown();
		while(!executor.isTerminated()) {
			if( stopFlag.get() ){
				System.out.println("shutdownNow in SingleBotRun");
				executor.shutdownNow();
				break;
			}
		}
		System.out.println("end SingleBotRun");
	}

}*/

class SaveBotStats extends Thread {
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
		//String stats = client.getEndStats(bot);
		//sqlHandler = new MySQLHandler("localhost","InstaBotDB","BotClient","123");
		//sqlHandler.addEntry("InstaBotLog", bot.getName(), stats);
		//sqlHandler.closeConnection();	
		client.saveEndStats(bot);	
	}
}

class Botlet{
	private final String port;
	private final String name;
	private final String pass;
	private String tags;

	Botlet(String name, String pass, String port){
		this.port = port;
		this.name = name;
		this.pass = pass;
	}

	Botlet(String name, String pass, String port, String tags){
		this(name, pass, port);
		this.tags = tags;
	}

	public void setTags(String tags){
		this.tags = tags;
	}

	public String getTags(){
		return tags;
	}

	public String getName(){
		return name;
	}

	public String getPass(){
		return pass;
	}

	public String getPort(){
		return port;
	}
}


class Client{

	private final String ServerIp;
	private final String HerderPort;
	private final String HerderHost;

	private final String RestIp = "http://192.168.178.11";
	private final String RestPort = "7000";
	private final String RestHost = RestIp + ":" + RestPort;

	Client(String ip, String port){
		ServerIp = ip;
		HerderPort = port;
		HerderHost = ip+":"+port;
	}

	public void addBot(Botlet bot){
		addBot(bot.getName(), bot.getPass(), bot.getTags(), bot.getPort());
	}

	public void addBot(String user, String pass, String tags, String port){
		try{
			sendGet(HerderHost+"/add?username="+user+"&password="+pass+"&tags="+tags+"&port="+port);
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	public void startBot2(Botlet bot){
		startBot2(bot.getName(), bot.getPass(), bot.getTags(), bot.getPort());
	}

	public void startBot2(String user, String pass, String tags, String port){
		try{
			sendGet(HerderHost+"/start2?username="+user+"&password="+pass+"&tags="+tags+"&port="+port);
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	public void stopHerder(){
		try{
			sendGet(HerderHost+"/stopserver");
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	public void startBot(String name){
		try{
			sendGet(HerderHost+"/start/"+name);
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	public void startBot(Botlet bot){
		try{
			sendGet(HerderHost+"/start/"+bot.getName());
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	public void pauseBot(Botlet bot){
		try{
			sendGet(ServerIp+":"+bot.getPort()+"/pause");
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	public void stopBot(Botlet bot){
		try{
			sendGet(ServerIp+":"+bot.getPort()+"/stop");
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	public String getStats(Botlet bot){
		try{
			return sendGet(ServerIp+":"+bot.getPort()+"/stats");
		} catch(Exception e){
			e.printStackTrace();
			return "";
		}
	}

	public String getEndStats(Botlet bot){
		try{
			return sendGet(ServerIp+":"+bot.getPort()+"/endstats");
		} catch(Exception e){
			e.printStackTrace();
			return "";
		}
	}

	public void saveEndStats(Botlet bot){
		saveStats(getEndStats(bot), bot.getName());
	}

	public void saveStats(Botlet bot){
		saveStats(getStats(bot), bot.getName());
	}

	public void saveStats(String stats, String name){
		try{
			sendPost(RestHost+"/data?username="+name, stats);
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	public HashMap<String,String> getStatsAsJson(Botlet bot){
		try{
			GsonBuilder builder = new GsonBuilder();
			return builder.create().fromJson(getStats(bot), new TypeToken<HashMap<String, String>>(){}.getType());
		} catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}

	public String getRuntime(Botlet bot){
		try{
			System.out.println(bot.getName()+": in getRuntime");
			//while(InetAddress.getByName(ServerIp+":"+bot.getPort()).isReachable(4));
			//return Integer.parseInt(getStatsAsJson(bot).get("sessionTime"));
			return getStatsAsJson(bot).get("sessionTime");
			//System.out.println(bot.getName()+": out of getRuntime");
		} catch(Exception e){
			/*try{
				Thread.sleep(4000);
				return Integer.parseInt(getStatsAsJson(bot).get("sessionTime"));
			} catch(Exception ex){
				ex.printStackTrace();
				return 10;
			}*/
			System.out.println("######### Exception in Client.getRuntime("+bot.getName()+")");
			System.out.println("-----> StackTrace disabled <-----");
			//e.printStackTrace();
			return null;
		}
	}

	public Integer getRuntimeFromHerder(Botlet bot){
		try{
			System.out.println(bot.getName()+": in getRuntimeFromHerder");
			return Integer.parseInt(sendGet(HerderHost+"/runtime/?name="+bot.getName()));
		}catch(Exception e){
			e.printStackTrace();
			return 10;
		}
	}

	public String sendGet(String url) throws Exception {
		System.out.println("Sending GET to: " + url);
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// optional default is GET
		con.setRequestMethod("GET");

		//add request header
		con.setRequestProperty("User-Agent", "Mozilla/5.0");

		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'GET' request to URL : " + url);
		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(
				new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null && inputLine.length() > 0) {
			//System.out.println("in sendget while: "+inputLine);
			response.append(inputLine);
		}
		in.close();

		//print result
		System.out.println(response.toString());
		return response.toString();
	}

	public String sendPost(String url, String body) throws Exception {
		System.out.println("Sending POST to: " + url);
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// optional default is GET
		con.setRequestMethod("POST");

		//add request header
		//con.setRequestProperty("User-Agent", "Mozilla/5.0");
		con.setRequestProperty("Content-Type", "application/json");
		
		if(body != null){
			con.setRequestProperty("Content-length", body.getBytes().length+"");
			con.setDoInput(true);
			con.setDoOutput(true);
			con.setUseCaches(false);

			OutputStream os = con.getOutputStream();
			os.write(body.getBytes("UTF-8"));
			os.close();
		}


		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'GET' request to URL : " + url);
		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(
				new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null && inputLine.length() > 0) {
			//System.out.println("in sendget while: "+inputLine);
			response.append(inputLine);
		}
		in.close();

		//print result
		System.out.println(response.toString());
		return response.toString();
	}


}

/*class DataServer {
	private HttpServer server;

	DataServer(){
		try{
			server = HttpServer.create(new InetSocketAddress(7000), 0);
			server.createContext("/data", new getDataHandler());
			server.createContext("/botnames", new getBotNamesHandler());
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public void start(){
		server.start();
	}


	class getDataHandler implements HttpHandler{
		private MySQLHandler sqlHandler;

		@Override
		public void handle(HttpExchange t) throws IOException {
			System.out.println("dataServer in handler");
			String resp = "not ok";
			Map<String, String> attributes = null;
			String query = t.getRequestURI().getQuery();
			if(query != null){
				attributes = queryToMap(query);
				System.out.println("after mapping attributes");
			}
			sqlHandler = new MySQLHandler("localhost","InstaBotDB","BotClient","123");
			System.out.println("after new MySqlHandler");
			if(attributes != null){
				System.out.println("in if of datahandler");
				resp = sqlHandler.getEntry("InstaBotLog",attributes.get("user"));
			} else {
				System.out.println("in else of datahandler");
				resp = sqlHandler.getEntry("InstaBotLog");
			}
			sqlHandler.closeConnection();


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

	}

	class getBotNamesHandler implements HttpHandler{
		private MySQLHandler sqlHandler;

		@Override
		public void handle(HttpExchange t) throws IOException {
			System.out.println("dataServer in handler");
			String resp = "not ok";
			Map<String, String> attributes = null;

			sqlHandler = new MySQLHandler("localhost","InstaBotDB","BotClient","123");
			System.out.println("after new MySqlHandler");
			System.out.println("in if of datahandler");
			resp = sqlHandler.getBotNames("InstaBotLog");
			sqlHandler.closeConnection();

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
		
	
}*/

class TerminalInput extends Thread {

	private Scanner s;
	private char in;
	private AtomicBoolean stopFlag;
	ScheduledThreadPoolExecutor executor;
	List<Botlet> bots;
	Client client;

	TerminalInput(List<Botlet> bots, Client client, ScheduledThreadPoolExecutor executor, AtomicBoolean stopFlag){
		s = new Scanner(System.in);
		this.bots = bots;
		this.executor = executor;
		this.client = client;
		this.stopFlag = stopFlag;
	}

	public void run() {
		System.out.println("##########\ns: Stop \n##########");
		while(!stopFlag.get()){
			try{
				in = (s.next()).charAt(0);
				s.nextLine();
			} catch (Exception e) {
				System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			}

			switch(in){

				case 's':
					System.out.println("In Stop");
					stopFlag.set(true);
					client.stopHerder();
					executor.shutdown();
					System.out.println("###########\nSystem Stoped!\n##########");
					break;
				default:
					System.out.println("##########\ns: Stop \n##########");
			}
		}
		System.out.println("###### END OF INPUT ######");
	}
}

class FritzBox{

	public void refreshIp(){
		getIp();
		getNewIp();
		try{
			Thread.sleep(10000);
		}catch(Exception e){
			e.printStackTrace();
		}
		getIp();
	}

	public void getNewIp(){
		System.out.println("#### New IP requested ####");
		String xmldata = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"+
			"<s:Envelope s:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\" xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\">"+
			"<s:Body>"+
			"<u:ForceTermination xmlns:u=\"urn:schemas-upnp-org:service:WANIPConnection:1\" />"+
			"</s:Body>"+
			"</s:Envelope>";

		//Create socket
		String hostname = "fritz.box";
		int port = 49000;
		try( Socket sock = new Socket(InetAddress.getByName(hostname), port);
				OutputStreamWriter ow = new OutputStreamWriter(sock.getOutputStream(),"UTF-8");
				BufferedWriter  wr = new BufferedWriter(ow);
		   ){
			wr.write("POST /igdupnp/control/WANIPConn1 HTTP/1.1");
			wr.write("Host: fritz.box:49000" + "\r\n");
			wr.write("SOAPACTION: \"urn:schemas-upnp-org:service:WANIPConnection:1#ForceTermination\""+"\r\n");
			wr.write("Content-Type: text/xml; charset=\"utf-8\""+"\r\n");
			wr.write("Content-Length: " + xmldata.length() + "\r\n");
			wr.write("\r\n");

			//Send data
			wr.write(xmldata);
			wr.flush();

			//sock.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void getIp(){
			String xmldata = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"+
				"<s:Envelope s:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\" xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\">"+
				"<s:Body>"+
				"<u:GetExternalIPAddress xmlns:u=\"urn:schemas-upnp-org:service:WANIPConnection:1\" />"+
				"</s:Body>"+
				"</s:Envelope>";


			//Create socket
			String hostname = "fritz.box";
			int port = 49000;
			try( Socket sock = new Socket(InetAddress.getByName(hostname), port);
				//Send header
				BufferedWriter  wr = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream(),"UTF-8"));
				//input
				BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			){

			wr.write("POST /igdupnp/control/WANIPConn1 HTTP/1.1");
			wr.write("Host: fritz.box:49000" + "\r\n");
			wr.write("SOAPACTION: \"urn:schemas-upnp-org:service:WANIPConnection:1#GetExternalIPAddress\""+"\r\n");
			wr.write("Content-Type: text/xml; charset=\"utf-8\""+"\r\n");
			wr.write("Content-Length: " + xmldata.length() + "\r\n");
			wr.write("\r\n");

			//Send data
			wr.write(xmldata);
			wr.flush();

			String userInput;
			while ((userInput = in.readLine()) != null) {

				 if(userInput.contains("<NewExternalIPAddress>")) {
					 System.out.println("###############");
					 System.out.println(userInput.replace("<NewExternalIPAddress>","").replace("</NewExternalIPAddress>",""));
					 System.out.println("###############");
					 break;
				 }
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
