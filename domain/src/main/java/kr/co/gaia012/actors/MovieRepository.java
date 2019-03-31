package kr.co.gaia012.actors;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieRepository extends JpaRepository<Long, Movie> {
}
