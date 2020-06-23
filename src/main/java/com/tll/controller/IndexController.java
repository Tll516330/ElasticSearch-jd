package com.tll.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author tll
 * @create 2020/6/22 13:38
 */
@Controller
public class IndexController {

    @GetMapping({"/","/index"})
    public String index(){
        return "index";
    }
}
