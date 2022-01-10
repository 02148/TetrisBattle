package MainServer;

import MainServer.Chat.ChatRepo;
import MainServer.GameRoom.GameRoomRepo;
import MainServer.UserMgmt.UserRepo;
import org.jspace.*;

import java.rmi.Remote;

public class MainServer {
    public static void main(String[] args) throws Exception {
        UserRepo users = new UserRepo();
        GameRoomRepo gameRooms = new GameRoomRepo();
        ChatRepo globalChat = new ChatRepo();

        //SpaceRepository userChannels = new SpaceRepository();
        SpaceRepository rooms = new SpaceRepository();

        users.create("niels");
        users.create("emilie");
        users.create("magn");

        users.login("niels");
        users.login("magn");
        users.logout("magn");
        //users.queryAllUsers();

        String roomId1 = gameRooms.create("niels");
        String roomId2 = gameRooms.create("emilie");
        String roomId3 = gameRooms.create("niels");

        gameRooms.addConnection("emilie", roomId1);
        gameRooms.addConnection("magn", roomId2);
        gameRooms.addConnection("magn", roomId3);

        gameRooms.close(roomId2);
        gameRooms.close(roomId3);

        //System.out.println("\nBEFORE");
        //gameRooms.queryAllRooms();
        gameRooms.removeConnection("niels", roomId1);
        gameRooms.addConnection("magn", roomId1);
        gameRooms.removeConnection("emilie", roomId1);
        //System.out.println("\nAFTER");
        //gameRooms.queryAllRooms();

        var gl = new GlobalListener();
        gl.setUsers(users);
        gl.setGameRooms(gameRooms);
        new Thread(gl).start();
    }
}

class GlobalListener implements Runnable {
    private UserRepo users;
    private GameRoomRepo gameRooms;

    public void setGameRooms(GameRoomRepo gameRooms) {
        this.gameRooms = gameRooms;
    }

    public void setUsers(UserRepo users) {
        this.users = users;
    }

    public GlobalListener() {
    }

    public void run() {
        SpaceRepository mainChannels = new SpaceRepository();
        SequentialSpace userToServer = new SequentialSpace();
        SequentialSpace serverToUser = new SequentialSpace();
        mainChannels.add("userToServer",userToServer);
        mainChannels.add("ServerToUser",serverToUser);
        mainChannels.addGate("tcp://localhost:6969/?conn");

        while(true) {
            Object[] userInput = new Object[0];
            try {
                userInput = userToServer.get(new FormalField(String.class), new FormalField(String.class), new FormalField(String.class));
                if (userInput[1] == "login") {
                    String UUID = users.create((String) userInput[0]);
                    serverToUser.put(userInput[0], "ok", UUID);
                } else if (userInput[1] == "create") {
                    String UUID = gameRooms.create((String) userInput[0]);
                    serverToUser.put(userInput[0],"ok", UUID);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

// TODO
// creates thread for each type of repo
// each thread contains a RemoteSpace and a listener for incoming connections

/* TODO DONE
// create user
// "login" user
// check if user exists
// check if user is logged in
// TODO
// Decide if user should be identified using only username, or if uuid should be passed on as well
*/

// TODO DONE
// create room
// host room
// establish connections to room -> interaction based coordination
// check if user is host of room
// assign host role and rights to other user if current host leaves
// close room when everyone has left
// TODO
//

// TODO
// create game sessions
// establish connections to game sessions -> stream based coordination
// set up data streams both ways