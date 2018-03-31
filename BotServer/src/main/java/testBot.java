import WebPageHandler.InstaJsonManager.*;
import WebPageHandler.*;
import MyUtilities.*;
import TimeUtil.*;
import RandomTools.*;
import Bot.*;

import java.io.OutputStream;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;

import java.lang.Process;
import java.lang.ProcessBuilder;
import java.lang.ProcessBuilder.Redirect;

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
	private Map<String, Thread> startedThreads;
	private Map<String, Thread> addedThreads;
	private Map<String, Process> startedProcesses;

	BotServer(){

		PrintToConsole.setSilent(true);

		startedThreads = new HashMap<String, Thread>();
		addedThreads = new HashMap<String, Thread>();
		startedProcesses = new HashMap<String, Process>();
		try{
			server = HttpServer.create(new InetSocketAddress(8000), 0);
			server.createContext("/add", new addBotHandler());
			server.createContext("/runtime", new getRuntimeHandler());
			server.createContext("/stopserver", new stopServerHandler());
			server.createContext("/start2", new startBotHandler3());
			server.setExecutor(null); // creates a default executor
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
		System.out.println("\n####################\n# BotServer Started #\n###################\n");
	}

	public void stop(){
		server.stop(1);
	}

	public void addContext(Thread thread){
		try{
			server.createContext("/start/"+thread.getName(), new startBotHandler(thread));
			addedThreads.put(thread.getName(), thread);
			System.out.println("\n==> Bot: "+thread.getName()+" added to context\n");
		} catch (Exception e){
			e.printStackTrace();
		}

	}

	private void addContext(List<Thread> threads){
		for (Thread thread : threads){
			try{
				server.createContext("/start/"+thread.getName(), new startBotHandler(thread));
				addedThreads.put(thread.getName(), thread);
				System.out.println("\n==> Bot: "+thread.getName()+" added to context\n");
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
			InstaBot bot = new InstaBot(attributes.get("username"), attributes.get("password"), Arrays.asList(attributes.get("tags").split("\\s*,\\s*")), attributes.get("port"));
			addContext(bot);

			String resp = "Ok";
			t.sendResponseHeaders(200, resp.length());
                        OutputStream os = t.getResponseBody();
                        os.write(resp.getBytes());
                        os.close();

		}
	}

	class startBotHandler3 implements HttpHandler {
		private Thread thread;
		private Thread bufThread;

		//startBotHandler(Thread thread){
		//	this.thread = thread;
		//}

		public void handle(HttpExchange t) throws IOException {
			System.out.println("in startBotHandler");
			//PrintToConsole.print("in startBotHandler");
			String resp = "\nBot is alrady running";

			Map<String, String> attributes = queryToMap(t.getRequestURI().getQuery());
			thread = new InstaBot(attributes.get("username"), attributes.get("password"), Arrays.asList(attributes.get("tags").split("\\s*,\\s*")), attributes.get("port"));

			/*if(threadIsRunning(thread.getName())){
				getThreadByName(thread.getName());
			}*/

			if(!threadIsRunning(thread.getName())){
				bufThread = new InstaBot((InstaBot) thread);
				bufThread.start();

                                startedThreads.put(bufThread.getName(), bufThread);

				resp = "\nStarted Bot";
			}
			t.sendResponseHeaders(200, resp.length());
			OutputStream os = t.getResponseBody();
			os.write(resp.getBytes());
			os.close();
			System.out.println("###########\nBot "+thread.getName()+" start request!\n##########");
		}
	}

	class startBotHandler2 implements HttpHandler {

		public void handle(HttpExchange t) throws IOException {
			Map<String, String> attributes = queryToMap(t.getRequestURI().getQuery());
			String command = "java -cp .:/home/pi/Java/Jars/gson-2.6.2.jar /home/pi/Java/SlimBot/RunBot "+attributes.get("username")+" "+attributes.get("password")+" "+attributes.get("tags")+" "+attributes.get("port");

			List<String> commandList = new ArrayList<String>();
			commandList.add("java");
			commandList.add("-cp");
			commandList.add(".:/home/pi/Java/Jars/gson-2.6.2.jar");
			commandList.add("RunBot");
			commandList.add(attributes.get("username"));
			commandList.add(attributes.get("password"));
			commandList.add(attributes.get("tags"));
			commandList.add(attributes.get("port"));

			System.out.println(command);

			if(startedProcesses.containsKey(attributes.get("username"))){
				System.out.println("Bot already started. Derstroing and restarting bot process");
				startedProcesses.get(attributes.get("username")).destroy();
				startedProcesses.remove(attributes.get("username"));
			}

			PrintToConsole.print("befor pb");
			ProcessBuilder pb = new ProcessBuilder(commandList);
			//pb.redirectOutput(Redirect.INHERIT);
			//pb.redirectError(Redirect.INHERIT);
			pb.inheritIO();
			Process process = pb.start();

			PrintToConsole.print("### after pb.start");

			startedProcesses.put(attributes.get("username"), process);	
			String resp = "Ok";
			t.sendResponseHeaders(200, resp.length());
                        OutputStream os = t.getResponseBody();
                        os.write(resp.getBytes());
                        os.close();

		}

	}

	class startBotHandler implements HttpHandler {
		private Thread thread;
		private Thread bufThread;

		startBotHandler(Thread thread){
			this.thread = thread;
		}

		public void handle(HttpExchange t) throws IOException {
			System.out.println("in startBotHandler");
			String resp = "\nBot is alrady running";
			if(!threadIsRunning(thread.getName())){
				bufThread = new InstaBot((InstaBot) thread);
				bufThread.start();

                                startedThreads.put(bufThread.getName(), bufThread);

				resp = "\nStarted Bot";
			}
			t.sendResponseHeaders(200, resp.length());
			OutputStream os = t.getResponseBody();
			os.write(resp.getBytes());
			os.close();
			System.out.println("###########\nBot "+thread.getName()+" start request!\n##########");
		}
	}

	class getRuntimeHandler implements HttpHandler {
		public void handle(HttpExchange t) throws IOException {
			Map<String, String> attributes = queryToMap(t.getRequestURI().getQuery());

			int buf = ( (InstaBot) addedThreads.get(attributes.get("name")) ).getRunTime();
			String resp = Integer.toString(buf);
			t.sendResponseHeaders(200, resp.length());
                        OutputStream os = t.getResponseBody();
                        os.write(resp.getBytes());
                        os.close();
		}
	}

	private boolean threadIsRunning(String name){
		Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
		for(Thread thrd : threadSet){
			if(name.equals(thrd.getName())) return true;
		}
		return false;
	}

	private Thread getThreadByName(String name){
		Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
		for(Thread thrd : threadSet){
			if(name.equals(thrd.getName())) return thrd;
		}
		return null;
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
