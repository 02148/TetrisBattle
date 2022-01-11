package MainServer.GameSession.Modules;

import org.jspace.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Duplicator implements Runnable {
    Space in, conns;
    List<RemoteSpace> out;
    int T;

    public Duplicator(Space in, List<String> out, Space conns) throws Exception {
        this.in = in;

        this.out = new ArrayList<>();
        for (String UUID : out) {
            String URI = "tcp://localhost:6969/" + UUID + "?conn";
            try {
                this.out.add(new RemoteSpace(URI));
            } catch (Exception e) {
                System.out.println(">>! Error adding USER-SESSION@" + URI);
            }
        }

        if (this.out.isEmpty())
            throw new Exception("Connecting to all RemoteSpaces failed.");

        this.conns = conns;
        this.T = 100;
    }

    @Override
    public void run() {

    }
}
