package u5_3;

import java.util.concurrent.atomic.AtomicInteger;

import static u5_3.Main.MAX_BUFFET_CAPCITY;
import static u5_4.ColorConstants.*;

public class Chef implements Runnable {
    private static final Object sempahor = new Object();
    private static AtomicInteger currentMissionaries = new AtomicInteger(0);
    private int fillingPace;
    private String name;
    private int cookingPace;
    private Buffet buffet;

    public Chef(String name, Buffet buffet, int fillingPace, int cookingPace) {
        this.name = name;
        this.buffet = buffet;
        this.fillingPace = fillingPace;
        this.cookingPace = cookingPace;
    }


    @Override
    public void run() {
        while (true) {
            try {
                cook();
                synchronized (sempahor) {
                    if (currentMissionaries.get() > MAX_BUFFET_CAPCITY - 1) {
                        System.out.printf("%s is trying filling the Buffet\n", name);
                        Thread.sleep(fillingPace);
                        buffet.fill();
                        currentMissionaries.updateAndGet((prev) -> prev - 5);
                        System.out.printf("%s%s filled the Buffet\n%s", ANSI_GREEN, name, ANSI_RESET);
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void cook() {
        try {
            Thread.sleep(cookingPace);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.printf("%s has cooked. Current Missionary-count: %s\n", name, currentMissionaries.incrementAndGet());
    }
}
