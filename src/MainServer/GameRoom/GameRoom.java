package MainServer.GameRoom;

import MainServer.UserMgmt.User;
import MainServer.Utils;

import java.util.Date;
import java.util.HashMap;


public class GameRoom {
    public String userHostUUID, UUID;
    public String name;
    public double timestamp;
    public int numDead;
    public HashMap<String, Integer> scores;

    public GameRoom(Object[] q) {
        this.userHostUUID = (String)q[0];
        this.UUID = (String)q[1];
        this.name = (String)q[2];
        this.timestamp = (double)q[3];
        this.numDead = (int)q[4];
        this.scores = (HashMap<String, Integer>) q[5];
    }

    public GameRoom(String userHostUUID, String UUID, String name, int numDead, HashMap<String,Integer> scores) {
        this.userHostUUID = userHostUUID;
        this.UUID = UUID;
        this.name = name;
        this.timestamp = Utils.getCurrentTimestamp();
        this.numDead = numDead;
        this.scores = scores;
    }

    public void addScore(String UUID, int score){scores.put(UUID,score);
    }

    public HashMap getScores(){
        return scores;
    }

    public String toString() {
        return  "GameRoom " + this.name +
                "\n  >> Hosted By " + this.userHostUUID +
                "\n  >> Created On " + new Date((long)this.timestamp*1000);
    }
}
