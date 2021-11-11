import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import utils.TramaUtils;
import utils.TramaUtilsLog;

public class ProtocolServerClientTest {

  File fileServer = new File("serverFile");
  File fileClient = new File("clientFile");


  @Test
  public void test_hello_protocol_client() {

    try {
      fileServer.createNewFile();
      fileClient.createNewFile();

      TramaUtils tramaUtilsC = new TramaUtils(new FileInputStream(fileClient),
          new FileOutputStream(fileServer));

      TramaUtilsLog tramaUtils = new TramaUtilsLog(new FileInputStream(fileServer),
          new FileOutputStream(fileClient));

      //Simulation that Server has written in the file after reading the Client content
      tramaUtils.writeHelloTrama(3456, "ServidorName", 0);

      ProtocolClientUtils protocolClient = new ProtocolClientUtils(tramaUtilsC);

      String[] responseClient = protocolClient.helloProtocol("Joan", 1234);

      String[] expectedClient = new String[]{String.valueOf(3456), "ServidorName"};

      assertEquals(responseClient, expectedClient);

    } catch (IOException ex) {
      ex.printStackTrace();
    }

  }

  @Test
  public void test_hello_protocol_server() {

    try {

      fileServer.createNewFile();
      fileClient.createNewFile();

      // We need to execute these lines before for the creation of directory
      // These lines can be found on Server Class
      File serverLog = new File("logsServer");
      if (!serverLog.mkdir()) {
        FileUtils.cleanDirectory(serverLog);
      }

      TramaUtilsLog tramaUtils = new TramaUtilsLog(new FileInputStream(fileServer),
          new FileOutputStream(fileClient));

      TramaUtils tramaUtilsC = new TramaUtils(new FileInputStream(fileClient),
          new FileOutputStream(fileServer));

      //Simulation of Client writing in the Servers outputFile
      tramaUtilsC.writeHelloTrama(1234, "Joan", 0);

      ProtocolServerUtils protocolServer = new ProtocolServerUtils(tramaUtils);

      String[] responseServer = protocolServer.helloProtocol("ServidorName", 3456);

      String[] expectedServer = new String[]{String.valueOf(1234), "Joan"};

      assertEquals(expectedServer, responseServer);

    } catch (IOException ex) {
      ex.printStackTrace();
    }

  }

  @Test
  public void test_secret_Client() {
    try {
      fileServer.createNewFile();
      fileClient.createNewFile();

      TramaUtils tramaUtilsC = new TramaUtils(new FileInputStream(fileClient),
          new FileOutputStream(fileServer));

      TramaUtilsLog tramaUtils = new TramaUtilsLog(new FileInputStream(fileServer),
          new FileOutputStream(fileClient));

      //Simulation that Server has written in the file after reading the Client content

      tramaUtils.writeTramaHash(tramaUtils.encodeHash("7"));
      tramaUtils.writeTrama("SECRET", "7", 0);

      ProtocolClientUtils protocolClient = new ProtocolClientUtils(tramaUtilsC);

      int responseClient = protocolClient.secretProtocol(7);
      int expectedClient = 7;

      assertEquals(responseClient, expectedClient);

    } catch (IOException | NoSuchAlgorithmException ex) {
      ex.printStackTrace();
    }
  }

  @Test
  public void test_secret_Server() {
    try {
      fileServer.createNewFile();
      fileClient.createNewFile();

      TramaUtils tramaUtilsC = new TramaUtils(new FileInputStream(fileClient),
          new FileOutputStream(fileServer));

      TramaUtilsLog tramaUtils = new TramaUtilsLog(new FileInputStream(fileServer),
          new FileOutputStream(fileClient));

      //Simulation that Server has written in the file after reading the Client content

      tramaUtilsC.writeTramaHash(tramaUtils.encodeHash("7"));
      tramaUtilsC.writeTrama("SECRET", "7", 0);

      ProtocolServerUtils protocolServer = new ProtocolServerUtils(tramaUtils);

      int responseClient = protocolServer.secretProtocol(7);
      int expectedClient = 7;

      assertEquals(responseClient, expectedClient);

    } catch (IOException | NoSuchAlgorithmException ex) {
      ex.printStackTrace();
    }
  }


}
