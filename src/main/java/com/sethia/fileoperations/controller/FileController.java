package com.sethia.fileoperations.controller;

import com.sethia.fileoperations.service.FileStorageService;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

@RestController
@RequestMapping("/api")
public class FileController {

    private static final Logger logger = LoggerFactory.getLogger(FileController.class);

    @Autowired
    private FileStorageService fileStorageService;

    @GetMapping("/downloadFile/{fileName:.+}")
    @ApiOperation(value="Download a file by File name", notes="Provide a file name for downloading it")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) {
        // Load file as Resource
        Resource resource = null;
        try {
            resource = fileStorageService.loadFileAsResource(fileName);
        }catch(FileNotFoundException ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }catch(MalformedURLException ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            logger.error("Could not determine file type.", ex);
        }

        // Fallback to the default content type if type could not be determined
        if(contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @GetMapping("/deleteFile/{fileName:.+}")
    @ApiOperation(value="Delete a file by File name", notes="Provide a file name for deleting it")
    public ResponseEntity<String> deleteFile(@PathVariable String fileName) {

        boolean isDeleted = fileStorageService.deleteFile(fileName);
        String response;
        HttpStatus status;
        if(isDeleted) {
            response = "This file has been deleted";
            status = HttpStatus.OK;
        }

        else {
            response = "could not delete the file";
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        logger.info(response);
        return ResponseEntity.status(status).body(status.toString());
    }

    @GetMapping("/createFile/{fileName:.+}")
    @ApiOperation(value="Create a file by File name", notes="Provide a file name for creating it. It will create a copy of the given file with appending '-copy' to the file name")
    public ResponseEntity<String> createFile(@PathVariable String fileName) {

        boolean isCreated = fileStorageService.copyFile(fileName);
        String response;
        HttpStatus status;
        if(isCreated) {
            response = "This file has been created";
            status = HttpStatus.OK;
        }
        else {
            response = "could not create the file";
            status = HttpStatus.NOT_FOUND;
        }
        logger.info(response);
        return ResponseEntity.status(status).body(status.toString());
    }
}

