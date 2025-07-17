package com.project.platform.service.impl;

import cn.hutool.core.net.url.UrlBuilder;
import com.project.platform.exception.CustomException;
import com.project.platform.service.FileService;
import com.project.platform.vo.FileInfoVO;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
public class FileServiceImpl implements FileService {

    @Value("${server.ip}")
    private String serverIp;
    @Value("${server.port}")
    private int serverPort;
    @Value("${files.uploads.path}")
    private String basePath;

    @Value("${files.uploads.baseUrl:}")
    private String fileBaseUrl;


    public FileInfoVO upload(MultipartFile multipartFile) throws IOException, NoSuchAlgorithmException {
        // Get the file extension of the uploaded file
        String fix = FilenameUtils.getExtension(multipartFile.getOriginalFilename());
        // Generate the full file name
        if (StringUtils.isBlank(fix)) {
            throw new CustomException("File extension cannot be empty");
        }
        // Generate file name using MD5
        // Although MD5 may produce hash collisions, it's sufficient for most common scenarios
        String md5 = getMD5Checksum(multipartFile);
        String newFileName = md5 + "." + fix;
        File newFile = createFile(newFileName);
        // Transfer the file directly to the specified path
        multipartFile.transferTo(new File(newFile.getAbsolutePath()));
        FileInfoVO fileInfoVO = new FileInfoVO();
        fileInfoVO.setUrl(getServer() + "/" + newFileName);
        fileInfoVO.setName(newFileName);
        return fileInfoVO;
    }


    private File createFile(String fileName) throws IOException {
        File file = new File(Paths.get(basePath, fileName).toString());
        if (file.exists()) {
            return file;
        }
        // Check if the configured directory exists; if not, create a new directory
        File parentFile = file.getParentFile();
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
        return file;
    }


    private String getServer() {
        if (StringUtils.isNotEmpty(fileBaseUrl)) {
            return fileBaseUrl;
        }
        String buildUrl = UrlBuilder.create()
                .setScheme("http")
                .setHost(serverIp)
                .setPort(serverPort)
                .addPath("file")
                .build();
        return buildUrl;
    }


    private String getFilePath(String fileName) {
        return basePath + fileName;

    }

    public File getFile(String fileName) throws IOException {
        File file = new File(getFilePath(fileName));
        return file;
    }

    /**
     * Calculate the MD5 checksum of a file
     *
     * @param file
     * @return
     * @throws NoSuchAlgorithmException
     * @throws IOException
     */
    private String getMD5Checksum(MultipartFile file) throws NoSuchAlgorithmException, IOException {
        MessageDigest md5Digest = MessageDigest.getInstance("MD5");
        byte[] fileBytes = file.getBytes();
        md5Digest.update(fileBytes);

        StringBuilder sb = new StringBuilder();
        for (byte b : md5Digest.digest()) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
