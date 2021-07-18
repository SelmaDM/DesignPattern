package appli;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;

import java.time.LocalTime;
import java.util.*;
import java.util.List;

public class MainController {
    public Label HelloWorld;

  @FXML
  private Rectangle rectDrag;
  @FXML
  private Label dropped,labeltitre,labeldate,labelheuremax,labelheuremin;
  @FXML
  private javafx.scene.control.TextField libelletext;
    @FXML
    private javafx.scene.control.RadioButton buttonId,buttonNom ;
    @FXML
    private javafx.scene.control.ComboBox heure_debut,heure_fin,minute_debut,minute_fin;
    @FXML
    private AnchorPane ap;
    @FXML
    private Button button;


    static int statID=0, check=0;
    static int statNom=0;
    static int statPlanning=0;

    File selectedFile;
    LocalDate jour;
    LocalDateTime startTime;
    LocalDateTime heure_min;
    LocalDateTime heure_max;

    String chemin,titre;

    String heures[] =
            { "00","01","02","03","04","05","06","07","08","09","10","11","12","13","14","15","16","17","18","19","20","21","22","23","23"};
    String minutes[] =
            { "00","01","02","03","04","05","06","07","08","09","10","11","12","13","14","15","16","17","18","19","20",
                    "21","22","23","23", "24","25","26","27","28","29",
                    "30","31","32","33","34","35","36","37","38","39",
                    "40","41","42","43","44","45","46","47","48","49",
                    "50","51","52","53","54","55","56","57","58","59"};

    @FXML
    public javafx.scene.control.CheckBox checkSansId,checkSansNom,checkSansPlanning;


