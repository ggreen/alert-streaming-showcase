

package io.cloudNativeData.showcase.alarm.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@Slf4j
public class IndexController {

    private final String alertFilterValue;
    private final String activityFilterValue;
    private final String sqlFilter;

    public IndexController(
            @Value("${stream.alert.filter.value:}")
            String alertFilterValue,
            @Value("${stream.activity.filter.value:}")
            String activityFilterValue,
            @Value("${stream.filter.sql:}")
            String sqlFilter) {
        this.alertFilterValue = alertFilterValue;
        this.activityFilterValue = activityFilterValue;
        this.sqlFilter = sqlFilter;
    }

    @RequestMapping("/")
    public String homePage(Model model)
    {
        model.addAttribute("alertFilterValue", alertFilterValue);
        model.addAttribute("activityFilterValue", activityFilterValue);
        model.addAttribute("alertWhereSqlFilter", sqlFilter);

        log.info("Model: {}", model);
        return "index";
    }
}
