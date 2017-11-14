package com.michaelcgood;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.michaelcgood.dao.SongDAO;
import com.michaelcgood.dao.UpdatedSongDAO;
import com.michaelcgood.model.ResponseModel;
import com.michaelcgood.model.SongModel;
import com.michaelcgood.model.UpdatedSong;
import com.michaelcgood.service.HTMLFormatter;

@RestController
@RequestMapping("/api/")
public class APIController {
    @Autowired
    SongDAO songDAO;
    
    @Autowired
    UpdatedSongDAO updatedDAO;
    
    @Autowired
    HTMLFormatter format;
    
    @GetMapping(value={"/show/","/show/{sid}"})
    public ResponseEntity<?> getRule(@RequestParam String sid, Model model){
        ResponseModel response = new ResponseModel();
        System.out.println("SID :::::" + sid);
        ArrayList<String> musicText = new ArrayList<String>();
        if(sid!=null){
            String sidString = sid;
            SongModel songModel = songDAO.findOne(sidString);
            System.out.println("get status of boolean during get ::::::" + songModel.getUpdated());
            if(songModel.getUpdated()==false ){
                
                musicText.add(songModel.getArtist());
                musicText.add(songModel.getSongTitle());
                String filterText = format.changeJsonToHTML(musicText);
                System.out.println("filtered rule text ::::::::" + filterText);
                response.setData(filterText);
                
            } else if(songModel.getUpdated()==true){
                UpdatedSong updated = updatedDAO.findBysid(sidString);
                String text = updated.getHtml();
                System.out.println("getting the updated text ::::::::" + text);
                response.setData(text);
            }
            
        }

        model.addAttribute("response", response);
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping(value={"/save/","/save/[sid]"}, consumes = MediaType.TEXT_HTML_VALUE)
    public @ResponseBody ResponseModel saveRule( @RequestBody String body, @RequestParam String sid){
        ResponseModel response = new ResponseModel();
        response.setData(body);
        SongModel oldSong = songDAO.findOne(sid);
        String songTitle = oldSong.getSongTitle();
        String artistName = oldSong.getArtist();
        if(oldSong.getUpdated() == false){
            UpdatedSong updatedSong = new UpdatedSong();
            updatedSong.setArtist(artistName);
            updatedSong.setSongTitle(songTitle);
            updatedSong.setHtml(body);
            updatedSong.setSid(sid);
            oldSong.setUpdated(true);
            songDAO.save(oldSong);
            updatedDAO.insert(updatedSong);
            System.out.println("get status of boolean during post :::::" + oldSong.getUpdated());
        }else{
            UpdatedSong currentSong = updatedDAO.findBysid(sid);
            currentSong.setHtml(body);
            updatedDAO.save(currentSong);
        }
        
        
        System.out.println("response body ::::::::::" + body);
        System.out.println("POST Response :::::::::" + response.getData());
        
        
        return response;
    }
    

}
