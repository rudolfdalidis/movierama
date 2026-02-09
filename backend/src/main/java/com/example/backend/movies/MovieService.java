package com.example.backend.movies;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.movies.dto.MovieDtos;
import com.example.backend.movies.dto.MovieRow;
import com.example.backend.users.UserRepository;
import com.example.backend.votes.Vote;
import com.example.backend.votes.VoteRepository;
import com.example.backend.votes.VoteType;

@Service
public class MovieService {

    private final MovieRepository movieRepository;
    private final UserRepository userRepository;
    private final VoteRepository voteRepository;

    public MovieService(MovieRepository movieRepository, UserRepository userRepository, VoteRepository voteRepository) {
        this.movieRepository = movieRepository;
        this.userRepository = userRepository;
        this.voteRepository = voteRepository;
    }

    @Transactional(readOnly = true)
    public List<MovieDtos.MovieListItem> listMovies(String sort, String submittedByUsername, Long currentUserId) {
        var safeSort = normalizeSort(sort);
        return movieRepository.findMovieRows(safeSort, blankToNull(submittedByUsername), currentUserId)
                .stream()
                .map(row -> toListItem(row, currentUserId))
                .toList();
    }

    @Transactional
    public MovieDtos.MovieListItem createMovie(MovieDtos.CreateMovieRequest req, Long currentUserId) {
        if (currentUserId == null) throw new UnauthorizedException();

        var user = userRepository.findById(currentUserId).orElseThrow();

        var m = new Movie();
        m.setTitle(req.title().trim());
        m.setDescription(req.description().trim());
        m.setSubmittedBy(user);
        movieRepository.save(m);

        var row = movieRepository.findMovieRowById(m.getId(), currentUserId);
        return toListItem(row, currentUserId);
    }

    /**
     * Vote semantics:
     * - If the user hasn't voted: creates a vote.
     * - If the user votes the opposite: switches.
     * - If the user votes the same: retracts (deletes).
     * - If type is null: retracts.
     */
    @Transactional
    public MovieDtos.MovieListItem vote(Long movieId, VoteType type, Long currentUserId) {
        if (currentUserId == null) throw new UnauthorizedException();

        var movie = movieRepository.findById(movieId).orElseThrow(NotFoundException::new);
        if (movie.getSubmittedBy().getId().equals(currentUserId)) {
            throw new ForbiddenException();
        }

        var existingOpt = voteRepository.findByUserIdAndMovieId(currentUserId, movieId);

        if (type == null) {
            existingOpt.ifPresent(voteRepository::delete);
        } else if (existingOpt.isEmpty()) {
            var user = userRepository.findById(currentUserId).orElseThrow();
            var v = new Vote();
            v.setUser(user);
            v.setMovie(movie);
            v.setType(type);
            voteRepository.save(v);
        } else {
            var existing = existingOpt.get();
            if (existing.getType() == type) {
                // same vote => retract
                voteRepository.delete(existing);
            } else {
                existing.setType(type);
                voteRepository.save(existing);
            }
        }

        var row = movieRepository.findMovieRowById(movieId, currentUserId);
        return toListItem(row, currentUserId);
    }

    private MovieDtos.MovieListItem toListItem(MovieRow row, Long currentUserId) {
        var user = new MovieDtos.UserSummary(row.getSubmittedById(), row.getSubmittedByUsername());
        var canVote = currentUserId != null && !row.getSubmittedById().equals(currentUserId);
        return new MovieDtos.MovieListItem(
                row.getId(),
                row.getTitle(),
                row.getDescription(),
                row.getCreatedAt(),
                user,
                row.getLikesCount(),
                row.getHatesCount(),
                row.getMyVote(),
                canVote
        );
    }

    private static String blankToNull(String s) {
        if (s == null) return null;
        var t = s.trim();
        return t.isEmpty() ? null : t;
    }

    private static String normalizeSort(String sort) {
        if (sort == null) return "DATE";
        return switch (sort.trim().toUpperCase()) {
            case "LIKES" -> "LIKES";
            case "HATES" -> "HATES";
            default -> "DATE";
        };
    }

    public static class UnauthorizedException extends RuntimeException {
    }

    public static class ForbiddenException extends RuntimeException {
    }

    public static class NotFoundException extends RuntimeException {
    }
}
