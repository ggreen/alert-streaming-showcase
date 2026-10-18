

package showcase.alarm.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
public class IndexController {

    private final String userIdAttribId;
    private final String userId;
    private final String sqlFilter;

    public IndexController(
            @Value("${stream.activity.filter.name:account}")
            String userIdAttribId,
            @Value("${stream.activity.filter.value:}")
            String userId,
            @Value("${stream.filter.sql:}")
            String sqlFilter) {
        this.userIdAttribId = userIdAttribId;
        this.userId = userId;
        this.sqlFilter = sqlFilter;
    }

    @RequestMapping("/")
    public String homePage(Model model)
    {
        model.addAttribute(userIdAttribId, userId);
        model.addAttribute("alertWhereSqlFilter", sqlFilter);
        return "index";
    }
}
