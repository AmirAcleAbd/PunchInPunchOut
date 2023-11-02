package punchit.punchinpunchout.CustomUtil;

import punchit.punchinpunchout.QueryCenter.Session;
import punchit.punchinpunchout.QueryCenter.Student;
import punchit.punchinpunchout.QueryCenter.StudentTrack;

import java.io.*;
import java.util.ArrayList;

public class QueueTrackBKP implements Serializable {

    private static QueueTrackBKP instance = null;
    private Node front;
    private Node rear;
    private int size;

    public QueueTrackBKP() {
        front = null;
        rear = null;
        size = 0;
    }

    public static QueueTrackBKP getInstance() {
        if (instance == null) {
            try {
                FileInputStream fis = new FileInputStream("QueueTrack.ser");
                ObjectInputStream ois = new ObjectInputStream(fis);
                instance = (QueueTrackBKP) ois.readObject();
                fis.close();
                ois.close();
            } catch (IOException | ClassNotFoundException e) {
                instance = new QueueTrackBKP();
            }
        }
        return instance;
    }

    public void saveInstance() {
        try {
            FileOutputStream fos = new FileOutputStream("QueueTrack.ser");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(instance);
            fos.close();
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    public void enqueue(Student student, Session studentSession) {
        Node newNode = new Node(student, studentSession);
        if (isEmpty()) {
            front = newNode;
            rear = newNode;
        } else {
            rear.next = newNode;
            newNode.prev = rear;
            rear = newNode;
        }
        size++;
    }

    public boolean dequeue() {
        if (isEmpty()) {
            return false;
        }

        Student student = front.data;
        Session studentSession = front.studentSession;
        studentSession.setTimeOut(System.currentTimeMillis());
        student.addSession(studentSession);
        StudentTrack.getInstance().addSessionToHistory(studentSession);
        front = front.next;
        if (front != null) {
            front.prev = null;
        } else {
            rear = null; // If the last element is dequeued
        }
        size--;
        return true;
    }

    public boolean dequeuSpecific(Student earlyBird){
        if (isEmpty()) {
            throw new IllegalStateException("Queue is empty");
        }
        Node current = front;
        while(current != null){
            if(current.data == earlyBird){
                Student student = current.data;
                Session studentSession = current.studentSession;
                studentSession.setTimeOut(System.currentTimeMillis());
                student.addSession(studentSession);
                StudentTrack.getInstance().addSessionToHistory(studentSession);
                if(current.prev == null){
                    front = current.next;
                }else{
                    current.prev.next = current.next;
                }
                if(current.next == null){
                    rear = current.prev;
                }else{
                    current.next.prev = current.prev;
                }
                size--;
                return true;
            }
            current = current.next;
        }
        return false;
    }

    public ArrayList<Session> getQueue(){
        ArrayList<Session> queue = new ArrayList<>();
        Node current = front;
        while(current != null){
            queue.add(current.studentSession);
            current = current.next;
        }
        return queue;
    }

    public Student peek() {
        if (isEmpty()) {
            throw new IllegalStateException("Queue is empty");
        }
        return front.data;
    }

    private static class Node implements Serializable {
        Student data;
        Session studentSession;
        Node prev;
        Node next;

        public Node(Student data, Session studentSession) {
            this.data = data;
            this.studentSession = studentSession;
        }
    }
}
