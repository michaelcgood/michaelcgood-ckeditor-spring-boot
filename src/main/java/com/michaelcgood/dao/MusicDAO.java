package com.michaelcgood.dao;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.michaelcgood.model.MusicModel;


@Repository
public interface MusicDAO extends MongoRepository<MusicModel, String> {
    
    MusicModel findTop1ByOrderByIdDesc();

}
