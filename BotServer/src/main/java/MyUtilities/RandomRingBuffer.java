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
	System.out.println("---in constructor");
	System.out.println(list);
	System.out.println("---after list");
    	valueList.addAll(list);
	System.out.println("---after add all");
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
