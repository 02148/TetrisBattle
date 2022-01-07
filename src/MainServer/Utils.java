package MainServer;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

public class Utils {
    public static String createUUID() {
        return UUID.randomUUID().toString();
    }

    /**
        NOTE: Java returns the Unix epoch value in seconds.
        In order to get the correct value for java.util.Date,
        multiply by 1000
    */
    public static double getCurrentTimestamp() {
        return Instant.now().getEpochSecond();

    }

    public static void main(String[] args) {
        System.out.println(createUUID());
        long epoch = (long)getCurrentTimestamp();

        System.out.println(new Date(epoch));
    }
}
