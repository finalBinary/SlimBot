package WebPageHandler.InstaJsonManager;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

public class UserJson{
    String logging_page_id;
    User user;

    public String getId(){
	return user.getId();
    }

    public String getFollowedByCount(){
		return user.getFollowedByCount();
	}

    public String getFollowCount(){
	return user.getFollowCount();
	}

    class User{
	Boolean biography;
	Boolean blocked_by_viewer;
	Boolean connected_fb_page;
	Boolean country_block;
	String external_url;
	String external_url_linkshimmed;
	HashMap<String, Integer> followed_by;
	Boolean followed_by_viewer;
	HashMap<String, Integer> follows;
	Boolean follows_viewer;
	String full_name;
	Boolean has_blocked_viewer;
	Boolean has_requested_viewer;
	String id;
	Boolean is_private;
	Boolean is_verified;
	Media media;

	public String getId(){
	    return id;
	}

	public String getFollowedByCount(){
		return Integer.toString(followed_by.get("count"));
	}

	public String getFollowCount(){
		return Integer.toString(follows.get("count"));
	}
    }

    class Media{
	Integer count;
	ArrayList<Node> nodes;
	HashMap<String, Object> page_info;
	String profile_pic_url;
	String profile_pic_url_hd;
	Boolean requested_by_viewer;
	String username;
    }

    class Node{
	String __typename;
	String caption;
	String code;
	HashMap<String, Integer> comments;
	Boolean comments_disabled;
	Integer date;
	HashMap<String, Integer> dimensions;
	String display_src;
	String gating_info;
	private String id;
	Boolean is_video;
	HashMap<String, Integer> likes;
	String media_preview;
	HashMap<String, String> owner;
	ArrayList<String> thumbnail_resources;
	String thumbnail_src;

	public String getId(){
	    return id;
	}
    }

}
