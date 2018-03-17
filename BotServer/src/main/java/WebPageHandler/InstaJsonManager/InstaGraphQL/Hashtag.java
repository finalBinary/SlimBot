package WebPageHandler.InstaJsonManager.InstaGraphQL;

public class Hashtag {
	String name;
	String is_top_media_only;
	EdgeWrapper edge_hashtag_to_media;
	EdgeWrapper edge_hashtag_to_top_posts;
	EdgeWrapper edge_hashtag_to_content_advisory;

	public EdgeWrapper getEdgeToMedia(){
		return edge_hashtag_to_media;
	}
}

	
