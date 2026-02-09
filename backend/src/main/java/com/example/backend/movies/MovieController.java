package com.example.backend.movies;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.movies.dto.MovieDtos;
import com.example.backend.votes.VoteType;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/movies")
public class MovieController {

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping
    public List<MovieDtos.MovieListItem> listMovies(
            @RequestParam(name = "sort", required = false, defaultValue = "DATE") String sort,
            @RequestParam(name = "submittedBy", required = false) String submittedByUsername,
            @RequestAttribute(value = "authUserId", required = false) Long currentUserId
    ) {
        return movieService.listMovies(sort, submittedByUsername, currentUserId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MovieDtos.MovieListItem createMovie(
            @RequestAttribute(value = "authUserId", required = false) Long currentUserId,
            @RequestBody @Valid MovieDtos.CreateMovieRequest req
    ) {
        return movieService.createMovie(req, currentUserId);
    }

    public record VoteRequest(VoteType type) {
    }

    @PutMapping("/{id}/vote")
    public MovieDtos.MovieListItem vote(
            @PathVariable("id") Long movieId,
            @RequestAttribute(value = "authUserId", required = false) Long currentUserId,
            @RequestBody(required = false) VoteRequest req
    ) {
        var type = req == null ? null : req.type();
        return movieService.vote(movieId, type, currentUserId);
    }

    @ExceptionHandler(MovieService.UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    void unauthorized() {
    }

    @ExceptionHandler(MovieService.ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    void forbidden() {
    }

    @ExceptionHandler(MovieService.NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    void notFound() {
    }
}
