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
        chat = new ChatRepo();


        //SpaceRepository userChannels = new SpaceRepository();
        SpaceRepository rooms = new SpaceRepository();

        //users.create("niels");
        //users.create("emilie");
        //users.create("magn");

        //users.login("niels");
        //users.login("magn");
        //users.logout("magn");
        //users.queryAllUsers();

        //String roomId1 = gameRooms.create("niels");
        //String roomId2 = gameRooms.create("emilie");
        //String roomId3 = gameRooms.create("niels");

        //gameRooms.addConnection("emilie", roomId1);
        //gameRooms.addConnection("magn", roomId2);
        //gameRooms.addConnection("magn", roomId3);

        //gameRooms.close(roomId2);
        //gameRooms.close(roomId3);

        //System.out.println("\nBEFORE");
        //gameRooms.queryAllRooms();
        //gameRooms.removeConnection("niels", roomId1);
        //gameRooms.addConnection("magn", roomId1);
        //gameRooms.removeConnection("emilie", roomId1);
        //System.out.println("\nAFTER");
        //gameRooms.queryAllRooms();


        var gl = new GlobalListener();
        gl.setUsers(users);
        gl.setGameRooms(gameRooms);
        new Thread(gl).start();

        var cl = new GlobalChatListener();
        cl.setChats(chat);
        cl.setUsers(users);
        new Thread(cl).start();
    }
}

class GlobalChatListener implements Runnable {
    private ChatRepo chats;
    private UserRepo users;

    public void setUsers(UserRepo users) {
        this.users = users;
    }

    public void setChats(ChatRepo chats){this.chats = chats;}

    public void run() {
        SpaceRepository chatChannels = new SpaceRepository();
        SequentialSpace globalChat = (SequentialSpace) chats.globalChat;
        chatChannels.add("globalChat",globalChat);

        chatChannels.addGate("tcp://localhost:6971/?conn");
        System.out.println("GlobalChatListner: Chat setup complete");
        while(true) {
            Object[] userInput = new Object[0];
            try {

                userInput = globalChat.get(new FormalField(String.class),
                        new FormalField(String.class),
                        new FormalField(String.class),
                        new FormalField(String.class),
                        new FormalField(String.class));

                if (userInput[1].equals("globalChat")){
                    SequentialSpace personalChat;

                    //Create personal chatSpace for each user
                    if(users.getPersonalChatSpace((String) userInput[0]) == null){
                        users.setPersonalChatSpace((String) userInput[0]);
                        personalChat = (SequentialSpace) users.getPersonalChatSpace((String) userInput[0]);
                        chatChannels.add((String) userInput[0],personalChat);
                        System.out.println("Personal chat created" );

                    } else {
                        personalChat = (SequentialSpace) users.getPersonalChatSpace((String) userInput[0]);
                    }

                    ChatMessage chat = chats.createMessage((String) userInput[0], (String) userInput[2],"globalChat");
                    //Added to the global chat
                    globalChat.put(userInput[0],"recived", (String) userInput[2],chat.timeStamp, (String) userInput[4]);

                    //Add to all other current user chatSpaces
                    sendToAllUsers((String) userInput[2], chat.timeStamp, (String) userInput[4]);

                    System.out.println("Send global chat: Server response sent");


                } else if(userInput[1].equals("gameRoomChat")){
                    Space newChatRoom = chats.createChatRoom((String) userInput[3]);
                    if(newChatRoom != null){
                        chatChannels.add((String) userInput[3], newChatRoom);
                        System.out.println("room UUID: " + (String) userInput[3]);
                    }
                    ChatMessage chat = chats.createMessage((String) userInput[0], (String) userInput[2], (String) userInput[3]);
                    globalChat.put(userInput[0],"ok",(String) userInput[2],chat.timeStamp);
                    System.out.println("Send local chat: Server response sent");
                } else if( userInput[1].equals("setupGlobalChat")){
                    SequentialSpace personalChat;
                    //Create personal chatSpace for each user
                    if(users.getPersonalChatSpace((String) userInput[0]) == null){
                        users.setPersonalChatSpace((String) userInput[0]);
                        personalChat = (SequentialSpace) users.getPersonalChatSpace((String) userInput[0]);
                        chatChannels.add((String) userInput[0],personalChat);
                        System.out.println("Personal chat created" );
                    } else {
                        personalChat = (SequentialSpace) users.getPersonalChatSpace((String) userInput[0]);
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private void sendToAllUsers(String message, double timeStamp, String username) throws InterruptedException {
        List<Object[]> userList = users.queryAllUsers();
        for(Object[] p : userList){
            String userUUID = (String) p[0];
            users.getPersonalChatSpace(userUUID).put(message, timeStamp, username);
            System.out.println("Chat sent to personal chat space of user: " + username);
        }
    }
}

class GlobalListener implements Runnable {
    private UserRepo users;
    private GameRoomRepo gameRooms;
    private ChatRepo chats;

    public void setGameRooms(GameRoomRepo gameRooms) {
        this.gameRooms = gameRooms;
    }

    public void setUsers(UserRepo users) {
        this.users = users;
    }

    public void setChats(ChatRepo chats){
        this.chats = chats;
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