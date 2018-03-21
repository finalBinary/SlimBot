package JsonHandler;

import MyUtilities.PrintToConsole;
/*
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
*/

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
//import com.google.gson.internal.LinkedTreeMap;
//import com.google.gson.reflect.TypeToken;
//import java.lang.reflect.Type;

public class JsonHandler {

	private static GsonBuilder builder;

	public JsonHandler(){
		builder = new GsonBuilder();
	}

	public static <T> T getJsonFromString(String jsonString, Class<T> var){
		PrintToConsole.print("-------\njsonStrng:\n"+jsonString+"\n-------");
                PrintToConsole.print("for class: "+var);
                //GsonBuilder builder = new GsonBuilder();
                T buf = (T) builder.create().fromJson(jsonString, var);
                PrintToConsole.print(buf);
                return builder.create().fromJson(jsonString, var);
	}

	/*public void getJsonFromString(String jsonString){
                //GsonBuilder builder = new GsonBuilder();
                picJsn = builder.create().fromJson(jsonString, TagJson.class);
                ScrollJson buf = builder.create().fromJson(jsonString, ScrollJson.class);
                for(SimpleNode node : picJsn.getSimpleNodes()){
                        PrintToConsole.print("id: "+node.getId());
                        PrintToConsole.print("Caption:\n"+Arrays.toString(node.getHashtags().toArray())+"\n");
                        PrintToConsole.print("Shortcode: "+node.getShortcode());
                }
        }*/
}
