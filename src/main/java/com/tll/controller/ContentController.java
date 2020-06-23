package com.tll.controller;

import com.tll.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author tll
 * @create 2020/6/23 9:10
 * 前端请求编写
 */
@RestController
public class ContentController {

    @Autowired
    private ContentService contentService;

    @GetMapping("/parse/{keywords}")
    public Boolean parseJD(@PathVariable("keywords") String keywords) throws IOException {
        return contentService.parseContent(keywords);
    }

    @GetMapping("/search/{keywords}/{pageNo}/{pageSizwe}")
    public List<Map<String,Object>> search(@PathVariable("keywords")String keywords,
                                           @PathVariable("pageNo")int pageNo,
                                           @PathVariable("pageSizwe")int pageSize) throws IOException {
        //判断不为空
        if (keywords == null){
            keywords = "java";
        }
        if (pageNo == 0){
            pageNo = 1;
        }
        if (pageSize == 0){
            pageSize = 30;
        }
        List<Map<String, Object>> maps = contentService.searchHight(keywords, pageNo, pageSize);
        return maps;
    }
}
