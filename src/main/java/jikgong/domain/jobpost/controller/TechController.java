package jikgong.domain.jobpost.controller;

import jikgong.domain.workexperience.entity.Tech;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TechController {

    /**
     * Tech Enum 문서화를 위한 컨트롤러
     */

    @GetMapping("/tech-list")
    public String getAllTechList(Model model) {
        Tech[] techEnums = Tech.values();
        model.addAttribute("techEnums", techEnums);
        return "techList";
    }
}
