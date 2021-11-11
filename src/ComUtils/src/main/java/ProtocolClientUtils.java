import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import utils.TramaUtils;

public class ProtocolClientUtils {

  private final TramaUtils communication;

  /**
   * Metodo constructor de la clase ProtocolClientUtils
   *
   * @param communication Instancia de la clase TramaUtils
   */
  public ProtocolClientUtils(TramaUtils communication) {
    this.communication = communication;
  }

  /**
   * Metodo para escribir y/o leer el protocolo HELLO para Client
   *
   * @param name Nombre del juagdor
   * @param id   ID del jugador
   * @return Id y name separados con un ;
   */
  public String[] helloProtocol(String name, int id)
      throws IOException {
    String trama;
    String[] ret;

    communication.writeHelloTrama(id, name, 0);

    trama = communication.readHelloTrama();

    ret = new String[]{trama.split(";")[1], trama.split(";")[2]};

    return ret;
  }

  /**
   * Metodo para escribir y/o leer el protocolo SECRET para Cliente
   *
   * @param secret Secreto del juagdor
   * @return Secreto del rival
   * @throws NoSuchAlgorithmException Excepcion debida a que no detecte el algoritmo de
   *                                  encriptacion
   * @throws IOException              Excepcion debida a un fallo de escritura en el Data Output
   *                                  Stream
   */
  public int secretProtocol(int secret) throws IOException, NoSuchAlgorithmException {
    int secretRival;
    byte[] hashRival, meinHash, hashRivalCheck;

    meinHash = communication.encodeHash(String.valueOf(secret));
    communication.writeTramaHash(meinHash);
    hashRival = communication.readHash();

    communication.writeTrama("SECRET", String.valueOf(secret), 0);
    secretRival = Integer.parseInt(communication.readTrama());
    hashRivalCheck = communication.encodeHash(String.valueOf(secretRival));

    for (int i = 0; i < hashRival.length; i++) {
      if (hashRival[i] != hashRivalCheck[i]) {
        communication.writeTrama("ERROR", "Exchange of secrets error", 0);
        throw new IOException("El Hash y secreto no coinciden");
      }
    }
    return secretRival;
  }

  /**
   * Metodo para escribir el protocolo INSULT y leer el protocolo COMEBACK de un jugador
   *
   * @param ins           Flag para saber si estamos en modo manual (null) o automático (insulto
   *                      random)
   * @param listaInsultos Lista de insultos del jugador
   * @return Insult y comeback de la ronda
   * @throws IOException Excepcion debida a un fallo de escritura en el Data Output Stream
   */
  public String[] insultProtocol(String ins, List<String> listaInsultos) throws IOException {
    String insult, comeback;
    int option;
    Scanner sc = new Scanner(System.in);
    Pattern pat = Pattern.compile("[0-9]*");
    Matcher mat;

    if (ins == null) {
      do {
        System.out.println("\nPirata, escoge tu insulto: ");
        display(listaInsultos);
        option = sc.nextInt() - 1;
        mat = pat.matcher(Integer.toString(option));
      } while (option >= listaInsultos.size() || !mat.matches());

      insult = listaInsultos.get(option);

    } else {
      insult = ins;
    }

    System.out.println(insult);
    communication.writeTrama("INSULT", insult, 0);

    comeback = communication.readTrama();
    System.out.println(comeback);

    return new String[]{insult, comeback};
  }

  /**
   * Metodo para escribir el protocolo COMEBACK y leer el protocolo INSULT de un jugador
   *
   * @param cb             Flag para saber si estamos en modo manual (null) o automático (comeback
   *                       random)
   * @param listaComebacks Lista de comebacks del jugador
   * @return Insult y comeback de la ronda
   * @throws IOException Excepcion debida a un fallo de escritura en el Data Output Stream
   */
  public String[] comebackProtocol(String cb, List<String> listaComebacks) throws IOException {
    String insult, comeback;
    int option;
    Scanner sc = new Scanner(System.in);
    Pattern pat = Pattern.compile("[0-9]*");
    Matcher mat;

    insult = communication.readTrama();
    System.out.println(insult);

    if (cb == null) {
      do {
        System.out.println("\nPirata, escoge tu comeback: ");
        display(listaComebacks);
        option = sc.nextInt() - 1;
        mat = pat.matcher(Integer.toString(option));
      } while (option >= listaComebacks.size() || !mat.matches());

      comeback = listaComebacks.get(option);
    } else {
      comeback = cb;
    }

    System.out.println(comeback);
    communication.writeTrama("COMEBACK", comeback, 0);

    return new String[]{insult, comeback};
  }

  /**
   * Metodo para mostrar listas por pantalla
   *
   * @param lista Lista a mostrar
   */
  private void display(List<String> lista) {
    for (String opcion : lista) {
      int index = lista.indexOf(opcion) + 1;
      System.out.println(index + ") " + opcion);
    }
  }

  /**
   * Metodo para escribir y/o leer el protocolo SHOUT para 1 jugador
   *
   * @param duelsRival Duelos ganados por el rival
   * @param ganador    Flag para saber si el jugador ha ganado el duelo
   * @param nomRival   Nombre del rival
   * @throws IOException Excepcion debida a un fallo de escritura en el Data Output Stream
   */
  public void shoutProtocol(int duelsRival, boolean ganador, String nomRival) throws IOException {
    String shout;
    if (ganador) {
      shout = "¡He ganado, " + nomRival + "!";
    } else {
      if (duelsRival == 3) {
        shout = "¡Has ganado, " + nomRival
            + "! Eres tan bueno que podrias luchar contra la Sword Master de la isla Mêlée!";
      } else {
        shout = "¡Has ganado, " + nomRival + "!";
      }
    }
    communication.writeTrama("SHOUT", shout, 0);
    shout = communication.readTrama();
    System.out.println(shout);
  }

  /**
   * Metodo usado en el momento que debemos realizar una escritura sobre un error del Client al
   * Servidor
   *
   * @param infoError A qué es debido el ERROR produced
   * @throws IOException possible error en la lectura del Socket
   */
  public void errorProtocol(String infoError) throws IOException {
    this.communication.writeTrama("ERROR", infoError, 0);
  }
}
