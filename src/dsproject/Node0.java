package dsproject;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Node0 {
	private long time_0;
	private Lock lock =new ReentrantLock(true);
	
	private Node0 (long t){
		time_0=t;
	}
	private Node0 (){
	
	}
	 private static final  Node0 instance =new Node0();
	 public static Node0 getInstance(){
	    	
	        return instance;}
		
	public void setTime(long t){
		if(lock.tryLock()){
			time_0=t;
			lock.unlock();
			}
	}
	public long getTime(){
		return time_0;
	}
	public void setIncrementTime(long t){
		if(lock.tryLock()){
		t=time_0+1;
		time_0=t;
		lock.unlock();
		}
	}

}
