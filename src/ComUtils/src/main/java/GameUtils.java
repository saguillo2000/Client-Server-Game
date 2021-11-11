public class GameUtils {

  private final DB_Insult_Comeback dataBase;

  /**
   * Metodo constructor de la clase GameUtils
   */
  public GameUtils() {
    this.dataBase = DB_Insult_Comeback.getInstance();
  }

  /**
   * Metodo para saber quien tiene prioridad a la hora de empezar un duelo
   *
   * @param sumSecrets Suma de los secretos de los jugadores
   * @param idMine     ID del jugador que pregunta
   * @param idRival    ID del jugador rival
   * @return True si el jugador que pregunta tiene prioridad, sino False
   */
  public boolean whoHasPriority(int sumSecrets, int idMine, int idRival) {
    if (sumSecrets % 2 == 0) {
      return Math.min(idMine, idRival) == idMine;
    }
    return Math.max(idMine, idRival) == idMine;
  }

  /**
   * Metodo para comprobar si un comeback corresponde a cierto insulto
   *
   * @param insult   Insulto del que queremos saber si es su comeback
   * @param comeback Comaback a comparar
   * @return True si coinciden, False sino
   */
  public boolean counterInsult(String insult, String comeback) {
    return dataBase.reply(insult, comeback);
  }


  /**
   * Metodo para establecer los primeros 2 insults y comebacks de un pirata
   *
   * @param pirata Jugador de la partida
   */
  public void addInsultsComebacks(Jugador pirata) {
    String insult;
    do {
      insult = dataBase.getRandomInsult();
    } while (pirata.listInsults().contains(insult));

    String comeback = dataBase.getComebackByInsult(insult);
    pirata.learnInsult(insult);

    if (!pirata.listComeback().contains(comeback)) {
      pirata.learnComeback(comeback);
    }

  }

  /**
   * Metodo para conseguir un nombre de pirata aleatorio
   *
   * @return Nombre de pirata random
   */
  public String getRandomPirateName() {
    return this.dataBase.getRandomNamePirate();
  }


  /**
   * Metodo para saber el ganador de una partida y/o ronda
   *
   * @param player         Jugador
   * @param round_counters Contadores de las rondas ganadas por el jugador ye el rival
   * @param insult         Insulto hecho
   * @param comeback       Comeback obtenido
   * @return True si el jugador ha ganado, False sino
   */
  public boolean whoIsWinner(Jugador player, int[] round_counters, String insult, String comeback) {

    boolean ganador = true;

    if (this.counterInsult(insult, comeback)) {
      if (player.isPriority()) {
        player.setPriority(false);
        round_counters[1]++;
        ganador = false;
      } else {
        player.setPriority(true);
        round_counters[0]++;
      }
    } else {
      if (player.isPriority()) {
        round_counters[0]++;
      } else {
        round_counters[1]++;
        ganador = false;
      }
    }

    return ganador;
  }

}
