package com.atguigu.gmall.minio.service;

import org.springframework.web.multipart.MultipartFile;

public interface OSSService {

    String upload(MultipartFile file) throws Exception;
}
