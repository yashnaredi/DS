package dsproject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
class Multicast2 implements Runnable
{

    Socket connection_0,connection_1;
    String type,ach;
    PrintWriter output_0,output_1;
    int process_ID;
    private Lock lock =new ReentrantLock(true);
    Node2 time= Node2.getInstance();
    Multicast2(Socket connection_0,Socket connection_1,String type,String ach) throws IOException
    {

    	this.connection_0=connection_0;
    	this.connection_1=connection_1;
    	this.type=type;
    	this.ach=ach;


    }
    Multicast2(Socket connection_0,Socket connection_1,String type,int process_ID) throws IOException
    {

    	this.connection_0=connection_0;
    	this.connection_1=connection_1;
    	this.type=type;
    	this.process_ID=process_ID;


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
				 lock.unlock();
	    		}
	    		if(lock.tryLock()){
	        		
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
public class Process2 {

	public static void main(String[] args) {
		 TreeMap<Double,Integer> tree_map = new TreeMap<Double,Integer>();
		 HashMap<Double,Integer> ack_hash_map =new HashMap<Double,Integer>();

		 DecimalFormat df = new DecimalFormat("#.0");
		Node2 time= Node2.getInstance();
		double time1=0;
		long ave=0;
		 int multicastFlag=0;
		try{
			 Socket processSock0=new Socket("localhost",5555);
			 Socket processSock1=new Socket("localhost",5556);
		      BufferedReader input0 =new BufferedReader(new InputStreamReader(processSock0.getInputStream()));
		      BufferedReader input1 =new BufferedReader(new InputStreamReader(processSock1.getInputStream()));
		      PrintWriter output_0 =new PrintWriter(processSock0.getOutputStream(), true);
		      PrintWriter output_1 =new PrintWriter(processSock1.getOutputStream(), true);
			 System.out.println("Process2 is connected ");
			 output_0.println("Set time");
			 output_1.println("Set time");
			 String answer0 = input0.readLine();
			 String answer1 = input1.readLine();
			 time1=time1+  Double.parseDouble(answer0);
			 time1=time1+  Double.parseDouble(answer1);
			 time1=time1+System.currentTimeMillis();
			 ave=(long)(time1/3);
			 output_0.println(ave);
			 output_1.println(ave);
			 time.setTime(ave);
			 System.out.println("node 2 time is "+time.getTime());
			// multicast
			 try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			 double timeM;
			
			 int j=1;
			    while(j<=6){
			    	j++;
			    	  String update0=input0.readLine();
			    	  String update1=input1.readLine();
			    	  //System.out.println(update0);

			    	  long t=0;
			    	  double dt=0;

			    	  try{
			      if(update0.contains("UPDATE")){

			    	  System.out.println(update0+" node 2");
			    	  update0=update0.replace("UPDATE,", "");
			    	  dt=Double.parseDouble(update0);
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
			    	  new Thread(new Multicast2(processSock0,processSock1,"ACK",update0)).start();
			    	  time.setIncrementTime(time.getTime());
				       timeM=Double.parseDouble(time.getTime()+".2");
						tree_map.put(timeM,0);
				      new Thread(new Multicast2(processSock0,processSock1,"UPDATE",time.getTime()+".2")).start();

			      }
			      if(update0.contains("ACK")){
			    	  System.out.println(update0+" node 2");
			    	  update0=update0.replace("ACK,", "");
			    	  dt=Double.parseDouble(update0);
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

					    	  System.out.println(update1+" node 2");
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
					    	  new Thread(new Multicast2(processSock0,processSock1,"ACK",update1)).start();


					      }
					      if(update1.contains("ACK")){
					    	  System.out.println(update1+" node 2");
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
			    	  update0="";
			    	  update1="";

			    	  }

              System.out.println("Finished");
              System.out.println("༼ つ ◕_◕ ༽つ༼ つ ◕_◕ ༽つ༼ つ ◕_◕ ༽つ");
			    for (Double treeKey : tree_map.keySet()) {
	    	        System.out.print(df.format(treeKey)+" Acknowledgement:");
	    	        System.out.println(tree_map.get(treeKey));
	    	    }



		}catch(Exception e){

		}

	}

}
