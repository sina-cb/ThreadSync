package Producer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.locks.Lock;

import DataStructure.CircularArray;
import DataStructure.DataItem;

public class ProducerThread implements Runnable{

	private int producerId = -1;
	private File log;
	private CircularArray buffer;
	private int sequenceNum = 1;
	private Lock producerLogWrite;
	
	/**
	 * The only constructor for the ProducerThread
	 * @param producerId Generated producer id for the thread
	 * @param buffer Circular array to be used a buffer
	 * @param producerLog File for the producers to write their logs to
	 * @param producerLogWrite Lock to be used between all the threads when they want to write to the log file
	 */
	public ProducerThread(int producerId, CircularArray buffer, File producerLog, Lock producerLogWrite){
		this.producerId = producerId;
		this.log = producerLog;
		this.buffer = buffer;
		this.producerLogWrite = producerLogWrite;
	}
	
	/**
	 * Here the magic happens for the producer threads! :D
	 */
	@Override
	public void run() {
		while(true){
			
			DataItem item = new DataItem(producerId, sequenceNum);
			sequenceNum++;
			
			//Writing to the producer log file. It needs mutual exclusion so that it won't get scrambled. 
			writeLog(generatedDataItemLog(item));
			
			//This while keeps looping till the produced item is finally store in the buffer.
			while(!item.isStored()){
				//First we check if the buffer is full or not.
				//If it is full we write a small log to the log file and then sleep for a random time (<2 seconds) and continue the loop from the beginning.  
				if (buffer.isFull()){
					writeLog(fullBufferLog(item));
					
					try {
						Thread.sleep(randomNumberLessThan(2));
					} catch (InterruptedException e) {
						System.err.println("Thread could not sleep");
						System.exit(-1);
					}
					continue;
				}
				
				//If the buffer is not full, the program reaches here. We first try to add the item to the buffer. If it fails, due to whatever reason, the producer
				//have to try to add the item again. 
				if (!addItemToBuffer(item)){
					//One of the threads might get away with the locks, but here we catch them :D
				}else{
					//If the program stores the item in the buffer successfully, it writes a log to the producers log file.
					writeLog(successfulInsertionLog(item));
					item.setStored();
				}
			}
		}
	}
	
	/**
	 * Just writes a string to the producers log file.
	 * @param log The log string which needs to be written to the log file.
	 * @return True if successful.
	 */
	private boolean writeLog(String log){
		try {
			producerLogWrite.lock();
			BufferedWriter bw = new BufferedWriter(new FileWriter(this.log, true));
			bw.append(log + "\n");
			bw.close();
			producerLogWrite.unlock();
		} catch (IOException e) {
			return false;
		}
		return true;
	}

	/**
	 * Formats a string for the situation when the buffer is full
	 * @param item The item we have produced
	 * @return The formatted string
	 */
	private String fullBufferLog(DataItem item) {
		return String.format("%d %s \"Buffer full - Insertion failed\"", producerId, item.toString());
	}
	
	/**
	 * Formats a string for the situation when the item is written to the buffer successfully.
	 * @param item The item we have produced
	 * @return The formatted string
	 */
	private String successfulInsertionLog(DataItem item) {
		return String.format("%d %s \"Successful Insertion\"", producerId, item.toString());
	}
	
	/**
	 * Formats a string for the situation when the we have created an item.
	 * @param item The item we have produced
	 * @return The formatted string
	 */
	private String generatedDataItemLog(DataItem d){
		return String.format("%d \"Generated\" %s", producerId, d.toString());
	}
	
	/**
	 * Just tried to add an item to the buffer using it's native method 'store(DataItem item)'
	 * @param item The item which needs to be stored
	 * @return True if successful
	 */
	private boolean addItemToBuffer(DataItem item){
		boolean result = buffer.store(item);
		return result;
	}
	
	/**
	 * Generates an integer random value less than the maximum value defined.
	 * @param max The maximum value for the generated number
	 * @return The random random below the maximum parameter
	 */
	private int randomNumberLessThan(int max){
		Random rand = new Random();
		return (int) (rand.nextDouble() * max * 1000);
	}
	
}
