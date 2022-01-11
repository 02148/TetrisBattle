package MainServer.GameSession;

import org.jspace.Space;
import org.jspace.SpaceRepository;
import org.jspace.StackSpace;

public class GameSession {
    SpaceRepository repo;
    StackSpace sessDataIn, sessDataShared, sessDataOut;
    Space conns;

    Producer fullP, deltaP;
    Transformer fullC, deltaC;
    String UUID;

    public GameSession(String uuid, Space conns) {
        this.UUID = uuid;
        this.repo = new SpaceRepository();
        this.conns = conns;

        this.sessDataIn = new StackSpace();
        this.sessDataShared = new StackSpace();
        this.sessDataOut = new StackSpace();

        this.repo.add(uuid, sessDataIn);

        // tcp://sess:6969/[room:UUID]?keep
        this.repo.addGate("tcp://localhost:6969/?keep");

        this.fullC = new Transformer(this.sessDataIn, this.sessDataShared, this.conns, "full");
        this.fullP = new Producer(this.sessDataShared, this.sessDataOut, this.conns, "full", "sync");

        this.deltaC = new Transformer(this.sessDataIn, this.sessDataShared, this.conns, "delta");
        this.deltaP = new Producer(this.sessDataShared, this.sessDataOut, this.conns, "delta", "changelist");


        (new Thread(this.fullP)).start();
        (new Thread(this.fullC)).start();
        (new Thread(this.deltaP)).start();
        (new Thread(this.deltaC)).start();
    }
}
