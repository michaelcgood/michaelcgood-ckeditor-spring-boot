package com.michaelcgood.service;

import java.util.ArrayList;

import org.springframework.stereotype.Service;

@Service
public class HTMLFormatter {
    
    public String changeJsonToHTML(ArrayList<String> json){
        
        String listString = String.join("<br/><br/> ", json);
        
        return listString;
    }

}
