package com.litvak.cloudstorage.repositories;

import com.litvak.cloudstorage.entities.DirApp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DirAppRepository extends JpaRepository<DirApp, Long> {
    DirApp findDirAppByName(String name);
    List<DirApp> findAllByDirId(Integer dir_id);
    DirApp findDirAppsById(Long id);
}
