package com.example.backend.movies;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.example.backend.movies.dto.MovieRow;

public interface MovieRepository extends JpaRepository<Movie, Long> {

    @Query("""
            select
                m.id as id,
                m.title as title,
                m.description as description,
                m.createdAt as createdAt,
                u.id as submittedById,
                u.username as submittedByUsername,
                sum(case when v.type = com.example.backend.votes.VoteType.LIKE then 1 else 0 end) as likesCount,
                sum(case when v.type = com.example.backend.votes.VoteType.HATE then 1 else 0 end) as hatesCount,
                vu.type as myVote
            from Movie m
            join m.submittedBy u
            left join com.example.backend.votes.Vote v on v.movie = m
            left join com.example.backend.votes.Vote vu on vu.movie = m and (:currentUserId is not null and vu.user.id = :currentUserId)
            where (:submittedByUsername is null or u.username = :submittedByUsername)
            group by m.id, m.title, m.description, m.createdAt, u.id, u.username, vu.type
            order by
                case when :sort = 'LIKES' then sum(case when v.type = com.example.backend.votes.VoteType.LIKE then 1 else 0 end) end desc,
                case when :sort = 'HATES' then sum(case when v.type = com.example.backend.votes.VoteType.HATE then 1 else 0 end) end desc,
                case when :sort = 'DATE' then m.createdAt end desc,
                m.createdAt desc
            """)
    List<MovieRow> findMovieRows(
            @Param("sort") String sort,
            @Param("submittedByUsername") String submittedByUsername,
            @Param("currentUserId") Long currentUserId
    );

    @Query("""
            select
                m.id as id,
                m.title as title,
                m.description as description,
                m.createdAt as createdAt,
                u.id as submittedById,
                u.username as submittedByUsername,
                sum(case when v.type = com.example.backend.votes.VoteType.LIKE then 1 else 0 end) as likesCount,
                sum(case when v.type = com.example.backend.votes.VoteType.HATE then 1 else 0 end) as hatesCount,
                vu.type as myVote
            from Movie m
            join m.submittedBy u
            left join com.example.backend.votes.Vote v on v.movie = m
            left join com.example.backend.votes.Vote vu on vu.movie = m and (:currentUserId is not null and vu.user.id = :currentUserId)
            where m.id = :movieId
            group by m.id, m.title, m.description, m.createdAt, u.id, u.username, vu.type
            """)
    MovieRow findMovieRowById(
            @Param("movieId") Long movieId,
            @Param("currentUserId") Long currentUserId
    );
}
