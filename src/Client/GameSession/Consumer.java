package Client.GameSession;

import org.jspace.*;

import java.io.IOException;
import java.util.BitSet;

public class Consumer implements Runnable {
    private RemoteSpace rs;
    private String packageType;
    double lastTimeStamp = 0;

    PackageHandlerConsumer packageHandlerConsumer;

    public Consumer(String URI, PackageHandlerConsumer packageHandlerConsumer, String packageType) throws IOException, InterruptedException {
        this.rs = new RemoteSpace(URI);
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
                    double newTimestamp = (double)data[1];
                    if(newTimestamp > lastTimeStamp) {
                        BitSet full = (BitSet) data[2];
                        lastTimeStamp = newTimestamp;
                        this.packageHandlerConsumer.removeOldDeltas(newTimestamp);
                        this.packageHandlerConsumer.applyFull(full);
                        this.packageHandlerConsumer.reapplyDeltas();
                        this.packageHandlerConsumer.updateViewModel();
                    }
                } else if (this.packageType.equals("delta")) {
                    var data = rs.get(new FormalField(Object.class),
                                              new FormalField(Object.class),
                                              new FormalField(Object.class));

                    double newTimestamp = (double)data[1];
                    if(newTimestamp > lastTimeStamp) {
                        int[] delta = (int[]) data[2];
                        lastTimeStamp = newTimestamp;
                        this.packageHandlerConsumer.addDeltaToQueue(newTimestamp, delta);
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