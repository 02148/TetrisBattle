package MainServer;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

public class Utils {
    public static String createUUID() {
        return UUID.randomUUID().toString();
    }

    /**
     *  Get a timestamp that is accurate to the second.
     *  NOTE: Java returns the Unix epoch value in seconds.
        In order to get the correct value for java.util.Date,
        multiply by 1000
       * @return Unit seconds
    */
    public static double getCurrentTimestamp() {
        return Instant.now().getEpochSecond();
    }

    /**
     * Get a timestamp that is accurate in milliseconds.
     * @return Unit milliseconds
     */
    public static double getCurrentExactTimestamp() {
        return  Instant.now().getEpochSecond() * Math.pow(10,3) +
                Instant.now().getNano() / Math.pow(10,6);
    }

    public static void main(String[] args) {
        System.out.println(createUUID());
        long epoch = (long)getCurrentTimestamp();
        long ex_epoch = (long)getCurrentExactTimestamp();
        System.out.println(epoch);
        System.out.println(ex_epoch);
        System.out.println(new Date(epoch));
    }
}
