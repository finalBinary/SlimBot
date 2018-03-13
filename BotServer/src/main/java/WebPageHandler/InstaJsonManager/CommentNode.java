package WebPageHandler.InstaJsonManager;

import java.util.HashMap;

public class CommentNode {
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

}
