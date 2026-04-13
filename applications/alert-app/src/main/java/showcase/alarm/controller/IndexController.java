

package showcase.alarm.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
public class IndexController {

    private final String userIdAttribId;
    private final String userId;

    public IndexController(
            @Value("${-stream.activity.filter.name:account}")
            String userIdAttribId,
            @Value("${stream.activity.filter.value:}")
            String userId) {
        this.userIdAttribId = userIdAttribId;
        this.userId = userId;
    }

    @RequestMapping("/")
    public String homePage(Model model)
    {
        model.addAttribute(userIdAttribId, userId);
        return "index";
    }
}
