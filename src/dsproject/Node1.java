package dsproject;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Node1 {
	private long time_1;
	

	 private Lock lock =new ReentrantLock(true);
	private Node1 (long t){
		time_1=t;
	}
	private Node1 (){
		
	}
	 private static final  Node1 instance =new Node1();
	 public static Node1 getInstance(){
	    	
	        return instance;}
	public void setTime(long t){
		if(lock.tryLock()){
		time_1=t;
		lock.unlock();
		}
	}
	
	public long getTime(){
		return time_1;
	}

	public void setIncrementTime(long t){
		if(lock.tryLock()){
		t=time_1+1;
		time_1=t;
		lock.unlock();
		}
	}
}
