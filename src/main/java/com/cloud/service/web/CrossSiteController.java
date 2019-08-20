package com.cloud.service.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author: HeYongLiu
 * @create: 08-20-2019
 * @description:
 **/
@Controller
public class CrossSiteController {

    @CrossOrigin("http://localhost:8118")
    @ResponseBody
    @RequestMapping("/test")
    public String test() {
        return "hello";
    }

}
