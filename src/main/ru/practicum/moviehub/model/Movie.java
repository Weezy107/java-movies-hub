package ru.practicum.moviehub.model;

import java.util.Objects;

public class Movie {
    private Long id;
    private String title;
    private Integer year;

    public Movie() {
    }

    public Movie(String title, Integer year) {
        this.title = title;
        this.year = year;
    }

    public Movie(Long id, String title, Integer year) {
        this.id = id;
        this.title = title;
        this.year = year;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Movie movie = (Movie) o;
        return Objects.equals(id, movie.id)
                && Objects.equals(title, movie.title)
                && Objects.equals(year, movie.year);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, year);
    }
}