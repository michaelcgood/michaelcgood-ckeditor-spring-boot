package com.michaelcgood.uploader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.michaelcgood.dao.MusicDAO;
import com.michaelcgood.job.UploadJob;


@Controller

public class FileUploadController {

    @Autowired
    private final StorageService storageService;

    @Autowired
    JobLauncher jobLauncher;

    @Autowired
    private UploadJob uploadJob;
    
    @Autowired
    MusicDAO musicDAO;
    
    

    public Path currentFilePath;

    public Path getCurrentFilePath() {
        return currentFilePath;
    }

    public void setCurrentFilePath(Path currentFilePath) {
        this.currentFilePath = currentFilePath;
    }

    public FileUploadController(StorageService storageService) {
        this.storageService = storageService;

    }



    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {

        Resource file = storageService.loadAsResource(filename);
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
            .body(file);
    }

    @PostMapping("/")
    public String postTheFile(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes)
        throws IOException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException, JobParametersInvalidException {
        // store the file to server
        storageService.store(file);
        // convert multipartfile to file
        File convFile = new File(file.getOriginalFilename());
        convFile.createNewFile();
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        String filepath = convFile.getAbsolutePath();
        Path xmlPathbb = Paths.get(filepath);
        // set current file path
        setCurrentFilePath(xmlPathbb);
        System.out.println("xmlPathbb:::::" + xmlPathbb);
        //
 
        Job job = uploadJob.UploadProcessor();
        jobLauncher.run(job, new JobParameters());

        return "uploadForm";
    }

}
