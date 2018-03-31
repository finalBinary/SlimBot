package Bot;

import WebPageHandler.*;
import WebPageHandler.InstaJsonManager.*;
import WebPageHandler.InstaJsonManager.InstaGraphQL.*;
import MyUtilities.*;
import RandomTools.*;
import TimeUtil.*;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

import java.io.IOException;
import java.io.OutputStream;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.net.InetSocketAddress;
import java.net.SocketException;

import com.google.gson.Gson;

public class InstaBot extends Thread{

	private AtomicBoolean pauseFlag = new AtomicBoolean(false);
	private AtomicBoolean stopFlag = new AtomicBoolean(false);
	private AtomicBoolean statFlag = new AtomicBoolean(false);
	private AtomicBoolean runningFlag = new AtomicBoolean(false);

	private String port;
	private boolean unfollow = true;
	private int lowerUnfollowNr = 0;
	private int upperUnfollowNr = 30;
	private int runTime = (int) (60 * 60 * 2);
	private boolean infRun = false;
	private final String username;
	private final String password;
	private int maxFollow = 200;
	private int maxLike = 500;

	private SimpleNode currentNode;
	private List<String> tagList;
	private int followed = 0;
	private int liked = 0;

	InstaHandler instaHandler;
	RandomRingBuffer<String> randomTagBuffer;
	RandomDecision randomDecision;
	TimeManager timeManager;
	BotStats stats;


	public InstaBot(String username, String password, List<String> tags, String port){

		this.username = username;
		this.password = password;
		this.tagList = tags;
		this.port = port;
		this.setName(username);

		randomTagBuffer = new RandomRingBuffer<String>(tags);
		randomDecision = new RandomDecision(100);
		timeManager = new TimeManager();
		stats = new BotStats();

		runTime = (int) (runTime*(0.9 + (randomDecision.randInt(100)/1000) ));
		System.out.println("Bot runtime: "+runTime);
	}

	public InstaBot(InstaBot bot){
		this(bot.getUsername(), bot.getPassword(), bot.getTagList(), bot.getPort());
	}

	private void  init(){
		try{
			instaHandler = new InstaHandler();
			instaHandler.silent(false);
			instaHandler.initialize(username);
		} catch (Exception e) {
			stopFlag.set(true);
			System.out.println("\nBot initialization failed!\n");
			e.printStackTrace();
		}
	}

