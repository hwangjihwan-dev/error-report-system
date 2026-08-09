package com.hwang.errorreport.service;

import lombok.Getter;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
public class FileStorageService {

    private static final String UPLOAD_DIR =
            System.getProperty("user.dir") + File.separator + "uploads";

    public StoredFile storeFile(MultipartFile file){

        if(file == null || file.isEmpty()){
            return null;
        }

        File dir = new File(UPLOAD_DIR);

        if(!dir.exists()){
            boolean created = dir.mkdirs();

            if(!created){
                throw new IllegalStateException("파일 업로드 디렉토리 생성에 실패했습니다.");
            }
        }

        String originalFileName = file.getOriginalFilename();
        String storedFileName = UUID.randomUUID() + "_" + originalFileName;
        String filePath = UPLOAD_DIR + File.separator + storedFileName;

        try{
            file.transferTo(new File(filePath));
        }catch(IOException e){
            throw new IllegalStateException("파일 저장 중 오류가 발생했습니다. filePath=" + filePath, e);
        }

        return new StoredFile(
                originalFileName,
                storedFileName,
                filePath,
                file.getSize()
        );
    }

    @Getter
    public static class StoredFile{

        private final String originalFileName;
        private final String storedFileName;
        private final String filePath;
        private final Long fileSize;

        public StoredFile(String originalFileName,
                          String storedFileName,
                          String filePath,
                          Long fileSize){
            this.originalFileName = originalFileName;
            this.storedFileName = storedFileName;
            this.filePath = filePath;
            this.fileSize = fileSize;
        }
    }
}
