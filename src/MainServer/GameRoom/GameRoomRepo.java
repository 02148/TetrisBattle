package MainServer.GameRoom;

import MainServer.Utils;
import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.SequentialSpace;
import org.jspace.Space;

public class GameRoomRepo {
    Space s, conns;

    public GameRoomRepo(Space s) {
        this.s = s;
        this.conns = new SequentialSpace();
    }

    private void insertGameRoom(GameRoom gr) throws InterruptedException {
        s.put(gr.userHost, gr.UUID, gr.timestamp);
    }

    private GameRoom getGameRoom(String uuid) throws InterruptedException {
        var q = s.getp(new FormalField(String.class), new ActualField(uuid), new FormalField(Double.class));
        return new GameRoom(q);
    }

    /**
     *
     * @param username Username of user that created the room.
     *                 This user will become host of the room.
     * @return UUID of room, used for connecting to room.
     */
    public String create(String username) throws InterruptedException {
        GameRoom gr = new GameRoom(
                username,
                Utils.createUUID()
        );
        insertGameRoom(gr);
        conns.put(username, gr.UUID);
        return gr.UUID;
    }

    public void changeHost(String uuid, String newHost) throws InterruptedException {
        GameRoom gr = getGameRoom(uuid);
        gr.userHost = newHost;
        insertGameRoom(gr);
    }

    public boolean isAdmin(String uuid, String username) throws InterruptedException {
        GameRoom gr = getGameRoom(uuid);
        return gr.userHost.equals(username);
    }

    public void addConnection(String username, String uuid) throws InterruptedException {
        conns.put(username, uuid);
    }

    public void removeConnection(String username, String uuid) throws InterruptedException {
        conns.getp(new ActualField(username), new ActualField(uuid));
    }

    public void closeRoom(String uuid) throws InterruptedException {
        getGameRoom(uuid);
    }

    public void queryAllRooms() throws InterruptedException {
        var allRooms = s.queryAll(new FormalField(String.class),
                new FormalField(String.class),
                new FormalField(Double.class)
        );

        for (var q : allRooms) {
            GameRoom gr = new GameRoom(q);
            System.out.println(gr);
            System.out.println("  >> Connections:");
            var allConns = conns.queryAll( new FormalField(String.class),
                    new ActualField(gr.UUID));
            for (var c : allConns)
                System.out.println("    >> " + c[0]);
        }
    }

}
