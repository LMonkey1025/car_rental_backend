package com.example.car_rental_project.service;

import com.example.car_rental_project.exception.ModerationException;

public interface ModerationService {

    /**
     * 使用大型語言模型檢查給定內容是否包含不當資訊。
     *
     * @param content 要進行審核的文字內容。
     * @throws ModerationException 如果內容被判定為不當或審核過程失敗時拋出。
     */
    void checkForInappropriateContent(String content) throws ModerationException;
}
