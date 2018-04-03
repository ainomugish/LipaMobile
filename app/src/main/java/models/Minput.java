/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

import entities.CanteenTrans;
import entities.Student;
import eventBusClasses.StudentChargeReply;

import java.util.List;

/**
 *
 * @author Mac
 */
public class Minput {
    
    public String apiKey;
    
    //CHEC STUDENT ACCOUNT NUMBER
    public String studentAccNumber;
    
    //ADD STUDENT 
    public Student student;
    
    //LOGIN
    public String username;
    public String password;
    
    
    //UPDATE BALANCE
    public int update_amount;
    public String transaction_id;

    //CANTEEN STUFF
    public CanteenTrans canteenTrans;

    public StudentChargeReply studentChargeReply;
    
}
