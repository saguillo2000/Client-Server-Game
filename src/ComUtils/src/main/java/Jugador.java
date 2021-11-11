import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class Jugador {

  private final Set<String> insults;
  private final Set<String> comebacks;
  private final String name;
  private final int id;
  private boolean priority;
  private int secret;
  private int duels;

  /**
   * Metodo constructor de la clase Jugador por defecto
   */
  public Jugador() {
    this.insults = new HashSet<>();
    this.comebacks = new HashSet<>();
    this.name = "";
    this.priority = false;
    this.secret = 0;
    this.duels = 0;
    this.id = this.generateRandom();
  }

  /**
   * Metodo constructor de la clase Jugador con parámetros
   *
   * @param name   Nombre del jugador
   * @param secret Secreto de jugador
   */
  public Jugador(String name, int secret) {
    this.insults = new HashSet<>();
    this.comebacks = new HashSet<>();
    this.name = name;
    this.priority = false;
    this.secret = secret;
    this.duels = 0;
    this.id = this.generateRandom();
  }

  /**
   * Metodo para conseguir el Id de un jugador
   *
   * @return ID del jugador
   */
  public int getId() {
    return id;
  }

  /**
   * Metodo para conseguir el secreto del jugador
   *
   * @return Secreto del jugador
   */
  public int getSecret() {
    return secret;
  }

  /**
   * Metodo para establecer un nuevo secreto de un jugador
   *
   * @param secret Nuevo secreto
   */
  public void setSecret(int secret) {
    this.secret = secret;
  }

  /**
   * Metodo para conseguir el nombre de un jugador
   *
   * @return Nombre del jugador
   */
  public String getName() {
    return name;
  }

  /**
   * Metodo para incrementar el contador de duelos ganados de un jugador
   */
  public void wonDuels() {
    this.duels += 1;
  }

  /**
   * Metodo para conseguir el numero de duelos ganados por parte de un jugador
   *
   * @return Numero de duelos ganados
   */
  public int getDuels() {
    return duels;
  }

  /**
   * Metodo para establecer el numero de duelos ganados por parte de un jugador
   *
   * @param duelos Numero de duelos a establecer
   */
  public void setDuels(int duelos) {
    duels = duelos;
  }

  /**
   * Metodo para añadir un nuevo insulto aprendido a la lista de insultos de jugador
   *
   * @param insult Nuevo insulto aprendido
   */
  public void learnInsult(String insult) {
    this.insults.add(insult);
  }

  /**
   * Metodo para añadir un nuevo comeback aprendido a la lista de comebacks de jugador
   *
   * @param comeback Nuevo comeback aprendido
   */
  public void learnComeback(String comeback) {
    this.comebacks.add(comeback);
  }

  /**
   * Metodo para listar los insultos de un jugador
   *
   * @return Lista de insultos
   */
  public List<String> listInsults() {
    return new ArrayList<>(insults);
  }

  /**
   * Metodo para listar los comebacks de un jugador
   *
   * @return Lista de comebacks en forma de string
   */
  public List<String> listComeback() {
    return new ArrayList<>(comebacks);
  }

  /**
   * Metodo para conseguir la prioridad de un jugador a la hora de mandar insultos
   *
   * @return True si tiene prioridad (insulta), False si no (manda comebacks)
   */
  public boolean isPriority() {
    return priority;
  }

  /**
   * Metodo para establecer la nueva prioridad
   *
   * @param priority Nueva prioridad
   */
  public void setPriority(boolean priority) {
    this.priority = priority;
  }

  /**
   * Generar el Random del ID
   *
   * @return id random
   */
  private int generateRandom() {
    Random random = new Random();
    return Math.abs(random.nextInt());
  }

  /**
   * Generacion de un insulto Random
   *
   * @return insulto random
   */
  public String generateRandomInsult() {
    int indexRandom = (int) (Math.random() * this.listInsults().size());
    return listInsults().get(indexRandom);
  }

  /**
   * Metodo para generar un comeback Random de la lista de comebacks
   *
   * @return comeback random
   */
  public String generateComebackRandom() {
    int indexRandom = (int) (Math.random() * this.listComeback().size());
    return listComeback().get(indexRandom);
  }

  /**
   * Metodo para generar un insult Random de la lista de insults
   *
   * @return insult random
   */
  public String getRandomInsult() {
    int indexRandom = (int) (Math.random() * this.listInsults().size());
    return listInsults().get(indexRandom);
  }
}
