package appli;


import java.io.File;
import java.util.*;

public class TEAMSProcessor {

    private Collection<People> _allpeople = null;
    private String _fileName;
    private String _startTime;
    private String _endTime;
    private String titre;

    List<People> peopleByDuration;


    public TEAMSProcessor(File _file, String _start, String _stop, String titre) {
        /*
         csv file to read
         start time of the course
         end time of the source
        */
        this._startTime = _start;
        this._endTime = _stop;
        this.titre=titre;
        // load CSV file
        this._fileName = _file.getName();

        // CALL THE FACTORY OF TEALSAttendanceList
        var teamsFile = TEAMSAttendanceList.create(_file);


        // filter to extract data for each people
        var lines = teamsFile.get_attlist();
        if (lines != null) {
            // convert lines in data structure with people & periods
            var filter =  TEAMSAttendanceListAnalyzer.create(lines);
            // cut periods before start time and after end time
            filter.setStartAndStop(_start, _stop);

            peopleByDuration = new ArrayList<>(filter.get_peopleList().values());

            //Trier selon les cas
            if(MainController.check == 1)
            {
                trier(new  TrierNom(peopleByDuration));
            }
            else if(MainController.check == 2)
            {
                trier(new  TrierId(peopleByDuration));
            }else trier(new  TrieHeure(peopleByDuration));


            // init the people collection
            this._allpeople = peopleByDuration;//filter.get_peopleList().values();
        }
    }

    public Collection<People> get_allpeople() {
        return _allpeople;
    }

    public String toHTMLCode() {

        String html = "<!DOCTYPE html> \n <html lang=\"fr\"> \n <head> \n <meta charset=\"utf-8\"> ";
        html += "<title> Attendance Report </title> \n <link rel=\"stylesheet\" media=\"all\" href=\"visu.css\"> \n";
        html += "</head> \n <body> \n ";
        html += "<h1> Rapport de connexion </h1>\n" +
                "\n" +
                "<div id=\"blockid\">\n" +
                "<table>\n" +
                "\t<tr>\n" +
                "\t\t<th> Date : </th>\n" +
                "\t\t<td> " + this._allpeople.iterator().next().getDate() + " </td>\n" +
                "\t</tr>\n" +
                "\t<tr>\n" +
                "\t\t<th> Heure début : </th>\n" +
                "\t\t<td> " + this._startTime + " </td>\n" +
                "\t</tr>\n" +
                "\t<tr>\n" +
                "\t\t<th> Heure fin : </th>\n" +
                "\t\t<td> " + this._endTime + " </td>\n" +
                "\t</tr>\n" +
                "\t<tr>\n" +
                "\t\t<th> Cours : </th>\n" +
                "\t\t<td> "+this.titre+" </td>\n" +
                "\t</tr>\n" +
                "\t<tr>\n" +
                "\t\t<th> Fichier analysé : </th>\n" +
                "\t\t<td> " + this._fileName + " </td>\n" +
                "\t</tr>\n" +
                "\t<tr>\n" +
                "\t\t<th> Nombre de connectés : </th>\n" +
                "\t\t<td> " + this._allpeople.size() + "  </td>\n" +
                "\t</tr>\n" +
                "</table>\n" +
                "</div>\n" +
                "\n" +
                "<h2> Durées de connexion</h2>\n" +
                "\n" +
                "<p> Pour chaque personne ci-dessous, on retrouve son temps total de connexion sur la plage déclarée du cours, ainsi qu'un graphe qui indique les périodes de connexion (en vert) et d'absence de connexion (en blanc). En pointant la souris sur une zone, une bulle affiche les instants de début et de fin de période. \n" +
                "</p>";
        html += "<div id=\"blockpeople\"> ";


        //ITERATOR
        Iterator<People> people = this._allpeople.iterator();
        while(people.hasNext()){
            html += people.next().getHTMLCode();
        }

	    html += "</div> \n </body> \n </html>";
        return html;
    }
    //méthode strétégie de Trie
    public void trier (Itri trie){
       trie.trier(peopleByDuration);

    }
}
