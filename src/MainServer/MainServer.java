package MainServer;

import MainServer.Chat.ChatRepo;
import MainServer.GameRoom.GameRoom;
import MainServer.GameRoom.GameRoomRepo;

import MainServer.GameSession.Combat.CombatEngine;
import MainServer.GameSession.GameSession;
import MainServer.GameSession.Test.TestProducer;
import MainServer.UserMgmt.User;
import MainServer.UserMgmt.UserRepo;
import common.Constants;
import org.jspace.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MainServer {
    public static UserRepo users;
    public static GameRoomRepo gameRooms;
    public static HashMap<String, GameSession> gameSessions = new HashMap<>();
    public static HashMap<String, CombatEngine> gameCombatEngines = new HashMap<>();



    public static void main(String[] args) throws Exception {
        users = new UserRepo();
        gameRooms = new GameRoomRepo();

        SpaceRepository chatChannels = new SpaceRepository();
        SpaceRepository gameSessionRepo = new SpaceRepository();
        SpaceRepository combatRepo = new SpaceRepository();

        chatChannels.addGate("tcp://" + Constants.IP_address + ":4242/?conn");


        var gl = new GlobalListener();
        gl.setUsers(users);
        gl.setGameRooms(gameRooms);
        gl.setChatChannels(chatChannels);
        gl.setGameSessionRepository(gameSessionRepo);
        gl.setCombatEngineRepository(combatRepo);
        gl.setGameSessions(gameSessions);
        gl.setCombatEngines(gameCombatEngines);
        new Thread(gl).start();

        var crl = new ChatRoomListener("globalChat");
        crl.setRooms(gameRooms);
        crl.setChat(chatChannels);
        new Thread(crl).start();
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

    Object[] messageInput = new Object[4];

    public void run() {
        try {
            while (rooms.exists(roomUUID)) {
                messageInput = chat.query(new FormalField(String.class),
                        new ActualField(roomUUID),
                        new FormalField(String.class),
                        new FormalField(Double.class),
                        new FormalField(String.class));

                for (int i = 0; i < rooms.queryConnections(roomUUID).size(); i++) {
                    chat.get(new ActualField(messageInput[0]),new ActualField(messageInput[3]));
                    System.out.println("Got read token from client");
                }
                chat.get(new FormalField(String.class),
                        new ActualField(roomUUID),
                        new FormalField(String.class),
                        new ActualField(messageInput[3]),
                        new FormalField(String.class));
                System.out.println("Deleted chatMessage in room " + roomUUID);

                /*input = chat.query(new FormalField(String.class),
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
                        new FormalField(String.class));*/
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
    private SpaceRepository gameSessionRepository;
    private SpaceRepository combatEngineRepo;
    private HashMap<String,GameSession> gameSessions;
    private HashMap<String,CombatEngine> gameCombatEngines;


    public void setChatChannels(SpaceRepository chatChannels) {
        this.chatChannels = chatChannels;
    }

    public void setGameRooms(GameRoomRepo gameRooms) {
        this.gameRooms = gameRooms;
    }

    public void setGameSessionRepository(SpaceRepository gameSessionRepo) {
        this.gameSessionRepository = gameSessionRepo;
    }

    public void setCombatEngineRepository(SpaceRepository combatEngineRepo) {
        this.combatEngineRepo = combatEngineRepo;
    }

    public void setUsers(UserRepo users) {
        this.users = users;
    }

    public void setGameSessions(HashMap<String,GameSession> gameSessions){
        this.gameSessions = gameSessions;
    }

    public void setCombatEngines(HashMap<String, CombatEngine> gameCombatEngines) {
        this.gameCombatEngines = gameCombatEngines;
    }

    public void run() {
        SpaceRepository mainChannels = new SpaceRepository();
        SequentialSpace userToServer = new SequentialSpace();
        SequentialSpace serverToUser = new SequentialSpace();
        mainChannels.add("userToServer",userToServer);
        mainChannels.add("serverToUser",serverToUser);
        //mainChannels.add("globalChat", chats.globalChat);

        mainChannels.addGate("tcp://" + Constants.IP_address+ ":6969/?conn");

        while(true) {
            Object[] userInput = new Object[0];
            try {
                userInput = userToServer.get(new FormalField(String.class),
                        new FormalField(String.class),
                        new FormalField(String.class),
                        new FormalField(String.class),
                        new FormalField(Integer.class)
                );

                if (userInput[1].equals("login")) {
                    String UUID = users.create((String) userInput[0]);
                    gameRooms.addConnection(UUID,"globalChat");
                    serverToUser.put(userInput[0], "ok", UUID);
                    System.out.println("Login: Sent response to client");

                } else if (userInput[1].equals("create")) {
                    String[] roomInfo = gameRooms.create((String) userInput[0]);
                    String UUID = roomInfo[0];
                    String roomNr = roomInfo[1];
                    serverToUser.put(userInput[0],"ok", UUID, roomNr);
                    gameRooms.removeConnection((String) userInput[0], "globalChat");

                    System.out.println("Create Room: Server response sent");

                    //Create chatroom for game
                    var crl = new ChatRoomListener(UUID);
                    crl.setChat(chatChannels);
                    crl.setRooms(gameRooms);
                    new Thread(crl).start();

                } else if (userInput[1].equals("join")) {
                    if (gameRooms.getUUID((String) userInput[2]).equals("The given room name was not found") || gameRooms.queryConnections(gameRooms.getUUID((String) userInput[2])).contains(userInput[1])) {
                        serverToUser.put(userInput[0], "Unable to join room", "");
                        System.out.println("Join Room: Server error response sent");
                    } else {

                        gameRooms.removeConnection((String) userInput[0],"globalChat");
                        gameRooms.addConnection((String) userInput[0], gameRooms.getUUID((String) userInput[2]));

                        serverToUser.put(userInput[0], "ok", gameRooms.getUUID((String) userInput[2]));
                        System.out.println("Join Room: Server response sent");
                    }
                } else if(userInput[1].equals("start")){
                    List<String> currPLayers = gameRooms.queryConnections((String) userInput[2]);
                    String roomUUID = (String) userInput[2];

                    //Get player names
                    List<String> currPlayerNames = new ArrayList<>();
                    for(String playerUUID : currPLayers){
                        currPlayerNames.add( users.queryUser(playerUUID).username);
                        System.out.println(users.queryUser(playerUUID).username);
                    }


                    if( !gameSessions.containsKey((String) userInput[2]) && !gameRooms.isHost(roomUUID, (String) userInput[0])){
                        //Person is not host so they cant start the game
                        serverToUser.put(userInput[0], "not ok", currPLayers, currPlayerNames);
                    }else{
                        serverToUser.put(userInput[0], "ok", currPLayers, currPlayerNames);

                        //Initialize game session
                        SequentialSpace conns = new SequentialSpace();

                        for(String playerUUID : currPLayers){
                            conns.put(playerUUID);
                        }

                        CombatEngine combatEngine = new CombatEngine(conns, combatEngineRepo, roomUUID);
                        (new Thread(combatEngine)).start();

                        gameCombatEngines.put((String) userInput[2], combatEngine);

                        GameSession gameSession   =  new GameSession(this.gameSessionRepository, roomUUID, conns);
                        gameSessions.put((String) userInput[2], gameSession);
                    }
                } else if (userInput[1].equals("leave")) {
                    List connections = gameRooms.queryConnections((String) userInput[2]);
                    if (connections.contains(userInput[0])) {
                        if (gameRooms.isHost((String) userInput[2],(String) userInput[0])) {
                            if (connections.size() == 1) {
                                if(gameCombatEngines.containsKey((String) userInput[2])) {
                                    gameCombatEngines.get((String) userInput[2  ]).deleteCombatEngine();
                                    gameSessions.get((String) userInput[2]).deleteGameSession();
                                }
                                gameRooms.close((String) userInput[2]);

                                System.out.println("Host deleted room");

                            } else {
                                gameRooms.removeConnection((String) userInput[0], (String) userInput[2]);
                                String newHost = gameRooms.queryConnections((String) userInput[2]).get(0);
                                gameRooms.changeHost((String) userInput[2], newHost);
                                System.out.println("Client left room and new user was assigned host role");
                            }
                        } else {
                            gameRooms.removeConnection((String) userInput[2], (String) userInput[0]);
                            System.out.println("Client left room");
                        }
                        gameRooms.addConnection((String) userInput[0],"globalChat");
                    } else {
                        System.out.println("The client cannot leave game room because it is not connected");
                    }
                } else if(userInput[1].equals("gameOver")){
                    GameRoom currGameRoom = gameRooms.getGameRoom((String) userInput[2]);

                    int numPLayersInCurrRoom = gameRooms.queryConnections((String) userInput[2]).size();

                    //One more person is dead
                    currGameRoom.numDead++;

                    //Add their score
                    currGameRoom.addScore((String) userInput[3], (Integer) userInput[4]);

                    //Check if only one person is alive
                    if(currGameRoom.numDead == numPLayersInCurrRoom){ //REMEMBER TO SAY -1
                        //Game can be ended
                        HashMap<String,Integer> scores = currGameRoom.getScores();

                        //Insert game room again
                        gameRooms.insertGameRoom(currGameRoom);

                        serverToUser.put(userInput[0], "ok", userInput[2], scores);

                        System.out.println("Sending game over response");

                        //DELETE GAME SESSION

                        gameCombatEngines.get((String) userInput[2]).deleteCombatEngine();
                        gameSessions.get((String) userInput[2]).deleteGameSession();
                        gameRooms.close((String) userInput[2]);

                        Thread.sleep(1000);

                        Object[] serverResponse = serverToUser.get(
                                new ActualField((String) userInput[0]),
                                new ActualField("ok"),
                                new ActualField( (String) userInput[2]),
                                new FormalField(Object.class)
                        );


                    } else {
                        gameRooms.insertGameRoom(currGameRoom);
                        serverToUser.put(userInput[0], "game not over", userInput[2], currGameRoom.getScores());
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