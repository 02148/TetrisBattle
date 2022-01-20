package Client.GameSession;

import com.google.gson.internal.LinkedTreeMap;
import common.Constants;
import org.jspace.*;

import java.io.IOException;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;

public class Consumer implements Runnable {
    private final RemoteSpace rs;
    private final String packageType, userUUID;
    private HashMap<String,Double> lastTimestampDelta, lastTimestampFull;
    private HashMap<String, ConsumerPackageHandler> consumerPackageHandlers;
    private boolean stop = false;

    public Consumer(String userUUID, List<String> opponentUUIDs, HashMap<String, ConsumerPackageHandler> consumerPackageHandlers, String packageType) throws IOException, InterruptedException {
        this.userUUID = userUUID;
        this.rs = new RemoteSpace("tcp://" + "10.209.222.2"+ ":1337/" + userUUID + "?keep");
        this.packageType = packageType;
        this.consumerPackageHandlers = consumerPackageHandlers;

        lastTimestampFull = new HashMap<>();
        lastTimestampDelta = new HashMap<>();
        for(String names : opponentUUIDs) {
            lastTimestampFull.put(names, 0.0);
            lastTimestampDelta.put(names, 0.0);
        }
    }

    @Override
    public void run() {
        while (!this.stop) {
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
                    for(int i = 0; i < 8; i++) {
                        if(data[i*3] == null || data[i*3].getClass() == LinkedTreeMap.class) break;
                        if(((String) data[i*3]).equals(this.userUUID)) continue;

                        String playerUUID = (String) data[i*3];
                        Double newTimestampFull = (Double) data[i*3+1];
                        BitSet full = (BitSet) data[i*3+2];


                        if(newTimestampFull > lastTimestampFull.get(playerUUID)) {
                            lastTimestampFull.put(playerUUID, newTimestampFull);
                            this.consumerPackageHandlers.get(playerUUID).removeOldDeltas(newTimestampFull);
                            this.consumerPackageHandlers.get(playerUUID).applyFull(full);
                            this.consumerPackageHandlers.get(playerUUID).reapplyDeltas();
                            this.consumerPackageHandlers.get(playerUUID).updateViewModel();
                        }
                    }

                    double newTimestampFull = (double)data[1];
                } else if (this.packageType.equals("delta")) {
                    var dataList = rs.getAll(new FormalField(Object.class),
                                                     new FormalField(Object.class),
                                                     new FormalField(Object.class));
                    for(Object data[] : dataList) {
                        String playerUUID = (String) data[0];
                        double newTimestampDelta = (double)data[1];
                        if(newTimestampDelta > lastTimestampDelta.get(playerUUID) && this.consumerPackageHandlers.containsKey(playerUUID)) {
                            int[] delta = (int[]) data[2];
                            lastTimestampDelta.put(playerUUID, newTimestampDelta);
                            this.consumerPackageHandlers.get(playerUUID).addDeltaToQueue(newTimestampDelta, delta);
                            this.consumerPackageHandlers.get(playerUUID).applyDelta(delta);
                            this.consumerPackageHandlers.get(playerUUID).updateViewModel();
                            this.consumerPackageHandlers.get(playerUUID).printBoard();
                        }
                    }
                } else {
                    throw new Exception("Unknown Package Type!");
                }


                Thread.sleep(10);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public void stop() {
        this.stop = true;
    }
}