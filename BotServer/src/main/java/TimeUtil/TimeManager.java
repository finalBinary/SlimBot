package TimeUtil;

import MyUtilities.PrintToConsole;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TimeManager {

    private final LocalDateTime start;

    public TimeManager(){
	start = LocalDateTime.now();
    }

    public boolean checkTime(int sec){ //returns false when time has past
	long diffInSeconds = java.time.Duration.between(start, LocalDateTime.now()).getSeconds();
	PrintToConsole.print("Time left: "+(sec - diffInSeconds));
	return sec > diffInSeconds;
    }

}

