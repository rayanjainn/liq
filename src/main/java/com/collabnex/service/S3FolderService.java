package com.collabnex.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;

@Service
public class S3FolderService {

    private final AmazonS3 amazonS3;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    public S3FolderService(AmazonS3 amazonS3) {
        this.amazonS3 = amazonS3;
    }

    public void createFolder(String folderPath) {

        // MUST end with /
        if (!folderPath.endsWith("/")) {
            folderPath = folderPath + "/";
        }

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(0);

        ByteArrayInputStream emptyContent =
                new ByteArrayInputStream(new byte[0]);

        amazonS3.putObject(bucketName, folderPath, emptyContent, metadata);
    }
}
