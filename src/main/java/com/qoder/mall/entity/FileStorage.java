package com.qoder.mall.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("tb_file_storage")
public class FileStorage {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String fileName;

    private Long fileSize;

    private String fileType;

    private String fileExtension;

    private byte[] fileData;

    private Long uploaderId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableLogic
    private Integer isDeleted;
}
