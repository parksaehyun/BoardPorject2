package org.choongang.file.services;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.choongang.file.constants.FileStatus;
import org.choongang.file.entities.FileInfo;
import org.choongang.file.exceptions.FileNotFoundException;
import org.choongang.file.repositories.FileInfoRepository;
import org.choongang.global.configs.FileProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@EnableConfigurationProperties(FileProperties.class)
public class FileInfoService {

    private final FileInfoRepository infoRepository;
    private final FileProperties properties;
    private final HttpServletRequest request;

    /**
     * 파일 1개 조회
     *
     * @param seq : 파일 등록번호
     * @return
     */
    public FileInfo get(Long seq) {
        FileInfo item = infoRepository.findById(seq).orElseThrow(FileNotFoundException::new);

        /**
         * 2차 가공
         * 1. 파일을 접근할 수 있는 URL - 보여주기 위한 목적
         * 2. 파일을 접근할 수 있는 PATH - 파일 삭제, 다운로드 등등
         */

        addFileInfo(item);

        return item;
    }

    /**
     * 파일 목록 조회
     *
     * @param gid
     * @param location
     * @param status - ALL: 완료 + 미완료, DONE - 완료, UNDONE - 미완료
     * @return
     */
    public List<FileInfo> getList(String gid, String location, FileStatus status) {

        return null;
    }

    /**
     * 파일 정보 추가 처리
     * - fileUrl, filePath
     *
     * @param item
     */
    public void addFileInfo(FileInfo item) {
        String fileUrl = getFileUrl(item);
        String filePath = getFilePath(item);

        item.setFileUrl(fileUrl);
        item.setFilePath(filePath);
    }

    // 브라우저 접근 주소
    public String getFileUrl(FileInfo item) {
        return request.getContextPath() + properties.getUrl() + "/" + getFolder(item.getSeq()) + "/" + getFileName(item);
    }

    // 실제 서버업로드(서버쪽) 파일 경로
    public String getFilePath(FileInfo item) {
        return properties.getPath() + "/" + getFolder(item.getSeq()) + "/" + getFileName(item);
    }

    public String getFolder(long seq) {
        return String.valueOf(seq % 10L);
    }

    public String getFileName(FileInfo item) {
        String fileName = item.getSeq() + Objects.requireNonNullElse(item.getExtension(), "");
        return fileName;
    }
}