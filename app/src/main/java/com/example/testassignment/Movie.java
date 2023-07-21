package com.example.testassignment;

public class Movie {

    private int movieID;
    private String title;
    private String director;
    private int yearRelease;
    private float rating;

    public Movie() {
    }

    public Movie(String title, String director, int yearRelease, float rating) {
        this.title = title;
        this.director = director;
        this.yearRelease = yearRelease;
        this.rating = rating;
    }

    public Movie(int movieID, String title, String director, int yearRelease, float rating) {
        this.movieID = movieID;
        this.title = title;
        this.director = director;
        this.yearRelease = yearRelease;
        this.rating = rating;
    }

    public int getMovieID() {
        return movieID;
    }

    public void setMovieID(int movieID) {
        this.movieID = movieID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public int getYearRelease() {
        return yearRelease;
    }

    public void setYearRelease(int yearRelease) {
        this.yearRelease = yearRelease;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

}
