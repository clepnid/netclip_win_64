package torrentLibreria.utils;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Looper {

    private final BlockingQueue<Runnable> queue;
    private final Thread thread;

    private static Looper mainLooper;

    public static Looper getMainLooper() {
        if (mainLooper == null) {
            mainLooper = new Looper();
        }
        return mainLooper;
    }

    private Looper() {
        queue = new LinkedBlockingQueue<>();
        thread = Thread.currentThread();
    }

    public void loop() {
        while (!Thread.interrupted()) {
            try {
                Runnable runnable = queue.take();
                runnable.run();
            } catch (InterruptedException e) {
                break;
            }
        }
        System.out.println("break");
    }

    public boolean post(Runnable runnable) {
        return queue.offer(runnable);
    }

    public Thread getThread() {
        return thread;
    }


}