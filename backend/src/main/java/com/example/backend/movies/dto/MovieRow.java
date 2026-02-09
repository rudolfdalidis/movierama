package com.example.backend.movies.dto;

import java.time.Instant;

import com.example.backend.votes.VoteType;

/**
 * A lightweight projection used for the movies list query.
 */
public interface MovieRow {
    Long getId();
    String getTitle();
    String getDescription();
    Instant getCreatedAt();

    Long getSubmittedById();
    String getSubmittedByUsername();

    long getLikesCount();
    long getHatesCount();

    VoteType getMyVote();
}
