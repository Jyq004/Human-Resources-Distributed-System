
package server.classes;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Multithread {
    
    // Configured a fixed thread pool to manage multiple client requests without overwhelming the server
    private static final ExecutorService threadPool = Executors.newFixedThreadPool(20);

    /**
     * Submits a Callable task to the thread pool and returns a Future.
     * This centralizes execution logic and handles multithreading.
     */
    public static <T> Future<T> executeTask(Callable<T> task) {
        return threadPool.submit(task);
    }

    /**
     * Clean shutdown method for the thread pool (e.g., when the server stops)
     */
    public static void shutdown() {
        threadPool.shutdown();
    }
}
