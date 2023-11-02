package punchit.punchinpunchout.QueryCenter;

import java.io.Serializable;

public class Session implements Serializable {
    private Course course;
    private String topic;
    private String date;
    private int id;
    private long timeIn;
    private long timeOut;

    public Session(int id, Course course,String topic, String date, long timeIn){
        this.id = id;
        this.course = course;
        this.topic = topic;
        this.date = date;
        this.timeIn = timeIn;
    }

    public void setTimeOut(long timeOut){
        this.timeOut = timeOut;
    }

    public long getTimeIn(){
        return timeIn;
    }

    public long getTimeOut(){
        return timeOut;
    }

    public int getId(){
        return id;
    }

    public String getName(){
        return StudentTrack.getInstance().getStudent(id).getFullName();
    }

    public String getDate(){
        return date;
    }

    public Course getCourse(){
        return course;
    }

    public Professor getProfessor(){
        return StudentTrack.getInstance().getStudent(id).getProfessor(course);
    }

    public String getTopic(){
        return topic;
    }

    public long getDuration(){
        return (timeOut - timeIn) / 1000 / 60;
    }
}
