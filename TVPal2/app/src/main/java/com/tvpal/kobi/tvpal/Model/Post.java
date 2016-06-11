package com.tvpal.kobi.tvpal.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by Kobi on 04/06/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Post {
    String showName;
    String userEmail;
    String text;
    String date;
    int currentPart;
    boolean finished;
    int grade;
    TVShow show;


    public Post() {
    }

    public Post(String showName, String userEmail, String text, String date ,int currentPart, boolean finished, int grade,TVShow show) {
        this.showName = showName;
        this.userEmail = userEmail;
        this.text = text;
        this.date = date;
        this.currentPart = currentPart;
        this.finished = finished;
        this.grade = grade;
        this.show = show;
    }


    public TVShow getShow() {
        return show;
    }

    public void setShow(TVShow show) {
        this.show = show;
    }
    @JsonIgnore
    public String getImagePath(){
        return this.show.getImagePath();
    }
    @JsonIgnore
    public int getEpisode(){
        return this.show.getEpisode();
    }
    @JsonIgnore
    public int getNumOfChapters(){
        return this.show.getNumOfChapters();
    }
    @JsonIgnore
    @Override
    public boolean equals(Object o) {
        return this.toString().equals(o.toString());
    }
    @JsonIgnore
    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }

    @JsonIgnore
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
                ", show=" + show +
                '}';
    }

    public String getShowName() {return showName;}

    public void setShowName(String showName) {this.showName = showName;}

    @JsonIgnore
    public String getEvent()
    {
        if((this.getNumOfChapters()-this.currentPart)==0)
        {
            return "Finished";
        }
        if(this.currentPart==0)
        {
            return "Started";
        }
        if ((this.getNumOfChapters()-this.currentPart)>0)
        {
            return "Is On";
        }
        return "Error";
    }
    @JsonIgnore
    public int getProgress(){
        return ((this.getNumOfChapters()-this.currentPart)/this.getNumOfChapters());
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
