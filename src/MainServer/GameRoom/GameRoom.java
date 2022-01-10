package MainServer.GameRoom;

import MainServer.Utils;

import java.util.Date;


public class GameRoom {
    public String userHostUUID, UUID;
    public double timestamp;

    public GameRoom(Object[] q) {
        this.userHostUUID = (String)q[0];
        this.UUID = (String)q[1];
        this.timestamp = (double)q[2];
    }

    public GameRoom(String userHostUUID, String UUID) {
        this.userHostUUID = userHostUUID;
        this.UUID = UUID;
        this.timestamp = Utils.getCurrentTimestamp();
    }

    public String toString() {
        return  "GameRoom@" + this.UUID +
                "\n  >> Hosted By " + this.userHostUUID +
                "\n  >> Created On " + new Date((long)this.timestamp*1000);
    }
}
