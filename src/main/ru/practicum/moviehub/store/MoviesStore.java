package ru.practicum.moviehub.store;

import ru.practicum.moviehub.model.Movie;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MoviesStore {
    private final Map<Long, Movie> movies = new HashMap<>();
    private long currentId = 1L;

    public List<Movie> getAll() {
        return new ArrayList<>(movies.values());
    }

    public List<Movie> getByYear(int year) {
        List<Movie> result = new ArrayList<>();
        for (Movie movie : movies.values()) {
            if (movie.getYear() != null && movie.getYear() == year) {
                result.add(movie);
            }
        }
        return result;
    }

    public Movie getById(long id) {
        return movies.get(id);
    }

    public Movie add(Movie movie) {
        long id = currentId++;
        Movie saved = new Movie(id, movie.getTitle(), movie.getYear());
        movies.put(id, saved);
        return saved;
    }

    public boolean deleteById(long id) {
        return movies.remove(id) != null;
    }

    public void clear() {
        movies.clear();
        currentId = 1L;
    }
}