	@Override
		public void run(){
			int nrOfNodesContainingTags = 0;
			int nrOfNodes = 0;

			//immediately stop if exeption in init occured
			if(stopFlag.get()) return;

			init();
			runningFlag.set(true);

			instaHandler.login(username, password);
			randomDecision.randomWait(1000, 2000);

			stats.setSessionTime(runTime);

			InputServer inputServer = new InputServer(this);
			inputServer.start();

			if(unfollow){
				//unfollow certain number of followers
				int unfollowed = instaHandler.unfollowChunkProtected(username, upperUnfollowNr);
				stats.setUnfollowedInSession(unfollowed);
			}

			randomDecision.randomWait(1000, 2000);

			while(infRun || (timeManager.checkTime(runTime) && !(followed >= maxFollow && liked >= maxLike) && !stopFlag.get())){

				while(pauseFlag.get()){PrintToConsole.print("pause while");};

				try{

					InstaHandler.TagSearchHandler tagHandler = instaHandler.new TagSearchHandler(randomTagBuffer.next());
					PrintToConsole.print("1st while");
				
					while( (currentNode = tagHandler.nextNewPic() ) != null && timeManager.checkTime(runTime) && followed <= maxFollow && liked <= maxLike && !stopFlag.get()){

						while(pauseFlag.get()){PrintToConsole.print("pause while");};
						PrintToConsole.print("2nd while");
                                        	
						System.out.println("-----------> next new pic");
                                        	
						randomDecision.randomWait(300, 1000);
                                        	
						if(currentNode.containsHashtag(tagList) && nrOfNodes > 0){
							nrOfNodesContainingTags++;
							nrOfNodes++;
							System.out.println("Nr. of Nodes: "+nrOfNodes+"\n"+
									"Nr. containing Tag: "+nrOfNodesContainingTags+"\n"+
									"Tag percent: " + (nrOfNodesContainingTags/nrOfNodes)*100 + "%");
						} else {
							nrOfNodes++;
							System.out.println("Nr. of Nodes: "+nrOfNodes+"\n"+
									"Nr. containing Tag: "+nrOfNodesContainingTags+"\n"+
									"Tag percent: " + (nrOfNodesContainingTags/nrOfNodes)*100 + "%");
						}
						
						if(randomDecision.rand(10)){
                                        	
							if(randomDecision.rand(10) && followed <= maxFollow){
								instaHandler.follow(currentNode.getOwner());
								followed++;
								stats.inkrementFollowedInSession();
							} else if (randomDecision.rand(30) && liked <= maxLike){
								instaHandler.like(currentNode.getId());
								liked++;
								stats.inkrementLikedInSession();
							}
						}
					}

				} catch (SocketException se){
					se.printStackTrace();

					//reestablich connection and login
					System.out.println("\n###\n reestabliching conection and login since SocketException occured\n###");
					init();
					instaHandler.login(username, password);
					randomDecision.randomWait(1000, 2000);
				} catch (Exception e){
					e.printStackTrace();

					this.stopBot();
					System.out.println("\n###\nStopping Bot since exeption occured\n###");
				}
			}

			stats.setFollowedBy(Integer.parseInt(instaHandler.getFollowedByCount(username)));
			stats.setFollowing(Integer.parseInt(instaHandler.getFollowingCount(username)));

			System.out.println("#### loging out ####");
			instaHandler.logout(username);

			if(timeManager.checkTime(runTime)){//wait untill runtime has passed
				PrintToConsole.print("Entering while(checktime)");
				while(timeManager.checkTime(runTime));
				PrintToConsole.print("Leaving while(checktime)");
			}
			waitForStatsRequest(300);//wait 5min for stat request
			inputServer.stop();
			runningFlag.set(false);
			PrintToConsole.print("out ===> ");
		}

		public String getPort(){
			return port;
		}
	
		public void stopBot(){
			setStop(true);
		}

		public String getUsername(){
			return username;
		}

		public String getPassword(){
			return password;
		}

		public List<String> getTagList(){
			return tagList;
		}

		public BotStats getBotStats(){
			return stats;
		}

		public boolean getPause(){
			return pauseFlag.get();
		}

		public boolean getStop(){
			return stopFlag.get();
		}

		public boolean getStat(){
			return statFlag.get();
		}

		public boolean botIsRunning(){
			PrintToConsole.print("in botIsRunning");
			return runningFlag.get();
		}

		public boolean getUnfollow(){
			return unfollow;
		}

		public boolean getInfRun(){
			return infRun;
		}

		public int getRunTime(){
			return runTime;
		}

		public int getMaxFollow(){
			return maxFollow;
		}	

		public int getMaxLike(){
			return maxLike;
		}

		public void setMaxFollow(int max){
			maxFollow = max;
		}

		public void setMaxLike(int max){
			maxLike = max;
		}

		public void setRunTime(int time){
			runTime = time;
		}

		public void setInfRun(boolean state){
			infRun = state;
		}
		public void setUnfollow(boolean state){
			unfollow = state;
		}

		public void setPause(boolean state){
			pauseFlag.set(state);
		}

		public void setStop(boolean state){
			stopFlag.set(state);
		}

		public void setStat(boolean state){
			statFlag.set(state);
		}

		private void waitForStatsRequest(int time){
			TimeManager timeMan = new TimeManager();
			PrintToConsole.print(getUsername()+" waiting for stats request; time = "+ timeMan.checkTime(time)+"statFlag = "+ getStat());
			
			while(!getStat() && timeMan.checkTime(time) && !stopFlag.get());
			setStat(false);
		}

}

