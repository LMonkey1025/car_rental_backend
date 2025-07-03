package com.example.car_rental_project.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    /**
     * 上傳檔案到儲存服務
     *
     * @param file             要上傳的 MultipartFile 物件
     * @param originalFileName 檔案的原始名稱，用於提取副檔名
     * @return 檔案成功上傳後的公開存取 URL
     * @throws IOException 如果檔案上傳過程中發生 I/O 錯誤
     */
    String uploadFile(MultipartFile file, String originalFileName) throws IOException;

    /**
     * 從儲存服務刪除檔案
     *
     * @param fileUrl 要刪除檔案的 URL
     * @throws IOException 如果檔案刪除過程中發生 I/O 錯誤
     */
    // void deleteFile(String fileUrl) throws IOException; // 我們可以稍後再加入這個功能

}
