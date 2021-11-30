package com.changgou.util;

import com.changgou.file.FastDFSFile;
import org.csource.common.MyException;
import org.csource.fastdfs.*;
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FastDFSUtil {

    private static TrackerServer trackerServer;
    private static StorageClient storageClient;
    private static TrackerClient trackerClient;
    /**
     * 加载Tracker
     */
    static {
        try {
            // 通过配置文件中的ip地址端口号, 加载Tracker
            ClientGlobal.init(new ClassPathResource("fdfs_client.conf").getPath());
            // 创建TrackerClient客户端对象
            trackerClient = new TrackerClient();
            // 通过TrackerClient对象获取TrackerServer信息
            trackerServer = trackerClient.getConnection();
            // 获取StorageClient对象
            storageClient = new StorageClient(trackerServer, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    /**
     * 文件上传
     */
    public static String[] upload(FastDFSFile fastDFSFile) throws IOException, MyException {
        String[] strings = storageClient.upload_file(fastDFSFile.getContent(), fastDFSFile.getExt(), null);

         return strings;
    }


    /**
     * 获取文件信息
     */
    public static FileInfo getFileInfo(String groupName, String remoteFileName) throws Exception{
        FileInfo fileInfo = storageClient.get_file_info(groupName, remoteFileName);
        return fileInfo;
    }

    /**
     * 文件下载
     */
    public static InputStream downloadFile(String groupName, String remoteFileName) throws IOException, MyException {
        byte[] bytes = storageClient.download_file(groupName, remoteFileName);
        return new ByteArrayInputStream(bytes);

    }

    /**
     * 删除文件
     */
    public static int deleteFile(String groupName, String remoteFileName) throws IOException, MyException {
        int i = storageClient.delete_file(groupName, remoteFileName);
        return i;
    }

    /**
     * 获取组的信息
     */
    public static StorageServer getStorages(String groupName) throws IOException {
        return trackerClient.getStoreStorage(trackerServer, groupName);
    }

    /**
     * 根据组名和文件名, 获取Storage的ip地址和端口号
     */
    public static ServerInfo[] getServerInfo(String groupName, String remoteFileName) throws IOException {
        return trackerClient.getFetchStorages(trackerServer, groupName, remoteFileName);
    }

    /**
     * 获取tracker服务地址
     */
    public static String getTrackerUrl() {
        return "http://" + trackerServer.getInetSocketAddress().getHostString() + ":" + ClientGlobal.getG_tracker_group();
    }

    /**
     * 测试
     */
    public static void main(String[] args) throws Exception {
        //FileInfo fileInfo = getFileInfo("group1", "M00/00/00/wKjThGGjWyaAC7_eAABfePPvcno440.jpg");
        //System.out.println("fileInfo = " + fileInfo);

        // InputStream is = downloadFile("group1", "M00/00/00/wKjThGGjWyaAC7_eAABfePPvcno440.jpg");

        // System.out.println(deleteFile("group1", "M00/00/00/wKjThGGjWyaAC7_eAABfePPvcno440.jpg"));


    }
}
