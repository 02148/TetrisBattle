package Client.GameSession;

import org.jspace.*;

import java.io.IOException;
import java.util.BitSet;

public class Consumer implements Runnable {
    private final RemoteSpace rs;
    private final String packageType, userUUID;
    double lastTimestampDelta = 0, lastTimestampFull = 0;
    PackageHandlerConsumer packageHandlerConsumer;

    public Consumer(String userUUID, PackageHandlerConsumer packageHandlerConsumer, String packageType) throws IOException, InterruptedException {
        this.userUUID = userUUID;
        this.rs = new RemoteSpace("tcp://localhost:1337/" + userUUID + "?keep");
        this.packageType = packageType;
        this.packageHandlerConsumer = packageHandlerConsumer;
    }

    @Override
    public void run() {
        while (true) {
            try {

                if (this.packageType.equals("full")) {
                    var data = rs.get(new FormalField(Object.class), new FormalField(Object.class), new FormalField(Object.class),
                                              new FormalField(Object.class), new FormalField(Object.class), new FormalField(Object.class),
                                              new FormalField(Object.class), new FormalField(Object.class), new FormalField(Object.class),
                                              new FormalField(Object.class), new FormalField(Object.class), new FormalField(Object.class),
                                              new FormalField(Object.class), new FormalField(Object.class), new FormalField(Object.class),
                                              new FormalField(Object.class), new FormalField(Object.class), new FormalField(Object.class),
                                              new FormalField(Object.class), new FormalField(Object.class), new FormalField(Object.class),
                                              new FormalField(Object.class), new FormalField(Object.class), new FormalField(Object.class));
                    double newTimestampFull = (double)data[1];
                    if(newTimestampFull > lastTimestampFull) {
                        BitSet full = (BitSet) data[2];
                        lastTimestampFull = newTimestampFull;
                        this.packageHandlerConsumer.removeOldDeltas(newTimestampFull);
                        this.packageHandlerConsumer.applyFull(full);
                        this.packageHandlerConsumer.reapplyDeltas();
                        this.packageHandlerConsumer.updateViewModel();
                    }
                } else if (this.packageType.equals("delta")) {
                    var data = rs.get(new FormalField(Object.class),
                                              new FormalField(Object.class),
                                              new FormalField(Object.class));

                    double newTimestampDelta = (double)data[1];
                    if(newTimestampDelta > lastTimestampDelta) {
                        int[] delta = (int[]) data[2];
                        lastTimestampDelta = newTimestampDelta;
                        this.packageHandlerConsumer.addDeltaToQueue(newTimestampDelta, delta);
                        this.packageHandlerConsumer.applyDelta(delta);
                        this.packageHandlerConsumer.updateViewModel();
                    }

                    //TODO: Take into account all players
                } else {
                    throw new Exception("Unknown Package Type!");
                }

                this.packageHandlerConsumer.printBoard();
                Thread.sleep(10);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}