package RandomTools;

import java.util.Random;

public class RandomDecision {

    int maxFollower;
    int followed;
    private Random randomGenerator = new Random();

    public RandomDecision(int max){
	maxFollower = max;
    }

    boolean decision() {
	if(followed < maxFollower && rand(5)){
	    followed++;
	    return true;
	}
	return false;
    }

    public boolean rand(int prop){
	if(prop >= randomGenerator.nextInt(100)+1) return true;
	return false;
    }


    public boolean rand(int cut, int upperLimit){
	if(cut > randomGenerator.nextInt(upperLimit)) return true;
	return false;
    }

    public int randInt(int max) {
	return (new Random()).nextInt(max);
    }

    public void randomWait(int lower, int upper){
	if(lower >= upper){
		System.out.println("upper < lower | straight return");
		return;
	} 
	int buf = randomGenerator.nextInt(upper);
	while(buf < lower) {
		buf= randomGenerator.nextInt(upper);
	}
	try{
		Thread.sleep(buf);
	} catch (Exception e){
		e.printStackTrace();
	}
    }

}
