package InstaJsonManager;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

public class ScrollJson implements SimpleJson {
    Data data;
    String status;

    public String getMediaCount(){
	return data.getMediaCount();
    }

    public String getName(){
	return data.getName();
    }

    public void printStatus(){
	System.out.println("Status: "+status+" Name: "+getName()+" Count: "+getMediaCount());

    }

    public ArrayList<SimpleNode> getSimpleNodes(){
	ArrayList<SimpleNode> bufList = new ArrayList<SimpleNode>();
	bufList.addAll(data.getMediaNodes());
	bufList.addAll(data.getTopPostNodes());
	return bufList;

    }

    public ArrayList<Node> getMediaNodes(){
	return data.getMediaNodes();
    }


    public ArrayList<Node> getTopPostNodes(){
	return data.getTopPostNodes();
    }



    public String getCursor(){
	return "";
    }

    class Data {
	Hashtag hashtag;

	public String getMediaCount(){
	    return hashtag.getMediaCount();
	}

	public String getName(){
	    return hashtag.getName();
	}

	public ArrayList<Node> getMediaNodes(){
	    return hashtag.getMediaNodes();
	}


	public ArrayList<Node> getTopPostNodes(){
	    return hashtag.getTopPostNodes();
	}
    }

    class Hashtag {
	HashMap<String,Object> edge_hashtag_to_content_advisory;
	EdgeHashtagToMedia edge_hashtag_to_media;
	EdgeHashtagToTopPosts edge_hashtag_to_top_posts;
	String name;

	public String getMediaCount(){
	    return edge_hashtag_to_media.getCount();
	}

	public String getName(){
	    return name;
	}

	public ArrayList<Node> getMediaNodes(){
	    return edge_hashtag_to_media.getMediaNodes();
	}


	public ArrayList<Node> getTopPostNodes(){
	    return edge_hashtag_to_top_posts.getTopPostNodes();
	}

    }

    class EdgeHashtagToMedia{
	Integer count;
	ArrayList<OuterNode> edges;
	//ArrayList<Node> edges;
	HashMap<String, String> page_info;

	public String getCount(){
	    return Integer.toString(count);
	}

	public ArrayList<Node> getMediaNodes(){
	    ArrayList<Node> bufList = new ArrayList<Node>();
	    for(OuterNode outer : edges){
		bufList.add(outer.getNode());
	    }
	    return bufList;
	}

	class OuterNode{
	    Node node;
	    public Node getNode(){
		return node;
	    }
	}
    }

    class EdgeHashtagToTopPosts{
	ArrayList<OuterNode> edges;

	public ArrayList<Node> getTopPostNodes(){
	    ArrayList<Node> bufList = new ArrayList<Node>();
	    for(OuterNode outer : edges){
		bufList.add(outer.getNode());
	    }
	    return bufList;
	}

	class OuterNode{
	    Node node;
	    public Node getNode(){
		return node;
	    }
	}
    }

    public class Node implements SimpleNode {
	boolean comments_disabled;
	HashMap<String,Integer> dimensions;
	String display_url;
	HashMap<String, Integer> edge_liked_by;
	Caption edge_media_to_caption;
	HashMap<String, String> edge_media_to_coment;
	String id;
	boolean is_video;
	HashMap<String, String> owner;
	String shortcode;
	Integer taken_at_timestamp;
	ArrayList<Object> thumbnail_resources;
	String thumbnail_src;

	public String getId(){
	    return id;
	}

	public String getShortcode(){
		return shortcode;
	}

	public String getLikes(){
	    return Integer.toString(edge_liked_by.get("count"));
	}

	public String getOwner(){
	    return owner.get("id");
	}

	public List<String> getHashtags(){
	    List<String> bufList = new ArrayList<String>();
	    for(String el : edge_media_to_caption.getCaption().split(" ")){
		if(el.contains("#")){
			bufList.add(el);
		}
	    }
	    return bufList;//Arrays.asList(edge_media_to_caption.getCaption());
	}

	class Caption{
	    ArrayList<CaptionNode> edges;

	    public String getCaption(){
		if(edges.size()>=1){
		    return edges.get(0).get("text");
		} else {
		    return "";
		}
	    }

	    class CaptionNode {
		HashMap<String, String> node;

		public String get(String key){
		    return node.get(key);
		}
	    }
	}


    }

}
