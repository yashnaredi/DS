package dsproject;


public class Message {
	private int fromProcess_ID,toProcess_ID;
	private long timeSent;
	private String type;
	public Message(){
		
	}
	public Message(String type,int fromProcess_ID, long timeSent) {
		
		this.fromProcess_ID = fromProcess_ID;
		
		this.timeSent = timeSent;
		this.type = type;
	}
	@Override
	public String toString() {
		return "Message [fromProcess_ID=" + fromProcess_ID + ", timeSent=" + timeSent + ", type=" + type + "]";
	}
	public int getFromProcess_ID() {
		return fromProcess_ID;
	}
	public void setFromProcess_ID(int fromProcess_ID) {
		this.fromProcess_ID = fromProcess_ID;
	}
	public int getToProcess_ID() {
		return toProcess_ID;
	}
	public void setToProcess_ID(int toProcess_ID) {
		this.toProcess_ID = toProcess_ID;
	}
	public long getTimeSent() {
		return timeSent;
	}
	public void setTimeSent(long timeSent) {
		this.timeSent = timeSent;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}

}
