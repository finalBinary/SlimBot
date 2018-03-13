package WebPageHandler.InstaJsonManager;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

public class JsonHandler {

    /*class ScrollJson{
	Data data;
	String status;

	class Data {
	    Hashtag hashtag;
	}

	class Hashtag {
	    HashMap<String,Object> edge_hashtag_to_content_advisory;
	    EdgeHashtagToMedia edge_hashtag_to_media;
	    EdgeHashtagToTopPosts edge_hashtag_to_top_posts;
	    String name;
	}

	class EdgeHashtagToMedia{
	    Integer count;
	    ArrayList<Node> edges;
	    HashMap<String, String> page_info;
	}

	class EdgeHashtagToTopPosts{
	    ArrayList<Node> edges;
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

	    public String getLikes(){
		return Integer.toString(edge_liked_by.get("count"));
	    }

	    public String getOwner(){
		return owner.get("id");
	    }

	    public List<String> getHashtags(){
		return Arrays.asList(edge_media_to_caption.getCaption());
	    }

	    class Caption{
		ArrayList<CaptionNode> edges;

		public String getCaption(){
		    	if(edges.size()>=1){
			    return edges.get(0).get("node");
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

    }*/

}
