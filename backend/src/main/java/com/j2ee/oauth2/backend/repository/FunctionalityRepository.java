package com.j2ee.oauth2.backend.repository;

import com.j2ee.oauth2.backend.models.Functionality;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface FunctionalityRepository extends JpaRepository<Functionality, Long> {
    Set<Functionality> findByIdIn(Set<Long> ids);
    Optional<Functionality> findByName(String name);

}
