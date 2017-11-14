package com.michaelcgood.model;

public class ResponseModel {
    private String data;
    
    public ResponseModel(){
            
    }
    
    public ResponseModel(String data){
            this.data = data;
    }

    public String getData() {
            return data;
    }

    public void setData(String data) {
            this.data = data;
    }
}