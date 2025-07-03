package com.example.car_rental_project.service.impl;

import com.example.car_rental_project.service.FileService; // 或者 FileStorageService，根據你介面的實際名稱
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.net.URI;
import java.util.UUID;

@Service
public class R2StorageService implements FileService { // 或者 FileStorageService

    @Value("${aws.s3.access-key-id}")
    private String accessKeyId; // 公鑰

    @Value("${aws.s3.secret-access-key}")
    private String secretAccessKey; // 私鑰

    @Value("${aws.s3.region}")
    private String region;

    @Value("${aws.s3.endpoint-url}")
    private String endpointUrl; // R2 的端點 URL，通常是 https://<account-id>.r2.cloudflarestorage.com

    @Value("${aws.s3.bucket-name}")
    private String bucketName; // 儲存庫的名稱

    @Value("${aws.s3.public-url-prefix}")
    private String publicUrlPrefix; // 公開存取的 URL 前綴

    private S3Client s3Client;

    /**
     * 初始化 S3Client
     * 這個方法會在 Spring 啟動時被調用，確保 S3Client 在使用前已經初始化。
     */
    @PostConstruct // @PostConstruct:為這個Bean初始化 確保在 Spring 啟動後初始化 S3Client
    public void init() {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKeyId, secretAccessKey);
        // 建立 S3Client，設定區域、憑證提供者和端點 URL
        this.s3Client = S3Client.builder()
                .region(Region.of(this.region))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .endpointOverride(URI.create(this.endpointUrl))
                .build();
    }

    /**
     * 上傳檔案到 R2 儲存服務
     * 
     * @param multipartFile    要上傳的 MultipartFile 物件
     * @param originalFileName 檔案的原始名稱，用於提取副檔名
     * 
     * @return 檔案成功上傳後的公開存取 URL
     */
    @Override
    public String uploadFile(MultipartFile multipartFile, String originalFileName) throws IOException {
        if (multipartFile == null || multipartFile.isEmpty()) {
            throw new IllegalArgumentException("上傳的檔案不可為空");
        }

        String fileExtension = ""; // 預設副檔名為空字串
        if (originalFileName != null && originalFileName.contains(".")) { // 如果原檔名有副檔名
            fileExtension = originalFileName.substring(originalFileName.lastIndexOf(".")); // 把副檔名取出來
        }
        // 產生一個唯一的檔案名稱，以避免衝突，同時保留副檔名
        String uniqueFileName = UUID.randomUUID().toString() + fileExtension;

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName) // 指定要上傳到的儲存庫
                .key(uniqueFileName) // 使用唯一檔案名稱作為鍵
                .contentType(multipartFile.getContentType()) // 設定檔案的內容類型
                .build(); // 建立 PutObjectRequest 物件 這個物件包含了上傳檔案所需的所有資訊，例如儲存庫名稱、檔案鍵和內容類型。

        try {
            s3Client.putObject(putObjectRequest,
                    RequestBody.fromInputStream(multipartFile.getInputStream(), multipartFile.getSize())); // 使用
                                                                                                           // S3Client
                                                                                                           // 上傳檔案
        } catch (Exception e) {
            throw new IOException("上傳檔案到 R2 失敗: " + e.getMessage(), e);
        }

        // 確保 publicUrlPrefix 以 / 結尾，且 uniqueFileName 不以 / 開頭
        String currentPublicUrlPrefix = this.publicUrlPrefix; // 取得公開存取的 URL 前綴
        if (!currentPublicUrlPrefix.endsWith("/")) { // 如果前綴不以 / 結尾，則添加 /
            currentPublicUrlPrefix += "/";
        }
        String cleanUniqueFileName = uniqueFileName.startsWith("/") ? uniqueFileName.substring(1) : uniqueFileName; // 確保檔案名稱不以
                                                                                                                    // /
                                                                                                                    // 開頭

        return currentPublicUrlPrefix + cleanUniqueFileName; // 返回檔案的公開存取 URL
    }

}
