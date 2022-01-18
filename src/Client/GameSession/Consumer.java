package Client.GameSession;

import Client.Logic.Controls;
import Client.Models.BoardState;
import com.google.gson.internal.LinkedTreeMap;
import static MainServer.Utils.getCurrentExactTimestamp;
import org.jspace.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;

public class Consumer implements Runnable {
    private RemoteSpace rs;
    public BoardState boardState;
    public Controls controller;
    private String packageType;
    double lastTimeStamp = 0;


    public Consumer(String URI, BoardState boardState, Controls controller, String packageType) throws IOException, InterruptedException {
        this.rs = new RemoteSpace(URI);
        this.boardState = boardState;
        this.controller = controller;
        this.packageType = packageType;
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
                    double newTime = (double)data[1];
                    if(newTime > lastTimeStamp) {
                        lastTimeStamp = newTime;
                        this.boardState.setBoardStateFromBitArray((BitSet) data[2]);
                        if(this.controller != null)
                            this.controller.updateView();
                    }
                } else if (this.packageType.equals("delta")) {
                    var data = rs.get(new FormalField(Object.class),
                                              new FormalField(Object.class),
                                              new FormalField(Object.class));
                    System.out.println();
//                    var changeListDto = (HashMap<String, ArrayList>)data[0];
//                    var res = changeListDto.get("player1");
//                    var res2 =(LinkedTreeMap<String, Double>) res.get(2);
//
//                    System.out.println();
                    double newTime = (double)data[1];
                    if(newTime > lastTimeStamp) {
                        lastTimeStamp = newTime;
                        this.boardState.updateBoardFromDeltaIntegerArray((int[]) data[2]);
                        if (this.controller != null)
                            this.controller.updateView();
                    }

                    //TODO: Take into account all players
                } else {
                    throw new Exception("Unknown Package Type!");
                }
                System.out.println(this.boardState);
                Thread.sleep(10);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}