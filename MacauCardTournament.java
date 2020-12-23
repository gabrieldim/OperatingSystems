import java.util.HashSet;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Semafori {
    public static int zeleniCounter = 0;

    public static Semaphore crevni = new Semaphore(2);
    public static Semaphore zeleni = new Semaphore(2);

    public static Semaphore crvenHere = new Semaphore(0);

    public static Semaphore ready = new Semaphore(0);

    public static Lock lock = new ReentrantLock();

}

class GreenPlayer extends Thread {


    public void execute() throws InterruptedException {
        Semafori.zeleni.acquire();
        System.out.println("Green player ready");
        Thread.sleep(50);
        System.out.println("Green player here");

        Semafori.lock.lock();
        Semafori.zeleniCounter++;
        boolean komandant = (Semafori.zeleniCounter == 1);
        Semafori.lock.unlock();



        for(int i=1; i<=3; i++) {

            if(komandant) {
                Semafori.crvenHere.acquire(2);
                Semafori.ready.release(4);
            }
            Semafori.ready.acquire();
            // TODO: the following code should be executed 3 times
            System.out.println("Game " + i + " started");
            Thread.sleep(200);
            System.out.println("Green player finished game " + i);

//            if(komandant) {
//                // TODO: only one player calls the next line per game
//                System.out.println("Game " + i + " finished");
//            }


        }

        if(komandant) {
            Semafori.zeleni.release(2);
            Semafori.crevni.release(2);
            Semafori.zeleniCounter = 0;
            // TODO: only one player calls the next line per match
            System.out.println("Match finished");
        }
    }

    @Override
    public void run() {
        try {
            execute();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class RedPlayer extends Thread {



    public void execute() throws InterruptedException {
        Semafori.crevni.acquire();
        System.out.println("Red player ready");

        Thread.sleep(50);
        System.out.println("Red player here");

        for(int i=1; i<=3; i++) {
            Semafori.crvenHere.release();
            Semafori.ready.acquire();

            // TODO: the following code should be executed 3 times
            System.out.println("Game "+ i +" started");
            Thread.sleep(200);
            System.out.println("Red player finished game "+ i);
        }


    }


    @Override
    public void run() {
        try {
            execute();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

public class MacauCardTournament {


    public static void main(String[] args) throws InterruptedException {
        HashSet<Thread> threads = new HashSet<Thread>();
        for (int i = 0; i < 30; i++) {
            RedPlayer red = new RedPlayer();
            threads.add(red);
            GreenPlayer green = new GreenPlayer();
            threads.add(green);
        }
        // start 30 red and 30 green players in background
        for(Thread t : threads) {
            t.start();
        }

        // after all of them are started, wait each of them to finish for 1_000 ms

        for(Thread t : threads) {
            t.join(1000);
        }
        // after the waiting for each of the players is done, check the one that are not finished and terminate them
        for(Thread t : threads) {
            if(t.isAlive()) {
                System.err.println("Possible deadlock");
                t.interrupt();
            }
        }

    }

}
