import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import org.junit.Test;
import utils.ComUtils;

public class ComUtilsInt8Test {

  @Test
  public void int8_test() {
    File file = new File("testInt");
    try {
      file.createNewFile();
      ComUtils comUtils = new ComUtils(new FileInputStream(file), new FileOutputStream(file));

      comUtils.write_int8(255);
      int readInt = comUtils.read_int8();
      assertEquals(255, readInt);

      comUtils.write_int8(256);
      int readInt2 = comUtils.read_int8();
      assertNotEquals(256, readInt2);

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
