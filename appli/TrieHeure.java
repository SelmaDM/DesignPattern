package appli;

import java.util.Collections;
import java.util.List;

//
/*
 *CLASSE DE TRI PAR HEURE D ARRIVEE
 */

public class TrieHeure implements Itri{

    /*
     * @param: List<People>
     * méthode de tri par heure
     */
    @Override
    public void trier(List<People> liste) {
        Collections.sort(liste);
    }

    List<People> liste;
    public TrieHeure(List<People> liste) {
        this.liste = liste;
    }
}
