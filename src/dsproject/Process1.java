package dsproject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Multicast1 implements Runnable
{

    Socket connection_0,connection_1;
    String type,ach;
    PrintWriter output_0,output_1;
    int process_ID;
    private Lock lock0 =new ReentrantLock(true);
    Node1 time= Node1.getInstance();
    Multicast1(Socket connection_0,Socket connection_1,String type,int process_ID) throws IOException
    {

    	this.connection_0=connection_0;
    	this.connection_1=connection_1;
    	this.type=type;
    	this.process_ID=process_ID;


    }
    Multicast1(Socket connection_0,Socket connection_1,String type,String ach) throws IOException
    {

    	this.connection_0=connection_0;
    	this.connection_1=connection_1;
    	this.type=type;
    	this.ach=ach;


    }

	public void run()
    {

    		if(type.equalsIgnoreCase("ack")){
    			try
    	        {
    	    		output_0 =new PrintWriter(connection_0.getOutputStream(), true);
    	    		output_1 =new PrintWriter(connection_1.getOutputStream(), true);
    	    		if(lock0.tryLock()){
    	    		
    	    		output_0.println(type+","+ach);
    	    		output_0.flush();
    				
    	        		output_1.println(type+","+ach);
    	        		output_1.flush();
    	    			 lock0.unlock();
    	        		}
    	          }
    	          catch(Exception e)
    	          {
    	              e.printStackTrace();
    	          }
    	    	}
        	if(type.equalsIgnoreCase("update")){
        		try
                {
    		output_0 =new PrintWriter(connection_0.getOutputStream(), true);
    		output_1 =new PrintWriter(connection_1.getOutputStream(), true);
    		if(lock0.tryLock()){
    		
    		output_0.println(type+","+ach);
    		output_0.flush();
			
        		output_1.println(type+","+ach);
        		output_1.flush();
    			 lock0.unlock();
        		}
          }
          catch(Exception e)
          {
              e.printStackTrace();
          }
        	}

    }
}

