package com.barry.full.repository;

import com.barry.full.entity.Jwt;
import com.barry.full.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public interface JwtRepository extends JpaRepository<Jwt,Long> {
    Optional<Jwt> findByToken(String token);

    List<Jwt> findByUserAndExpire(User user, boolean b);

    @Query("FROM Jwt j WHERE j.user.username = :username")
    Stream<Jwt> getUserTokens(String username);

    Optional<Jwt> findOneByUserAndExpire(User user, boolean b);

    void deleteAllByExpire(boolean expire);
}