    public void babaHello(){
        heure_debut.getItems().addAll(heures);
        heure_fin.getItems().addAll(heures);
        minute_debut.getItems().addAll(minutes);
        minute_fin.getItems().addAll(minutes);


        rectDrag.setOnDragOver(new EventHandler<DragEvent>() {

            @Override
            public void handle(DragEvent event) {
                Dragboard db = event.getDragboard();
                if (db.hasFiles()) {
                    event.acceptTransferModes(TransferMode.COPY);
                } else {
                    event.consume();
                }
            }
        });
        rectDrag.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                Dragboard db = event.getDragboard();
                boolean success = false;
                if (db.hasFiles()) {
                    dropped.setText(db.getFiles().toString());
                    //rectDrag.setFill(new Image(new FileInputStream(db) ));
                    success = true;
                }

                event.setDropCompleted(success);
                event.consume();
                // on récupère le chemin du ficheir
                chemin=dropped.getText().substring(1,dropped.getText().length()-1);
                int index=chemin.lastIndexOf("\\")+1;
                String nomFichier=chemin.substring(index); // oui je sais j'aurais pu faire un getName() mais ça aurait été moins drôle

                //on affiche le nom du fichier sur l'IHM
                labeltitre.setText(nomFichier);

                //on crée un fichier avec le chemin récupéré
                selectedFile = new File(chemin);

                //on récupère et on affiche la date du cours en récupérant la date du premier participant
                var teamsProcessor = new TEAMSProcessor(selectedFile,"19/01/2021 à 10:15:00", "19/01/2021 à 11:45:00",titre);
                Optional<People> firstElement = teamsProcessor.get_allpeople().stream().findFirst();
                startTime = TEAMSDateTimeConverter.StringToLocalDateTime(firstElement.get().get_start());
                labeldate.setText(startTime.getDayOfMonth()+" "+startTime.getMonth()+" "+startTime.getYear());


                //on récupère l'heure minimum d'arrivé et l'heure maximum du départ
                Collection<LocalDateTime> heureDebut= new ArrayList<>();
                ArrayList<LocalDateTime> heureFin= new ArrayList<>();

                // CALL THE FACTORY OF TEALSAttendanceList
                var teamsFile =  TEAMSAttendanceList.create(selectedFile);
                var lines = teamsFile.get_attlist();
                if (lines != null) {
                    // convert lines in data structure with people & periods
                    var filter =  TEAMSAttendanceListAnalyzer.create(lines);

                    // on récupère la liste des peoples
                    List<People> peopleList = new ArrayList<>(filter.get_peopleList().values());
                    Iterator people = peopleList.iterator();
                    while(people.hasNext()){
                        People p= (People) people.next();
                        Iterator<TEAMSPeriod> periods = p.get_periodList().iterator();
                       // System.out.println(periods.next());
                        while (periods.hasNext()) {
                            //on récupère les toutes les heures d'arrivées et de départs
                            var period = periods.next();
                            LocalDateTime begin = period.get_start();
                            LocalDateTime end = period.get_end();
                            heureDebut.add(begin);
                            if (end!=null) heureFin.add(end);

                        }

                    }

                }
                //on récupère l'heure minimum d'arrivée
                String heureminimum=heureDebut.stream().min(LocalDateTime::compareTo).toString();
                //on récupère l'heure maximum départ
                String heuremaximum=heureFin.stream().max(LocalDateTime::compareTo).toString();

                heure_min = LocalDateTime.parse(heureminimum.substring(9,heureminimum.length()-1));
                heure_max = LocalDateTime.parse(heuremaximum.substring(9,heuremaximum.length()-1));

                labelheuremax.setText(heure_max.toLocalTime().toString());
                labelheuremin.setText(heure_min.toLocalTime().toString());

            }
        });

    }

    public void sayHelloWorld(ActionEvent actionEvent) {

        //
        affichagePeoples();
        listnerRadioButton();
        LocalTime hd=LocalTime.parse(heure_debut.getValue().toString()+":"+minute_debut.getValue().toString()+":"+"00");
        LocalTime hf=LocalTime.parse(heure_fin.getValue().toString()+":"+minute_fin.getValue().toString()+":"+"00");
//        Stage stage = (Stage) ap.getScene().getWindow();

        //vérifier que les heures saisies soient cohérentes
        if (heure_max.toLocalTime().compareTo(hd)<0 || heure_min.toLocalTime().compareTo(hf)>0 ){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERREUR!!");
            alert.setHeaderText("Les heures saisies ne correspondent pas au planning du fichier !!");
            alert.showAndWait();
        }else {

        try {

            jour = startTime.toLocalDate();
            String jourS=jour.toString();

            //on affiche l'heure et la date du debut et de la fin du cours
            String heuredebut= jourS.substring(8,10)+"/"+jourS.substring(5,7)+"/"+jourS.substring(0,4)+" à "+heure_debut.getValue().toString()+":"+minute_debut.getValue().toString()+":"+"00";
            String heurefin=jourS.substring(8,10)+"/"+jourS.substring(5,7)+"/"+jourS.substring(0,4)+" à "+heure_fin.getValue().toString()+":"+minute_fin.getValue().toString()+":"+"00";




            // on récupère le nom de la matière pour l'afficher
            titre=libelletext.getText();
            var teamsProcessor = new TEAMSProcessor(selectedFile,heuredebut, heurefin,titre);

            System.out.println( teamsProcessor.toHTMLCode() );
            try {
                //remplissage du fichier index.html avec le code html de la page
                FileWriter myWriter = new FileWriter("index.html");
                myWriter.write(teamsProcessor.toHTMLCode());
                myWriter.close();


                //Enregistrer le fichier en .html et .txt
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Enregistrer votre fichier");
                fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("HTML", "*.html"),
                        new FileChooser.ExtensionFilter("Texte", "*.txt"));
                fileChooser.setInitialFileName("index");
                Stage thisStage = (Stage) button.getScene().getWindow();
                File file = fileChooser.showSaveDialog(thisStage);
                FileWriter mon = new FileWriter(file);
                mon.write(teamsProcessor.toHTMLCode());
                mon.close();



                // Affichage de la page HTML
                File htmlFile = new File("index.html");
                Desktop.getDesktop().browse(htmlFile.toURI());
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        } catch (NullPointerException e) {

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERREUR!!");
            alert.setHeaderText("Vous n'avez pas introduit de fichier ou les heures du début/fin du cours !!");
            alert.showAndWait();

        }}


    }
    /*
    * Methode pour vérifier le radiobutton sélectionné pour le tri
     */

    public void listnerRadioButton(){
        if (buttonNom.isSelected())
        {
            check=1;
        }else if (buttonId.isSelected()){
            check=2;
        }
            }
/*
* Methode pour affichage selon le checkbox selectionné
 */
    public void affichagePeoples(){
        if (checkSansId.isSelected()==true){
            statID=1;
        }else statID=0;

        if (checkSansNom.isSelected()==true){
            statNom=1;
        }else statNom=0;
        if (checkSansPlanning.isSelected()==true){
            statPlanning=1;
        }else statPlanning=0;


    }
}
