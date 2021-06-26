package com.shan.store.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * 表单上传策略实体
 */
@Data
@NoArgsConstructor
public class UploadPolicyResDto implements Serializable {

    /**
     * 上传地址
     */
    private String uploadUrl;

    /**
     * 上传表单input的属性（表单字段）
     */
    private List<KeyAndValueResDto> formFields = Collections.emptyList();

}
