package MainServer.GameSession;

import MainServer.GameSession.Modules.*;
import org.jspace.*;

import java.util.HashMap;

public class GameSession {
    SpaceRepository repo;
    StackSpace p1, p2, p3, p4, p5;
    HashMap<String, Space> p6s;
    Space conns;

    Dispatcher dispatcher;
    Transformer transformer;
    Collector collector;
    Duplicator duplicator;
    String UUID;

    public GameSession(String uuid, Space conns) throws Exception {
        this.UUID = uuid;
        this.repo = new SpaceRepository();
        this.conns = conns;

        this.p1 = new StackSpace();
        this.p2 = new StackSpace();
        this.p3 = new StackSpace();
        this.p4 = new StackSpace();
        this.p5 = new StackSpace();
        this.p6s = new HashMap<String, Space>();

        // add space for producers on client side (ingoing)
        this.repo.add(uuid, this.p1);

        // add spaces for consumers on client side (outgoing)
        var curConns = conns.queryAll(new FormalField(String.class));
        System.out.println(curConns);
        for (var c : curConns) {
            String curUserUUID = (String)c[0];
            StackSpace clientOutSpace = new StackSpace();
            this.p6s.put(curUserUUID, clientOutSpace);
            this.repo.add(curUserUUID, clientOutSpace);
        }

        // tcp://sess:6969/[room:UUID]?keep
        this.repo.addGate("tcp://localhost:6969/?keep");

        this.dispatcher = new Dispatcher(this.p1, this.p2, this.p3, this.conns);
        this.transformer = new Transformer(this.p3, this.p4, this.conns);
        this.collector = new Collector(this.p2, this.p4, this.p5);
        this.duplicator = new Duplicator(this.p5, this.p6s, this.conns);

        (new Thread(this.dispatcher)).start();
        (new Thread(this.transformer)).start();
        (new Thread(this.collector)).start();
        (new Thread(this.duplicator)).start();
    }
}
