

package io.cloudNativeData.alarm.ai.source.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
public class IndexController {

    @Value("${stream.activity.filter.value}")
    private String filterValue;

    @Value("${stream.activity.filter.name:account}")
    private String filterPropName;

    @RequestMapping("/")
    public String homePage(Model model)
    {
        model.addAttribute("filterPropName", filterPropName);
        model.addAttribute("filterValue", filterValue);
        return "index";
    }
}
