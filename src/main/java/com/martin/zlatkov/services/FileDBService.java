package com.martin.zlatkov.services;

import com.martin.zlatkov.models.FileDB;
import com.martin.zlatkov.repositories.FileDBRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

@Service
public class FileDBService {

    @Autowired
    private FileDBRepository fileDBRepository;
    private FileDB file;

    public FileDB store(MultipartFile file) throws IOException {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        FileDB File = new FileDB(fileName, file.getContentType(), file.getBytes());
        return fileDBRepository.save(File);
    }

    public List<FileDB> getAll(){ return fileDBRepository.findAll(); }

    public FileDB getFile(String id) { return fileDBRepository.findById(id).get(); }

    public Stream<FileDB> getAllFiles() {
        return fileDBRepository.findAll().stream();
    }

    public FileDB findByName (String name){ return fileDBRepository.findFileDBByName(name);}

}
