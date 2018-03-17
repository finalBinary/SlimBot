import Bot.*;
import java.util.Arrays;

public class RunBot{

	public static void main(String [] args){
		if(args.length == 4){
			InstaBot bot = new InstaBot(args[0], args[1],Arrays.asList(args[2].split("\\s*,\\s*")),args[3]);
			bot.start();
		}
	}

}
