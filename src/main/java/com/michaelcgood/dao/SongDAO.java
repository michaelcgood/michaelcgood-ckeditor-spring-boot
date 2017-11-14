package com.michaelcgood.dao;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.michaelcgood.model.SongModel;

@Repository
public interface SongDAO extends MongoRepository<SongModel, String> {

}
