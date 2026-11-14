package ru.practicum.moviehub.http;

import com.sun.net.httpserver.HttpExchange;
import ru.practicum.moviehub.model.Movie;
import ru.practicum.moviehub.store.MoviesStore;

import java.io.IOException;

public class MovieByIdHandler extends BaseHttpHandler {
    private final MoviesStore store;

    public MovieByIdHandler(MoviesStore store) {
        this.store = store;
    }

    @Override
    public void handle(HttpExchange ex) throws IOException {
        String method = ex.getRequestMethod();
        if (!method.equalsIgnoreCase("GET") && !method.equalsIgnoreCase("DELETE")) {
            sendError(ex, 405, "Метод не поддерживается");
            return;
        }

        String path = ex.getRequestURI().getPath();
        String idPart = path.substring("/movies/".length());

        long id;
        try {
            id = Long.parseLong(idPart);
        } catch (NumberFormatException e) {
            sendError(ex, 400, "Некорректный ID");
            return;
        }

        if (method.equalsIgnoreCase("GET")) {
            Movie movie = store.getById(id);
            if (movie == null) {
                sendError(ex, 404, "Фильм не найден");
            } else {
                sendJson(ex, 200, gson.toJson(movie));
            }
        } else {
            boolean deleted = store.deleteById(id);
            if (deleted) {
                sendNoContent(ex);
            } else {
                sendError(ex, 404, "Фильм не найден");
            }
        }
    }
}