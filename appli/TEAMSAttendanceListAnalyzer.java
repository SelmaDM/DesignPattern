package appli;

import java.util.*;

public class TEAMSAttendanceListAnalyzer {

    private final LinkedList<String> _attlist;
    private HashMap<String, People> _peopleList;

    // FACTORY BY REPLACING CONSTRUCTOR CALL BY STATIC METHOD
    private TEAMSAttendanceListAnalyzer(LinkedList<String> _attlist) {
        this._attlist = _attlist;
        this.processList();
    }
    static TEAMSAttendanceListAnalyzer create(LinkedList<String> _attlist) {
        return new TEAMSAttendanceListAnalyzer( _attlist);
    }

    private void processList() {
        if (this._attlist != null) {

            //ITERATOR
            this._peopleList = new HashMap<>();
            Iterator<String> element = this._attlist.iterator();
            // first line unused
            element.next();
            // process all lines
            while (element.hasNext()) {
                String input = element.next();

                String[] infos = input.split("\t");
                if (infos.length==3) {
                    String identite = infos[0];
                    String action = infos[1];
                    String instant = infos[2];

                    if (this._peopleList.containsKey(identite)) {
                        People person = this._peopleList.get(identite);
                        person.addPeriod(action, instant);
                        this._peopleList.replace(identite,person);
                    } else {
                        People person = People.create(identite);
                        person.addPeriod(action, instant);
                        this._peopleList.put(identite, person);
                    }
                }
            }
        }
    }

    public HashMap<String, People> get_peopleList() {
        return _peopleList;
    }

    public void setStartAndStop(String start, String stop) {
        Collection<People> allpeople = _peopleList.values();

        //ITERATOR
        Iterator<People> person = allpeople.iterator();
        while(person.hasNext())
        {
            var period = person.next();
            period.forceEndTimeAt(stop);
            period.forceStartTimeAt(start);
        }


    }

}
