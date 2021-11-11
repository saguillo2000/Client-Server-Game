import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import org.apache.commons.io.FileUtils;
import utils.ServerLog;
import utils.TramaUtilsLog;

public class Server {

  public static void main(String[] args) {

    ServerSocket serverSocket = null;
    Socket socketClient1, socketClient2;
    int port, mode;
    HashMap<String, String> options = new HashMap<>();

    argumentsChecker(args);

    for (int i = 0; i < args.length; i = i + 2) {
      options.put(args[i], args[i + 1]);
    }

    port = Integer.parseInt(options.get("-p"));
    mode = Integer.parseInt(options.get("-m"));

    try {
      File serverLog = new File("logsServer");
      if (!serverLog.mkdir()) {
        FileUtils.cleanDirectory(serverLog);
      }
      serverSocket = new ServerSocket(port);

      while (true) {
        socketClient1 = serverSocket.accept();

        if (mode == 1) {
          Thread newGame = new Thread(new Game(socketClient1, mode,
              new TramaUtilsLog(socketClient1.getInputStream(),
                  socketClient1.getOutputStream())));
          newGame.start();
        } else {
          socketClient2 = serverSocket.accept();

          Thread newGame = new Thread(new Game(socketClient1, socketClient2, mode,
              new TramaUtilsLog(socketClient1.getInputStream(),
                  socketClient1.getOutputStream()),
              new TramaUtilsLog(socketClient2.getInputStream(), socketClient2.getOutputStream())));

          newGame.start();
        }
      }

    } catch (IOException ex) {
      try {
        ServerLog.writeLog(2, "ERROR", ex.getMessage());
        assert serverSocket != null;
        serverSocket.close();
        System.out.println("SERVER: ¡Hasta la vista, camaradas! Se despide el capitan ");
      } catch (IOException e) {
        System.out.println("Failed to close socket connection cause server is null.");
      }
    }

  }

  /**
   * Metodo para comprobar los argumentos introducidos y actuar en función de ellos
   *
   * @param args Argumentos introducidos
   */
  private static void argumentsChecker(String[] args) {
    if (args.length == 0 || args.length > 4) {
      System.exit(0);
    }

    if (args[0].equals("-h")) {
      System.out.println("Us: java –jar server.jar -p <port> [-m 1|2]");
      System.exit(0);
    }

    if (!args[0].equals("-p") && !args[2].equals("-m")) {
      System.out.println("Error en els parametres, per consultar posa -h");
      System.exit(1);
    }
    try {
      int port = Integer.parseInt(args[1]);
      int mode = Integer.parseInt(args[3]);

      if (port < 0 || port > 65535) {
        System.out.println("No has introducido un puerto valido del 0 al 65535");
        System.exit(0);
      }

      if (mode != 1 && mode != 2) {
        System.out.println("Modalidad de juego mal especificada");
        System.exit(0);
      }

    } catch (NumberFormatException ex) {
      System.out.println("No se ha introducido un número en uno de los campos");
      System.exit(0);
    }

  }

}

