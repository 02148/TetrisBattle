package Client.GameSession;

import Client.Logic.Controls;
import Client.Models.BoardState;
import Client.UI.Board;
import MainServer.GameSession.Combat.AttackObject;
import common.Constants;
import org.jspace.FormalField;
import org.jspace.QueueSpace;
import org.jspace.RemoteSpace;

import java.util.ArrayList;
import java.util.List;

public class AttackProducer implements Runnable{
    private Controls controller;
    private String gameUUID, userUUID;
    private QueueSpace pendingAttacks;
    private RemoteSpace combatSpace;
    private List<String> connections;

    public AttackProducer(String gameUUID, String userUUID, Controls controller, List<String> conns) {
        try {
            this.combatSpace = new RemoteSpace("tcp://" + Constants.IP_address+ ":42069/" + gameUUID+ "?keep");
            this.gameUUID = gameUUID;
            this.userUUID = userUUID;
            this.controller = controller;
            this.connections = conns;
            this.pendingAttacks = new QueueSpace();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public String getReceiverUUID() {
        int selectedUser = this.controller.getSelectedOpponent();
        if(selectedUser == -1 || connections.size() < selectedUser)
            return "none";
        return connections.get(selectedUser);
    }

    public void queueAttack(int amountLines) {
        String sendTo = getReceiverUUID();
        try {
            this.pendingAttacks.put(new AttackObject(amountLines, this.userUUID, sendTo));
        } catch(Exception e) {}
    }

    @Override
    public void run() {
        while(true) {
            sendAttack();
        }
    }

    public void sendAttack() {
        try {
            AttackObject attackObj = (AttackObject) this.pendingAttacks.get(new FormalField(AttackObject.class))[0];
            this.combatSpace.put("outgoing", attackObj);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
