package com.shan.store.controller;

import com.shan.store.dto.ApiResult;
import com.shan.store.service.StoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/store")
public class StoreController {

    @Autowired
    private StoreService storeService;

    /**
     * 获取文件下载链接
     *
     * @param fileKey       文件唯一标识
     * @param isPrivate     是否私有资源
     */
    @GetMapping("/downloadUrl")
    public ApiResult getDownloadUrl(@RequestParam("fileKey") String fileKey,
                                    @RequestParam("isPrivate") boolean isPrivate) {
        return ApiResult.success(storeService.getDownloadUrl(fileKey, isPrivate));
    }

    /**
     * 获取表单上传策略
     *
     * @param isPrivate     是否私有资源
     */
    @GetMapping("/uploadPolicy")
    public ApiResult getUploadPolicy(@RequestParam("isPrivate") boolean isPrivate) {
        return ApiResult.success(storeService.getFormUploadPolicy(isPrivate));
    }

}
