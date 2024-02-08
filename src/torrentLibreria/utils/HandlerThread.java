package torrentLibreria.utils;

public class HandlerThread extends Thread {

    private final Object lock = new Object();
    private Handler handler;

    public HandlerThread(String name) {
        super(name);
    }

    public Handler getHandler() {
        synchronized (lock) {
            while (handler == null) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    // Ignorar interrupciones
                }
            }
            return handler;
        }
    }

    @Override
    public void run() {
        synchronized (lock) {
            handler = new Handler(Looper.getMainLooper());
            lock.notify();
        }
        Looper.getMainLooper().loop();
    }

    @Override
    public synchronized void start() {
        super.start();
        getHandler();
    }
}