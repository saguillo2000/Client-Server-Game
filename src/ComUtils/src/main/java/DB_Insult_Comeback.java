import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class DB_Insult_Comeback {

  private static DB_Insult_Comeback db;
  private Map<String, String> dic_insults_comebacks = null;
  private List<String> list_pirate_name = null;

  /**
   * Metodo constructor de la clase DB_Insult_Comeback
   */
  public DB_Insult_Comeback() {
    Gson gson = new Gson();
    JsonReader reader;

    try {

      reader = new JsonReader(new InputStreamReader(this.getClass().
          getClassLoader().getResourceAsStream("Insults_Comebacks.json"),
          StandardCharsets.UTF_8));

      this.dic_insults_comebacks = gson.fromJson(reader, Map.class);

      reader = new JsonReader(new InputStreamReader(this.getClass().
          getClassLoader().getResourceAsStream("PiratesNames.json"),
          StandardCharsets.UTF_8));

      this.list_pirate_name = gson.fromJson(reader, List.class);

      reader.close();
    } catch (IOException exception) {
      exception.printStackTrace();
    }
  }

  /**
   * Pattern Singleton for memory efficiency
   *
   * @return Data_Base
   */
  public static DB_Insult_Comeback getInstance() {
    if (db == null) {
      db = new DB_Insult_Comeback();
    }
    return db;
  }

  /**
   * Metodo para conseguir un insulto random del HashMap dic
   *
   * @return Insulto random
   */
  public String getRandomInsult() {
    int random = (int) (Math.random() * dic_insults_comebacks.keySet().size());
    return (String) dic_insults_comebacks.keySet().toArray()[random];
  }

  /**
   * Metodo para conseguir el Comeback de un insulto
   *
   * @param insult Insulto del que queremos su comeback
   * @return Comeback deseado
   */
  public String getComebackByInsult(String insult) {
    return dic_insults_comebacks.get(insult);
  }

  /**
   * Metodo para comprobar si un comeback corresponde a cierto insulto
   *
   * @param insult   Insulto del que queremos saber si es su comeback
   * @param comeback Comaback a comparar
   * @return True si coinciden, False sino
   */
  public boolean reply(String insult, String comeback) {
    return comeback.equals(dic_insults_comebacks.get(insult));
  }

  /**
   * Metodo para consguir un nombre aleatorio de pirata
   *
   * @return Nombre random
   */
  public String getRandomNamePirate() {
    int random = (int) (Math.random() * list_pirate_name.size());
    return list_pirate_name.get(random);
  }

}
