package MainServer;

import Client.ChatListener;
import MainServer.Chat.ChatMessage;
import MainServer.Chat.ChatRepo;
import MainServer.GameRoom.GameRoomRepo;
import MainServer.UserMgmt.User;
import MainServer.UserMgmt.UserRepo;
import javafx.beans.binding.ObjectExpression;
import org.jspace.*;

import java.util.List;

public class MainServer {
    public static UserRepo users;
    public static GameRoomRepo gameRooms;
    public static ChatRepo chat;


    public static void main(String[] args) throws Exception {
        users = new UserRepo();
        gameRooms = new GameRoomRepo();

        SpaceRepository chatChannels = new SpaceRepository();
        SequentialSpace globalChat = new SequentialSpace();
        chatChannels.add("globalChat",globalChat);
        chatChannels.addGate("tcp://localhost:4242/?conn");

        var gl = new GlobalListener();
        gl.setUsers(users);
        gl.setGameRooms(gameRooms);
        gl.setChatChannels(chatChannels);
        new Thread(gl).start();

        var crl = new ChatRoomListener("globalChat");
        crl.setChat(globalChat);
        new Thread(crl).start();
    }
}

//Deletes old messages in Space
class ChatRoomListener implements Runnable {
    private SequentialSpace chat;
    private String roomUUID;

    public void setChat(SequentialSpace chat){this.chat = chat;}

    public ChatRoomListener(String roomUUID) {
        this.roomUUID = roomUUID;
    }

    Object[] input = new Object[4];
    public void run() {
        while (true)
        try {
            input = chat.query(new FormalField(String.class),
                    new ActualField(roomUUID),
                    new FormalField(String.class),
                    new FormalField(Double.class),
                    new FormalField(String.class));
            if ((Double) input[3] > Utils.getCurrentTimestamp() - 100) {
                Thread.sleep(100);
            }
            System.out.println("Deleted chatMessage in room " + roomUUID);
            chat.get(new FormalField(String.class),
                    new ActualField(roomUUID),
                    new FormalField(String.class),
                    new FormalField(Double.class),
                    new FormalField(String.class));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class GlobalListener implements Runnable {
    private UserRepo users;
    private GameRoomRepo gameRooms;
    private SpaceRepository chatChannels;

    public void setChatChannels(SpaceRepository chatChannels) {
        this.chatChannels = chatChannels;
    }

    public void setGameRooms(GameRoomRepo gameRooms) {
        this.gameRooms = gameRooms;
    }

    public void setUsers(UserRepo users) {
        this.users = users;
    }

    public void run() {
        SpaceRepository mainChannels = new SpaceRepository();
        SequentialSpace userToServer = new SequentialSpace();
        SequentialSpace serverToUser = new SequentialSpace();
        mainChannels.add("userToServer",userToServer);
        mainChannels.add("serverToUser",serverToUser);
        //mainChannels.add("globalChat", chats.globalChat);

        mainChannels.addGate("tcp://localhost:6969/?conn");

        while(true) {
            Object[] userInput = new Object[0];
            try {
                userInput = userToServer.get(new FormalField(String.class),
                        new FormalField(String.class),
                        new FormalField(String.class)
                );

                if (userInput[1].equals("login")) {
                    String UUID = users.create((String) userInput[0]);
                    serverToUser.put(userInput[0], "ok", UUID);
                    System.out.println("Login: Sent response to client");

                } else if (userInput[1].equals("create")) {
                    String UUID = gameRooms.create((String) userInput[0]);
                    serverToUser.put(userInput[0],"ok", UUID);
                    System.out.println("Create Room: Server response sent");

                    //Create chatroom for game
                    var crl = new ChatRoomListener(UUID);
                    SequentialSpace chat = new SequentialSpace();
                    crl.setChat(chat);
                    new Thread(crl).start();

                } else if (userInput[1].equals("join")) {
                    if (gameRooms.queryConnections((String) userInput[2]).contains(userInput[1])) {
                        serverToUser.put(userInput[0], "Already connected", "");
                        System.out.println("Join Room: Server error response sent");
                    } else {
                        gameRooms.addConnection((String) userInput[0], (String) userInput[2]);
                        serverToUser.put(userInput[0], "ok", gameRooms.getUUID((String) userInput[2]));
                        System.out.println("Join Room: Server response sent");
                    }
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