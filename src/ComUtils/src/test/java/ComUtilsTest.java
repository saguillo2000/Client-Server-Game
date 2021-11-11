import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import org.junit.Test;
import utils.ComUtils;

public class ComUtilsTest {

  @Test
  public void example_test() {
    File file = new File("testing");
    try {
      file.createNewFile();
      ComUtils comUtils = new ComUtils(new FileInputStream(file), new FileOutputStream(file));
      comUtils.write_int32(2);
      int readedInt = comUtils.read_int32();

      assertEquals(2, readedInt);

    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
