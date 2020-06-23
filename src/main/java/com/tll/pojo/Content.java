package com.tll.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author tll
 * @create 2020/6/23 8:57
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Content {
    private String title;
    private String price;
    private String img;
}
