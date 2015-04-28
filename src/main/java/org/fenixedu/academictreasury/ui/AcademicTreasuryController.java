package org.fenixedu.academictreasury.ui;

import org.fenixedu.bennu.spring.portal.SpringApplication;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@SpringApplication(group = "logged", path = "academicTreasury", title = "title.AcademicTreasury", hint = "Academic Treasury")
@SpringFunctionality(app = AcademicTreasuryController.class, title = "title.AcademicTreasury", accessGroup = "anyone")
@RequestMapping(value="/academicTreasury")
public class AcademicTreasuryController {

    @RequestMapping
    public String home(Model model) {
        model.addAttribute("world", "World");
        return "academicTreasury/home";
    }
    
}
