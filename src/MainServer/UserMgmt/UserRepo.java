package MainServer.UserMgmt;

import MainServer.Utils;
import org.jspace.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class UserRepo {
    Space s;
    public HashMap<String,Space> localUserChatRepository = new HashMap<>();

    public UserRepo() {
        this.s = new SequentialSpace();

    }


    private void insertUser(User u) throws InterruptedException {
        s.put(u.UUID, u.username, u.noOfWins, u.timestamp, u.isLoggedIn);
    }

    private User getUser(String UUID) throws InterruptedException {
        Object[] q = s.getp(new ActualField(UUID),
                new FormalField(String.class),
                new FormalField(Integer.class),
                new FormalField(Double.class),
                new FormalField(Boolean.class));
        return new User(q);
    }

    public String create(String username) throws Exception {
        //It should be possible for users to have the same username
        /*if (exists(username))
            throw new Exception("User already exists");*/

        String UUID = Utils.createUUID();
        User u = new User(
                username,
                UUID
        );

        insertUser(u);
        return UUID;
    }

    public void login(String UUID) throws Exception {
        User u = getUser(UUID);
        if (u.isLoggedIn)
            throw new Exception("User already logged in!");
        // if not logged in, log in
        u.isLoggedIn = true;
        insertUser(u);
    }

    public void logout(String UUID) throws Exception {
        User u = getUser(UUID);
        if (!u.isLoggedIn)
            throw new Exception("User is not logged in!");
        // if logged in, log out
        u.isLoggedIn = false;
        insertUser(u);
    }
    public void setPersonalChatSpace(String UUID) throws IOException {
        SequentialSpace personalChatSpace = new SequentialSpace();
        localUserChatRepository.put(UUID, personalChatSpace);
    }

    public Space getPersonalChatSpace(String UUID){
        return localUserChatRepository.get(UUID);
    }

    public boolean exists(String UUID) throws InterruptedException {
        Object[] q = s.queryp(new ActualField(UUID),
                 new FormalField(String.class),
                 new FormalField(Integer.class),
                 new FormalField(Double.class),
                 new FormalField(Boolean.class));
        return q != null;
    }

    public List<Object[]> queryAllUsers() throws InterruptedException {
        var allUsers = s.queryAll(new FormalField(String.class),
                new FormalField(String.class),
                new FormalField(Integer.class),
                new FormalField(Double.class),
                new FormalField(Boolean.class));

        for (var q : allUsers)
            System.out.println(new User(q));

        return allUsers;
    }
}
