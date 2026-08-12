package com.hwang.errorreport.service;

import lombok.Getter;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public class FileStorageService {

    private static final String UPLOAD_DIR =
            System.getProperty("user.dir") + File.separator + "uploads";

    private static final long MAX_FILE_SIZE = 20 * 1024 * 1024;

    private static final List<String> ALLOWED_EXTENSIONS = List.of("jpg", "jpeg", "png", "gif", "pdf", "txt", "zip");

    public StoredFile storeFile(MultipartFile file){

        if(file == null || file.isEmpty()){
            return null;
        }

        validateFile(file);

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

    private void validateFile(MultipartFile file){
        if(file.getSize() > MAX_FILE_SIZE){
            throw new IllegalArgumentException("첨부파일은 20MB 이하만 업로드할 수 있습니다.");
        }

        String originalFileName = file.getOriginalFilename();

        if(originalFileName == null || originalFileName.isBlank()){
            throw new IllegalArgumentException("파일명이 올바르지 않습니다.");
        }

        String extension = getExtension(originalFileName);

        if(!ALLOWED_EXTENSIONS.contains(extension)){
            throw new IllegalArgumentException("허용되지 않는 파일 형식입니다.");
        }
    }

    public void deleteFile(String filePath){
        if(filePath == null || filePath.isBlank()){
            return;
        }
        File file = new File(filePath);

        if(file.exists() && !file.delete()){
            throw new IllegalArgumentException("첨부파일 삭제 중 오류가 발생했습니다.");
        }
    }

    private String getExtension(String fileName){
        int dotIndex = fileName.lastIndexOf(".");

        if(dotIndex == -1 || dotIndex == fileName.length() - 1){
            throw new IllegalArgumentException("확장자가 없는 파일은 업로드 할 수 없습니다.");
        }

        return fileName.substring(dotIndex + 1).toLowerCase();
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
