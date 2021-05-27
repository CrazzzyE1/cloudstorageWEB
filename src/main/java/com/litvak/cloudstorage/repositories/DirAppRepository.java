package com.litvak.cloudstorage.repositories;

import com.litvak.cloudstorage.entities.DirApp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DirAppRepository extends JpaRepository<DirApp, Long> {
    Optional<DirApp> findDirAppByName(String name);
    List<DirApp> findAllByDirId(Integer dir_id);
    Optional<DirApp> findDirAppsById(Long id);
}
