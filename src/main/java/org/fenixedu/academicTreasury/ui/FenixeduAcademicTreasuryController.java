package org.fenixedu.academicTreasury.ui;

import org.fenixedu.bennu.spring.portal.SpringApplication;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/fenixedu-academic-treasury")
@SpringApplication(group = "logged", path = "fenixedu-academic-treasury", title = "title.FenixeduAcademicTreasury")
@SpringFunctionality(app = FenixeduAcademicTreasuryController.class, title = "title.FenixeduAcademicTreasury")
public class FenixeduAcademicTreasuryController {

    @RequestMapping
    public String home(Model model) {
        model.addAttribute("world", "World");
        return "fenixedu-academic-treasury/home";
    }

}
