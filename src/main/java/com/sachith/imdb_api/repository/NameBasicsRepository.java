package com.sachith.imdb_api.repository;

import com.sachith.imdb_api.entity.NameBasics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NameBasicsRepository extends JpaRepository<NameBasics, String> {
}
