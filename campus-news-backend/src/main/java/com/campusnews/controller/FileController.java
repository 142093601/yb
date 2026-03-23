package com.campusnews.controller;

import com.campusnews.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 文件上传接口
 */
@Tag(name = "文件上传")
@RestController
@RequestMapping("/api/file")
public class FileController {

    @Value("${file.upload-path}")
    private String uploadPath;

    @Value("${server.port}")
    private int serverPort;

    @Operation(summary = "上传文件")
    @PostMapping("/upload")
    public Result<?> upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("请选择文件");
        }

        // 检查文件类型
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
        if (!suffix.matches("\\.(jpg|jpeg|png|gif|webp|mp4|pdf)$")) {
            return Result.error("不支持的文件格式");
        }

        // 按日期创建目录
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        File dir = new File(uploadPath + datePath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 生成唯一文件名
        String newFilename = UUID.randomUUID().toString().replace("-", "") + suffix;
        File dest = new File(dir, newFilename);

        try {
            file.transferTo(dest);
        } catch (IOException e) {
            return Result.error("文件上传失败：" + e.getMessage());
        }

        String fileUrl = "/uploads/" + datePath + "/" + newFilename;

        Map<String, String> data = new HashMap<>();
        data.put("url", fileUrl);
        data.put("name", originalFilename);

        return Result.success("上传成功", data);
    }
}
