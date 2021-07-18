package appli;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

//CLASSE DE TRI PAR NOM
public class TrierNom implements Itri{

List<People> liste;

    public TrierNom(List<People> liste) {
        this.liste = liste;
    }


    /*
     * @param: List<People>
     * méthode de tri par nom
     */
    @Override
    public void trier(List<People> liste) {
        Collections.sort(liste, (a, b) -> a.getName().compareToIgnoreCase(b.getName()));
    }
}
