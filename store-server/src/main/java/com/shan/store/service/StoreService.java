package com.shan.store.service;

import com.shan.store.dto.UploadPolicyResDto;

/**
 * 存储功能抽象接口
 */
public interface StoreService {

    /**
     * 获取下载链接
     *
     * @param fileKey       文件唯一标识
     * @param isPrivate     是否私有资源
     */
    String getDownloadUrl(String fileKey, boolean isPrivate);

    /**
     * 获取表单上传策略
     *
     * @param isPrivate     是否私有资源
     */
    UploadPolicyResDto getFormUploadPolicy(boolean isPrivate);

}
