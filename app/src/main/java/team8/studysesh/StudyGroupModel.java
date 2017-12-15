package team8.studysesh;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by sebastian on 11/2/15.
 */
public class StudyGroupModel {

    public String owner = "";
    public String theClass = "";
    public String location = "";
    public int cap = 0;
    public String description = "";
    public int members = 0;
    public String time = "";

    StudyGroupModel(String inputOwner, String inputClass, String inputLocation, int inputCapacity,
                    String inputDescription, String inputTime) {
        owner = inputOwner;
        theClass = inputClass;
        location = inputLocation;
        cap = inputCapacity;
        members = 1;
        description = inputDescription;
        time = inputTime;
    }

    public String toString() {
        String toReturn = theClass + "<br>" + "Started: "+ time
                + "<br>Location: " + location + "<br>Capacity: " + members +"/" + cap;
        return toReturn;
    }

}
