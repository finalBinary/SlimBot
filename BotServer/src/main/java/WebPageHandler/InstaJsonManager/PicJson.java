package WebPageHandler.InstaJsonManager;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

public class PicJson {
    GraphQl graphql;

    public Boolean getFollowedStatus(){
	return graphql.getFollowedStatus();
    }

    public Boolean getLikedStatus(){
	return graphql.getLikedStatus();
    }

    public String getUserId(){
	return graphql.getUserId();
    }

    public String getLikeCount(){
	return graphql.getLikeCount();
    }

    public ArrayList<LikeNode> getLikeNodes(){
	return graphql.getLikeNodes();
    }

    public String getCaption(){
	return graphql.getCaption();
    }

    public String getCommentCount(){    
	return graphql.getCommentCount();
    }                                                               
    public ArrayList<CommentNode> getComments(){
	return graphql.getComments();
    }                                                                                       
    public String getPicId(){
	return graphql.getPicId();
    }                    

    public String getShortcode(){
	return graphql.getShortcode();
    }

    class GraphQl{
	ShortcodeMedia shortcode_media;

	public Boolean getFollowedStatus(){
	    return shortcode_media.getFollowedStatus();
	}

	public Boolean getLikedStatus(){
	    return shortcode_media.getLikedStatus();
	}

	public String getUserId(){
	    return shortcode_media.getUserId();
	}

	public String getLikeCount(){
	    return shortcode_media.getLikeCount();
	}

	public ArrayList<LikeNode> getLikeNodes(){
	    return shortcode_media.getLikeNodes();
	}

	public String getCaption(){
	    return shortcode_media.getCaption();
	}

	public String getCommentCount(){
	    return shortcode_media.getCommentCount();
	}

	public ArrayList<CommentNode> getComments(){
	    return shortcode_media.getComments();
	}

	public String getPicId(){
	    return shortcode_media.getPicId();
	}

	public String getShortcode(){
	    return shortcode_media.getShortcode();
	}
    }

    class ShortcodeMedia{
	String __typename;
	String caption_is_edited;
	String comments_disabled;
	HashMap<String, Integer> dimensions;
	ArrayList<Object> display_resources;
	String display_url;
	EdgeMediaPreviewLike edge_media_preview_like;
	EdgeMediaToCaption edge_media_to_caption;
	EdgeMediaToComment edge_media_to_comment;
	Object gating_info;
	String id;
	Boolean is_ad;
	Boolean is_video;
	Object location;
	String media_preview;
	HashMap<String,String> owner;
	String shortcode;
	Integer taken_at_timestamp;
	String video_url;
	Integer video_view_count;
	Boolean viewer_has_liked;

	public Boolean getFollowedStatus(){
	    return Boolean.valueOf(owner.get("followed_by_viewer"));
	}

	public Boolean getLikedStatus(){
	    return viewer_has_liked;
	}

	public String getUserId(){
	    return owner.get("id");
	}

	public String getLikeCount(){
	    return edge_media_preview_like.getLikeCount();
	}

	public ArrayList<LikeNode> getLikeNodes(){
	    return edge_media_preview_like.getLikeNodes();
	}

	public String getCaption(){
	    return edge_media_to_caption.getCaption();
	}

	public String getCommentCount(){
	    return edge_media_to_comment.getCount();
	}

	public ArrayList<CommentNode> getComments(){
	    return edge_media_to_comment.getComments();
	}

	public String getPicId(){
	    return id;
	}

	public String getShortcode(){
	    return shortcode;
	}

    }

    class EdgeMediaPreviewLike {
	Integer count;
	ArrayList<OuterLikeNode> edges;

	public String getLikeCount(){
	    return Integer.toString(count);
	}

	public ArrayList<LikeNode> getLikeNodes(){
	    ArrayList<LikeNode> bufList = new ArrayList<LikeNode>();
	    for(OuterLikeNode outer : edges){
		bufList.add(outer.getNode());
	    }
	    return bufList;
	}

	class OuterLikeNode {
	    LikeNode node;

	    public LikeNode getNode(){
		return node;
	    }
	}

    }

    class EdgeMediaToCaption {
	ArrayList<OuterCaptionNode> edges;

	public String getCaption(){
	    if(edges.size() >=1){
		return edges.get(0).getCaption();
	    } else {
		return "";
	    }
	}


	class OuterCaptionNode{
	    CaptionNode node;

	    public String getCaption(){
		return node.getCaption();
	    }

	    public class CaptionNode{
		String text;
		public String getCaption(){
		    return text;
		}

	    }
	}

    }

    class EdgeMediaToComment {
	Integer count;
	ArrayList<OuterCommentNode> edges;
	HashMap<String, Object> page_info;

	public String getCount(){
	    return Integer.toString(count);
	}

	public ArrayList<CommentNode> getComments(){
	    ArrayList<CommentNode> bufList = new ArrayList<CommentNode>();
	    for(OuterCommentNode outerNode : edges){
		bufList.add(outerNode.getNode());
	    }
	    return bufList;
	}

	class OuterCommentNode{
	    CommentNode node;

	    public CommentNode getNode(){
		return node;
	    }
	}

    }

    /* public class CommentNode {
       Integer created_at;
       String id;
       HashMap<String, String> owner;
       String text;

       public String getId(){
       return id;
       }

       public String getText(){
       return text;
       }

       public Integer getCreatedAt(){
       return created_at;
       }

       public HashMap<String, String> getOwner(){
       return owner;
       }

       }*/

    /*class LikeNode {
      String id;
      String profile_pic_url;
      String username;

      public String getId(){
      return id;
      }

      public String getUsername(){
      return username;
      }
      }*/

}
