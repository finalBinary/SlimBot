package WebPageHandler;

import WebPageHandler.InstaJsonManager.*;

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
import java.util.HashSet;
import java.net.HttpURLConnection;
import java.util.Iterator;

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

public class InstaHandler extends PageHandler{

	private String USER_ID;

	private final String queryId_loadMore = "17875800862117404";
	private final String queryId_following = "17874545323001329";
	private final String queryId_follower = "17851374694183129";

	private final String queryUrl = "https://www.instagram.com/graphql/query/?query_id=";

	private final String loginAjaxUrl = "https://www.instagram.com/accounts/login/ajax/";
	private final String loginRefUrl = "https://www.instagram.com/accounts/login/";
	private final String logoutUrl = "https://www.instagram.com/accounts/logout/";
	private final String logoutRef = "https://www.instagram.com/{USERNAME}/";
	private final String tagUrl = "https://www.instagram.com/explore/tags/{TAG}/";
	private final String scrollUrl = queryUrl + queryId_loadMore + "&variables={\"tag_name\":\"{TAG}\",\"first\":{FIRST},\"after\":\"{CURSOR}\"}";
	private final String followUrl = "https://www.instagram.com/web/friendships/{USERID}/follow/";
	private final String followRef = "https://www.instagram.com/p/{USER_ID}/";
	private final String unfollowUrl = "https://www.instagram.com/web/friendships/{USERID}/unfollow/";
	private final String unfollowRef = "https://www.instagram.com/p/{USER_ID}/";
	private final String likeUrl = "https://www.instagram.com/web/likes/{PICID}/like/";
	private final String likeRef = "https://www.instagram.com/p/{USER_ID}/";
	private final String unlikeUrl = "https://www.instagram.com/web/likes/{PICID}/unlike/";
	private final String followingUrl = queryUrl + queryId_following + "&variables={\"id\":\"{USERID}\",\"first\":{FIRST}}";
	private final String followerUrl = queryUrl +queryId_follower + "&variables={\"id\":\"{USERID}\",\"first\":{FIRST}}";
	private final String profileUrl = "https://www.instagram.com/{USERNAME}/?__a=1";

	TagJson picJsn;

