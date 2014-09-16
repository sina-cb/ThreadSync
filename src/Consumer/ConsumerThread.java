package Consumer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.locks.Lock;

import DataStructure.CircularArray;
import DataStructure.DataItem;

public class ConsumerThread implements Runnable{

	private int consumerId = -1;
	private File log;
	private CircularArray buffer;
	private Lock consumerLogWrite;
	
	/**
	 * The only constructor for the ConsumerThread
	 * @param producerId Generated consumer id for the thread
	 * @param buffer Circular array to be used a buffer
	 * @param producerLog File for the consumer to write their logs to
	 * @param producerLogWrite Lock to be used between all the threads when they want to write to the log file
	 */
	public ConsumerThread(int consumerId, CircularArray buffer, File consumerLog, Lock consumerLogWrite){
		this.consumerId = consumerId;
		this.log = consumerLog;
		this.buffer = buffer;
		this.consumerLogWrite = consumerLogWrite;
	}
	
	/**
	 * Here the magic happens for the consumer threads! :D
	 */
	@Override
	public void run() {
		while(true){
			writeLog(attemptForConsume());
			DataItem item = buffer.remove();
			writeLog(successfulRemove(item));
			
			try {
				Thread.sleep(randomNumberLessThan(5));
			} catch (InterruptedException e) {
				System.err.println("Error while sleeping in consumer thread");
				System.exit(-1);
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
			consumerLogWrite.lock();
			BufferedWriter bw = new BufferedWriter(new FileWriter(this.log, true));
			bw.append(log + "\n");
			bw.close();
			consumerLogWrite.unlock();
		} catch (IOException e) {
			return false;
		}
		return true;
	}
	
	/**
	 * Formats a string for the situation when the consumer first attempts to read and consume an item
	 * @return The formatted string
	 */
	private String attemptForConsume(){
		return String.format("%d \"Attempting to remove item from buffer\"", consumerId);
	}
	
	/**
	 * Formats a string for the situation when the consumer has consumed the item
	 * @param item The item we have consumed
	 * @return The formatted string
	 */
	private String successfulRemove(DataItem item){
		return String.format("%d %s \"Successfully removed\"", consumerId, item.toString());
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
