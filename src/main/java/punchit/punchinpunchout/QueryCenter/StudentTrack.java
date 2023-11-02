package punchit.punchinpunchout.QueryCenter;

import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class StudentTrack implements Serializable {

    private static StudentTrack instance;

    private ArrayList<Student> students;
    private ArrayList<Session> totalSessionHistory;
    private static int uniqueID;

    private HashMap<String, String> adminUsers;

    private StudentTrack(){
        students = new ArrayList<>();
        totalSessionHistory = new ArrayList<>();
        adminUsers = new HashMap<>();
        uniqueID = 23456789;
    }

    public static StudentTrack getInstance(){
        if (instance == null) {
            try {
                FileInputStream fis = new FileInputStream("target/CrytsalPalace/StudentTrack.ser");
                ObjectInputStream ois = new ObjectInputStream(fis);
                instance = (StudentTrack) ois.readObject();
                fis.close();
                ois.close();
            } catch (IOException | ClassNotFoundException e) {
                instance = new StudentTrack();
            }
        }
        return instance;
    }

    public void saveInstance() {
        try {
            FileOutputStream fos = new FileOutputStream("target/CrytsalPalace/StudentTrack.ser");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(instance);
            fos.close();
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Student> getStudents(){
        return students;
    }

    public ArrayList<Session> getTotalSessionHistory(){
        return totalSessionHistory;
    }

    public Student getStudent(int id){
        for(Student s: students){
            if(s.getId() == id){
                return s;
            }
        }
        return null;
    }

    public void addSessionToHistory(Session session){
        totalSessionHistory.add(session);
    }

    public void populateSessionHistory(){
        for(Student s: students){
            for(Session session: s.getSessionHistory()){
                totalSessionHistory.add(session);
            }
        }
    }

    public boolean checkForAdmin(String username){
        return adminUsers.containsKey(username);
    }

    public Session getLastSession(){
        if(totalSessionHistory.size() == 0){
            return null;
        }
        return totalSessionHistory.get(totalSessionHistory.size() - 1);
    }

    public boolean addAdmin(String username, String password){
        if(adminUsers.containsKey(username)){
            return false;
        }
        adminUsers.put(username, password);
        return true;
    }

    public boolean validateLoginAdmin(String username, String password){
        if(adminUsers.containsKey(username)){
            return adminUsers.get(username).equals(password);
        }
        return false;
    }

    public Student addStudent(String firstName, String lastName){
        //random 8 digit number
        int randomID = (int) (Math.random() * 100000000);
        Student student = new Student(randomID, firstName, lastName);
        while(isThere(student)){
            randomID = (int) (Math.random() * 100000000);
            student.setId(randomID);
        }
        students.add(student);
        return student;
    }


    public void addStudent(Student student){
        students.add(student);
    }

    public boolean removeStudent(Student student){
        if(!students.contains(student)){
            return false;
        }
        students.remove(student);
        return true;
    }

    public void exportToCSV(){
        try {
            FileWriter writer = new FileWriter("target/CrytsalPalace/StudentTrack.csv");
            writer.append("ID,First Name,Last Name,Session History\n");
            for(Student s: students){
                writer.append(s.getId() + "," + s.getFirstName() + "," + s.getLastName() + ",");
                for(Session session: s.getSessionHistory()){
                    writer.append(session.toString() + ",");
                }
                writer.append("\n");
            }
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isThere(Student student){
        for(Student s: students){
            if(s.getId() == student.getId()){
                return true;
            }
        }
        return false;
    }

    public boolean isThere(int id){
        for(Student s: students){
            if(s.getId() == id){
                return true;
            }
        }
        return false;
    }
}
