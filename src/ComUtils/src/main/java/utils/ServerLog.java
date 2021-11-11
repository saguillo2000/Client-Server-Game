package utils;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ServerLog {

  /**
   * Metodo para escribir en el Log del protocolo HELLO
   *
   * @param flow      Flag para denotar si es escritura (1) o lectura (0) o error (2)
   * @param operation Tipo de protocolo realizado
   * @param id        ID del jugador
   * @param data      mensaje
   * @throws IOException Excepción debida a un fallo de escritura
   */
  public static void writeLog(int flow, String operation, String id, String data)
      throws IOException {
    String logMessage =
        getCurrentMilis() + " " + flow + " " + operation + " " + id + " " + data;
    ServerLog.write(ServerLog.getFile(), logMessage);
  }

  /**
   * Metodo para escribir en el Log del protocolo
   *
   * @param flow      Flag para denotar si es escritura (1) o lectura (0)
   * @param operation Tipo de protocolo realizado (HASH,SECRET...)
   * @param data      mensaje
   * @throws IOException Excepción debida a un fallo de escritura
   */
  public static void writeLog(int flow, String operation, String data) throws IOException {
    String logMessage = ServerLog.getCurrentMilis() + " " + flow + " " + operation + " " + data;
    ServerLog.write(ServerLog.getFile(), logMessage);
  }

  /**
   * Metodo para escribir en el fichero
   *
   * @param filePath   Path del archivo
   * @param logMessage Mensaje a escribir
   * @throws IOException Excepción debida a un fallo de escritura
   */
  private static void write(String filePath, String logMessage) throws IOException {
    try {
      FileWriter logFile = new FileWriter(filePath, true);
      PrintWriter printWriter = new PrintWriter(logFile);
      printWriter.println(logMessage);
      printWriter.close();
    } catch (IOException exception) {
      throw new IOException("No se ha podido escribir el mensaje");
    }
  }

  /**
   * Metodo para conseguir el tiempo exacto en que se produce una escritura en el log
   *
   * @return Hora:minutos:segundos::milesimas
   */
  private static String getCurrentMilis() {
    long currentMillis = System.currentTimeMillis();
    DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm:ss:SSS");
    Date date = new Date(currentMillis);
    return dateFormat.format(date);
  }

  /**
   * Metodo para conseguir el archivo log
   *
   * @return Path del archivo
   */
  private static String getFile() {
    String nameFile = "server" + Thread.currentThread().getName() + ".log";
    return "logsServer/" + nameFile;
  }

}
