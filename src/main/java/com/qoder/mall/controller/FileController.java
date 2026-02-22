package com.qoder.mall.controller;

import com.qoder.mall.common.result.Result;
import com.qoder.mall.dto.response.FileUploadResponse;
import com.qoder.mall.entity.FileStorage;
import com.qoder.mall.service.IFileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
@Tag(name = "文件服务", description = "文件上传/下载接口")
public class FileController {

    private final IFileService fileService;

    @PostMapping("/upload")
    @Operation(summary = "上传文件")
    public Result<FileUploadResponse> upload(@RequestParam("file") MultipartFile file,
                                             Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        return Result.success(fileService.upload(file, userId));
    }

    @GetMapping("/{fileId}")
    @Operation(summary = "访问文件")
    public ResponseEntity<byte[]> getFile(@PathVariable Long fileId) {
        FileStorage file = fileService.getFile(fileId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, file.getFileType())
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getFileName() + "\"")
                .body(file.getFileData());
    }
}
