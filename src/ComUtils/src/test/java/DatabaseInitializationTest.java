import static org.junit.Assert.assertEquals;

import org.junit.Test;


public class DatabaseInitializationTest {

  @Test
  public void test_dataBase() {
    DB_Insult_Comeback db = DB_Insult_Comeback.getInstance();
    DB_Insult_Comeback db1 = DB_Insult_Comeback.getInstance();

    assertEquals(db, db1);
  }

}
