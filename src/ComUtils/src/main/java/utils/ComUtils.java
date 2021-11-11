package utils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class ComUtils {

  private final DataInputStream dataInputStream;
  private final DataOutputStream dataOutputStream;

  /**
   * Metodo constructor de la clase ComUtils
   *
   * @param inputStream  nuevo Data Input Stream
   * @param outputStream nuevo Data Output Stream
   * @throws IOException Excepcion debida a : - Primer byte no puede ser leido por alguna razon a
   *                     parte de ser el ultimo del file, - El input stream ha sido cerrado o -
   *                     Otros erores de I/O.
   */
  public ComUtils(InputStream inputStream, OutputStream outputStream) throws IOException {
    dataInputStream = new DataInputStream(inputStream);
    dataOutputStream = new DataOutputStream(outputStream);
  }

  /**
   * Metodo que lee 4 bytes de informacion y retorba su numero entero
   *
   * @return Integer transformado a su forma entera
   * @throws IOException Excepcion debida a errores de transformacion de bytes
   */
  public int read_int32() throws IOException {
    byte[] bytes = read_bytes(4);
    return bytesToInt32(bytes, Endianness.BIG_ENNDIAN);
  }

  /**
   * Metodo para escribir la array del HASH
   *
   * @param hash Array de bytes a escribir
   * @throws IOException Excepcion debida a errores de transformacion de bytes
   */
  public void write_hash_int32(byte[] hash) throws IOException {
    dataOutputStream.write(hash, 0, 32);
  }

  /**
   * Metodo que escribe un numero de Bytes en un array aplicandole el tipo de Endiannness y luego en
   * el Data Output Stream
   *
   * @param number Cantidad de bytes que queremos escribir
   * @throws IOException Excepcion debida a un fallo de escriptura del Output Data Stream
   */
  public void write_int32(int number) throws IOException {
    byte[] bytes = int32ToBytes(number, Endianness.BIG_ENNDIAN);
    dataOutputStream.write(bytes, 0, 4);
  }

  /**
   * Metodo que escribe en un array de 1 un numero aplicandole el Endianness y luego en el Data
   * Output Stream
   *
   * @param number Numero que queremos escribir
   * @throws IOException Excepcion debida a un fallo de escriptura del Output Data Stream
   */
  public void write_int8(int number) throws IOException {
    byte[] bytes = new byte[1];
    bytes[0] = (byte) (number);
    dataOutputStream.write(bytes, 0, 1);
  }

  /**
   * Metodo que lee un array de dismension 1 y retorna el nuemro que contiene descodificado
   *
   * @return Numero que buscamos
   * @throws IOException Excepcion debiada a un fallo en read_bytes
   */
  public int read_int8() throws IOException {
    byte[] bInt = read_bytes(1);
    return (bInt[0] & 0xFF);
  }

  /**
   * Metodo que lee un String de tamaño invariable, cuyo tamaño ya viene predefinido
   *
   * @return String final leido
   * @throws IOException Excepcion debido a un fallo en read_bytes
   */
  public String read_string() throws IOException {
    ArrayList<Byte> bStr = new ArrayList<>();
    byte temp = 0;

    do {
      if (temp != 0) {
        bStr.add(temp);
      }
      temp = read_bytes(1)[0];
    } while (temp != 0);

    byte[] res = new byte[bStr.size()];
    for (int i = 0; i < bStr.size(); i++) {
      res[i] = bStr.get(i);
    }
    return new String(res, StandardCharsets.ISO_8859_1);
  }

  /**
   * Metodo que escribe un String de tamaño invariable, cuyo tamaño ya viene predefinido
   *
   * @param str String que queremos escribir
   * @throws IOException Excepcion debido a un fallo de escriptura del Output Data Stream
   */
  public void write_string(String str) throws IOException {
    int size = str.length();
    byte[] bStr = new byte[size];

    for (int i = 0; i < size; i++) {
      bStr[i] = (byte) str.charAt(i);
    }
    dataOutputStream.write(bStr, 0, size);
  }

  /**
   * Metodo que, segun el tipo de Endianness, transforma un numero entero en una array de Bytes
   *
   * @param number     Integer que queremos transformar
   * @param endianness Tipo de almacenamiento que estamos usando
   * @return Array de Bytes llena
   */
  private byte[] int32ToBytes(int number, Endianness endianness) {
    byte[] bytes = new byte[4];

    if (Endianness.BIG_ENNDIAN == endianness) {
      bytes[0] = (byte) ((number >> 24) & 0xFF);
      bytes[1] = (byte) ((number >> 16) & 0xFF);
      bytes[2] = (byte) ((number >> 8) & 0xFF);
      bytes[3] = (byte) (number & 0xFF);
    } else {
      bytes[0] = (byte) (number & 0xFF);
      bytes[1] = (byte) ((number >> 8) & 0xFF);
      bytes[2] = (byte) ((number >> 16) & 0xFF);
      bytes[3] = (byte) ((number >> 24) & 0xFF);
    }
    return bytes;
  }

  /**
   * Metodo qu etransforma bytes en numeros enteros
   *
   * @param bytes      Bytes a transformar
   * @param endianness Formato de almacenamiento que usamos
   * @return Integer transfromado a su version entera
   */
  private int bytesToInt32(byte[] bytes, Endianness endianness) {
    int number;
    if (Endianness.BIG_ENNDIAN == endianness) {
      number = ((bytes[0] & 0xFF) << 24) | ((bytes[1] & 0xFF) << 16) |
          ((bytes[2] & 0xFF) << 8) | (bytes[3] & 0xFF);
    } else {
      number = (bytes[0] & 0xFF) | ((bytes[1] & 0xFF) << 8) |
          ((bytes[2] & 0xFF) << 16) | ((bytes[3] & 0xFF) << 24);
    }
    return number;
  }

  /**
   * Metodo que lee una cantidad de Bytes a través del Data Input Stream
   *
   * @param numBytes Cantidad de Bytes que queremos leer
   * @return Array de bytes llena
   * @throws IOException Excepcion debida a una tuberia rota
   */
  private byte[] read_bytes(int numBytes) throws IOException {
    int bytesread, len = 0;
    byte[] bStr = new byte[numBytes];

    do {
      bytesread = dataInputStream.read(bStr, len, numBytes - len);
      if (bytesread == -1) {
        throw new IOException("Broken Pipe");
      }
      len += bytesread;
    } while (len < numBytes);
    return bStr;
  }


  /**
   * Metodo que lee Strigs de tamaño variable y cuyo tamañi viene predefinido Reads variable size
   * String, which size is previously defined
   *
   * @param size Tamaó especifico del
   * @return String final
   * @throws IOException Excepcion debida a un fallo en read_bytes
   */
  public String read_string_variable(int size) throws IOException {
    byte[] bHeader;
    char[] cHeader = new char[size];
    int numBytes;

    bHeader = read_bytes(size);
    for (int i = 0; i < size; i++) {
      cHeader[i] = (char) bHeader[i];
    }
    numBytes = Integer.parseInt(new String(cHeader));

    byte[] bStr = read_bytes(numBytes);
    char[] cStr = new char[numBytes];

    for (int i = 0; i < numBytes; i++) {
      cStr[i] = (char) bStr[i];
    }
    return String.valueOf(cStr);
  }

  /**
   * Metodo que escribe strings de tamaño variable y cuyo tamaño viene predefinido
   *
   * @param size Numero de Bytes para su tamaño
   * @param str  String que queremos escribir
   * @throws IOException Excepcion debida a un fallo de escriptura del Output Data Stream
   */
  public void write_string_variable(int size, String str) throws IOException {
    byte[] bHeader = new byte[size];
    StringBuilder strHeader;
    int numBytes;

    numBytes = str.length();

    strHeader = new StringBuilder(String.valueOf(numBytes));
    int len;
    if ((len = strHeader.length()) < size) {
      for (int i = len; i < size; i++) {
        strHeader.insert(0, "0");
      }
    }
    for (int i = 0; i < size; i++) {
      bHeader[i] = (byte) strHeader.charAt(i);
    }
    dataOutputStream.write(bHeader, 0, size);
    dataOutputStream.writeBytes(str);
  }

  /**
   * Metodo para leer la codificación Hash de bytes a hexadecimal
   *
   * @return String final del Hash h(secret)
   * @throws IOException Excepcion debida a un fallo en read_bytes
   */
  public byte[] read_encodedHash() throws IOException {
    return read_bytes(32);
  }


  /**
   * Tipos de Endian usados para almacenar Datos de mas de 1 bytes en un ordenador
   */
  public enum Endianness {
    BIG_ENNDIAN,
    LITTLE_ENDIAN
  }
}


