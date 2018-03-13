package WebPageHandler.InstaJsonManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

public class FollowNode {
    Boolean followed_by_viewer;
    String full_name;
    String id;
    Boolean is_verified;
    String profile_pic_url;
    Boolean requested_by_viewer;
    String username;

    public Boolean getFollowedStatus(){
	return followed_by_viewer;
    }

    public String getId(){
	return id;
    }

    public String getUsername(){
	return username;
    }

    public boolean getFollowedByViewer(){
	return followed_by_viewer;
    }
}
