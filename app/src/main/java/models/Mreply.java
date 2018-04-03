package models;

import com.google.gson.Gson;
        import com.google.gson.GsonBuilder;
        import entities.Schools;
        import entities.Student;
        import java.util.List;

/**
 *
 * @author Mac
 */
public class Mreply {

    //public Gson gson;
    static Gson toStringGson = new GsonBuilder().setPrettyPrinting().create();

    public String transType;
    public boolean result;
    public String errorMessage;
    public String message;

    //CHECK STUDENT ACC VARS
    // public boolean validStudentAcc;
    public Student student;

    //LIST SCHOOLS
    public List<Schools> schoolList;

    //LIST SCHOOL
    public List<Student> studentList;

    public Mreply(String type) {
        System.out.println("+++++++++++: TRANS INIT:" + type);
        this.transType = type;
        //  gson = new Gson();
    }

    public Mreply() {
    }

    //UTILS FOR PROCESSING
    //SET ERROR
    public void setError(String error) {
        result = false;
        errorMessage = error;
    }

    //SET SUCCESS
    public void setSucc(String message) {
        result = true;
        this.message = message;
    }

    @Override
    public String toString() {
        return toStringGson.toJson(this);
    }

}
