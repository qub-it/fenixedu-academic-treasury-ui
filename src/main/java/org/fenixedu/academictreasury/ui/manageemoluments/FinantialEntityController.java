package org.fenixedu.academictreasury.ui.manageemoluments;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.fenixedu.academictreasury.ui.AcademicTreasuryBaseController;
import org.fenixedu.academictreasury.ui.AcademicTreasuryController;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.treasury.domain.FinantialEntity;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import pt.ist.fenixframework.Atomic;

@Component("org.fenixedu.academictreasury.ui.manageemoluments.FinantialEntityController")
@SpringFunctionality(app = AcademicTreasuryController.class, title = "label.title.manageEmoluments",
        accessGroup = "treasuryBackOffice")
@RequestMapping("/academictreasury/manageemoluments/finantialentity")
public class FinantialEntityController extends AcademicTreasuryBaseController {

    @RequestMapping
    public String home(Model model) {
        return "forward:/academictreasury/manageemoluments/finantialentity/choosefinantialentity";
    }

    @RequestMapping(value = "/choosefinantialentity")
    public String chooseFinantialEntity(Model model) {
        model.addAttribute("choosefinantialentityResultsDataSet", getSearchUniverseChooseFinantialEntityDataSet());
        return "academicTreasury/manageemoluments/finantialentity/choosefinantialentity";
    }

    private List<FinantialEntity> getSearchUniverseChooseFinantialEntityDataSet() {
        return FinantialEntity.findWithBackOfficeAccessFor(Authenticate.getUser())
                .sorted(FinantialEntity.COMPARE_BY_NAME).collect(Collectors.toList());
    }

    @RequestMapping(value = "/chooseFinantialEntity/choose/{oid}")
    public String processChooseFinantialEntityToChooseAction(@PathVariable("oid") FinantialEntity finantialEntity, Model model) {
        return String.format("redirect:/academictreasury/manageemoluments/product/searchemoluments/%s",
                finantialEntity.getExternalId());
    }

    private FinantialEntity getFinantialEntity(Model m) {
        return (FinantialEntity) m.asMap().get("finantialEntity");
    }

    private void setFinantialEntity(FinantialEntity finantialEntity, Model m) {
        m.addAttribute("finantialEntity", finantialEntity);
    }

}
