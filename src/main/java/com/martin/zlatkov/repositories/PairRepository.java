package com.martin.zlatkov.repositories;

import com.martin.zlatkov.models.Pair;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PairRepository extends JpaRepository<Pair, Integer> {

    @Query("select u from Pair u where u.fileDBName = ?1")
    Pair findByfileDBName (@Param("name") String name);
}
