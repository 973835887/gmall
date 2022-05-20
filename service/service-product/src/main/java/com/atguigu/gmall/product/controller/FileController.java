package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.minio.service.OSSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/admin/product")
public class FileController {

    @Autowired
    OSSService ossService;

    //文件上传
    @PostMapping("/fileUpload")
    public Result uploadFile(MultipartFile file) throws Exception {
       String path = ossService.upload(file);
        return Result.ok(path);
    }
}
