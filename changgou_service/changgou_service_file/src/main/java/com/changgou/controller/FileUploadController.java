package com.changgou.controller;

import com.changgou.file.FastDFSFile;
import com.changgou.util.FastDFSUtil;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/upload")
@CrossOrigin
public class FileUploadController {
    /**
     * 文件上传
     *
     */
    @PostMapping
    public Result_my upload(@RequestParam(value = "file")MultipartFile file) throws Exception {
        FastDFSFile fastDFSFile = new FastDFSFile();
        fastDFSFile.setName(file.getOriginalFilename());
        fastDFSFile.setContent(file.getBytes());
        fastDFSFile.setExt(StringUtils.getFilenameExtension(file.getOriginalFilename()));
        // 调用FastDFSUtil拱极路上传
        String[] strings = FastDFSUtil.upload(fastDFSFile);
        // 拼接文件路径
        String url = "http://192.168.211.132:8080/" + strings[0] + "/" + strings[1];
        return Result_my.ok().message("上传成功").data("url", url);
    }


}
