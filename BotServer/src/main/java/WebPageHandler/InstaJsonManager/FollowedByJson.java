package WebPageHandler.InstaJsonManager;

import WebPageHandler.InstaJsonManager.InstaGraphQL.*;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

public class FollowedByJson{
    Data data;
    String status;

    public ArrayList<FollowNode> getFollowNodes(){
	return data.getFollowNodes();
    }

    class Data{
	User user;

	public ArrayList<FollowNode> getFollowNodes(){
	    return user.getFollowNodes();
	}
    }

    class User{
	EdgeFollowedBy edge_followed_by;

	public ArrayList<FollowNode> getFollowNodes(){
	    return edge_followed_by.getFollowNodes();
	}

    }

    class EdgeFollowedBy{
	Integer count;
	    ArrayList<OuterNode> edges;
	HashMap<String, Object> page_info;

	public ArrayList<FollowNode> getFollowNodes(){
	    ArrayList<FollowNode> bufList = new ArrayList<FollowNode>();
	    for(OuterNode outer : edges){
		bufList.add(outer.getNode());
	    }
	    return bufList;
	}

    }

    class OuterNode{
	FollowNode node;

	public FollowNode getNode(){
	    return node;
	}

    }

    /*class FollowNode {
	Boolean followed_by_viewer;
	String full_name;
	String id;
	Boolean is_verified;
	String profile_pic_url;
	Boolean requested_by_viewer;
	String username;

	public Boolean getFollowedStatus(){
	    return followed_by_user;
	}

	public String getId(){
	    return id;
	}

	public String getUsername(){
	    return username;
	}
    }*/

}
