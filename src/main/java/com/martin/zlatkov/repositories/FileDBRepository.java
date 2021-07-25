package com.martin.zlatkov.repositories;

import com.martin.zlatkov.models.FileDB;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FileDBRepository extends JpaRepository<FileDB, String> {

    @Query("SELECT f FROM FileDB f WHERE f.name = ?1")
    FileDB findFileDBByName(@Param("name") String name);

}
