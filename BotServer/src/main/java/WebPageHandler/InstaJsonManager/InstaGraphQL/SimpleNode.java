package WebPageHandler.InstaJsonManager.InstaGraphQL;

import java.util.ArrayList;
import java.util.List;

public interface SimpleNode {

    public String getLikes();
    public List<String> getHashtags();
    public boolean containsHashtag(String tag);
    public boolean containsHashtag(List<String> tags);
    public String getId();
    public String getOwner();
    public String getShortcode();

}
