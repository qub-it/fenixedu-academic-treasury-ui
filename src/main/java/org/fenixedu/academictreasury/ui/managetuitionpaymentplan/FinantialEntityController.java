/**
 * This file was created by Quorum Born IT <http://www.qub-it.com/> and its 
 * copyright terms are bind to the legal agreement regulating the FenixEdu@ULisboa 
 * software development project between Quorum Born IT and ServiÃ§os Partilhados da
 * Universidade de Lisboa:
 *  - Copyright Â© 2015 Quorum Born IT (until any Go-Live phase)
 *  - Copyright Â© 2015 Universidade de Lisboa (after any Go-Live phase)
 *
 * Contributors: xpto@qub-it.com
 *
 * 
 * This file is part of FenixEdu AcademicTreasury.
 *
 * FenixEdu AcademicTreasury is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FenixEdu AcademicTreasury is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FenixEdu AcademicTreasury.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.fenixedu.academictreasury.ui.managetuitionpaymentplan;

import java.util.stream.Collectors;

import org.fenixedu.academictreasury.ui.AcademicTreasuryBaseController;
import org.fenixedu.academictreasury.ui.AcademicTreasuryController;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.treasury.domain.FinantialEntity;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Component("org.fenixedu.academictreasury.ui.managetuitionpaymentplan")
@SpringFunctionality(app = AcademicTreasuryController.class, title = "label.title.manageTuitionPaymentPlan",
        accessGroup = "logged")
@RequestMapping(FinantialEntityController.CONTROLLER_URL)
public class FinantialEntityController extends AcademicTreasuryBaseController {

    public static final String CONTROLLER_URL = "/academictreasury/managetuitionpaymentplan/finantialentity";
    private static final String JSP_PATH = "academicTreasury/managetuitionpaymentplan/finantialentity";

    @RequestMapping
    public String home(Model model) {
        return "forward:" + CONTROLLER_URL + "/";
    }

    private static final String _CHOOSEFINANTIALENTITY_URI = "/choosefinantialentity";
    public static final String CHOOSEFINANTIALENTITY_URL = CONTROLLER_URL + _CHOOSEFINANTIALENTITY_URI;

    @RequestMapping(value = _CHOOSEFINANTIALENTITY_URI)
    public String chooseFinantialEntity(final Model model) {
        model.addAttribute("choosefinantialentityResultsDataSet", FinantialEntity.findAll().collect(Collectors.toSet()));

        return jspPage("choosefinantialentity");
    }

    private static final String _CHOOSEFINANTIALENTITY_TO_CHOOSE_ACTION_URI = "/chooseFinantialEntity/choose/";
    public static final String CHOOSEFINANTIALENTITY_TO_CHOOSE_ACTION_URL = CONTROLLER_URL
            + _CHOOSEFINANTIALENTITY_TO_CHOOSE_ACTION_URI;

    @RequestMapping(value = _CHOOSEFINANTIALENTITY_TO_CHOOSE_ACTION_URI + "{finantialEntityId}")
    public String processChooseFinantialEntityToChooseAction(
            @PathVariable("finantialEntityId") final FinantialEntity finantialEntity, final Model model,
            final RedirectAttributes redirectAttributes) {

        return redirect(DegreeCurricularPlanController.CONTROLLER_URL + "/" + finantialEntity.getExternalId(), model,
                redirectAttributes);
    }

    private String jspPage(final String page) {
        return JSP_PATH + "/" + page;
    }

}
