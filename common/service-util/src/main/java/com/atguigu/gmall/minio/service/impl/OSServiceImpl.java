package com.atguigu.gmall.minio.service.impl;

import com.atguigu.gmall.minio.config.MinioProperties;
import com.atguigu.gmall.minio.service.OSSService;
import io.minio.MinioClient;
import io.minio.PutObjectOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

@Service
public class OSServiceImpl implements OSSService {
    @Autowired
    MinioClient minioClient;
    @Autowired
    MinioProperties minioProperties;

    @Override
    public String upload(MultipartFile file) throws Exception {
        //获取不重复的文件名
        String filename = UUID.randomUUID().toString().replace("1","") + "_" +file.getOriginalFilename();
        //获取字符流以及参数设置
        InputStream inputStream = file.getInputStream();
        PutObjectOptions options = new PutObjectOptions(inputStream.available(),-1);
        //设置文件上传类型,避免下载
        String contentType = file.getContentType();
        options.setContentType(contentType);
        minioClient.putObject(minioProperties.getBucket(),filename,inputStream,options);

        //获取文件长传的路径
        String path = minioProperties.getEndpoint() + "/" + minioProperties.getBucket() + "/" + filename;
        return path;
    }
}
