package com.example.demo.utils.beans;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 陈坤
 * @serial 2019/2/26
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Target {

    private String id;
    private String name;
    private Integer age;

}
