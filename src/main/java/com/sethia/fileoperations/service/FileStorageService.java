package com.sethia.fileoperations.service;

import com.sethia.fileoperations.controller.FileController;
import com.sethia.fileoperations.utility.fileStorageProperties;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileStorageService {
    private final Path fileStorageLocation;

    private static final Logger logger = LoggerFactory.getLogger(FileController.class);

    @Autowired
    public FileStorageService(fileStorageProperties fileStorageProperties) throws Exception{
        this.fileStorageLocation = Paths.get(fileStorageProperties.getServerDir())
                .toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new Exception("Could not get the directory from where the files will be downloaded or stored.", ex);
        }
    }


    public Resource loadFileAsResource(String fileName) throws FileNotFoundException,MalformedURLException {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if(resource.exists()) {
                return resource;
            } else {
                logger.error(fileName + " not found !!");
                throw new FileNotFoundException();
            }
        } catch (MalformedURLException ex) {
            logger.error("Unable to load File : "+fileName);
            throw new MalformedURLException();
        }
    }

    public boolean deleteFile(String fileName)
    {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Files.delete(filePath);
        } catch(NoSuchFileException e){
            logger.error("file not found!!" , e);
            return false;
        }
        catch (Exception e) {
            logger.error("unable to delete file" , e);
            return false;
        }
        return true;
    }
    public boolean copyFile(String fileName){
        try {
            Path original_path = this.fileStorageLocation.resolve(fileName).normalize();
            File original = original_path.toFile();

            File copied = new File(original_path.toString()+"-copy");
            FileUtils.copyFile(original, copied);

        } catch (Exception e){
            logger.error("file not found!!" , e);
            return false;
        }

        return true;
    }
}
