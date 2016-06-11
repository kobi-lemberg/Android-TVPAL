package com.tvpal.kobi.tvpal.Model;


/**
 * Created by Kobi on 04/06/2016.
 */
public class Post {
    String showName;
    String userEmail;
    String text;
    String date;
    int currentPart;
    boolean finished;
    int grade;
    int season;
    String event;
    String imagePath ;


    public Post() {
    }

    public Post(String showName, String userEmail, String text, String date,int season ,int currentPart, boolean finished, int grade,String event,String imagePath) {
        this.showName = showName;
        this.userEmail = userEmail;
        this.text = text;
        this.date = date;
        this.season = season;
        this.currentPart = currentPart;
        this.finished = finished;
        this.grade = grade;
        this.event = event;
        this.imagePath = imagePath;
    }


    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
    public int getSeason() {
        return season;
    }

    public void setSeason(int season) {
        this.season = season;
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
                ", showID='" + showName + '\'' +
                ", userEmail='" + userEmail + '\'' +
                ", text='" + text + '\'' +
                ", date='" + date + '\'' +
                ", currentPart=" + currentPart +
                ", finished=" + finished +
                ", grade=" + grade +
                '}';
    }

    public String getShowName() {return showName;}

    public void setShowName(String showName) {this.showName = showName;}

    public String getEvent() {return event;}

    public void setEvent(String event) {this.event = event;}

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
