package com.yasinmall.controller.portal.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Yasin Zhang
 */
@Controller
public class IndexController {

    @RequestMapping("/")
    public String index() {
        return "index";
    }

}
