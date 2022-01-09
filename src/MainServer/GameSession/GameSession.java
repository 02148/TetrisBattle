package MainServer.GameSession;

import org.jspace.SequentialSpace;
import org.jspace.Space;
import org.jspace.SpaceRepository;
import org.jspace.StackSpace;

public class GameSession {
    SpaceRepository repo;
    StackSpace sessDataIn, sessDataShared, sessDataOut;
    Space conns;

    Producer p;
    Consumer c;
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

        this.c = new Consumer(this.sessDataIn, this.sessDataShared, this.conns);
        this.p = new Producer(this.sessDataShared, this.sessDataOut, this.conns);

        (new Thread(this.p)).start();
        (new Thread(this.c)).start();
    }
}