	public void initialize(String user){
		String userUrl = profileUrl.replace("{USERNAME}", user);
		try{
			UserJson userJsn = getJsonFromString(GetPageContent(userUrl), UserJson.class);
			USER_ID = userJsn.getId();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public void printId(){//remove ---
		System.out.println("USER_ID: "+USER_ID);
	}

	public String login(String user, String password) {
		String response;
		try{
			final String params = "username="+URLEncoder.encode(user, "UTF-8")+"&"+"password="+URLEncoder.encode(password, "UTF-8");
			GetPageContent(loginRefUrl);
			response = sendPost(loginAjaxUrl, params, loginRefUrl);
			return response;
		} catch (Exception e){
			e.printStackTrace();
		}
		return "";
	}

	public String logout(String user){
		String token = "";
		String response;

		try{
			if (msCookieManager.getCookieStore().getCookies().size() > 0) {
				for (HttpCookie cookie : msCookieManager.getCookieStore().getCookies() ){
					if(cookie.toString().split("=")[0].equals("csrftoken")){
						token = cookie.toString().split("=")[1];
					}
				}
			}

			final String params = "csrfmiddlewaretoken="+URLEncoder.encode(token, "UTF-8");
			response = sendPost(logoutUrl, params, logoutRef.replace("{USERNAME}",user));
			return response;

		}catch(Exception e){
			e.printStackTrace();
		}
		return "";

	}

	public String getFollowedByCount(String user){
		try{
			return getJsonFromString(GetPageContent(profileUrl.replace("{USERNAME}",user)), UserJson.class).getFollowedByCount();
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

	public String getFollowingCount(String user){
		try{
			return getJsonFromString(GetPageContent(profileUrl.replace("{USERNAME}",user)), UserJson.class).getFollowCount();
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

	public FollowedByJson getFollowedBy(String first){
		try{
			return getJsonFromString(GetPageContent(followerUrl.replace("{USERID}",USER_ID).replace("{FIRST}",first)), FollowedByJson.class);
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

	public FollowJson getFollowing(String first){
		try{
			return getJsonFromString(GetPageContent(followingUrl.replace("{USERID}",USER_ID).replace("{FIRST}",first)), FollowJson.class);
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

	public String followUrl(String userId){ 
		return followUrl.replace("{USERID}", userId); 
	}

	public String unfollowUrl(String userId){ 
		return unfollowUrl.replace("{USERID}", userId);
	}

	public String likeUrl(String picId){ 
		return likeUrl.replace("{PICID}", picId); 
	}

	public String unlikeUrl(String picId){ 
		return unlikeUrl.replace("{PICID}", picId); 
	}

	public void unfollow(String userId){
		try{
                        sendPost(unfollowUrl(userId), "", unfollowRef.replace("{USER_ID}",USER_ID));
                        System.out.println("#########################\nUNFOLLOWED: "+userId+"\n#########################");
                } catch(Exception e){
                        e.printStackTrace();
                }
	}

	public void follow(String userId){
		try{
			sendPost(followUrl(userId), "", followRef.replace("{USER_ID}",USER_ID));
			System.out.println("#########################\nFOLLOWED: "+userId+"\n#########################");
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	public void like(String userId){
		try{
			sendPost(likeUrl(userId), "", likeRef.replace("{USER_ID}",USER_ID));
			System.out.println("#########################\nLIKED: "+userId+"\n#########################");
		} catch(Exception e){
			e.printStackTrace();
		}
	}


	public void unfollowChunk(String user, int unfollowNr){
		int index = 0;
		ArrayList<FollowNode> bufList = getFollowing(getFollowingCount(user)).getFollowNodes();
		for(FollowNode node : bufList){
				unfollow(node.getId());
				index++;

			if(index >= unfollowNr) break;
		}
		
	}

	public int unfollowChunkProtected(String user, int unfollowNr){
                int index = 0;
                ArrayList<FollowNode> bufList = getFollowing(getFollowingCount(user)).getFollowNodes();
		List<String> IdList = new ArrayList<String>();
		for(FollowNode follower : getFollowedBy(getFollowedByCount(user)).getFollowNodes() ){
			IdList.add(follower.getId());
		}

                for(FollowNode node : bufList){
			if( !IdList.contains( node.getId() )){
                                unfollow(node.getId());
                                index++;
			}else{System.out.println("avoided to unfollow following user");}
                        if(index >= unfollowNr) return index;
                }
		return index;
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
		String[] bufArray;
		Document doc = Jsoup.parse(html);
		Elements scriptElements = doc.getElementsByTag("script");
		
		for(Element scriptElement : scriptElements){
		
			bufArray = scriptElement.html().split("=");
			if (bufArray[0].equals("window._sharedData ")) return bufArray[1].substring(0, bufArray[1].length() - 1);
		}

		return "";
	}

	public static <T> T getJsonFromString(String jsonString, Class<T> var){
		GsonBuilder builder = new GsonBuilder();
		return builder.create().fromJson(jsonString, var);
	}

	public <T> SimpleJson getSimpleJsonFromString(String jsonString, Class<T> type){
		return (SimpleJson) getJsonFromString(jsonString, type);
	}

	public void getJsonFromString(String jsonString){
		GsonBuilder builder = new GsonBuilder();
		picJsn = builder.create().fromJson(jsonString, TagJson.class);
		ScrollJson buf = builder.create().fromJson(jsonString, ScrollJson.class);
		for(SimpleNode node : picJsn.getSimpleNodes()){
			printToConsole("id: "+node.getId());
			printToConsole("Caption:\n"+Arrays.toString(node.getHashtags().toArray())+"\n");
			printToConsole("Shortcode: "+node.getShortcode());
		}
	}

	public void getJson(String html){
		getJsonFromString(getJsonString(html));
	}


	public class TagSearchHandler{
		private final String tag;
		private String cursor;
		private TagJson.Tag tagJson;
		private ScrollJson scrollJson;
		ArrayList<SimpleNode> nodeList;
		private Iterator<SimpleNode> iter;
		private HashSet<String> hashId = new HashSet<String>();


		public TagSearchHandler(String tag){
			this.tag = tag;
			initialTagSearch();
		}

		private void initialTagSearch(){
			try{
				tagJson = (getJsonFromString(GetPageContent(tagUrl.replace("{TAG}", tag)+"?__a=1"), TagJson.OuterTag.class) ).getTag();
			} catch(Exception e){
				e.printStackTrace();
			}
			cursor = tagJson.getCursor();
			nodeList = tagJson.getSimpleNodes();
			iter = nodeList.iterator();
		}

		private void updateCursor(){
			try{
				scrollJson = getJsonFromString(GetPageContent(scrollUrl.replace("{TAG}",tag).replace("{CURSOR}", cursor).replace("{FIRST}","6")), ScrollJson.class);
			} catch(Exception e){
				e.printStackTrace();
			}
			cursor = scrollJson.getCursor();
		}

		public void scrollPics(){
			System.out.println("in scrollPics");
			try{
				scrollJson = getJsonFromString(GetPageContent(scrollUrl.replace("{TAG}",tag).replace("{CURSOR}",cursor).replace("{FIRST}","6")), ScrollJson.class);
			} catch(Exception e){
				e.printStackTrace();
			}
			cursor = scrollJson.getCursor();
			nodeList = scrollJson.getSimpleNodes();
			iter = nodeList.iterator();
			System.out.println("out scrollPics");

		}

		public SimpleNode nextNewPic(){
			printToConsole("nextNewPic");
			SimpleNode bufNode = nextPic();
			if(bufNode == null){
				System.out.println("null 1");
				return null;
			}
			while(hashId.contains(bufNode.getId())){
				printToConsole("=============> in nextNewPic while");
				bufNode = nextPic();
				if(bufNode == null){
					System.out.println("null 2");
					return null;
				}
			}
			hashId.add(bufNode.getId());
			printToConsole("afte add hashId, size = "+hashId.size());
			return bufNode;

		}

		public SimpleNode nextPic(){
			printToConsole("nextPic:");
			SimpleNode bufNode;
			if(!iter.hasNext()){
				scrollPics();
				if(cursor.equals("")){
					System.out.println("cursor empty");
					return null;
				}
			}
			while(iter.hasNext()){
				printToConsole("in while");
				bufNode = iter.next();
				printToConsole("after iter.next()");
				if(bufNode != null){
					printToConsole("return bufNode");
					return bufNode;
				}
				if(!iter.hasNext()){
					scrollPics();
					if(cursor.equals("")){
						System.out.println("if: NULL");
						return null;
					}
				}
			}
			System.out.println("OUT NULL");
			return null;
		}

	}

}
