package appli;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

//CLASSE DE TRIE PAR ID
public class TrierId implements Itri{

    List<People> liste;

    public TrierId(List<People> liste) {
        this.liste = liste;
    }

    /*
     * @param: List<People>
     * méthode de tri par ID
     */
    @Override
    public void trier(List<People> liste) {
        Collections.sort(liste, (a, b) -> a.get_id().compareToIgnoreCase(b.get_id()));
    }
}
