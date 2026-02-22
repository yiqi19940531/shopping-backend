package com.qoder.mall.service;

import com.qoder.mall.dto.response.FileUploadResponse;
import com.qoder.mall.entity.FileStorage;
import org.springframework.web.multipart.MultipartFile;

public interface IFileService {

    FileUploadResponse upload(MultipartFile file, Long uploaderId);

    FileStorage getFile(Long fileId);
}
