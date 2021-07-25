package com.martin.zlatkov.services;

import com.martin.zlatkov.models.Pair;
import com.martin.zlatkov.repositories.PairRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PairService {

    private PairRepository pairRepository;

    @Autowired
    public PairService(PairRepository pairRepository) {
        this.pairRepository = pairRepository;
    }

    public Pair findExistingPair(String name) {
        return pairRepository.findByfileDBName(name);
    }
}
