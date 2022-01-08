package MainServer.GameSession;

import org.jspace.SequentialSpace;
import org.jspace.Space;
import org.jspace.StackSpace;

public class TestProducer implements Runnable {
    Space s;

    public TestProducer(StackSpace s) {
        this.s = s;
    }

    @Override
    public void run() {

    }
}
