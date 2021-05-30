package com.litvak.cloudstorage.repositories;

import com.litvak.cloudstorage.entities.DirApp;
import com.litvak.cloudstorage.entities.FileApp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileAppRepository extends JpaRepository<FileApp, Long> {

    @Query(value =
            "SELECT f.id, f.name, f.name_system, f.size, f.strsize, f.date, f.time, f.dir_id" +
                    " FROM files as f" +
                    " JOIN directories as d ON f.dir_id = d.id" +
                    " JOIN users as u ON d.user_id = u.id" +
                    " WHERE u.id = ?2 AND LOWER(f.name) LIKE %?1% ;", nativeQuery = true)
    List<FileApp> findAllByUserIdAndSearchLine(String search, Long userId);

    @Query(value =
            "SELECT sum(f.size) " +
                    "FROM files as f " +
                    "JOIN directories as d ON f.dir_id = d.id " +
                    "JOIN users as u ON d.user_id = u.id " +
                    "WHERE u.username = ?1 ;", nativeQuery = true)
    Long findSpaceSize(String login);

    List<FileApp> findAllByDirApp(DirApp dirApp);
    List<FileApp> findAllByNameSystem(String nameSystem);
//    Optional<FileApp> findByName(String name);
    void deleteByNameAndDirApp(String name, DirApp dir);
}
