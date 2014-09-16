package DataStructure;

public class CircularArray {

	private int size = 5;
	private int head = 0;
	private int tail = 0;
	private DataItem[] array;
	
	/**
	 * This is the default constructor and it will set the size of the buffer to 5 by default.
	 */
	public CircularArray(){
		this(5);
	}
	
	/**
	 * This constructor accepts the size of the buffer as an integer value.
	 * @param size Size of the buffer
	 */
	public CircularArray(int size){
		this.size = size;
		array = new DataItem[size];
	}
	
	/**
	 * @param item The data item which needs to be stored.
	 * @return This method will return true if the operation was successful.
	 */
	public synchronized boolean store(DataItem item){
		if (!isFull()){
			array[tail] = item;
			item.setStored();
			tail--;
			if (tail < 0){
				tail = size - 1;
			}
			notify();
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * Remove a data item from the buffer and returns value of that data item.
	 * @return The data item at the head of the queue. Returns null if the operation was not successful.
	 */
	public synchronized DataItem remove(){
		while(isEmpty()){
			try {
				wait();
			} catch (InterruptedException e) {
				System.err.println("There was an error while waiting...");
				return null;
			}
		}
		DataItem temp = array[head];
		head--;
		if (head < 0){
			head = size - 1;
		}
		return temp;
	}
	
	/**
	 * Checks if the buffer is empty or not
	 * @return Returns true if the buffer is empty 
	 */
	public synchronized boolean isEmpty(){
		return (head == tail);
	}
	
	/**
	 * Checks if the buffer is full or not
	 * @return Returns true if the buffer is full 
	 */
	public synchronized boolean isFull(){
		boolean cond = (head + 1) % size == tail;
		
		if (cond){
			return cond;
		}else{
			return false;
		}
		
	}
	
}
