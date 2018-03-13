package MyUtilities;

import java.util.List;
import java.util.Arrays;

public class RingBuffer<T>{

	private final List<T> bufList;
	private final int size;
	private int index = 0;

	public RingBuffer(List<T> list){
	        System.out.println("in Ringbuffer constructor");
		bufList = list;
		size = list.size();
	}

	public T next(){
		if(index >= size) index = 0;
		return bufList.get(index++);
	}

}
