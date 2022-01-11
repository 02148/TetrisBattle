package MainServer.GameSession;

import MainServer.GameSession.Modules.*;
import org.jspace.FormalField;
import org.jspace.Space;
import org.jspace.SpaceRepository;
import org.jspace.StackSpace;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class GameSession {
    SpaceRepository repo;
    StackSpace inSpace, fullSpace, deltaSpace, tFullSpace, tDeltaSpace;
    HashMap<String, Space> dupFullSpace, dupDeltaSpace;
    Space conns;

    Dispatcher dispatcher;
    Transformer transformerDelta, transformerFull;
    Duplicator duplicatorDelta, duplicatorFull;
    String UUID;

    public GameSession(String uuid, Space conns) throws Exception {
        this.UUID = uuid;
        this.repo = new SpaceRepository();
        this.conns = conns;

        this.inSpace = new StackSpace();
        this.fullSpace = new StackSpace();
        this.deltaSpace = new StackSpace();
        this.tFullSpace = new StackSpace();
        this.tDeltaSpace = new StackSpace();
        this.dupFullSpace = new HashMap<>();
        this.dupDeltaSpace = new HashMap<>();

        // add spaces for consumers on client side (outgoing)
        var curConns = conns.queryAll(new FormalField(String.class));
        System.out.println(curConns);
        for (var c : curConns) {
            String curUserUUID = (String)c[0];
            StackSpace deltaOutSpace = new StackSpace();
            StackSpace fullOutSpace = new StackSpace();
            this.dupDeltaSpace.put(curUserUUID, deltaOutSpace);
            this.dupFullSpace.put(curUserUUID, fullOutSpace);
            this.repo.add(curUserUUID, deltaSpace);
        }

        // add space for producers on client side (ingoing)
        this.repo.add(uuid, this.inSpace);

        // tcp://sess:6969/[room:UUID]?keep
        this.repo.addGate("tcp://localhost:6969/?keep");

        this.dispatcher = new Dispatcher(this.inSpace, this.deltaSpace, this.fullSpace, this.conns);
        this.transformerDelta = new Transformer(this.deltaSpace, this.tDeltaSpace, this.conns);
        this.transformerFull = new Transformer(this.fullSpace, this.tFullSpace, this.conns);
        this.duplicatorDelta = new Duplicator(this.tDeltaSpace, this.dupDeltaSpace, this.conns);
        this.duplicatorFull = new Duplicator(this.tFullSpace, this.dupFullSpace, this.conns);

        (new Thread(this.dispatcher)).start();
        (new Thread(this.transformerDelta)).start();
        (new Thread(this.transformerFull)).start();
        (new Thread(this.duplicatorDelta)).start();
        (new Thread(this.duplicatorFull)).start();
    }
}
