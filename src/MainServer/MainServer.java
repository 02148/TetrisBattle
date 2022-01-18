package MainServer;

import MainServer.Chat.ChatRepo;
import MainServer.GameRoom.GameRoomRepo;

import MainServer.GameSession.Test.TestProducer;
import MainServer.UserMgmt.UserRepo;
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
        chatChannels.addGate("tcp://10.209.231.86:4242/?conn");


        var gl = new GlobalListener();
        gl.setUsers(users);
        gl.setGameRooms(gameRooms);
        gl.setChatChannels(chatChannels);
        new Thread(gl).start();

        var crl = new ChatRoomListener("globalChat");
        crl.setRooms(gameRooms);
        crl.setChat(chatChannels);
        new Thread(crl).start();

        TestProducer.main(new String[]{}); // Delete me pls 🥵
    }
}

//Deletes old messages in Space
class ChatRoomListener implements Runnable {
    private GameRoomRepo rooms;
    private SpaceRepository chatChannels;
    private SequentialSpace chat;
    private String roomUUID;

    public void setRooms(GameRoomRepo rooms) {
        this.rooms = rooms;
    }

    public void setChat(SpaceRepository chatChannels) {
        this.chatChannels = chatChannels;
        this.chat = new SequentialSpace();
        this.chatChannels.add(roomUUID,chat);
    }

    public ChatRoomListener(String roomUUID) {
        this.roomUUID = roomUUID;
    }

    Object[] input = new Object[4];

    public void run() {
        try {
            while (rooms.exists(roomUUID)) {

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
            }
            chatChannels.remove(roomUUID);
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

        mainChannels.addGate("tcp://10.209.231.86:6969/?conn");

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
                    crl.setChat(chatChannels);
                    crl.setRooms(gameRooms);
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
                } else if (userInput[1].equals("leave")) {
                    List connections = gameRooms.queryConnections((String) userInput[2]);
                    if (connections.contains(userInput[0])) {
                        if (gameRooms.isHost((String) userInput[2],(String) userInput[0])) {
                            if (connections.size() == 1) {
                                gameRooms.close((String) userInput[2]);
                                System.out.println("Host deleted room");
                            } else {
                                gameRooms.removeConnection((String) userInput[2], (String) userInput[0]);
                                String newHost = gameRooms.queryConnections((String) userInput[2]).get(0);
                                gameRooms.changeHost((String) userInput[2], newHost);
                                System.out.println("Client left room and new user was assigned host role");
                            }
                        } else {
                            gameRooms.removeConnection((String) userInput[2], (String) userInput[0]);
                            System.out.println("Client left room");
                        }
                    } else {
                        System.out.println("The client cannot leave game room because it is not connected");
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