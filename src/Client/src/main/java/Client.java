import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import utils.TramaUtils;

public class Client {

  public static void main(String[] args) {

    Socket socket = new Socket();
    InetAddress ipServer;
    int port, mode, round_Mine;
    int idRival, duelsRival, round_Rival;
    boolean seguirJugando = true, ganador = false;
    String nomRival, nomPlayer;
    String[] idAndNameRival;
    HashMap<String, String> options = new HashMap<>();
    String[] insultAndComeback;
    Scanner sc = new Scanner(System.in);
    GameUtils gameUtils = new GameUtils();

    argumentsChecker(args);

    for (int i = 0; i < args.length; i = i + 2) {
      options.put(args[i], args[i + 1]);
    }

    port = Integer.parseInt(options.get("-p"));
    mode = Integer.parseInt(options.get("-i"));

    try {
      ipServer = InetAddress.getByName(options.get("-s"));
      socket = new Socket(ipServer, port);
    } catch (IOException e) {
      try {
        socket.close();
      } catch (IOException ioException) {
        ioException.printStackTrace();
      }

    }

    try {
      Jugador client = new Jugador();
      TramaUtils tramaUtil = new TramaUtils(socket.getInputStream(), socket.getOutputStream());
      ProtocolClientUtils protocol = new ProtocolClientUtils(tramaUtil);
      socket.setSoTimeout(30000);

      if (mode == 0) {
        while (seguirJugando) {

          nomPlayer = iniciarPartida(gameUtils, mode);
          client = updateClient(client, nomPlayer);
          client.setDuels(0);

          idAndNameRival = sendHello(protocol, client);
          idRival = Integer.parseInt(idAndNameRival[0]);
          nomRival = idAndNameRival[1];
          duelsRival = 0;

          while (client.getDuels() != 3 && duelsRival != 3) {

            client.setSecret(generateSecret(mode, sc));
            int secretoRiv = sendSecret(protocol, client);
            client.setPriority(gameUtils.whoHasPriority(secretoRiv + client.getSecret(),
                client.getId(), idRival));
            generateInsultsComebacks(client, gameUtils);

            round_Mine = 0;
            round_Rival = 0;

            while (round_Mine != 2 && round_Rival != 2) {

              if (client.isPriority()) {
                System.out.println("\nComienza a jugar el pirata " + client.getName());
                insultAndComeback = protocol.insultProtocol(null, client.listInsults());
                client.learnComeback(insultAndComeback[1]);

              } else {
                System.out.println("\nComienza a jugar el pirata " + nomRival);
                insultAndComeback = protocol.comebackProtocol(null, client.listComeback());
                client.learnInsult(insultAndComeback[0]);
              }

              int[] round_counters = new int[]{round_Mine, round_Rival};
              ganador = gameUtils
                  .whoIsWinner(client, round_counters, insultAndComeback[0], insultAndComeback[1]);
              round_Mine = round_counters[0];
              round_Rival = round_counters[1];
              System.out.println(
                  nomRival + " " + round_Rival + " VS " + round_Mine + " " + client.getName()
                      + "\n");
            }

            if (round_Mine == 2) {
              client.wonDuels();
            } else {
              duelsRival++;
            }
            generarShout(duelsRival, nomRival, ganador, client, protocol);
          }
          seguirJugando = continuarJugando(sc);
          if (!seguirJugando) {
            socket.close();
          }
        }

      } else {

        while (seguirJugando) {

          nomPlayer = iniciarPartida(gameUtils, mode);
          client = updateClient(client, nomPlayer);
          client.setDuels(0);

          idAndNameRival = sendHello(protocol, client);
          idRival = Integer.parseInt(idAndNameRival[0]);
          nomRival = idAndNameRival[1];
          duelsRival = 0;
          Thread.sleep(1000);

          while (client.getDuels() != 3 && duelsRival != 3) {
            client.setSecret(generateSecret(mode, sc));
            Thread.sleep(1000);

            client.setPriority(gameUtils
                .whoHasPriority(sendSecret(protocol, client) + client.getSecret(), client.getId(),
                    idRival));
            generateInsultsComebacks(client, gameUtils);

            round_Mine = 0;
            round_Rival = 0;

            while (round_Mine != 2 && round_Rival != 2) {

              if (client.isPriority()) {
                System.out.println("\nComienza a jugar el pirata " + client.getName());
                insultAndComeback = protocol
                    .insultProtocol(client.getRandomInsult(), client.listInsults());
                client.learnComeback(insultAndComeback[1]);

              } else {
                System.out.println("\nComienza a jugar el pirata " + nomRival);
                insultAndComeback = protocol
                    .comebackProtocol(client.generateComebackRandom(), client.listComeback());
                client.learnInsult(insultAndComeback[0]);
              }
              Thread.sleep(2000);
              int[] round_counters = new int[]{round_Mine, round_Rival};
              ganador = gameUtils
                  .whoIsWinner(client, round_counters, insultAndComeback[0], insultAndComeback[1]);
              round_Mine = round_counters[0];
              round_Rival = round_counters[1];
              System.out.println(
                  nomRival + " " + round_Rival + " VS " + round_Mine + " " + client.getName()
                      + "\n");
            }

            if (round_Mine == 2) {
              client.wonDuels();
            } else {
              duelsRival++;
            }

            generarShout(duelsRival, nomRival, ganador, client, protocol);
            Thread.sleep(2000);
          }
          seguirJugando = continuarJugando(sc);
          if (!seguirJugando) {
            socket.close();
          }
        }
      }
    } catch (SocketTimeoutException ex) {
      try {
        TramaUtils tramaError = new TramaUtils(socket.getInputStream(), socket.getOutputStream());
        tramaError
            .writeTrama("ERROR", "¡Ha pasado tu tiempo, marinero! La partida se ha ahogado.", 0);
      } catch (IOException e) {
        e.printStackTrace();
      }
    } catch (IOException | NoSuchAlgorithmException | InterruptedException ex) {
      ex.printStackTrace();
      System.out.println("ERROR: " + ex.getMessage());
    }
  }

