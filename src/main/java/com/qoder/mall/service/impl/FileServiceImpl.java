package com.qoder.mall.service.impl;

import com.qoder.mall.common.exception.BusinessException;
import com.qoder.mall.dto.response.FileUploadResponse;
import com.qoder.mall.entity.FileStorage;
import com.qoder.mall.mapper.FileStorageMapper;
import com.qoder.mall.service.IFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements IFileService {

    private final FileStorageMapper fileStorageMapper;

    private static final Set<String> ALLOWED_TYPES = Set.of(
            "image/jpeg", "image/png", "image/gif", "image/webp"
    );
    private static final long MAX_SIZE = 5 * 1024 * 1024; // 5MB

    @Override
    public FileUploadResponse upload(MultipartFile file, Long uploaderId) {
        if (file.isEmpty()) {
            throw new BusinessException("文件不能为空");
        }
        if (!ALLOWED_TYPES.contains(file.getContentType())) {
            throw new BusinessException("只支持 jpg/png/gif/webp 格式");
        }
        if (file.getSize() > MAX_SIZE) {
            throw new BusinessException("文件大小不能超过5MB");
        }

        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        }

        try {
            FileStorage fileStorage = new FileStorage();
            fileStorage.setFileName(originalFilename);
            fileStorage.setFileSize(file.getSize());
            fileStorage.setFileType(file.getContentType());
            fileStorage.setFileExtension(extension);
            fileStorage.setFileData(file.getBytes());
            fileStorage.setUploaderId(uploaderId);
            fileStorageMapper.insert(fileStorage);

            return FileUploadResponse.builder()
                    .fileId(fileStorage.getId())
                    .url("/api/files/" + fileStorage.getId())
                    .build();
        } catch (IOException e) {
            throw new BusinessException("文件上传失败");
        }
    }

    @Override
    public FileStorage getFile(Long fileId) {
        FileStorage file = fileStorageMapper.selectById(fileId);
        if (file == null) {
            throw new BusinessException("文件不存在");
        }
        return file;
    }
}
