package punchit.punchinpunchout.QueryCenter;

import java.io.Serializable;
import java.util.*;

public class Student implements Serializable {
    private int id;
    private String firstName;
    private String lastName;
    private Set<Course> courses;
    private Map<Course, Professor> professorCourseMap;
    private int minutesTotal;
    private ArrayList<Session> sessionHistory;


    public Student(int id, String firstName, String lastName){
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.minutesTotal = 0;
        this.courses = EnumSet.noneOf(Course.class);
        this.professorCourseMap = new HashMap<>();
        this.sessionHistory = new ArrayList<>();

    }

    public Student(String firstName, String lastName, int id, Course course, Professor professor){
        this.firstName = firstName;
        this.lastName = lastName;
        this.id = id;
        this.minutesTotal = 0;
        this.courses = EnumSet.noneOf(Course.class);
        this.professorCourseMap = new HashMap<>();
        this.sessionHistory = new ArrayList<>();
        addCourse(course, professor);
    }

    public String getFirstName(){
        return firstName;
    }

    public String getLastName(){
        return lastName;
    }

    public int getId(){
        return id;
    }

    public void setFirstName(String firstName){
        this.firstName = firstName;
    }

    public void setLastName(String lastName){
        this.lastName = lastName;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getIDString(){
        return String.valueOf(id);
    }

    public String getFullName(){
        return firstName + " " + lastName;
    }

    public int getMinutesTotal() {
        return minutesTotal;
    }

    public boolean addCourse(Course course, Professor professor){
        if(courses.contains(course)){
            return false;
        }
        courses.add(course);
        professorCourseMap.put(course, professor);
        return true;
    }

    public boolean addCourse(Course course){
        Professor[] p = Professor.values();
        if(courses.contains(course)){
            return false;
        }
        courses.add(course);
        int random = (int)(Math.random() * 5);
        professorCourseMap.put(course, p[random]);
        return true;
    }

    public boolean removeCourse(Course course){
        if(!courses.contains(course)){
            return false;
        }
        courses.remove(course);
        professorCourseMap.remove(course);
        return true;
    }

    public void clearCourses(){
        courses.clear();
        professorCourseMap.clear();
    }

    public Set<Course> getCourses(){
        return courses;
    }

    public Professor getProfessor(Course course){
        return professorCourseMap.get(course);
    }

    public boolean addSession(Session session){
        if(sessionHistory.contains(session)){
            return false;
        }
        sessionHistory.add(session);
        minutesTotal += session.getDuration();
        return true;
    }

    public Session getSession(String date){
        for(Session session: sessionHistory){
            if(session.getDate().equals(date)){
                return session;
            }
        }
        return null;
    }

    public ArrayList<Session> getSessionHistory(){
        return sessionHistory;
    }

}
