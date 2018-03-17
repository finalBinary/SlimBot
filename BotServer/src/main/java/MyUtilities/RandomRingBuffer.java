package MyUtilities;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class RandomRingBuffer<T>{

    private List<T> valueList = new ArrayList<T>();
    private List<T> bufferList = new ArrayList<T>();
    private int index = 0;
    private Random randomGenerator = new Random();


    public RandomRingBuffer(T[] array){
	valueList = new ArrayList<T>(Arrays.asList(array));
	bufferList.addAll(valueList);
    }

    public RandomRingBuffer(List<T> list){
	PrintToConsole.print("---in constructor");
	PrintToConsole.print(list);
	PrintToConsole.print("---after list");
    	valueList.addAll(list);
	PrintToConsole.print("---after add all");
	bufferList.addAll(valueList);
    }

    public T next(){
	T buf;
	if(bufferList.size() == 0) bufferList.addAll(valueList);
	index = randomGenerator.nextInt(bufferList.size());
	buf = bufferList.get(index);
	bufferList.remove(index);
	return buf;
    }

}
