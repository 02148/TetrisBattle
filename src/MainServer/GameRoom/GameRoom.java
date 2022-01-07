package MainServer.GameRoom;

import MainServer.Utils;

import java.util.Date;


public class GameRoom {
    public String userHost, UUID;
    public double timestamp;

    public GameRoom(Object[] q) {
        this.userHost = (String)q[0];
        this.UUID = (String)q[1];
        this.timestamp = (double)q[2];
    }

    public GameRoom(String userHost, String UUID) {
        this.userHost = userHost;
        this.UUID = UUID;
        this.timestamp = Utils.getCurrentTimestamp();
    }

    public String toString() {
        return  "GameRoom@" + this.UUID +
                "\n  >> Created By " + this.userHost +
                "\n  >> Created On " + new Date((long)this.timestamp*1000);
    }
}
