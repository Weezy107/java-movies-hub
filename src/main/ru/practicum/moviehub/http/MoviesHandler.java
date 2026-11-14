package ru.practicum.moviehub.http;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import ru.practicum.moviehub.model.Movie;
import ru.practicum.moviehub.store.MoviesStore;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;

public class MoviesHandler extends BaseHttpHandler {
    private static final int MIN_YEAR = 1888;
    private static final int MAX_TITLE_LENGTH = 100;
    private final MoviesStore store;

    public MoviesHandler(MoviesStore store) {
        this.store = store;
    }

    @Override
    public void handle(HttpExchange ex) throws IOException {
        String method = ex.getRequestMethod();
        String query = ex.getRequestURI().getQuery();

        if (method.equalsIgnoreCase("GET")) {
            if (query == null || query.isBlank()) {
                sendJson(ex, 200, gson.toJson(store.getAll()));
            } else {
                handleFilterByYear(ex, query);
            }
        } else if (method.equalsIgnoreCase("POST")) {
            handleCreateMovie(ex);
        } else {
            sendError(ex, 405, "Метод не поддерживается");
        }
    }

    private void handleFilterByYear(HttpExchange ex, String query) throws IOException {
        String yearValue = null;
        String[] params = query.split("&");
        for (String param : params) {
            String[] pair = param.split("=", 2);
            if (pair.length == 2 && "year".equals(pair[0])) {
                yearValue = pair[1];
                break;
            }
        }

        if (yearValue == null) {
            sendError(ex, 400, "Некорректный параметр запроса — 'year'");
            return;
        }

        try {
            int year = Integer.parseInt(yearValue);
            sendJson(ex, 200, gson.toJson(store.getByYear(year)));
        } catch (NumberFormatException e) {
            sendError(ex, 400, "Некорректный параметр запроса — 'year'");
        }
    }

    private void handleCreateMovie(HttpExchange ex) throws IOException {
        String contentType = ex.getRequestHeaders().getFirst(HEADER_CONTENT_TYPE);
        if (contentType == null || !contentType.toLowerCase().startsWith("application/json")) {
            sendError(ex, 415, "Неподдерживаемый Content-Type");
            return;
        }

        String body = new String(ex.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        if (body.isBlank()) {
            sendError(ex, 400, "Тело запроса не должно быть пустым");
            return;
        }

        Movie movie;
        try {
            movie = gson.fromJson(body, Movie.class);
        } catch (JsonSyntaxException e) {
            sendError(ex, 400, "Некорректный JSON");
            return;
        }

        if (movie == null) {
            sendError(ex, 400, "Некорректный JSON");
            return;
        }

        List<String> details = validateMovie(movie);
        if (!details.isEmpty()) {
            sendValidationError(ex, "Ошибка валидации", details);
            return;
        }

        Movie created = store.add(movie);
        sendJson(ex, 201, gson.toJson(created));
    }

    private List<String> validateMovie(Movie movie) {
        List<String> errors = new ArrayList<>();
        String title = movie.getTitle();
        if (title == null || title.isBlank()) {
            errors.add("название не должно быть пустым");
        } else if (title.length() > MAX_TITLE_LENGTH) {
            errors.add("длина названия не должна превышать 100 символов");
        }

        int maxYear = Year.now().getValue() + 1;
        Integer year = movie.getYear();
        if (year == null || year < MIN_YEAR || year > maxYear) {
            errors.add("год должен быть между " + MIN_YEAR + " и " + maxYear);
        }
        return errors;
    }
}