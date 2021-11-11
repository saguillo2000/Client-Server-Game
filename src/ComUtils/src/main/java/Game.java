import java.io.IOException;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import utils.ServerLog;
import utils.TramaUtilsLog;

public class Game implements Runnable {

  private final Socket socketClient1;
  private final int mode;
  private final TramaUtilsLog tramaComs;
  private final GameUtils gameUtils;
  private Socket socketClient2 = null;
  private TramaUtilsLog tramaComs2 = null;

  /**
   * Metodo constructor de la clase Game para 1 jugador
   *
   * @param socketClient1 Socket del cliente
   * @param mode          Modo de juego
   * @param trama_utils   Instancia de TramaUtils
   */
  public Game(Socket socketClient1, int mode, TramaUtilsLog trama_utils) {
    this.socketClient1 = socketClient1;
    this.mode = mode;
    this.tramaComs = trama_utils;
    this.gameUtils = new GameUtils();
  }

  /**
   * Metodo constructor de la clase Game para 2 jugadores
   *
   * @param socketClient1 Socket del cliente 1
   * @param socketClient2 Socket del cliente 2
   * @param mode          Modo de juego
   * @param tramaUtils1   Instancia de TramaUtils del primer jugador
   * @param tramaUtils2   Instancia de TramaUtils del segundo jugador
   */
  public Game(Socket socketClient1, Socket socketClient2, int mode,
      TramaUtilsLog tramaUtils1, TramaUtilsLog tramaUtils2) {
    this.socketClient1 = socketClient1;
    this.socketClient2 = socketClient2;
    this.tramaComs = tramaUtils1;
    this.tramaComs2 = tramaUtils2;
    this.mode = mode;
    this.gameUtils = new GameUtils();
  }

  /**
   * Metodo para iniciar el run del juego segun su modo
   */
  public void run() {

    if (mode == 1) {
      try {
        this.logicGameOnePlayer();
        socketClient1.setSoTimeout(30000);
      } catch (IOException | NoSuchAlgorithmException exception) {
        try {
          ServerLog.writeLog(2, "ERROR", exception.getMessage());
          if (exception.getMessage().contains("Broken Pipe")) {
            System.out.println("CLIENT: ¡Hasta la vista, capitan!");
          }
          this.socketClient1.close();
        } catch (IOException e) {
          System.out.println("Failed to close socket connection.");
        }
      }
    } else {
      try {
        this.logicGameTwoPlayer();
        socketClient1.setSoTimeout(30000);
        socketClient2.setSoTimeout(30000);
      } catch (IOException | NoSuchAlgorithmException exception) {
        try {
          ServerLog.writeLog(2, "ERROR", exception.getMessage());
          this.socketClient1.close();
          this.socketClient2.close();
        } catch (IOException e) {
          System.out.println("Failed to close socket connection.");
        }
      }
    }
  }

  /**
   * Metodo de logica de juevo Client VS Server
   *
   * @throws IOException              Excepcion debida a un fallo de escritura en el Data Output
   *                                  Stream
   * @throws NoSuchAlgorithmException Excepcion debida a que no detecte el algoritmo de
   *                                  encriptacion
   */
  public void logicGameOnePlayer() throws IOException, NoSuchAlgorithmException {

    Jugador server = new Jugador();
    ProtocolServerUtils protocol = new ProtocolServerUtils(this.tramaComs);
    String nameServer, nomRival, comeback, insult;
    int idRival, secretRival, round_Mine, round_Rival, duelsRival;

    while (true) {

      nameServer = this.gameUtils.getRandomPirateName();
      if (!nameServer.equals(server.getName())) {
        int indexRandom = (int) (Math.random() * 2000) + 1;
        server = new Jugador(nameServer, indexRandom);
      }

      String[] id_and_name = protocol.helloProtocol(server.getName(), server.getId());
      idRival = Integer.parseInt(id_and_name[0]);
      nomRival = id_and_name[1];

      duelsRival = 0;
      boolean ganador = false;

      while (server.getDuels() != 3 && duelsRival != 3) {

        secretRival = protocol.secretProtocol(server.getSecret());

        server.setPriority(this.gameUtils.whoHasPriority(secretRival + server.getSecret(),
            server.getId(), idRival));

        gameUtils.addInsultsComebacks(server);

        round_Mine = 0;
        round_Rival = 0;

        while (round_Mine != 2 && round_Rival != 2) {

          String[] arr = protocol.insultComebackProtocol(server);

          insult = arr[0];
          comeback = arr[1];

          int[] counterInfo = new int[]{round_Mine, round_Rival};

          ganador = gameUtils.whoIsWinner(server, counterInfo, insult, comeback);

          round_Mine = counterInfo[0];
          round_Rival = counterInfo[1];

        }
        if (round_Mine == 2) {
          server.wonDuels();
        } else {
          duelsRival++;
        }
        protocol.shoutProtocol(duelsRival, ganador, nomRival);
      }
    }
  }

  /**
   * Metodo de logica de juego Client VS Client
   *
   * @throws IOException              Excepcion debida a un fallo de escritura en el Data Output
   *                                  Stream
   * @throws NoSuchAlgorithmException Excepcion debida a que no detecte el algoritmo de
   *                                  encriptacion
   */
  public void logicGameTwoPlayer() throws IOException, NoSuchAlgorithmException {

    ProtocolServerUtils protocol = new ProtocolServerUtils(this.tramaComs, this.tramaComs2);
    String comeback, insult;
    int idClient1, idClient2, secretClient1, secretClient2, insultComebackClient1,
        insultComebackClient2, duelsClient1, duelsClient2;

    while (true) {
      String[] tramasHello = protocol.helloProtocol2Players();

      idClient1 = Integer.parseInt(tramasHello[0]);
      idClient2 = Integer.parseInt(tramasHello[1]);

      duelsClient1 = 0;
      duelsClient2 = 0;

      boolean priorityClient1;

      while (duelsClient1 != 3 && duelsClient2 != 3) {

        int[] tramasSecret = protocol.secretProtocol2Players();

        secretClient1 = tramasSecret[0];
        secretClient2 = tramasSecret[1];

        priorityClient1 = gameUtils
            .whoHasPriority(secretClient1 + secretClient2, idClient1, idClient2);

        insultComebackClient1 = 0;
        insultComebackClient2 = 0;

        while (insultComebackClient1 != 2 && insultComebackClient2 != 2) {

          if (priorityClient1) {
            insult = tramaComs.readTrama();
            tramaComs2.writeTrama("INSULT", insult, 0);
            comeback = tramaComs2.readTrama();
            tramaComs.writeTrama("COMEBACK", comeback, 0);

          } else {
            insult = tramaComs2.readTrama();
            tramaComs.writeTrama("INSULT", insult, 0);
            comeback = tramaComs.readTrama();
            tramaComs2.writeTrama("COMEBACK", comeback, 0);
          }

          Jugador temp = new Jugador();
          temp.setPriority(priorityClient1);

          int[] insults_comebacks = new int[]{insultComebackClient1, insultComebackClient2};

          gameUtils.whoIsWinner(temp, insults_comebacks, insult, comeback);
          priorityClient1 = temp.isPriority();

          insultComebackClient1 = insults_comebacks[0];
          insultComebackClient2 = insults_comebacks[1];

        }
        protocol.shoutProtocol2Players();
        if (insultComebackClient1 == 2) {
          duelsClient1++;
        } else {
          duelsClient2++;
        }
      }
    }
  }
}
