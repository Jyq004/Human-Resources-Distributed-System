package server;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

public class Multithread {

    /**
     * Submits a Callable task to a newly spawned Thread and returns a Future.
     * This uses default threading (manual thread creation) per request.
     * @param taskName A description of what the thread is doing
     * @param task The logic to execute
     */
    public static <T> Future<T> executeTask(String taskName, Callable<T> task) {
        Callable<T> wrappedTask = () -> {
            String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            System.out.println("[Thread-" + Thread.currentThread().getId() + "] Executing task: '" + taskName + "' at " + time);
            return task.call();
        };

        FutureTask<T> futureTask = new FutureTask<>(wrappedTask);
        // Create an explicit, standard java.lang.Thread for every execution
        Thread t = new Thread(futureTask);
        t.start();
        return futureTask;
    }

    /**
     * No thread pool to shut down anymore.
     */
    public static void shutdown() {
        // Obsolete since we are not using ExecutorService
    }
}
