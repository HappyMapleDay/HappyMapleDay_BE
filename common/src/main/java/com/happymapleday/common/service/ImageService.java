package com.happymapleday.common.service;

import com.oracle.bmc.objectstorage.ObjectStorage;
import com.oracle.bmc.objectstorage.requests.DeleteObjectRequest;
import com.oracle.bmc.objectstorage.requests.PutObjectRequest;
import com.oracle.bmc.objectstorage.responses.PutObjectResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageService {

    private final ObjectStorage objectStorageClient;

    @Value("${oci.object-storage.namespace}")
    private String namespace;

    @Value("${oci.object-storage.bucket-name}")
    private String bucketName;

    @Value("${oci.object-storage.region}")
    private String region;

    @Value("${oci.object-storage.endpoint}")
    private String endpoint;

    @Value("${oci.object-storage.folder-prefix}")
    private String folderPrefix;

    /**
     * 이미지를 OCI Object Storage에 업로드
     * 
     * @param file 업로드할 이미지 파일
     * @param folder 저장할 폴더 경로 (예: "bosses", "items")
     * @return 업로드된 이미지의 Public URL
     */
    public String upload(MultipartFile file, String folder) {
        try {
            // 파일명 생성 (UUID + 원본 확장자)
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : "";
            String fileName = UUID.randomUUID().toString() + extension;
            String objectName = folderPrefix + "/" + folder + "/" + fileName;

            log.info("이미지 업로드 시작 - Object Name: {}", objectName);

            // InputStream 가져오기
            InputStream inputStream = file.getInputStream();

            // PutObject 요청 생성
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .namespaceName(namespace)
                    .bucketName(bucketName)
                    .objectName(objectName)
                    .contentType(file.getContentType())
                    .contentLength(file.getSize())
                    .putObjectBody(inputStream)
                    .build();

            // 업로드 실행
            PutObjectResponse response = objectStorageClient.putObject(putObjectRequest);
            
            // Public URL 생성
            String publicUrl = String.format("%s/n/%s/b/%s/o/%s", 
                    endpoint, namespace, bucketName, objectName);

            log.info("이미지 업로드 완료 - URL: {}", publicUrl);

            return publicUrl;

        } catch (Exception e) {
            log.error("이미지 업로드 실패 - Folder: {}, FileName: {}", folder, file.getOriginalFilename(), e);
            throw new RuntimeException("이미지 업로드에 실패했습니다: " + e.getMessage(), e);
        }
    }

    /**
     * OCI Object Storage에서 이미지 삭제
     * 
     * @param imageUrl 삭제할 이미지의 Public URL
     */
    public void delete(String imageUrl) {
        try {
            // URL에서 Object Name 추출
            // 예: https://objectstorage.ap-seoul-1.oraclecloud.com/n/namespace/b/bucket/o/bosses/image.png
            // -> bosses/image.png
            String objectName = extractObjectNameFromUrl(imageUrl);

            log.info("이미지 삭제 시작 - Object Name: {}", objectName);

            // DeleteObject 요청 생성
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .namespaceName(namespace)
                    .bucketName(bucketName)
                    .objectName(objectName)
                    .build();

            // 삭제 실행
            objectStorageClient.deleteObject(deleteObjectRequest);

            log.info("이미지 삭제 완료 - Object Name: {}", objectName);

        } catch (Exception e) {
            log.error("이미지 삭제 실패 - URL: {}", imageUrl, e);
            throw new RuntimeException("이미지 삭제에 실패했습니다: " + e.getMessage(), e);
        }
    }

    /**
     * URL에서 Object Name 추출
     * 
     * @param imageUrl Public URL
     * @return Object Name (예: "bosses/image.png")
     */
    private String extractObjectNameFromUrl(String imageUrl) {
        // URL 형식: https://objectstorage.{region}.oraclecloud.com/n/{namespace}/b/{bucket}/o/{objectName}
        String[] parts = imageUrl.split("/o/");
        if (parts.length < 2) {
            throw new IllegalArgumentException("잘못된 이미지 URL 형식입니다: " + imageUrl);
        }
        return parts[1];
    }
}


