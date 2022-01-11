package MainServer.UserMgmt;

import MainServer.Utils;
import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.SequentialSpace;
import org.jspace.Space;

public class UserRepo {
    Space s;

    public UserRepo() {
        this.s = new SequentialSpace();
    }

    private void insertUser(User u) throws InterruptedException {
        s.put(u.username, u.UUID, u.noOfWins, u.timestamp, u.isLoggedIn);
    }

    private User getUser(String username) throws InterruptedException {
        Object[] q = s.getp(new ActualField(username),
                new FormalField(String.class),
                new FormalField(Integer.class),
                new FormalField(Double.class),
                new FormalField(Boolean.class));
        return new User(q);
    }

    public String create(String username) throws Exception {
        if (exists(username))
            throw new Exception("User already exists");

        String UUID = Utils.createUUID();
        User u = new User(
                username,
                UUID
        );

        insertUser(u);
        return UUID;
    }

    public void login(String username) throws Exception {
        User u = getUser(username);
        if (u.isLoggedIn)
            throw new Exception("User already logged in!");
        // if not logged in, log in
        u.isLoggedIn = true;
        insertUser(u);
    }

    public void logout(String username) throws Exception {
        User u = getUser(username);
        if (!u.isLoggedIn)
            throw new Exception("User is not logged in!");
        // if logged in, log out
        u.isLoggedIn = false;
        insertUser(u);
    }

    public boolean exists(String username) throws InterruptedException {
        Object[] q = s.queryp(new ActualField(username),
                 new FormalField(String.class),
                 new FormalField(Integer.class),
                 new FormalField(Double.class),
                 new FormalField(Boolean.class));
        return q != null;
    }

    public void queryAllUsers() throws InterruptedException {
        var allUsers = s.queryAll(new FormalField(String.class),
                new FormalField(String.class),
                new FormalField(Integer.class),
                new FormalField(Double.class),
                new FormalField(Boolean.class));

        for (var q : allUsers)
            System.out.println(new User(q));
    }
}
