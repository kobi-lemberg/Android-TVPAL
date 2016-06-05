package com.tvpal.kobi.tvpal.Model;


/**
 * Created by Kobi on 04/06/2016.
 */
public class Post {
    String postID;
    String showID;
    String userEmail;
    String text;
    String date;
    int currentPart;
    boolean finished;
    int grade;

    public Post() {
    }

    public Post(String showID, String userEmail, String text, String date, int currentPart, boolean finished, int grade) {
        this.showID = showID;
        this.userEmail = userEmail;
        this.text = text;
        this.date = date;
        this.currentPart = currentPart;
        this.finished = finished;
        this.grade = grade;
    }

    public Post(String postID, String showID, String userEmail, String date, String text, int currentPart, boolean finished, int grade) {
        this.postID = postID;
        this.showID = showID;
        this.userEmail = userEmail;
        this.date = date;
        this.text = text;
        this.currentPart = currentPart;
        this.finished = finished;
        this.grade = grade;
    }

    @Override
    public boolean equals(Object o) {
        return this.toString().equals(o.toString());
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }

    @Override
    public String toString() {
        return "Post{" +
                "postID='" + postID + '\'' +
                ", showID='" + showID + '\'' +
                ", userEmail='" + userEmail + '\'' +
                ", text='" + text + '\'' +
                ", date='" + date + '\'' +
                ", currentPart=" + currentPart +
                ", finished=" + finished +
                ", grade=" + grade +
                '}';
    }

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public String getShowID() {
        return showID;
    }

    public void setShowID(String showID) {
        this.showID = showID;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public int getCurrentPart() {
        return currentPart;
    }

    public void setCurrentPart(int currentPart) {
        this.currentPart = currentPart;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }
}
