import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import org.junit.Test;
import utils.TramaUtils;

public class TramaOPTest {

  File file = new File("testing");

  @Test
  public void trama_op_hello_test() {

    try {
      TramaUtils tramaUtils = new TramaUtils(new FileInputStream(file), new FileOutputStream(file));
      Random random = new Random();
      int rn = random.nextInt();

      tramaUtils.writeHelloTrama(rn, "EloiPUERTAS", 0);
      String result = tramaUtils.readHelloTrama();

      String expected = "HELLO;" + rn + ";EloiPUERTAS;";

      assertEquals(expected, result);

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void trama_op_hash_test() {
    try {
      String stringHash1, stringHash2;
      TramaUtils tramaUtils = new TramaUtils(new FileInputStream(file), new FileOutputStream(file));

      byte[] hashSecret = tramaUtils.encodeHash("7");
      stringHash1 = tramaUtils.getHashHexadecimal(hashSecret);

      tramaUtils.writeTramaHash(hashSecret);

      byte[] result = tramaUtils.readHash();
      stringHash2 = tramaUtils.getHashHexadecimal(result);

      assertEquals(stringHash1, stringHash2);

    } catch (IOException | NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
  }


  @Test
  public void trama_op_secret_test() {
    try {
      TramaUtils tramaUtils = new TramaUtils(new FileInputStream(file), new FileOutputStream(file));

      tramaUtils.writeTrama("SECRET", "7", 0);
      String result = tramaUtils.readTrama();

      String expected = "7";

      assertEquals(expected, result);

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void trama_op_insult_test() {

    try {
      TramaUtils tramaUtils = new TramaUtils(new FileInputStream(file), new FileOutputStream(file));

      tramaUtils.writeTrama("INSULT", "¿Has dejado ya de usar pañales?", 0);
      String result = tramaUtils.readTrama();

      String expected = "¿Has dejado ya de usar pañales?";

      assertEquals(expected, result);

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void trama_op_comeback_test() {

    try {
      TramaUtils tramaUtils = new TramaUtils(new FileInputStream(file), new FileOutputStream(file));

      tramaUtils.writeTrama("COMEBACK", "¿Por qué? ¿Acaso querías pedir uno prestado?",
          0);
      String result = tramaUtils.readTrama();

      String expected = "¿Por qué? ¿Acaso querías pedir uno prestado?";

      assertEquals(expected, result);

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void trama_op_shout_test() {

    try {
      TramaUtils tramaUtils = new TramaUtils(new FileInputStream(file), new FileOutputStream(file));

      tramaUtils.writeTrama("SHOUT",
          "¡Has ganado, EloiPerts. Ets tan bo que podria lluitar amb "
              + "la Sword Master de la illa Mêlée!", 0);

      String expected = "¡Has ganado, EloiPerts. Ets tan bo que podria lluitar "
          + "amb la Sword Master de la illa Mêlée!";
      String result = tramaUtils.readTrama();

      assertEquals(expected, result);

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void trama_op_error_test() {

    try {
      TramaUtils tramaUtils = new TramaUtils(new FileInputStream(file), new FileOutputStream(file));

      tramaUtils.writeTrama("CROQUETA", "EPUERTAS", 0);
      String result = tramaUtils.readTrama();

      String expected = "EPUERTAS";

      assertEquals(expected, result);

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
