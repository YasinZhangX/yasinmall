package com.yasinmall.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author yasin
 */
public interface IFileService {

    String upload(MultipartFile file, String path);

}
