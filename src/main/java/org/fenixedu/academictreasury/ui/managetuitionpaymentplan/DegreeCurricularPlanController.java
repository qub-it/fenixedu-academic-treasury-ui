/**
 * This file was created by Quorum Born IT <http://www.qub-it.com/> and its 
 * copyright terms are bind to the legal agreement regulating the FenixEdu@ULisboa 
 * software development project between Quorum Born IT and ServiÃ§os Partilhados da
 * Universidade de Lisboa:
 *  - Copyright Â© 2015 Quorum Born IT (until any Go-Live phase)
 *  - Copyright Â© 2015 Universidade de Lisboa (after any Go-Live phase)
 *
 * Contributors: ricardo.pedro@qub-it.com, anil.mamede@qub-it.com
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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.DegreeCurricularPlan;
import org.fenixedu.academic.domain.ExecutionDegree;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academictreasury.ui.AcademicTreasuryBaseController;
import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.fenixedu.treasury.domain.FinantialEntity;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Lists;

import edu.emory.mathcs.backport.java.util.Collections;

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
    public String chooseDegreeCurricularPlan(@PathVariable("finantialEntityId") FinantialEntity finantialEntity,
            @PathVariable("executionYearId") final ExecutionYear executionYear, final Model model) {

        List<DegreeCurricularPlan> degreeCurricularPlanList = Lists.newArrayList(ExecutionDegree.getAllByExecutionYear(executionYear)
                .stream().map(e -> e.getDegreeCurricularPlan()).collect(Collectors.toList()));
        
        Collections.sort(degreeCurricularPlanList, DegreeCurricularPlan.DEGREE_CURRICULAR_PLAN_COMPARATOR_BY_DEGREE_TYPE_AND_EXECUTION_DEGREE_AND_DEGREE_CODE);
        
        model.addAttribute("choosedegreecurricularplanResultsDataSet", degreeCurricularPlanList);
        
        model.addAttribute("finantialEntity", finantialEntity);
        model.addAttribute("executionYear", executionYear);
        
        final List<ExecutionYear> executionYearList = new ArrayList<ExecutionYear>(ExecutionYear.readNotClosedExecutionYears());

        Collections.sort(executionYearList, Collections.reverseOrder(new Comparator<ExecutionYear>() {

            @Override
            public int compare(final ExecutionYear o1, final ExecutionYear o2) {
                int c = o1.getBeginLocalDate().compareTo(o2.getBeginLocalDate());
                
                return c != 0 ? c : o1.getExternalId().compareTo(o2.getExternalId());
            }
        }));
        
        model.addAttribute("executionYearOptions", executionYearList);

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

        return redirect(String.format(TuitionPaymentPlanController.SEARCH_URL + "%s/%s/%s", finantialEntity.getExternalId(),
                executionYear.getExternalId(), degreeCurricularPlan.getExternalId()), model, redirectAttributes);
    }
    
    private String jspPage(final String page) {
        return JSP_PATH + "/" + page;
    }

}
