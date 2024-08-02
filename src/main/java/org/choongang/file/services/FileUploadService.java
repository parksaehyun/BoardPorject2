package org.choongang.file.services;

import lombok.RequiredArgsConstructor;
import org.choongang.file.entities.FileInfo;
import org.choongang.file.repositories.FileInfoRepository;
import org.choongang.global.configs.FileProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@EnableConfigurationProperties(FileProperties.class)
public class FileUploadService {

    private final FileInfoRepository fileInfoRepository;
    private final FileProperties properties;

    public List<FileInfo> upload(MultipartFile[] files, String gid, String location) {
        /**
         * 1. 파일 정보 저장
         * 2. 파일을 서버로 이동
         * 3. 이미지이면 썸네일 생성
         * 4. 업로드한 파일목록 반환
         */

        gid = StringUtils.hasText(gid) ? gid : UUID.randomUUID().toString(); // 없어도 db에 저장되는 값이 있어야 해서 이거 씀

        List<FileInfo> uploadedFiles = new ArrayList<>();

        // 1. 파일 정보 저장
        for (MultipartFile file : files) {
            String fileName = file.getOriginalFilename(); // 업로드한 파일의 실제 이름
            String contentType = file.getContentType(); // 파일 형식
            String extension = fileName.substring(fileName.lastIndexOf(".")); // ex) image.png -> 끝에서 잘라와서 파일 확장자 가져오기

            FileInfo fileInfo = FileInfo.builder()
                    .gid(gid)
                    .location(location)
                    .fileName(fileName)
                    .extension(extension) // 나중에 파일경로 찾을 때 필요
                    .contentType(contentType)
                    .build();

            fileInfoRepository.saveAndFlush(fileInfo); // 웹서버와 파일저장공간을 분리하는 경우가 많음 // 서버가 너무 부담스러워 해서


            // 2. 파일을 서버로 이동
            long seq = fileInfo.getSeq();
            String uploadDir = properties.getPath() + "/" + (seq % 10L);
            File dir = new File(uploadDir);
            if (!dir.exists() || !dir.isDirectory()) {
                dir.mkdir(); // 파일을 분산해서 저장
            }

            String uploadPath = uploadDir + "/" + seq + extension;
            try {
                file.transferTo(new File(uploadPath));
                uploadedFiles.add(fileInfo); //업로드 성공 파일 정보
            } catch (IOException e) {
                e.printStackTrace();
                // 파일 이동 실패 시 정보 삭제
                fileInfoRepository.delete(fileInfo);
                fileInfoRepository.flush();
            }
        }
        return uploadedFiles;
    }
}
