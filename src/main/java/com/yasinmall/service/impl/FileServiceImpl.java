package com.yasinmall.service.impl;

import com.google.common.collect.Lists;
import com.yasinmall.service.IFileService;
import com.yasinmall.util.FTPUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * @author yasin
 */
@Service("iFileService")
public class FileServiceImpl implements IFileService {

    private Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

    /**
     * 上传文件到FTP服务器
     */
    @Override
    public String upload(MultipartFile file, String path) {
        String fileName = file.getOriginalFilename();
        // 扩展名
        String fileExtensionName = fileName.substring(fileName.lastIndexOf(".") + 1);
        String uploadFileName = UUID.randomUUID().toString() + "." + fileExtensionName;
        logger.info("开始上传文件,上传文件名:{%s},上传路径:{%s},新文件名:{%s}", fileName, path, uploadFileName);

        // 确定目录是否存在，若不存在，则创建目录
        File fileDir = new File(path);
        if (!fileDir.exists()) {
            fileDir.setWritable(true);
            fileDir.mkdirs();
        }
        File targetFile = new File(path, uploadFileName);

        try {
            // 开始上传
            file.transferTo(targetFile);

            // 文件上传成功,将targetFile上传到FTP服务器上
            FTPUtil.uploadFile(Lists.newArrayList(targetFile));

            // 上传完成后删除upload下的文件
            targetFile.delete();
        } catch (IOException e) {
            logger.error("上传文件异常", e);
        }

        return targetFile.getName();
    }

}
