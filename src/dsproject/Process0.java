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


class Multicast0 implements Runnable
{

    Socket connection_0,connection_1;
    String type,ach;
    PrintWriter output_0,output_1;
    int process_ID;
    private Lock lock =new ReentrantLock(true);
    Node0 time= Node0.getInstance();
    Multicast0(Socket connection_0,Socket connection_1,String type,int process_ID) throws IOException
    {

    	this.connection_0=connection_0;
    	this.connection_1=connection_1;
    	this.type=type;
    	this.process_ID=process_ID;


    }
    Multicast0(Socket connection_0,Socket connection_1,String type,String ach) throws IOException
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
	    		if(lock.tryLock()){
	    		
	    		output_0.println(type+","+ach);
	    		output_0.flush();
				
	        		output_1.println(type+","+ach);
	        		output_1.flush();
	    			 lock.unlock();
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
    		if(lock.tryLock()){
    			
    		output_0.println(type+","+ach);
    		output_0.flush();
			
                        output_1.println(type+","+ach);
        		output_1.flush();
    			 lock.unlock();
        		}
          }
          catch(Exception e)
          {
              e.printStackTrace();
          }
    	}
    }
}


public class Process0 {
static int connection1=0,connection2=0;

	public static void main(String[] args) throws IOException {
		Node0 time= Node0.getInstance();
		 TreeMap<Double,Integer> tree_map = new TreeMap<>();
		 HashMap<Double,Integer> ack_hash_map =new HashMap<>();

		 DecimalFormat df = new DecimalFormat("#.0");
		 Socket processSock2=null,processSock1 = null;
		 int timeFlag=0;
		 int multicastFlag=0;
	      ServerSocket server0Sock = new ServerSocket(5555);
	      while(connection1==0){
	    	  System.out.println("Listening 0");
	    	   processSock1 = server0Sock.accept();
    		   System.out.println(processSock1.getPort()+" Process0 is connected to process 1");


    		  connection1=1;
	      }
	      while(connection2==0){
	    	  System.out.println("Listening 0");
	    	   processSock2 = server0Sock.accept();
    		   System.out.println(processSock2.getPort()+" Process0 is connected to process 2");


    		  connection2=1;

	      }

	      BufferedReader input1 =new BufferedReader(new InputStreamReader(processSock1.getInputStream()));
	      BufferedReader input2 =new BufferedReader(new InputStreamReader(processSock2.getInputStream()));
	      PrintWriter output_1 =new PrintWriter(processSock1.getOutputStream(), true);
	      PrintWriter out2 =new PrintWriter(processSock2.getOutputStream(), true);
	      while(timeFlag==0){
		  if(input1.readLine().contains("Set time")){
			  try {
					Thread.sleep(700);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			  output_1.println(System.currentTimeMillis());
			  String timeS = input1.readLine();
			  time.setTime(Long.parseLong(timeS));
			  System.out.println("node 0! time: "+time.getTime()+"\n");
			  timeFlag=1;
		  }
		  else if(input2.readLine().contains("Set time")){
			  try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			  out2.println(System.currentTimeMillis());
			  String timeS = input2.readLine();
			  time.setTime(Long.parseLong(timeS));
			  System.out.println("node 0! time: "+time.getTime()+"\n");
			  timeFlag=1;
		  }

	  }
	      double timeM;
	      
	      while(multicastFlag==0){
    		for(int i=0;i<2;i++){
    	  time.setIncrementTime(time.getTime());

	       timeM=Double.parseDouble(time.getTime()+".0");
	       
			tree_map.put(timeM,0);
	      new Thread(new Multicast0(processSock1,processSock2,"UPDATE",time.getTime()+".0")).start();
    		}
    		multicastFlag=1;
	      }
	      int j=1;
	      while(j<=6){
	    	  j++;
	    	  String update2=input2.readLine();
	    	  String update1=input1.readLine();

	    	  long t=0;
	    	  double dt=0;
	    	  

	    	  try{
	      if(update2.contains("UPDATE")){
	    	  System.out.println(update2+" node 0");
	    	  update2=update2.replace("UPDATE,", "");

	    	  dt=Double.parseDouble(update2);
	    	  t=(long)dt;
	    	  if(t==time.getTime()){
	    		  time.setTime(time.getTime()+1);

	    	  }
	    	  else{
	    		  time.setTime(Math.max(t,time.getTime())+1);
	    	  }
	    	  
	    	  if(ack_hash_map.containsKey(dt)){

    			  int k=(int) ack_hash_map.get(dt);
    			  tree_map.put(dt,k+1);
    			  ack_hash_map.remove(dt);
    		  }
	    	  else{
	    	  tree_map.put(dt,1);
	    	  }
	    	  new Thread(new Multicast0(processSock1,processSock2,"ACK",update2)).start();

	      }
	      if(update2.contains("ACK")){

	    	  System.out.println(update2+" node 0");
	    	  update2=update2.replace("ACK,", "");
	    	  dt=Double.parseDouble(update2);
	    	  if(tree_map.containsKey(dt)){
	    		  if(ack_hash_map.containsKey(dt)){
	    			  int k=(int) ack_hash_map.get(dt);

	    			  tree_map.put(dt,k+1);
	    			  ack_hash_map.remove(dt);
	    		  }
	    		  else{
	    		  int v=(int) tree_map.get(dt);
	    		  tree_map.put(dt,v+1);
	    		  }

	    	  }
	    	  else{
	    		  ack_hash_map.put(dt, ack_hash_map.get(dt)+1);
	    	  }

	      }
	    	  }catch(Exception e){

	    		 
		      }
	    	  try{
	       if(update1.contains("UPDATE")){
	    	  System.out.println(update1+" node 0");
	    	  update1=update1.replace("UPDATE,", "");

	    	  dt=Double.parseDouble(update1);
	    	  t=(long)dt;
	    	  if(t==time.getTime()){
	    		  time.setTime(time.getTime()+1);

	    	  }
	    	  else{
	    		  time.setTime(Math.max(t,time.getTime())+1);
	    	  }
	    	  
	    	  if(ack_hash_map.containsKey(dt)){

    			  int k=(int) ack_hash_map.get(dt);
    			  tree_map.put(dt,k+1);
    			  ack_hash_map.remove(dt);
    		  }
	    	  else{
	    	  tree_map.put(dt,1);
	    	  }
	    	  new Thread(new Multicast0(processSock1,processSock2,"ACK",update1)).start();

	      }
	       if(update1.contains("ACK")){

		    	  System.out.println(update1+" node 0");
		    	  update1=update1.replace("ACK,", "");
		    	  dt=Double.parseDouble(update1);
		    	  if(tree_map.containsKey(dt)){
		    		  if(ack_hash_map.containsKey(dt)){
		    			  int k=(int) ack_hash_map.get(dt);

		    			  tree_map.put(dt,k+1);
		    			  ack_hash_map.remove(dt);
		    		  }
		    		  else{
		    		  int v=(int) tree_map.get(dt);
		    		  tree_map.put(dt,v+1);
		    		  }

		    	  }
		    	  else{
		    		  ack_hash_map.put(dt, ack_hash_map.get(dt)+1);
		    	  }

		      }
	    	  }catch(Exception e){
	    		 
		      }
	    	  update1="";
	    	  update2="";

	      }

        System.out.println("Finished");
        System.out.println("༼ つ ◕_◕ ༽つ༼ つ ◕_◕ ༽つ༼ つ ◕_◕ ༽つ");
	      for (Double treeKey : tree_map.keySet()) {
  	        System.out.print(df.format(treeKey)+" Acknowledgement:");
  	        System.out.println(tree_map.get(treeKey));
  	    }



	}

}