class InputServer{
	HttpServer server;
	private final String response = "\nOk";
	private InstaBot bot;

	InputServer(InstaBot bot){
		try{
			this.bot = bot;
			server = HttpServer.create(new InetSocketAddress(Integer.parseInt(bot.getPort())), 0);
			server.createContext("/stop", new stopHandler());
			server.createContext("/pause", new pauseHandler());
			server.createContext("/stats", new statResponse());
			server.createContext("/endstats", new endStatResponse());
			server.createContext("/connection", new connectionHandler());
			server.setExecutor(null); // creates a default executor
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	public void start(){
		server.start();
	}

	public void stop(){
		server.stop(1);
	}

	class connectionHandler implements HttpHandler {

		public void handle(HttpExchange t) throws IOException {
			t.sendResponseHeaders(200, response.length());
			OutputStream os = t.getResponseBody();
			os.write(response.getBytes());
			os.close();
			System.out.println("###########\n"+bot.getUsername()+" Checked Connection!\n##########");
		}
	}

	class stopHandler implements HttpHandler {

		public void handle(HttpExchange t) throws IOException {
			bot.setStop(true);
			t.sendResponseHeaders(200, response.length());
			OutputStream os = t.getResponseBody();
			os.write(response.getBytes());
			os.close();
			System.out.println("###########\n"+bot.getUsername()+" System Stoped!\n##########");
		}
	}

	class pauseHandler implements HttpHandler {

		public void handle(HttpExchange t) throws IOException {
			PrintToConsole.print("in        ###########################");
			if(bot.getPause()){ 
				bot.setPause(false);
				System.out.println("###########\n"+bot.getUsername()+" System Continued!\n##########");
			} else {
				bot.setPause(true);
				System.out.println("###########\n"+bot.getUsername()+" System Paused!\n##########");
			}
			t.sendResponseHeaders(200, response.length());
			OutputStream os = t.getResponseBody();
			os.write(response.getBytes());
			os.close();
		}
	}

	class statResponse implements HttpHandler {
	
		public void handle(HttpExchange t) throws IOException {
			Gson gson = new Gson();
			String json = gson.toJson(bot.getBotStats());
			t.sendResponseHeaders(200, json.length());
                        OutputStream os = t.getResponseBody();
                        os.write(json.getBytes());
                        os.close();
                        System.out.println("###########\n"+bot.getUsername()+" JSON sendt!\n##########");
		}

	}

	class endStatResponse implements HttpHandler {

                public void handle(HttpExchange t) throws IOException {
                        Gson gson = new Gson();
                        String json = gson.toJson(bot.getBotStats());
                        t.sendResponseHeaders(200, json.length());
                        OutputStream os = t.getResponseBody();
                        os.write(json.getBytes());
                        os.close();
                        bot.setStat(true);
                        System.out.println("###########\n"+bot.getUsername()+" End JSON sendt!\n##########");
                }

        }

}

class BotStats{
	int followedBy;
	int following;
	int followedInSession;
	int likedInSession;
	int unfollowedInSession;
	int commentedInSession;
	int sessionTime;

	BotStats(){
		followedBy = 0;
	        following = 0;
		followedInSession = 0;
        	likedInSession = 0;
        	unfollowedInSession = 0;
        	commentedInSession = 0;
        	sessionTime = 0;
	}

	public void inkrementFollowedInSession(){
		followedInSession++;
	}

	public void inkrementLikedInSession(){
		likedInSession++;
	}

	public void setUnfollowedInSession(int val){
		unfollowedInSession = val;
	}

	public void setFollowedBy(int val){
		followedBy = val;
	}

	public void setFollowing(int val){
		following = val;
	}

	public void setSessionTime(int time){
		sessionTime = time;
	}

}
