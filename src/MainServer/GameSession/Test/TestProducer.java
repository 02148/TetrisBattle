package MainServer.GameSession.Test;

import org.jspace.FormalField;
import org.jspace.Space;
import org.jspace.StackSpace;

import java.util.Date;
import java.util.Random;

import static MainServer.Utils.getCurrentExactTimestamp;
import static MainServer.Utils.getCurrentTimestamp;

public class TestProducer implements Runnable {
    Space s;
    Random rnd;
    int T = 10; // T = 1/F

    public TestProducer(StackSpace s) {
        this.s = s;
        this.rnd = new Random();
    }

    @Override
    public void run() {
        double cur = 0;
        while (true) {
            try {
                cur += rnd.nextGaussian();
                s.put(getCurrentExactTimestamp(), cur);
                Thread.sleep(T);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        StackSpace s = new StackSpace();
        TestProducer tp = new TestProducer(s);
        (new Thread(tp)).start();

        while (true) {
            try {
                var raw_data = s.getp(new FormalField(Double.class), new FormalField(Double.class));
                long epoch = (long) (double) raw_data[0];
                double data = (double) raw_data[1];

                String fs = String.format("%.3f",data).replace(',','.');
                System.out.println(fs + " " + epoch);
            } catch (Exception ignored) {
                ignored.printStackTrace();
            }

            Thread.sleep(tp.T);
        }
    }
}
