package punchit.punchinpunchout.QueryCenter;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;

public class QueueTrack implements Serializable {

    private static QueueTrack instance = null;
    private LinkedList<Session> openQueue;


    public QueueTrack() {
        openQueue = new LinkedList<>();
    }

    public void enqueue(Session session){
        openQueue.add(session);
    }

    public void dequeue(){
        Session topSesh = openQueue.getFirst();
        topSesh.setTimeOut(System.currentTimeMillis());
        StudentTrack.getInstance().getStudent(topSesh.getId()).addSession(topSesh);
        StudentTrack.getInstance().addSessionToHistory(topSesh);
        openQueue.removeFirst();
    }

    public void dequeue(int id){
        for(Session s: openQueue){
            if(s.getId() == id){
                s.setTimeOut(System.currentTimeMillis());
                StudentTrack.getInstance().getStudent(s.getId()).addSession(s);
                StudentTrack.getInstance().addSessionToHistory(s);
                openQueue.remove(s);
                break;
            }
        }
    }

    public Session contains(int id){
        for(Session s: openQueue){
            if(s.getId() == id){
                return s;
            }
        }
        return null;
    }

    public void dequeueAll(){
        for(Session s: openQueue){
            s.setTimeOut(System.currentTimeMillis());
            StudentTrack.getInstance().getStudent(s.getId()).addSession(s);
            StudentTrack.getInstance().addSessionToHistory(s);
        }
        openQueue.clear();
    }

    public LinkedList<Session> getOpenQueue(){
        return openQueue;
    }

    public static QueueTrack getInstance() {
        if (instance == null) {
            try {
                FileInputStream fis = new FileInputStream("target/CrytsalPalace/QueueTrack.ser");
                ObjectInputStream ois = new ObjectInputStream(fis);
                instance = (QueueTrack) ois.readObject();
                fis.close();
                ois.close();
            } catch (IOException | ClassNotFoundException e) {
                instance = new QueueTrack();
            }
        }
        return instance;
    }

    public void saveInstance() {
        try {
            FileOutputStream fos = new FileOutputStream("target/CrytsalPalace/QueueTrack.ser");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(instance);
            fos.close();
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
