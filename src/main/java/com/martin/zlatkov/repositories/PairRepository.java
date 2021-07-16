package com.martin.zlatkov.repositories;

import com.martin.zlatkov.models.Pair;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PairRepository extends JpaRepository<Pair, Integer> {

}
