package MainServer.UserMgmt;

import MainServer.Utils;
import org.jspace.RemoteSpace;

import java.io.IOException;
import java.util.Date;

public class User {
    public String username, UUID;
    public int noOfWins;
    public double timestamp;
    public boolean isLoggedIn;

    public User(Object[] q) {
        this(
            (String)q[0],
            (String)q[1],
            (int)q[2],
            (double)q[3],
            (boolean)q[4]
        );
    }

    public User(String username, String UUID, int noOfWins, double timestamp, boolean isLoggedIn) {
        this.username = username;
        this.UUID = UUID;
        this.noOfWins = noOfWins;
        this.timestamp = timestamp;
        this.isLoggedIn = isLoggedIn;
    }

    public User(String username, String UUID) {
        this.username = username;
        this.UUID = UUID;
        this.noOfWins = 0;
        this.timestamp = Utils.getCurrentTimestamp();
        this.isLoggedIn = false;
    }


    public String toString() {
        return  this.username + "@" +
                this.UUID +
                "\n  >> Created On " + new Date((long) this.timestamp*1000) +
                "\n  >> Wins: " + this.noOfWins +
                "\n  >> Login Status: " + (this.isLoggedIn ? "Currently logged in" : "Currently not logged in");
    }
}
