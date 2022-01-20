package Client.GameSession;

import Client.Models.BoardState;
import common.Constants;
import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.RemoteSpace;

import java.io.IOException;

public class AttackConsumer implements Runnable {
    private BoardState boardState;
    private String userUUID, gameUUID;
    private RemoteSpace combatSpace;
    private boolean stop = false;

    public AttackConsumer(String userUUID, String gameUUID, BoardState boardState) {
         try {
            this.combatSpace = new RemoteSpace("tcp://" + "localhost"+ ":42069/" + gameUUID+ "?keep");
            this.boardState = boardState;
            this.userUUID = userUUID;
            this.gameUUID = gameUUID;
         } catch(Exception e) {
             e.printStackTrace();
         }
    }


    @Override
    public void run() {
        while(!this.stop) {
            try {
                var data = this.combatSpace.get(new ActualField("incoming"),
                                                        new ActualField(this.userUUID),
                                                        new FormalField(Integer.class));
                this.boardState.addToAttackQueue((int) data[2]);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void stop() {
        this.stop = true;
    }
}
