package WebPageHandler.InstaJsonManager;

import WebPageHandler.InstaJsonManager.InstaGraphQL.*;

public class UserJson{
    String logging_page_id;
    Boolean show_suggested_profiles;
    GraphQL graphql;

    public String getId(){
	return graphql.getUser().getId();
    }

    public String getFollowedByCount(){
		return Integer.toString(graphql.getUser().getFollowedByCount());
    }

    public String getFollowCount(){
	return Integer.toString(graphql.getUser().getFollowCount());
    }
}
