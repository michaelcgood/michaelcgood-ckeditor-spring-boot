package com.michaelcgood.job;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import org.bson.Document;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.michaelcgood.dao.SongDAO;
import com.michaelcgood.model.SongModel;
import com.michaelcgood.uploader.FileUploadController;

@Configuration
@EnableBatchProcessing
public class UploadJob {
    
    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private MongoTemplate mongoTemplate;
    
    @Autowired
    FileUploadController uploadController;
    
    @Autowired
    private SongDAO songDAO;
    
    public static int PRETTY_PRINT_INDENT_FACTOR = 4;
    
    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1")
            .tasklet(new Tasklet() {
                @Override
                public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {


                    // insert ssg xccdf
                    Path xmlDocPath = uploadController.getCurrentFilePath();
                    String jsonB =processXML2JSON(xmlDocPath);
                    insertToMongo(jsonB);
                    return RepeatStatus.FINISHED;
                }
            })
            .build();
    }
    
    
    @Bean
    public Step step2(){
        return stepBuilderFactory.get("step2")
            .tasklet(new Tasklet(){
                @Override
                public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception{
                    
                    doAQuery();
                    return RepeatStatus.FINISHED;
                }
            })
            
            .build();
    }
    
    @Bean
    public Job UploadProcessor(){
        return jobBuilderFactory.get("UploadProcessor")
            .start(step1())
            .next(step2())
            .build();
        
        
    }
    
    public List<SongModel> doAQuery() throws JsonParseException, JsonMappingException, IOException{
        Query query = new Query();
        query.addCriteria(Criteria.where("Music.id")
            .is("MUS-1"))
        .fields()
        .include("Music.songs.song");
        String result = mongoTemplate.findOne(query, String.class, "foo");
        Integer indexBegin = result.indexOf("{ \"song\" : ")+12;
        Integer indexEnd = result.length()-4;
        String resultFilter = result.substring(indexBegin, indexEnd).trim();
        StringBuilder finalJson = new StringBuilder(resultFilter);
        finalJson.insert(0, "[");
        finalJson.insert(finalJson.length(), "]");
        System.out.println("resulting json:::::: " + finalJson.toString());
        ObjectMapper objectMapper = new ObjectMapper();
        
        TypeFactory typeFactory = objectMapper.getTypeFactory();
        CollectionType collectionType = typeFactory.constructCollectionType(
            List.class, SongModel.class);
        List<SongModel> songList = objectMapper.readValue(finalJson.toString(), collectionType);
        for(SongModel songmodel:songList){
            songmodel.setUpdated(false);
            songDAO.insert(songmodel);
            System.out.println("inserted this songmodel ::: " + songmodel);
        }

        return songList;
    }
    
    private void insertToMongo(String jsonString) {
        Document doc = Document.parse(jsonString);
        mongoTemplate.insert(doc, "foo");
    }
    
    private String processXML2JSON(Path xmlDocPath) throws JSONException {
        String XML_STRING = null;
        try {
            XML_STRING = Files.lines(xmlDocPath)
                .collect(Collectors.joining("\n"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONObject xmlJSONObj = XML.toJSONObject(XML_STRING);
        String jsonPrettyPrintString = xmlJSONObj.toString(PRETTY_PRINT_INDENT_FACTOR);
        System.out.println("PRINTING STRING :::::::::::::::::::::" + jsonPrettyPrintString);

        return jsonPrettyPrintString;
    }

}
