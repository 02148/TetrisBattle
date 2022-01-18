package MainServer.GameRoom;

import MainServer.UserMgmt.User;
import MainServer.Utils;

import java.util.Date;


public class GameRoom {
    public String userHostUUID, UUID;
    public int number;
    public double timestamp;

    public GameRoom(Object[] q) {
        this.userHostUUID = (String)q[0];
        this.UUID = (String)q[1];
        this.number = (int)q[2];
        this.timestamp = (double)q[3];
    }

    public GameRoom(String userHostUUID, String UUID, int number) {
        this.userHostUUID = userHostUUID;
        this.UUID = UUID;
        this.number = number;
        this.timestamp = Utils.getCurrentTimestamp();
    }

    public String toString() {
        return  "GameRoom " + this.number +
                "\n  >> Hosted By " + this.userHostUUID +
                "\n  >> Created On " + new Date((long)this.timestamp*1000);
    }
}
