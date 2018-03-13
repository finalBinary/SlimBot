package WebPageHandler.InstaJsonManager;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

public interface SimpleNode {

    public String getLikes();
    public List<String> getHashtags();
    public boolean containsHashtag(String tag);
    public boolean containsHashtag(List<String> tags);
    public String getId();
    public String getOwner();
    public String getShortcode();

}
