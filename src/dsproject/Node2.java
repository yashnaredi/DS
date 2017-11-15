package dsproject;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Node2 {
	private long time_2;
	

	private Lock lock =new ReentrantLock(true);
	private Node2 (long t){
		time_2=t;
	}
	private Node2 (){
		
	}
	 private static final  Node2 instance =new Node2();
	 public static Node2 getInstance(){
	    	
	        return instance;}
	public void setTime(long t){
		if(lock.tryLock()){
			time_2=t;
			lock.unlock();
			}
	}
	public long getTime(){
		return time_2;
	}


	public void setIncrementTime(long t){
		if(lock.tryLock()){
		t=time_2+1;
		time_2=t;
		lock.unlock();
		}
	}

}
