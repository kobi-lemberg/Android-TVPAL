package com.tvpal.kobi.tvpal.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by Kobi on 04/06/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TVShow {
    String name;
    String mainActor;
    int season;
    int episode;
    String category;
    String summery;
    String lastUpdated;
    String imagePath;

    public TVShow() {}

/*    public TVShow(String id, String name, String mainActor, int episodes, String category, String lastUpdated, String summery) {
        this.id = id;
        this.name = name;
        this.mainActor = mainActor;
        this.episodes = episodes;
        this.category = category;
        this.lastUpdated = lastUpdated;
        this.summery = summery;
    }*/

    public TVShow(String name, String mainActor,int season,int episode ,String category, String lastUpdated, String summery,String imagePath) {
        this.name = name;
        this.mainActor = mainActor;
        this.season = season;
        this.episode = episode;
        this.category = category;
        this.lastUpdated = lastUpdated;
        this.summery = summery;
        this.imagePath = imagePath;
    }

    @JsonIgnore
    @Override
    public String toString() {
        return "TVShow{" +
                "name='" + name + '\'' +
                ", mainActor='" + mainActor + '\'' +
                ", season=" + season +
                ", episode=" + episode +
                ", category='" + category + '\'' +
                ", summery='" + summery + '\'' +
                ", lastUpdated='" + lastUpdated + '\'' +
                ", imagePath='" + imagePath + '\'' +
                '}';
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


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMainActor() {
        return mainActor;
    }

    public void setMainActor(String mainActor) {
        this.mainActor = mainActor;
    }

    public int getSeason() {
        return season;
    }

    public void setSeason(int season) {
        this.season = season;
    }

    public int getEpisode() {
        return episode;
    }

    public void setEpisode(int episode) {
        this.episode = episode;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSummery() {
        return summery;
    }

    public void setSummery(String summery) {
        this.summery = summery;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
