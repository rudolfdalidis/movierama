package com.example.backend.movies.dto;

import java.time.Instant;

import com.example.backend.votes.VoteType;

import jakarta.validation.constraints.NotBlank;

public class MovieDtos {

    public record CreateMovieRequest(
            @NotBlank String title,
            @NotBlank String description
    ) {
    }

    public record UserSummary(Long id, String username) {
    }

    /**
     * Payload used by the homepage.
     * Includes like/hate counts, and (optionally) the current user's vote.
     */
    public record MovieListItem(
            Long id,
            String title,
            String description,
            Instant createdAt,
            UserSummary submittedBy,
            long likesCount,
            long hatesCount,
            VoteType myVote,
            boolean canVote
    ) {
    }
}
