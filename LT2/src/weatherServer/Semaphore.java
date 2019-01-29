package weatherServer;

public class Semaphore {
    private int max;
    private int val;

    public Semaphore(int num) {
        this.max = num;
        this.val = num;
    }

    public synchronized void attempt() throws InterruptedException {
        while (this.val <= 0) wait();
        this.val--;
        this.notify();
    }

    public synchronized void close() throws InterruptedException {
        while (this.val >= this.max) wait();
        this.val++;
        System.out.println(this.val);
        this.notify();
    }
}
