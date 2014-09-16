package DataStructure;

public class DataItem {
	
	private int threadId;
	private int sequenceNum;
	private boolean stored = false;
	
	/**
	 * Just a constructor for the DataItem class
	 * @param threadId The producer thread ID
	 * @param sequenceNum Number of the items which the producer thread has created plus 1
	 */
	public DataItem(int threadId, int sequenceNum){
		this.threadId = threadId;
		this.sequenceNum = sequenceNum;
	}
	
	/**
	 * Formats the DataItem into a small string
	 */
	@Override
	public String toString(){
		return String.format("%d_%d", threadId, sequenceNum);
	}

	/**
	 * Checks if a particular item has been stored before or not.
	 * This is used when the buffer is full and the producer can't store the item at its first try.
	 * @return True if the item has been stored before.
	 */
	public boolean isStored(){
		return stored;
	}
	
	/**
	 * When the item is stored in the buffer this method must be called to let the producer know that the item has been stored and
	 * it does not need to try to store it again.
	 */
	public void setStored(){
		stored = true;
	}
	
}
