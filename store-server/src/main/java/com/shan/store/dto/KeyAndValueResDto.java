package com.shan.store.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

/**
 * key and value 实体
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class KeyAndValueResDto implements Serializable {

    /**
     * 键
     */
    private String key;

    /**
     * 值
     */
    private String value;

}