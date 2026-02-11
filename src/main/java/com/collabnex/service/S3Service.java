

package com.collabnex.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class S3Service {

    private final AmazonS3 amazonS3;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    public S3Service(AmazonS3 amazonS3) {
        this.amazonS3 = amazonS3;
    }

    // ===== UPLOAD =====
    public String uploadFile(MultipartFile file, String s3Key) {
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());

            amazonS3.putObject(
                    new PutObjectRequest(
                            bucketName,
                            s3Key,
                            file.getInputStream(),
                            metadata
                    )
            );

            return amazonS3.getUrl(bucketName, s3Key).toString();

        } catch (Exception e) {
            throw new RuntimeException("S3 upload failed", e);
        }
    }

    // ===== DELETE (FIXED – NO 500 ERROR) =====
    public void deleteFile(String s3Key) {
        try {
            // ✅ Direct delete (SAFE even if file does not exist)
            amazonS3.deleteObject(bucketName, s3Key);
            System.out.println("✅ DELETE REQUEST SENT TO S3: " + s3Key);
        } catch (Exception e) {
            e.printStackTrace();
            // ❗ DO NOT throw exception – allow DB delete to continue
        }
    }
}
