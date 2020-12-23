import java.util.HashSet;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class VolleyballTournament {

    public static Semaphore sala = new Semaphore(12);
    public static Semaphore kabina = new Semaphore(4);
    public static Semaphore gameStarted = new Semaphore(0);

    public static int readyPlayers = 0;

    public static Lock ready = new ReentrantLock();



    public static void main(String[] args) throws InterruptedException {
        HashSet<Thread> threads = new HashSet<>();
        for (int i = 0; i < 60; i++) {
            Player p = new Player();
            Thread newThread = new Thread(p);
            threads.add(newThread);
        }
        // run all threads in background
        for(Thread t : threads) {
            t.start();
        }

        // after all of them are started, wait each of them to finish for maximum 2_000 ms
        for(Thread t : threads) {
            t.join(2000);
        }

        for(Thread t : threads) {
            if(t.isAlive()) {
                // for each thread, terminate it if it is not finished
                System.out.println("Possible deadlock!");
                t.interrupt();
            }
        }
        System.out.println("Tournament finished.");

    }
}

class Player implements Runnable {

    public void execute() throws InterruptedException {
        // at most 12 players should print this in parallel
        VolleyballTournament.sala.acquire();

        System.out.println("Player inside.");

        // at most 4 players may enter in the dressing room in parallel
        VolleyballTournament.kabina.acquire();
        System.out.println("In dressing room.");
        Thread.sleep(10);// this represent the dressing time
        VolleyballTournament.kabina.release();

        VolleyballTournament.ready.lock();
        VolleyballTournament.readyPlayers++;
        VolleyballTournament.ready.unlock();

        if(VolleyballTournament.readyPlayers == 12) {
            VolleyballTournament.gameStarted.release(12);
        }

        VolleyballTournament.gameStarted.acquire();

        // after all players are ready, they should start with the game together
        System.out.println("Game started.");



        Thread.sleep(100);// this represent the game duration
        VolleyballTournament.ready.lock();
        VolleyballTournament.readyPlayers--;
        System.out.println("Player done.");
        VolleyballTournament.ready.unlock();


        if(VolleyballTournament.readyPlayers == 0) {
            // only one player should print the next line, representing that the game has finished
            System.out.println("Game finished.");
            VolleyballTournament.sala.release(12);
            return;
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