  /**
   * Metodo para generar el protocolo HELLO
   *
   * @param protocol Instancia de la clase ProtocolClientUtils
   * @param client   Jugador actual
   * @return ID y nombre del rival
   * @throws IOException Excepcion debida a un fallo de escritura en el Data Output Stream
   */
  private static String[] sendHello(ProtocolClientUtils protocol, Jugador client)
      throws IOException {
    String[] id_and_name = protocol.helloProtocol(client.getName(), client.getId());
    System.out.println(client.getName() + " vas a combatir contra " + id_and_name[1]);
    return new String[]{id_and_name[0], id_and_name[1]};
  }

  /**
   * Metodo para actualizar el jugador si cambia de nombre al iniciar una partida
   *
   * @param client    Jugador actual
   * @param nomPlayer Nombre antiguo
   * @return Cliente actualizado
   */
  private static Jugador updateClient(Jugador client, String nomPlayer) {
    if (!nomPlayer.equals(client.getName())) {
      int indexRandom = (int) (Math.random() * 2000) + 1;
      return new Jugador(nomPlayer, indexRandom);
    } else {
      return client;
    }
  }

  /**
   * Metodo para generar el protocolo SHOUT
   *
   * @param duelsRival Duelos ganados por el rival
   * @param nomRival   Nombre del rival
   * @param ganador    Flags de quien ha ganado el duelo y/o partida
   * @param client     Jugador actual
   * @param protocol   Instancia de la clase ProtocolClientUtils
   * @throws IOException Excepcion debida a un fallo de escritura en el Data Output Stream
   */
  private static void generarShout(int duelsRival, String nomRival, boolean ganador, Jugador client,
      ProtocolClientUtils protocol) throws IOException {
    protocol.shoutProtocol(duelsRival, ganador, nomRival);
    System.out.println(
        "Duelos " + nomRival + " " + duelsRival + " VS " + client.getDuels() + " Duelos " + client
            .getName() + "\n");
  }

  /**
   * Metodo para determinar si se jugará otra partida o no
   *
   * @param sc Instancia de la clase Scanner
   * @return True si se jugará otra partida, sino False
   */
  private static boolean continuarJugando(Scanner sc) {
    System.out.println("¿Deseas seguir jugando? [S/n]");
    String answer = sc.next().toLowerCase(Locale.ROOT);
    if (!answer.equals("s")) {
      System.out.println("¡Hasta la vista, marinero!");
      return false;
    }
    return true;
  }

