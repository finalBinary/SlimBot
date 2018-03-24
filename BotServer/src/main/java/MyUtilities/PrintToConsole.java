package MyUtilities;

public final class PrintToConsole{
	private static boolean silent = true;

	private PrintToConsole(){
		silent = true;
	}

	public static void setSilent(boolean isSilent){
		silent = true;//isSilent;
	}

	public static void print(Object text){
		if(!silent) System.out.println(text);
	}
}
