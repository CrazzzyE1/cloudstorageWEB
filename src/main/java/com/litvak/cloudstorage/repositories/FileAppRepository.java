package com.litvak.cloudstorage.repositories;

import com.litvak.cloudstorage.entities.FileApp;
import com.litvak.cloudstorage.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileAppRepository extends JpaRepository<FileApp, Long> {

    @Query(value =
            "SELECT f.id, f.name, f.name_system, f.size, f.date, f.time, f.dir_id" +
                    " FROM files as f" +
                    " JOIN directories as d ON f.dir_id = d.id" +
                    " JOIN users as u ON d.user_id = u.id" +
                    " WHERE u.id = ?2 AND f.name LIKE %?1% ;", nativeQuery = true)
    List<FileApp> findAllByUserIdAndSearchLine(String search, Long userId);

    @Query(value =
            "SELECT sum(f.size) " +
                    "FROM files as f " +
                    "JOIN directories as d ON f.dir_id = d.id " +
                    "JOIN users as u ON d.user_id = u.id " +
                    "WHERE u.id = ?1 ;", nativeQuery = true)
    Integer findSpaceSize(Long userId);

//    @Query("select u from User u where u.firstname like %?1")
//    List<User> findByFirstnameEndsWith(String firstname);
//    SELECT f.id, f.name, f.name_system, f.size, f.date, f.time, f.dir_id
//    FROM files as f
//    JOIN directories as d
//    ON f.dir_id = d.id
//    JOIN users as u
//    ON d.user_id = u.id
//    WHERE u.id = '5'
//    ;
}
