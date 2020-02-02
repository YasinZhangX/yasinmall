package com.yasinmall.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

/**
 * @author yasin
 */
@Slf4j
public class FTPUtil {

    private static String ftpIp = PropertiesUtil.getProperty("ftp.server.ip");
    private static String ftpUser = PropertiesUtil.getProperty("ftp.user");
    private static String ftpPass = PropertiesUtil.getProperty("ftp.pass");

    private String ip;
    private int port;
    private String user;
    private String passwd;
    private FTPClient ftpClient;

    public FTPUtil(String ip, int port, String user, String passwd) {
        this.ip = ip;
        this.port = port;
        this.user = user;
        this.passwd = passwd;
    }

    public static boolean uploadFile(List<File> filList) throws IOException {
        FTPUtil ftpUtil = new FTPUtil(ftpIp, 21, ftpUser, ftpPass);

        log.info("开始连接FTP服务器");
        boolean result = ftpUtil.uploadFile("img", filList);

        log.info("文件上传结束,上传结果:" + result);

        return result;
    }

    private boolean uploadFile(String remotePath, List<File> fileList) throws IOException {
        boolean uploaded = true;
        FileInputStream fileInputStream = null;

        // 连接FTP服务器
        if (connectServer(this.ip, this.port, this.user, this.passwd)) {
            try {
                ftpClient.changeWorkingDirectory(remotePath);
                ftpClient.setBufferSize(1024);
                ftpClient.setControlEncoding("UTF-8");
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
                ftpClient.enterLocalPassiveMode();
                for (File fileItem : fileList) {
                    fileInputStream = new FileInputStream(fileItem);
                    if (uploaded) {
                        uploaded = ftpClient.storeFile(fileItem.getName(), fileInputStream);
                    }
                }
            } catch (IOException e) {
                log.error("上传文件异常", e);
                uploaded = false;
                e.printStackTrace();
            } finally {
                fileInputStream.close();
                ftpClient.disconnect();
            }
        }

        return uploaded;
    }

    public static void main(String[] args) {

    }

    private boolean connectServer(String ip, int port, String user, String passwd) {
        boolean isSuccess = false;
        ftpClient = new FTPClient();
        try {
            ftpClient.connect(ip);
            isSuccess = ftpClient.login(user, passwd);
        } catch (IOException e) {
            log.error("连接服务器失败", e);
        }
        return isSuccess;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public FTPClient getFtpClient() {
        return ftpClient;
    }

    public void setFtpClient(FTPClient ftpClient) {
        this.ftpClient = ftpClient;
    }
}
