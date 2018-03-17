package WebPageHandler.InstaJsonManager.InstaGraphQL;

import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

public class Node implements SimpleNode{
	boolean comments_disabled;
	String id;
	String text;
	EdgeWrapper edge_media_to_caption;
	String shortcode;
	EdgeWrapper edge_media_to_comment;
	int taken_at_timestamp;
	Map<String, Integer> dimensions;
	String display_url;
	EdgeWrapper edge_liked_by;
	EdgeWrapper edge_media_preview_like;
	Map<String, String> owner;
	String thumbnail_src;
	List<ThumbnailResource> thumbnail_resources;
	boolean is_video;

	public String getLikes(){
		return Integer.toString(edge_liked_by.getCount());
	}

	public List<String> getHashtags(){
		return new ArrayList<String>();
	}

	public String getText(){
		return text;
	}

	public boolean containsHashtag(String tag){
		try{
			List<Edge> mediaCaptionEdges = edge_media_to_caption.getEdges();
			if(mediaCaptionEdges.size() > 0){
				String caption = mediaCaptionEdges.get(0).getNode().getText();
				return caption.contains(tag);
			}
		} catch(NullPointerException npEX) {
			npEX.printStackTrace();
		} catch(IndexOutOfBoundsException obEX){
			obEX.printStackTrace();
		}
		return false;
	}

	public boolean containsHashtag(List<String> tags){
		for(String tag : tags){
			if(containsHashtag(tag)) return true;
		}
		return false;
	}

	public String getId(){
		return id;
	}

	public String getOwner(){
		return owner.get("id");
	}

	public String getShortcode(){
		return shortcode;
	}

	class ThumbnailResource {
		String src;
		int config_width;
		int config_height;
	}
}
