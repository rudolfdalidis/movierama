package com.example.backend.votes;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteRepository extends JpaRepository<Vote, Long> {

    Optional<Vote> findByUserIdAndMovieId(Long userId, Long movieId);

    boolean existsByUserIdAndMovieId(Long userId, Long movieId);
}
