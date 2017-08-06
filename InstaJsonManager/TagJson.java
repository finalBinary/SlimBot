package InstaJsonManager;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

public class TagJson implements SimpleJson{

    String activity_counts;
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
		System.out.println("in EntryData, getMediaNodes: size=1");
		System.out.println("Count: "+TagPage.get(0).getTag().getMedia().getCount());
		return TagPage.get(0).getTag().getMedia().getNodes();

	    } else {
		System.out.println("TagPage strange size");
		return new ArrayList<Node>();
	    }

	}

	public ArrayList<Node> getTopPostsNodes(){
	    if(TagPage.size() == 1){
		return TagPage.get(0).getTag().getTopPosts().getNodes();
	    } else {
		System.out.println("TagPage strange size");
		return new ArrayList<Node>();
	    }

	}

	public String getCursor(){
	    return TagPage.get(0).getTag().getMedia().getCursor();
	}

	class OuterTag {
	    Tag tag;

	    public Tag getTag(){
		return tag;
	    }


	    class Tag {
		String content_advisory;
		Media media;
		String name;
		TopPosts top_posts;

		Media getMedia(){
		    System.out.println("in getMedia");
		    System.out.println(name);
		    System.out.println("after name");
		    return media;
		}

		TopPosts getTopPosts(){
		    return top_posts;
		}

	    }
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
		return page_info.get("end_cursor");
	    }
	}

	class TopPosts {
	    ArrayList<Node> nodes;

	    ArrayList<Node> getNodes(){
		return nodes;
	    }
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
	    for(String el : caption.split(" ")){
		if(el.contains("#")){
		    bufList.add(el);
		}
	    }
	    return bufList;
	}

	public String getOwner(){
	    return owner.get("id");
	}

	public String getId(){
	    return id;
	}

    }


}
