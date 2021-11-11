package utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ComUtilsService {

  private final ComUtils comUtils;

  /**
   * Constructor function to initialize a new ComUtils
   *
   * @param inputStream  Input Stream we need
   * @param outputStream Output Stream we will use
   * @throws IOException Exception due to failure initializing ComUtils
   */
  public ComUtilsService(InputStream inputStream, OutputStream outputStream) throws IOException {
    comUtils = new ComUtils(inputStream, outputStream);
  }

  /**
   * Method used for testing ComUtils' writing functions
   */
  public void writeTest() {
    try {
      comUtils.write_string("Bona");
      comUtils.write_int32(56);
      comUtils.write_string_variable(5, "adeeu");
    } catch (IOException ex) {
      System.out.println(ex.getMessage());
    }
  }

  /**
   * Method used for testing ComUtils' reading functions
   *
   * @return Final String read
   */
  public String readTest() {
    String result = "";

    try {
      result += comUtils.read_string();
      result += "\n";
      result += comUtils.read_int32();
      result += "\n";
      result += comUtils.read_string_variable(5);
    } catch (IOException ex) {
      return ex.getMessage();
    }
    return result;
  }


}
