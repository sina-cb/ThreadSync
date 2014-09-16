package Shared;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import Consumer.ConsumerThread;
import DataStructure.CircularArray;
import Producer.ProducerThread;

public class Main {

	public static void main(String[] args){

		//Input and output initializations
		File producerLog = new File("producers.log");
		File consumerLog = new File("consumers.log");
		producerLog.delete();
		consumerLog.delete();
		try {
			producerLog.createNewFile();
			consumerLog.createNewFile();
		} catch (IOException e) {
			System.err.println("Some error while trying to handle the log files");
			System.exit(-1);
		}
		
		Scanner input = new Scanner(System.in);
		
		//Command line parameters
		int consumerNum = -1;
		int producerNum = -1;
		int bufferSize = -1;

		//Reading command line parameters
		System.out.println("Please enter number of producers:");
		producerNum = input.nextInt();
		
		System.out.println("Please enter number of consumers:");
		consumerNum = input.nextInt();
		
		System.out.println("Please enter the buffer size:");
		bufferSize = input.nextInt();
		
		//This is the shared buffer among all the producers and the consumers
		CircularArray buffer = new CircularArray(bufferSize + 1);
		
		//These two arrays are used to hold the producer and consumer threads. 
		ArrayList<ProducerThread> producers = new ArrayList<>();
		ArrayList<ConsumerThread> consumers = new ArrayList<>();
		
		//Initializing shared locks for threads
		Lock producerLogWrite = new ReentrantLock();
		Lock consumerLogWrite = new ReentrantLock();
		
		//Threads initialization  
		for (int i = 0; i < producerNum; i++){
			producers.add(new ProducerThread(i + 1, buffer, producerLog, producerLogWrite));
		}
		for (int i = 0; i < consumerNum; i++){
			consumers.add(new ConsumerThread(i + 1, buffer, consumerLog, consumerLogWrite));
		}
		
		//Starting All the threads
		for (ProducerThread p : producers){
			(new Thread(p)).start();
		}
		for (ConsumerThread c : consumers){
			(new Thread(c)).start();
		}
		
		input.close();
		
	}
	
}
