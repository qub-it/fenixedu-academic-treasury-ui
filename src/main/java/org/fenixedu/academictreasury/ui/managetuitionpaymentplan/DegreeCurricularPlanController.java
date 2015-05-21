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

import org.fenixedu.academic.domain.DegreeCurricularPlan;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academictreasury.ui.AcademicTreasuryBaseController;
import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.fenixedu.treasury.domain.FinantialEntity;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Component("org.fenixedu.academicTreasury.ui.manageTuitionPaymentPlan")
@BennuSpringController(value = FinantialEntityController.class)
@RequestMapping(DegreeCurricularPlanController.CONTROLLER_URL)
public class DegreeCurricularPlanController extends AcademicTreasuryBaseController {

    public static final String CONTROLLER_URL = "/academictreasury/managetuitionpaymentplan/degreecurricularplan";
    public static final String JSP_PATH = "academicTreasury/managetuitionpaymentplan/degreecurricularplan";

    @RequestMapping
    public String home(Model model) {
        return "forward:" + CONTROLLER_URL + "/";
    }

    private static final String _CHOOSEDEGREECURRICULARPLAN_URI = "/choosedegreecurricularplan";
    public static final String CHOOSEDEGREECURRICULARPLAN_URL = CONTROLLER_URL + _CHOOSEDEGREECURRICULARPLAN_URI;

    @RequestMapping(value = _CHOOSEDEGREECURRICULARPLAN_URI + "/{finantialEntityId}/{executionYearId}")
    public String chooseDegreeCurricularPlan(@PathVariable("executionYearId") final ExecutionYear executionYear, final Model model) {
        model.addAttribute("choosedegreecurricularplanResultsDataSet", DegreeCurricularPlan.readBolonhaDegreeCurricularPlans());

        return jspPage("choosedegreecurricularplan");
    }

    private static final String _CHOOSEDEGREECURRICULARPLAN_TO_CHOOSE_ACTION_URI = "/chooseDegreeCurricularPlan/choose/";
    public static final String CHOOSEDEGREECURRICULARPLAN_TO_CHOOSE_ACTION_URL = CONTROLLER_URL
            + _CHOOSEDEGREECURRICULARPLAN_TO_CHOOSE_ACTION_URI;

    @RequestMapping(value = _CHOOSEDEGREECURRICULARPLAN_TO_CHOOSE_ACTION_URI
            + "/{finantialEntityId}/{executionYearId}/{degreeCurricularPlanId}")
    public String processChooseDegreeCurricularPlanToChooseAction(
            @PathVariable("finantialEntityId") final FinantialEntity finantialEntity,
            @PathVariable("executionYearId") final ExecutionYear executionYear,
            @PathVariable("degreeCurricularPlanId") final DegreeCurricularPlan degreeCurricularPlan, final Model model,
            final RedirectAttributes redirectAttributes) {

        return redirect("/academictreasury/managetuitionpaymentplan/tuitionpaymentplan/", model, redirectAttributes);
    }

    private String jspPage(final String page) {
        return JSP_PATH + "/" + page;
    }

}
