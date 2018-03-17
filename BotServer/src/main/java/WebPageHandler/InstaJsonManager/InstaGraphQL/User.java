package WebPageHandler.InstaJsonManager.InstaGraphQL;

public class User {
	String biography;
	boolean blocked_by_viewer;
	boolean country_block;
	String external_url;
	String external_url_linkshimmed;
	EdgeWrapper edge_followed_by;
	boolean followed_by_viewer;
	EdgeWrapper edge_follow;
	boolean follows_viewer;
	String full_name;
	boolean has_blocked_viewer;
	boolean has_requested_viewer;
	String id;
	boolean is_private;
	boolean is_verified;
	int mutual_followers;
	String profile_pic_url;
	String profile_pic_url_hd;
	boolean requested_by_viewer;
	String username;
	String connected_fb_page;
	EdgeWrapper edge_owner_to_timeline_media;
	EdgeWrapper edge_saved_media;
	EdgeWrapper edge_media_collections;

	public String getId(){
		return id;
	}

	public int getFollowedByCount(){
		return edge_followed_by.getCount();
	}

	public int getFollowCount(){
		return edge_follow.getCount();
	}
}

	
