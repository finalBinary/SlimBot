package WebPageHandler.InstaJsonManager.InstaGraphQL;

import java.util.ArrayList;
import java.util.List;

public class EdgeWrapper {
	int count;
	PageInfo page_info;
	List<Edge> edges;

	public List<Edge> getEdges(){
		return edges;
	}

	public PageInfo getPageInfo(){
		return page_info;
	}

	public int getCount(){
		return count;
	}

	public ArrayList<SimpleNode> getSimpleNodes(){
		ArrayList<SimpleNode> nodeList = new ArrayList<SimpleNode>();
		for(Edge edge : edges){
			nodeList.add(edge.getNode());
		}
		return nodeList;
	}

	public String getEndCursor(){
		return page_info.end_cursor;
	}

	class PageInfo {
		boolean has_next_page;
		String end_cursor;
	}
}
