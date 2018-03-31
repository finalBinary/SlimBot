package WebPageHandler;

import WebPageHandler.InstaJsonManager.*;
import WebPageHandler.InstaJsonManager.InstaGraphQL.*;
import MyUtilities.*;
import JsonHandler.*;

import java.net.HttpCookie;
import java.net.URLEncoder;
import java.net.HttpURLConnection;
import java.net.SocketException;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Map;
import java.util.StringJoiner;
import java.util.HashSet;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import java.util.HashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.Connection;


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

	public void initialize(String user){
		String userUrl = profileUrl.replace("{USERNAME}", user);
		try{
			UserJson userJsn = JsonHandler.getJsonFromString(GetPageContent(userUrl), UserJson.class);
			USER_ID = userJsn.getId();
			System.out.println("--instahandler init succesfull----");
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public void printId(){//remove ---
		System.out.println("USER_ID: "+USER_ID);
	}

	public String login(String user, String password) {
		//String response;
		try{
			final String params = "username="+URLEncoder.encode(user, "UTF-8")+"&"+"password="+URLEncoder.encode(password, "UTF-8");
			GetPageContent(loginRefUrl);
			return sendPost(loginAjaxUrl, params, loginRefUrl);
			//return response;
		} catch (Exception e){
			e.printStackTrace();
		}
		return "";
	}

	public String logout(String user){
		String token = "";
		//String response;

		try{
			for (HttpCookie cookie : msCookieManager.getCookieStore().getCookies() ){
				if(cookie.toString().split("=")[0].equals("csrftoken")){
					token = cookie.toString().split("=")[1];
				}
			}

			final String params = "csrfmiddlewaretoken="+URLEncoder.encode(token, "UTF-8");
			return sendPost(logoutUrl, params, logoutRef.replace("{USERNAME}",user));
			//return response;

		}catch(Exception e){
			e.printStackTrace();
		}
		return "";
	}

	public String getFollowedByCount(String user){
		try{
			return JsonHandler.getJsonFromString(GetPageContent(profileUrl.replace("{USERNAME}",user)), UserJson.class).getFollowedByCount();
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

	public String getFollowingCount(String user){
		try{
			return JsonHandler.getJsonFromString(GetPageContent(profileUrl.replace("{USERNAME}",user)), UserJson.class).getFollowCount();
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

	public FollowedByJson getFollowedBy(String first){
		try{
			return JsonHandler.getJsonFromString(GetPageContent(followerUrl.replace("{USERID}",USER_ID).replace("{FIRST}",first)), FollowedByJson.class);
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

	public FollowJson getFollowing(String first){
		try{
			return JsonHandler.getJsonFromString(GetPageContent(followingUrl.replace("{USERID}",USER_ID).replace("{FIRST}",first)), FollowJson.class);
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
				PrintToConsole.print("###"+user+":");
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
		PrintToConsole.print("getting json string from: "+html);
		String[] bufArray;
		Document doc = Jsoup.parse(html);
		Elements scriptElements = doc.getElementsByTag("script");
		
		for(Element scriptElement : scriptElements){
		
			bufArray = scriptElement.html().split("=");
			if (bufArray[0].equals("window._sharedData ")) return bufArray[1].substring(0, bufArray[1].length() - 1);
		}

		return "";
	}

	public <T> SimpleJson getSimpleJsonFromString(String jsonString, Class<T> type){
		return (SimpleJson) JsonHandler.getJsonFromString(jsonString, type);
	}

	public class TagSearchHandler{
		private final String tag;
		private String cursor;
		private TagJson tagJson;
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
				tagJson = (JsonHandler.getJsonFromString(GetPageContent(tagUrl.replace("{TAG}", tag)+"?__a=1"), TagJson.class) );
			} catch(Exception e){
				e.printStackTrace();
			}
			cursor = tagJson.getEndCursor();
			nodeList = tagJson.getSimpleNodes();
			iter = nodeList.iterator();
		}

		private void updateCursor(){
			try{
				scrollJson = JsonHandler.getJsonFromString(GetPageContent(scrollUrl.replace("{TAG}",tag).replace("{CURSOR}", cursor).replace("{FIRST}","6")), ScrollJson.class);
			} catch(Exception e){
				e.printStackTrace();
			}
			cursor = scrollJson.getCursor();
		}

		public void scrollPics() throws SocketException{
			PrintToConsole.print("in scrollPics");
			try{
				scrollJson = JsonHandler.getJsonFromString(GetPageContent(scrollUrl.replace("{TAG}",tag).replace("{CURSOR}",cursor).replace("{FIRST}","100")), ScrollJson.class);
			} catch(Exception e){
				e.printStackTrace();
			}
			cursor = scrollJson.getCursor();
			nodeList = scrollJson.getSimpleNodes();
			iter = nodeList.iterator();
			PrintToConsole.print("out scrollPics");
		}

		public SimpleNode nextNewPic() throws SocketException{
			PrintToConsole.print("nextNewPic");
			SimpleNode bufNode = nextPic();
			if(bufNode == null) return null;
			
			while(hashId.contains(bufNode.getId())){
				PrintToConsole.print("=============> in nextNewPic while");
				bufNode = nextPic();
				if(bufNode == null) return null;
			}
			hashId.add(bufNode.getId());
			PrintToConsole.print("afte add hashId, size = "+hashId.size());
			return bufNode;

		}

		public SimpleNode nextPic() throws SocketException{
			PrintToConsole.print("nextPic:");
			SimpleNode bufNode;
			if(!iter.hasNext()){
				scrollPics();
				if(cursor.equals("")){
					PrintToConsole.print("cursor empty");
					return null;
				}
			}
			while(iter.hasNext()){
				PrintToConsole.print("in while");
				bufNode = iter.next();
				PrintToConsole.print("after iter.next()");
				if(bufNode != null){
					PrintToConsole.print("return bufNode");
					return bufNode;
				}
				if(!iter.hasNext()){
					scrollPics();
					if(cursor.equals("")){
						PrintToConsole.print("if: NULL");
						return null;
					}
				}
			}
			PrintToConsole.print("OUT NULL");
			return null;
		}

	}

}
