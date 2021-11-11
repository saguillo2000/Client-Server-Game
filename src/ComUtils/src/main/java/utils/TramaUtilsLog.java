package utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class TramaUtilsLog {

  private final String[] headerList;
  private final ComUtils utils;

  /**
   * Método constructor de la clase TramaUtils
   *
   * @param inputStream  Datos nuevos que entran
   * @param outputStream Datos nuevos a salir
   * @throws IOException Excepción debida a un fallo en ComUtils
   */
  public TramaUtilsLog(InputStream inputStream, OutputStream outputStream) throws IOException {
    headerList = new String[]{"HELLO", "HASH", "SECRET", "INSULT", "COMEBACK", "SHOUT", "ERROR"};
    File file = new File("tramaMessage");
    utils = new ComUtils(inputStream, outputStream);
  }

  /**
   * Metodo para conseguir el numero que codifica el tipo de mensaje que introducimos
   *
   * @param message Mensaje del que queremos saber la posicion en la lista de Protocolos
   * @return Posicion del mensaje en la lista de Protocolos
   */
  public int getIntMessage(String message) {
    for (int i = 0; i < headerList.length; i++) {
      if (headerList[i].equals(message)) {
        return i + 1;
      }
    }
    return 7;
  }

  /**
   * Metodo para conseguir un protocolo mediante su posicion en la lisra de protocolos
   *
   * @param opCode Posicion de la lista
   * @return Tipo de protocolo que estamos tratando
   */
  public String getStrMessage(int opCode) {
    return headerList[opCode - 1];
  }


  /**
   * Metodo para escribir el mensaje de una trama
   *
   * @param message Mensaje o datos a escribir
   * @throws IOException Excepcion debida a un fallo al escribir el Output Data Stream
   */
  public void writeStrTrama(String message) throws IOException {
    utils.write_string(message);
  }

  /**
   * Metodo para escribir el protocolo HELLO
   *
   * @param id         ID del jugador
   * @param name       Nombre del jugador
   * @param endianness Tipo de Endianness
   * @throws IOException Excepcion debida a un fallo al escribir el Output Data Stream
   */
  public void writeHelloTrama(int id, String name, int endianness) throws IOException {
    utils.write_int8(1);
    utils.write_int32(id);
    writeStrTrama(name);
    utils.write_int8(endianness);
    ServerLog.writeLog(1, "HELLO", String.valueOf(id), name);
  }

  /**
   * Metodo para escribir los distintos campos de la trama
   *
   * @param opCode Numero de OpCode que codifica una posición de protocolList
   * @param data   Mensaje de la trama
   * @param endi   Tipo de Endianess
   * @throws IOException Excepcion debida a un fallo de escritura de la Trama
   */
  public void writeTrama(String opCode, String data, int endi)
      throws IOException {
    int opCod = getIntMessage(opCode);
    utils.write_int8(opCod);
    writeStrTrama(data);
    utils.write_int8(endi);
    ServerLog.writeLog(1, opCode, data);
  }

  /**
   * Metodo para escribir el protocolo HASH
   *
   * @param hash Array de Bytes del HASH a escribir
   * @throws IOException Excepcion debida a un fallo al escribir el Output Data Stream
   */
  public void writeTramaHash(byte[] hash) throws IOException {
    utils.write_int8(2);
    utils.write_hash_int32(hash);
    ServerLog.writeLog(1, "HASH", this.getHashHexadecimal(hash));
  }

  /**
   * Metodo para leer los distintos campos de la trama
   *
   * @return String final de como es la trama
   * @throws IOException Excepcion debia a un fallo en read_int8
   */
  public String readTrama() throws IOException {
    int opCode = utils.read_int8();
    String data = utils.read_string();

    if (opCode == 7) {
      ServerLog.writeLog(0, "ERROR", data);
      throw new IOException("ERROR  " + data);
    }

    ServerLog.writeLog(0, this.getStrMessage(opCode), data);

    return data;
  }

  /**
   * Metodo para leer el protocolo HELLO
   *
   * @return String final de como es la trama
   * @throws IOException Excepcion debia a un fallo en read_int8
   */
  public String[] readHelloTrama() throws IOException {
    int opCode = utils.read_int8();

    if (opCode == 7) {
      String infoError = utils.read_string();
      ServerLog.writeLog(0, "ERROR", infoError);
      throw new IOException("ERROR  " + infoError);
    }

    String id = String.valueOf(utils.read_int32());
    String data = utils.read_string();

    ServerLog.writeLog(0, this.getStrMessage(opCode), id, data);

    return new String[]{id, data};

  }

  /**
   * Metodo para leer el protocolo HASH
   *
   * @return Array final de como es la trama
   * @throws IOException Excepcion debia a un fallo en read_int8
   */
  public byte[] readHash() throws IOException {
    int op = utils.read_int8();

    if (op == 7) {
      String infoError = utils.read_string();
      ServerLog.writeLog(0, "ERROR", infoError);
      throw new IOException("ERROR  " + infoError);
    }
    byte[] hash = utils.read_encodedHash();
    ServerLog.writeLog(0, this.getStrMessage(op), this.getHashHexadecimal(hash));
    return hash;
  }

  /**
   * Metodo para codificar el secret con la funcion Hash y la encriptacion SHA-256
   *
   * @param secret secreto que queremos codificar
   * @throws NoSuchAlgorithmException Excepcion debida a que no detecte el algoritmo de
   *                                  encriptacion
   * @throws IOException              Excepcion debida a un fallo de escritura en el Data Output
   *                                  Stream
   */
  public byte[] encodeHash(String secret) throws NoSuchAlgorithmException, IOException {
    MessageDigest digest = MessageDigest.getInstance("SHA-256");
    return digest.digest(secret.getBytes(StandardCharsets.UTF_8));
  }


  /**
   * Metodo para conseguir la version legible del array de Bytes del HASH
   *
   * @param hash Array de Bytes del HASH
   * @return String final resultante de la trama
   */
  public String getHashHexadecimal(byte[] hash) {
    StringBuilder hexString = new StringBuilder();

    for (byte b : hash) {
      String hex = Integer.toHexString(0xff & b);
      if (hex.length() == 1) {
        hexString.append('0');
      }
      hexString.append(hex);
    }
    return hexString.toString();
  }

}

