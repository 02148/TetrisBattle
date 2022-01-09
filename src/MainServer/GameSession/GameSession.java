package MainServer.GameSession;

public class GameSession {
    Producer p;
    Consumer c;
    String UUID;

    public GameSession(String uuid) {
        this.p = new Producer();
        this.c = new Consumer();
        this.UUID = uuid;
    }
}
