package models;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import entities.MealTransactions;
import entities.Meals;
import entities.Schools;
import entities.Student;
import entities.StudentCredz;
import entities.GatePass;

/**
 *
 * @author Mac
 */
public class Trans {

    //public Gson gson;
    static Gson toStringGson = new GsonBuilder().setPrettyPrinting().create();

    public String apiKey;

    public String transType;
    public boolean result;
    public String errorMessage;
    public String message;
    public Integer schoolId;

    //CHECK STUDENT ACC VARS
    // public boolean validStudentAcc;
    public Student student;
    public Meals meals;
    public MealTransactions mealTransactions;

    //LIST SCHOOLS
    public List<Schools> schoolList;

    //LIST SCHOOL
    public List<Student> studentList;

    //LIST GATE PASS
    public List<GatePass> gatePassTrans;

    public int pin_change_log_num;
    public int meal_change_log_num;

    public List<StudentCredz> studentCredzList;
    public List<Meals> mealsTrans;
    //public List<MealTransactions> mealTransactions;

    public int deviceBalance;

    public Trans(String type) {
        System.out.println("+++++++++++: TRANS INIT:" + type);
        this.transType = type;
        //  gson = new Gson();
    }



    public Trans() {
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
