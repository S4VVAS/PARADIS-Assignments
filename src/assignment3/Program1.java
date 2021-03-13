// Peter Idestam-Almquist, 2020-02-04.
// [Replace this comment with your own name.]

// [Do necessary modifications of this file.]

package assignment3;

import java.util.Arrays;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ForkJoinPool;


// [You are welcome to add some import statements.]

public class Program1 {
	final static int NUM_WEBPAGES = 40;
	private static WebPage[] webPages = new WebPage[NUM_WEBPAGES];
	private static BlockingQueue<WebPage> queue = new ArrayBlockingQueue<WebPage>(NUM_WEBPAGES);

	private static ForkJoinPool pool = new ForkJoinPool();

	// [You are welcome to add some variables.]

	// [You are welcome to modify this method, but it should NOT be parallelized.]
	private static void initialize() {
		for (int i = 0; i < NUM_WEBPAGES; i++) {
			webPages[i] = new WebPage(i, "http://www.site.se/page" + i + ".html");
		}
	}

	// [Do modify this sequential part of the program.]
	private static void downloadWebPages(WebPage page) {
		page.download();
	}

	// [Do modify this sequential part of the program.]
	private static void analyzeWebPages(WebPage page) {
		page.analyze();
	}

	// [Do modify this sequential part of the program.]
	private static void categorizeWebPages(WebPage page) {
		page.categorize();
	}

	// [You are welcome to modify this method, but it should NOT be parallelized.]
	private static void presentResult() {
		for (int i = 0; i < NUM_WEBPAGES; i++) {
			System.out.println(webPages[i]);
		}
	}

	private static void task(WebPage page) {
		synchronized (page) {
			downloadWebPages(page);
			analyzeWebPages(page);
			categorizeWebPages(page);
		}
	}

	public static void main(String[] args) {
		// Initialize the list of webpages.
		initialize();

		// Start timing.
		long start = System.nanoTime();

		//Producer
		pool.submit(() -> Arrays.stream(webPages)
					.parallel()
					.forEach(i -> {queue.add(i);})).join();
		
		//Consumer
		pool.submit(() -> queue.parallelStream().forEach(i -> {task(i);})).join();

		// Stop timing.
		long stop = System.nanoTime();

		// Present the result.
		presentResult();

		// Present the execution time.
		System.out.println("Execution time (seconds): " + (stop - start) / 1.0E9);
	}

}
