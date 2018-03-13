package WebPageHandler.InstaJsonManager;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

public class TagJson implements SimpleJson{

    HashMap<String, Integer> activity_counts;
    HashMap<String, String> config;
    String country_code;
    HashMap<String, Float> display_properties_server_guess;
    EntryData entry_data;
    boolean environment_switcher_visible_server_guess;
    HashMap<String, Boolean> gatekeepers;
    public String hostname;
    String language_code;
    String platform;
    boolean probably_has_app;
    Object qe;
    boolean show_app_install;

    public EntryData getEntryData(){
	return entry_data;
    }

    public ArrayList<SimpleNode> getSimpleNodes(){
	ArrayList<SimpleNode> bufList = new ArrayList<SimpleNode>();
	bufList.addAll(entry_data.getMediaNodes());
	bufList.addAll(entry_data.getTopPostsNodes());
	return bufList;

    }

    public ArrayList<Node> getMediaNodes(){
	return entry_data.getMediaNodes();
    }

    public ArrayList<Node> getTopPostsNodes(){
	return entry_data.getTopPostsNodes();
    }

    public void checkStuff(){
	System.out.println("config,csrf_token: "+config.get("csrf_token"));
    }

    public String getCursor(){
	return entry_data.getCursor();
    }

    class EntryData {

	ArrayList<OuterTag> TagPage;

	public ArrayList<Node> getMediaNodes(){
	    if(TagPage.size() == 1){
		return TagPage.get(0).getTag().getMediaNodes();
	    } else {
		System.out.println("TagPage strange size");
		return new ArrayList<Node>();
	    }

	}

	public ArrayList<Node> getTopPostsNodes(){
	    if(TagPage.size() == 1){
		return TagPage.get(0).getTag().getTopPostsNodes();
	    } else {
		System.out.println("TagPage strange size");
		return new ArrayList<Node>();
	    }

	}

	public String getCursor(){
	    return TagPage.get(0).getTag().getCursor();
	}
    }

    public class OuterTag {
	Tag tag;

	public Tag getTag(){
	    return tag;
	}
    }


    public class Tag {
	String content_advisory;
	Media media;
	String name;
	TopPosts top_posts;

	public ArrayList<SimpleNode> getSimpleNodes(){
	    ArrayList<SimpleNode> bufList = new ArrayList<SimpleNode>();
	    bufList.addAll(getMediaNodes());
	    bufList.addAll(getTopPostsNodes());
	    return bufList;
	}

	public ArrayList<Node> getMediaNodes(){
	    return media.getNodes();
	}

	public ArrayList<Node> getTopPostsNodes(){
	    return top_posts.getNodes();
	}

	public String getCursor(){
	    return media.getCursor();
	}

	/*Media getMedia(){
	  return media;
	  }*/

	/*TopPosts getTopPosts(){
	  return top_posts;
	  }*/

    }


    class Media {
	int count;
	ArrayList<Node> nodes;
	HashMap<String,String> page_info;

	ArrayList<Node> getNodes(){
	    return nodes;
	}

	int getCount(){
	    return count;
	}

	String getCursor(){
	    String buf;
	    buf =  page_info.get("end_cursor");
	    if(buf == null){
		return "";
	    } else {
		return buf;
	    }
	}
    }

    class TopPosts {
	ArrayList<Node> nodes;

	ArrayList<Node> getNodes(){
	    return nodes;
	}
    }



    public class Node implements SimpleNode{
	String caption;
	String code;
	HashMap<String,Integer> count;
	boolean comments_disabled;
	int date;
	HashMap<String,Integer> dimensions;
	String display_src;
	String id;
	boolean is_video;
	HashMap<String,Integer> likes;
	HashMap<String,String> owner;
	ArrayList<Object> thumbnail_resources;
	String thumbnail_src;
	int video_views;

	public String getShortcode(){
	    return code;
	}

	public String getLikes(){
	    return Integer.toString(likes.get("likes"));
	}

	public List<String> getHashtags(){
	    List<String> bufList = new ArrayList<String>();

	    if(caption == null) return null;

	    for(String el : caption.split(" ")){
		if(el.contains("#")){
		    bufList.add(el.replace("#",""));
		}
	    }
	    return bufList;
	}

	public boolean containsHashtag(String tag){
	    return getHashtags().contains(tag);
	}

	public boolean containsHashtag(List<String> tags){
	    List<String> bufList = getHashtags();
	    for(String tag : tags){
		if(bufList.contains(tag)) return true;
	    }
	    return false;
	}

	public String getOwner(){
	    return owner.get("id");
	}

	public String getId(){
	    return id;
	}

    }


}
