//Assignment 1 by Savvas Giortsis sagi2536

package assignment1;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Scanner;



public class Factorizer implements Runnable {

	private BigInteger min = BigInteger.TWO, max, product, step;
	@GuardedBy("lock")
	private BigInteger factor1 = BigInteger.ZERO, factor2 = BigInteger.ZERO;
	private Object factorlock = new Object();
	private Object minlock = new Object();
	
	private boolean isDone = false;

	@ThreadSafe
	public Factorizer(BigInteger prod, int numThreads) {
		this.max = prod;
		this.product = prod;
		this.step = BigInteger.valueOf(numThreads);
	}

	public void run() {
		BigInteger number = BigInteger.TWO; //Default value for init
		synchronized (minlock) {
			min = min.add(BigInteger.ONE);
			number = min;
		}
	
		while (number.compareTo(max) <= 0) { // While min is smaller or equal to max
			if(isDone)
				return;

			if (!isDone && product.remainder(number).compareTo(BigInteger.ZERO) == 0) {
				isDone = true;
				synchronized (factorlock) {
					factor1 = number;
					factor2 = product.divide(factor1);
				}
				return;
			}
			number = number.add(step);
		}
	}
	
	public int getFact1() {
		return factor1.intValue();
	}
	public int getFact2() {
		return factor2.intValue();
	}
	
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		
		System.out.print("Enter product: ");
		BigInteger product = BigInteger.valueOf(scanner.nextLong()); //TRY 4611686014132420609
		System.out.print("\nEnter number of threads: ");
		int numThreads = scanner.nextInt();
		
		scanner.close();
		
		final long startTime = System.currentTimeMillis();
		
		ArrayList<Thread> threads = new ArrayList<Thread>();
		Factorizer fact = new Factorizer(product, numThreads);

		for (int i = 0; i < numThreads; i++) {
			Thread t = new Thread(fact);
			threads.add(t);
		}
		for (int i = 0; i < numThreads; i++) 
			threads.get(i).start();
		for (int i = 0; i < numThreads; i++) 
			try {
				threads.get(i).join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		
		final long endTime = System.currentTimeMillis();
		
		if(fact.getFact1() != 1 && fact.getFact2() != 1) //Arguably you can have a isPrime method.
			System.out.println("\nFactor 1: " + fact.getFact1() + " , Factor 2: " + fact.getFact2());
		else
			System.out.println("\nNo factorization possible");
		System.out.println("Total time of execution: " + (endTime - startTime)/1000.0 + " seconds");

	}

}
