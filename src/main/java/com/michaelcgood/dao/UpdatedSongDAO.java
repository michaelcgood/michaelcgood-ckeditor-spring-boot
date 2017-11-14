package com.michaelcgood.dao;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.michaelcgood.model.UpdatedSong;

@Repository
public interface UpdatedSongDAO extends MongoRepository<UpdatedSong, String> {
    UpdatedSong findBysid(String sid);
}
