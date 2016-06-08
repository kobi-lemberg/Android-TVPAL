package com.tvpal.kobi.tvpal.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by Kobi on 04/06/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TVShow {
    String id;
    String name;
    String mainActor;
    int episodes;
    String category;
    String summery;
    String lastUpdated;

    public TVShow() {}

    public TVShow(String id, String name, String mainActor, int episodes, String category, String lastUpdated, String summery) {
        this.id = id;
        this.name = name;
        this.mainActor = mainActor;
        this.episodes = episodes;
        this.category = category;
        this.lastUpdated = lastUpdated;
        this.summery = summery;
    }

    public TVShow(String name, String mainActor, int episodes, String category, String lastUpdated, String summery) {
        this.id = "";
        this.name = name;
        this.mainActor = mainActor;
        this.episodes = episodes;
        this.category = category;
        this.lastUpdated = lastUpdated;
        this.summery = summery;
    }
    @JsonIgnore
    @Override
    public String toString() {
        return "TVShow{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", mainActor='" + mainActor + '\'' +
                ", episodes=" + episodes +
                ", category='" + category + '\'' +
                ", summery='" + summery + '\'' +
                ", lastUpdated='" + lastUpdated + '\'' +
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public int getEpisodes() {
        return episodes;
    }

    public void setEpisodes(int episodes) {
        this.episodes = episodes;
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

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }


}
