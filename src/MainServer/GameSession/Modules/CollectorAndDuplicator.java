package MainServer.GameSession.Modules;

import org.jspace.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

public class CollectorAndDuplicator implements Runnable {
  private Space in, conns;
  private HashMap<String, Space> out;
  private String type;
  private int T;

  public CollectorAndDuplicator(Space in, HashMap<String, Space> out, Space conns, String type) throws Exception {
    this.in = in;
    this.out = out;
    this.conns = conns;
    this.type = type;
    this.T = 100;
  }

  @Override
  public void run() {
    while (true) {
      try {
        if(this.type.equals("delta")) {
          evaluateDeltaPackage();
        } else if(this.type.equals("full")) {
          evaluateFullPackage();
        } else{
          break;
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  public void evaluateDeltaPackage() throws Exception{
    var curConns = conns.queryAll(new FormalField(String.class));

    Object[] raw_data_delta = this.in.get(
            new FormalField(String.class),
            new FormalField(Double.class),
            new FormalField(Object.class));
    if (raw_data_delta == null || raw_data_delta[0] == null) {
      throw new Exception("DUPLICATOR >> No new delta data");
    }

    String packageUserUUID = (String) raw_data_delta[0];

    for (var c : curConns) {
      String userUUID = (String) c[0];

      if (out.containsKey(userUUID) && !Objects.equals(userUUID, packageUserUUID))
        out.get(userUUID).put(raw_data_delta);
    }
  }

  public void evaluateFullPackage() throws  Exception {
    var curConns = conns.queryAll(new FormalField(String.class));
                                            // userUUID                           Timestamp                       Bitset
    Object[] raw_data_full = this.in.get(new FormalField(Object.class), new FormalField(Object.class), new FormalField(Object.class),
                                         new FormalField(Object.class), new FormalField(Object.class), new FormalField(Object.class),
                                         new FormalField(Object.class), new FormalField(Object.class), new FormalField(Object.class),
                                         new FormalField(Object.class), new FormalField(Object.class), new FormalField(Object.class),
                                         new FormalField(Object.class), new FormalField(Object.class), new FormalField(Object.class),
                                         new FormalField(Object.class), new FormalField(Object.class), new FormalField(Object.class),
                                         new FormalField(Object.class), new FormalField(Object.class), new FormalField(Object.class),
                                         new FormalField(Object.class), new FormalField(Object.class), new FormalField(Object.class));
    if (raw_data_full == null || raw_data_full[0] == null) {
      throw new Exception("DUPLICATOR >> No new full data");
    }

    for (var c : curConns) {
      String userUUID = (String) c[0];

      if (out.containsKey(userUUID))
        out.get(userUUID).put(raw_data_full);
    }
  }
}