public class Process1 {
static int connection2=0;
	public static void main(String[] args) throws IOException {
		Node1 time= Node1.getInstance();
		 TreeMap<Double,Integer> tm = new TreeMap<Double,Integer>();
		 HashMap<Double,Integer> ackHash =new HashMap<Double,Integer>();
		 Socket ProcessSock2=null;
		 int timeFlag=0;
		 int multicastFlag=0;
		 DecimalFormat df = new DecimalFormat("#.0");
	      ServerSocket server1Sock = new ServerSocket(5556);

	      while(connection2==0){
	    	  System.out.println("Listening 1");
	    	   ProcessSock2 = server1Sock.accept();
    		   System.out.println(ProcessSock2.getPort()+" Process1 is connected to Process 2");
    		  connection2=1;

	      }
	      Socket ProcessSock0=new Socket("localhost",5555);
		   System.out.println(ProcessSock0.getPort()+" Process1 is connected to Process 0");

	      BufferedReader input0 =new BufferedReader(new InputStreamReader(ProcessSock0.getInputStream()));

	      BufferedReader input2 =new BufferedReader(new InputStreamReader(ProcessSock2.getInputStream()));
	      PrintWriter output_0 =new PrintWriter(ProcessSock0.getOutputStream(), true);
	      PrintWriter out2 =new PrintWriter(ProcessSock2.getOutputStream(), true);
	      while(timeFlag==0){
			  if(input2.readLine().contains("Set time")){
				  try {
						Thread.sleep(300);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				  out2.println(System.currentTimeMillis());
				  String timeS = input2.readLine();
				  time.setTime(Long.parseLong(timeS));
				  System.out.println("node 1! time: "+time.getTime()+"\n");
				  timeFlag=1;
			  }
	      }
	      double timeM;
	      
	      for(int i=0;i<2;i++){
	    	  time.setIncrementTime(time.getTime());
		       timeM=Double.parseDouble(time.getTime()+".1");
				tm.put(timeM,0);
		      new Thread(new Multicast1(ProcessSock0,ProcessSock2,"UPDATE",time.getTime()+".1")).start();
	      }
	      int j=1;
	      while(j<=6){
	    	  
	    	  j++;
	    	  String update2=input2.readLine();
	    	  String update0=input0.readLine();
	    	  long t=0;
	    	  double dt=0;
	    	  

	    	  try{
	      if(update2.contains("UPDATE")){

	    	  System.out.println(update2+" node 1");
	    	  update2=update2.replace("UPDATE,", "");
	    	  dt=Double.parseDouble(update2);
	    	  t=(long)dt;
	    	  if(t==time.getTime()){
	    		  time.setTime(time.getTime()+1);

	    	  }
	    	  else{
	    		  time.setTime(Math.max(t,time.getTime())+1);
	    	  }
	    	  if(ackHash.containsKey(dt)){

    			  int k=(int) ackHash.get(dt);
    			  tm.put(dt,k+1);
    			  ackHash.remove(dt);
    		  }
	    	  else{
	    	  tm.put(dt,1);
	    	  }
	    	  new Thread(new Multicast1(ProcessSock0,ProcessSock2,"ACK",update2)).start();


	      }
	      if(update2.contains("ACK")){

	    	  System.out.println(update2+" node 1");
	    	  update2=update2.replace("ACK,", "");
	    	  dt=Double.parseDouble(update2);

	    	  if(tm.containsKey(dt)){
	    		  if(ackHash.containsKey(dt)){

	    			  int k=(int) ackHash.get(dt);
	    			  tm.put(dt,k+1);
	    			  ackHash.remove(dt);
	    		  }
	    		  else{

	    		  int v=(int) tm.get(dt);
	    		  tm.put(dt,v+1);
	    		  }

	    	  }
	    	  else{

	    		  ackHash.put(dt, 1);

	    		  

	    	  }
	      }
	    	  }catch(Exception e){

	    		  
		      }
	    	  try{
	    	      if(update0.contains("UPDATE")){

	    	    	  System.out.println(update0+" node 1");
			    	  update0=update0.replace("UPDATE,", "");
			    	  dt=Double.parseDouble(update0);
			    	  t=(long)dt;
			    	  if(t==time.getTime()){
			    		  time.setTime(time.getTime()+1);

			    	  }
			    	  else{
			    		  time.setTime(Math.max(t,time.getTime())+1);
			    	  }
			    	  if(ackHash.containsKey(dt)){
			    			
		    			  int k=(int) ackHash.get(dt);
		    			  tm.put(dt,k+1);
		    			  ackHash.remove(dt);
		    		  }
			    	  else{
			    	  tm.put(dt,1);
			    	  }

	    	    	  new Thread(new Multicast1(ProcessSock0,ProcessSock2,"ACK",update0)).start();



	    	      }
	    	      if(update0.contains("ACK")){

			    	  System.out.println(update0+" node 1");
			    	  update0=update0.replace("ACK,", "");
			    	  dt=Double.parseDouble(update0);
			    	  //System.out.println(ackHash.containsKey(dt)+" yoooib "+dt);
			    	  if(tm.containsKey(dt)){
			    		  if(ackHash.containsKey(dt)){

			    			  int k=(int) ackHash.get(dt);

			    			  tm.put(dt,k+1);
			    			  ackHash.remove(dt);
			    		  }
			    		  else{

			    		  int v=(int) tm.get(dt);
			    		  tm.put(dt,v+1);
			    		  }

			    	  }
			    	  else{

			    		  ackHash.put(dt, 1);

			    		  System.out.println("ackHasg "+ackHash.entrySet().toString());
			    	  }

			      }
	    	    	  }catch(Exception e){

	    	    		 
	    		      }
	    	  update0="";
	    	  update2="";

	    	  }

          System.out.println("Finished");
          System.out.println("༼ つ ◕_◕ ༽つ༼ つ ◕_◕ ༽つ༼ つ ◕_◕ ༽つ");
          
	      for (Double treeKey : tm.keySet()) {
  	        System.out.print(df.format(treeKey)+" Acknowledgement:");
  	        System.out.println(tm.get(treeKey));
  	    }


	}

}
