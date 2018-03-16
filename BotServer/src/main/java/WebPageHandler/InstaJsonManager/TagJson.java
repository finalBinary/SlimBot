package WebPageHandler.InstaJsonManager;

import WebPageHandler.InstaJsonManager.InstaGraphQL.*;

import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

public class TagJson implements SimpleJson{
	GraphQL graphql;

	public String getCursor(){
		return getEndCursor();
	}

	public GraphQL getGraphQL(){
		return graphql;
	}

	public String getEndCursor(){
		return graphql.getHashtag().getEdgeToMedia().getEndCursor();
	}

	public ArrayList<SimpleNode> getSimpleNodes(){
		return graphql.getHashtag().getEdgeToMedia().getSimpleNodes();
	}
}
