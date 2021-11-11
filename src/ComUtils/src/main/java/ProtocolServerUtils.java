import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import utils.TramaUtilsLog;

public class ProtocolServerUtils {

  private final TramaUtilsLog communication;
  private TramaUtilsLog communication2 = null;

  /**
   * Metodo constrcutor de la clase ProtocolUtils para 1 jugador
   *
   * @param communication Instancia de la clase TramaUtils
   */
  public ProtocolServerUtils(TramaUtilsLog communication) {
    this.communication = communication;
  }

  /**
   * Metodo constrcutor de la clase ProtocolUtils para 2 jugadores
   *
   * @param communication  Instancia de la clase TramaUtils del jugador 1
   * @param communication2 Instancia de la clase TramaUtils del jugador 2
   */
  public ProtocolServerUtils(TramaUtilsLog communication, TramaUtilsLog communication2) {
    this.communication = communication;
    this.communication2 = communication2;
  }

  /**
   * Metodo para escribir y/o leer el protocolo HELLO para Server
   *
   * @param name Nombre del juagdor
   * @param id   ID del jugador
   * @return Id y name separados con un ;
   */
  public String[] helloProtocol(String name, int id) throws IOException {
    String[] trama;

    trama = communication.readHelloTrama();

    communication.writeHelloTrama(id, name, 0);

    return trama;
  }

  /**
   * Metodo para escribir y/o leer el protocolo HELLO para 2 jugadores
   *
   * @return Array con las 2 tramas
   * @throws IOException Excepcion debida a un fallo de escritura en el Data Output Stream
   */
  public String[] helloProtocol2Players() throws IOException {
    String[] tramaClient1, tramaClient2;
    String name1, name2, id1, id2;

    tramaClient1 = this.communication.readHelloTrama();
    tramaClient2 = this.communication2.readHelloTrama();

    id1 = tramaClient1[0];
    id2 = tramaClient2[0];

    name1 = tramaClient1[1];
    name2 = tramaClient2[1];

    if (id1.equals(id2)) {
      communication.writeTrama("ERROR", " ¡No eres tu, soy yo! !Hasta la vista!", 0);
      communication2.writeTrama("ERROR", " ¡No eres tu, soy yo! !Hasta la vista!", 0);
      throw new IOException("ERROR Mismo ID entre los dos clientes");
    }

    this.communication2.writeHelloTrama(Integer.parseInt(id1), name1, 0);
    this.communication.writeHelloTrama(Integer.parseInt(id2), name2, 0);

    return new String[]{id1, id2, name1, name2};
  }

  /**
   * Metodo para escribir y/o leer el protocolo SECRET para 1 jugador
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
    byte[] hashRival, meinHash;
    String hashRivalChecked, hashRivalParsed;

    hashRival = communication.readHash();

    meinHash = communication.encodeHash(String.valueOf(secret));
    communication.writeTramaHash(meinHash);

    secretRival = Integer.parseInt(communication.readTrama());

    communication.writeTrama("SECRET", String.valueOf(secret), 0);

    hashRivalParsed = communication.getHashHexadecimal(hashRival);
    hashRivalChecked = communication
        .getHashHexadecimal(communication.encodeHash(String.valueOf(secretRival)));

    if (!hashRivalChecked.equals(hashRivalParsed)) {
      communication.writeTrama("ERROR", "Exchange of secrets error", 0);
      throw new IOException("El Hash y secreto no coinciden");
    }

    return secretRival;
  }

  /**
   * Metodo para escribir y/o leer el protocolo SECRET para 2 jugadores
   *
   * @return Array de secretos
   * @throws NoSuchAlgorithmException Excepcion debida a que no detecte el algoritmo de
   *                                  encriptacion
   * @throws IOException              Excepcion debida a un fallo de escritura en el Data Output
   *                                  Stream
   */
  public int[] secretProtocol2Players() throws IOException, NoSuchAlgorithmException {

    byte[] hashClient1, hashClient2;
    int secretClient1, secretClient2;
    String hashClient1Parsed, hashClient2Parsed, hashCheck1, hashCheck2;

    hashClient1 = this.communication.readHash();
    hashClient2 = this.communication2.readHash();

    this.communication2.writeTramaHash(hashClient1);
    this.communication.writeTramaHash(hashClient2);

    secretClient1 = Integer.parseInt(this.communication.readTrama());
    secretClient2 = Integer.parseInt(this.communication2.readTrama());

    this.communication2.writeTrama("SECRET", String.valueOf(secretClient1), 0);
    this.communication.writeTrama("SECRET", String.valueOf(secretClient2), 0);

    hashClient1Parsed = communication.getHashHexadecimal(hashClient1);
    hashCheck1 = communication
        .getHashHexadecimal(communication.encodeHash(String.valueOf(secretClient1)));

    hashClient2Parsed = communication.getHashHexadecimal(hashClient2);
    hashCheck2 = communication
        .getHashHexadecimal(communication.encodeHash(String.valueOf(secretClient2)));

    if (!hashClient1Parsed.equals(hashCheck1) && !hashClient2Parsed.equals(hashCheck2)) {
      communication.writeTrama("ERROR", "Exchange of secrets error", 0);
      throw new IOException("El Hash y secreto no coinciden");
    }
    return new int[]{secretClient1, secretClient2};
  }

  /**
   * Metodo para escribir y/o leer los procolocos de INSULT y COMEBACK
   *
   * @param player Jugador actual
   * @return Insulto y comeback de la ronda
   * @throws IOException Excepcion debida a un fallo de escritura en el Data Output Stream
   */
  public String[] insultComebackProtocol(Jugador player) throws IOException {
    String insult, comeback;

    if (player.isPriority()) {
      insult = player.generateRandomInsult();
      communication.writeTrama("INSULT", insult, 0);
      comeback = communication.readTrama();
      player.learnComeback(comeback);

    } else {
      insult = communication.readTrama();
      comeback = player.generateComebackRandom();
      communication.writeTrama("COMEBACK", comeback, 0);
      player.learnInsult(insult);
    }

    return new String[]{insult, comeback};
  }

  /**
   * Metodo para escribir y/o leer el protocolo SHOUT para 1 jugador
   *
   * @param duelsRival Duelos ganados por el rival
   * @param ganador    Flag para saber si el jugador ha ganado el duelo
   * @param nomRival   Nombre del rival
   * @throws IOException Excepcion debida a un fallo de escritura en el Data Output Stream
   */
  public void shoutProtocol(int duelsRival, boolean ganador, String nomRival)
      throws IOException {
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
    communication.readTrama();
  }

  /**
   * Metodo para escribir y/o leer el protocolo SHOUT para 2 jugadores
   *
   * @throws IOException Excepcion debida a un fallo de escritura en el Data Output Stream
   */
  public void shoutProtocol2Players() throws IOException {
    String shoutClient1, shoutClient2;

    shoutClient1 = this.communication.readTrama();
    shoutClient2 = this.communication2.readTrama();

    this.communication2.writeTrama("SHOUT", shoutClient1, 0);
    this.communication.writeTrama("SHOUT", shoutClient2, 0);
  }

}