  /**
   * Metodo para realizar los pasos previos a una partida
   *
   * @param gameUtils Instancia de la clase GameUtils
   * @param mode      modo de juego
   * @return Nombre e ID del rival
   * @throws IOException Excepcion debida a un fallo de escritura en el Data Output Stream
   */
  private static String iniciarPartida(GameUtils gameUtils, int mode) throws IOException {
    Scanner sc = new Scanner(System.in);
    String nomPlayer;

    System.out.println("Bienvenido pirata,¿como te llamas?");
    if (mode == 0) {
      nomPlayer = sc.nextLine();
    } else {
      nomPlayer = gameUtils.getRandomPirateName();
      System.out.println(nomPlayer);
    }
    return nomPlayer;
  }


  /**
   * Metodo para generar los insultos y comebacks randoms del principio
   *
   * @param client    Jugador que quiere los insultos y comebacks
   * @param gameUtils Instancia de GameUtils
   */
  public static void generateInsultsComebacks(Jugador client, GameUtils gameUtils) {
    for (int i = 0; i < 2; i++) {
      if (client.listInsults().size() != 16) {
        gameUtils.addInsultsComebacks(client);
      }
    }
  }

  /**
   * Metodo para generar un secreto
   *
   * @param mode modo de juego
   * @param sc   Instancia de la clase Scanner
   * @return secreto final
   */
  public static int generateSecret(int mode, Scanner sc) {
    String secretClient;
    Pattern pat = Pattern.compile("[0-9]*");
    Matcher mat;

    if (mode == 0) {
      do {
        System.out.println("Introduce tu secreto (solo números, por favor): ");
        secretClient = sc.next();
        mat = pat.matcher(secretClient);
      } while (!mat.matches());

    } else {
      secretClient = String.valueOf((int) (Math.random() * 2000) + 1);
    }
    System.out.println("Tu secreto es: " + secretClient);
    return Integer.parseInt(secretClient);
  }

  /**
   * Metodo para enviar el protocolo SECRET
   *
   * @param protocol Intsancia de la Clase ProtocolClientUtils
   * @param client   Jugador actual
   * @return Secreto del rival
   * @throws NoSuchAlgorithmException Excepcion debida a que no detecte el algoritmo de
   *                                  encriptacion
   * @throws IOException              Excepcion debida a un fallo de escritura en el Data Output
   *                                  Stream
   */
  private static int sendSecret(ProtocolClientUtils protocol, Jugador client)
      throws IOException, NoSuchAlgorithmException {
    int secretServer = protocol.secretProtocol(client.getSecret());
    System.out.println("¡Secreto recibido, pirata!\nEl secreto del rival es: " + secretServer);
    return secretServer;
  }

  /**
   * Metodo para comprobar los argumentos introducidos y actuar en función de ellos
   *
   * @param args Argumentos introducidos
   */
  private static void argumentsChecker(String[] args) {
    if (args.length == 0 || args.length > 6) {
      System.exit(0);
    }

    if (args[0].equals("-h")) {
      System.out.println("Us: java -jar client -s <maquina_servidora> -p <port> [-i 0|1]");
      System.exit(0);
    }

    if (!args[0].equals("-s") && !args[2].equals("-p") && !args[4].equals("-i")) {
      System.out.println("Error en els parametres, per consultar posa -h");
      System.exit(1);
    }
    try {
      InetAddress ip = InetAddress.getByName(args[1]);
      int port = Integer.parseInt(args[3]);
      int mode = Integer.parseInt(args[5]);

      if (port < 0 || port > 65535) {
        System.out.println("No has introducido un puerto valido del 0 al 65535");
        System.exit(0);
      }

      if (mode != 0 && mode != 1) {
        System.out.println("Modalidad de juego mal especificada");
        System.exit(0);
      }

    } catch (NumberFormatException | UnknownHostException ex) {
      System.out.println("No se ha introducido un número en uno de los campos");
      System.exit(0);
    }

  }
}
