package Client.GameSession;

import Client.Models.BoardState;
import common.Constants;
import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.RemoteSpace;

public class AttackConsumer implements Runnable {
    private BoardState boardState;
    private String userUUID, gameUUID;
    private RemoteSpace combatSpace;

    public AttackConsumer(String userUUID, String gameUUID, BoardState boardState) {
         try {
            this.combatSpace = new RemoteSpace("tcp://" + Constants.IP_address+ ":42069/" + gameUUID+ "?keep");
            this.boardState = boardState;
            this.userUUID = userUUID;
            this.gameUUID = gameUUID;
         } catch(Exception e) {
             e.printStackTrace();
         }
    }


    @Override
    public void run() {
        while(true) {
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
}
