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
 * This file is part of FenixEdu Academictreasury.
 *
 * FenixEdu Academictreasury is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FenixEdu Academictreasury is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FenixEdu Academictreasury.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.fenixedu.academictreasury.ui.managecoursefunctioncost;

import static org.fenixedu.academictreasury.util.Constants.academicTreasuryBundle;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.fenixedu.academic.domain.CompetenceCourse;
import org.fenixedu.academic.domain.DegreeCurricularPlan;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academictreasury.domain.coursefunctioncost.CourseFunctionCost;
import org.fenixedu.academictreasury.dto.coursefunctioncost.CourseFunctionCostBean;
import org.fenixedu.academictreasury.ui.AcademicTreasuryBaseController;
import org.fenixedu.academictreasury.ui.AcademicTreasuryController;
import org.fenixedu.bennu.core.domain.exceptions.DomainException;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.treasury.util.Constants;
import org.joda.time.Interval;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

//@Component("org.fenixedu.academictreasury.ui.manageCourseFunctionCost") <-- Use for duplicate controller name disambiguation
@SpringFunctionality(app = AcademicTreasuryController.class, title = "label.title.manageCourseFunctionCost",
        accessGroup = "logged")
// CHANGE_ME accessGroup = "group1 | group2 | groupXPTO"
//or
//@BennuSpringController(value=AcademictreasuryController.class) 
@RequestMapping(CourseFunctionCostController.CONTROLLER_URL)
public class CourseFunctionCostController extends AcademicTreasuryBaseController {

    public static final String CONTROLLER_URL = "/academictreasury/managecoursefunctioncost/coursefunctioncost";
    private static final String JSP_PATH = "academicTreasury/managecoursefunctioncost/coursefunctioncost/";

//

    @RequestMapping
    public String home(Model model) {
        //this is the default behaviour, for handling in a Spring Functionality
        return "forward:" + CONTROLLER_URL + "/";
    }

    private CourseFunctionCost getCourseFunctionCost(Model model) {
        return (CourseFunctionCost) model.asMap().get("courseFunctionCost");
    }

    private void setCourseFunctionCost(CourseFunctionCost courseFunctionCost, Model model) {
        model.addAttribute("courseFunctionCost", courseFunctionCost);
    }

    private static final String _SEARCH_URI = "/";
    public static final String SEARCH_URL = CONTROLLER_URL + _SEARCH_URI;

    @RequestMapping(value = _SEARCH_URI)
    public String search(@RequestParam(value = "executionyear", required = false) ExecutionYear executionYear, @RequestParam(
            value = "degreecurricularplan", required = false) DegreeCurricularPlan degreeCurricularPlan, @RequestParam(
            value = "competencecourses", required = false) CompetenceCourse competenceCourses, Model model) {
        List<CourseFunctionCost> searchcoursefunctioncostResultsDataSet =
                filterSearchCourseFunctionCost(executionYear, degreeCurricularPlan, competenceCourses);

        //add the results dataSet to the model
        model.addAttribute("searchcoursefunctioncostResultsDataSet", searchcoursefunctioncostResultsDataSet);
        model.addAttribute(
                "CourseFunctionCost_executionYear_options",
                ExecutionYear.readNotClosedExecutionYears().stream().sorted(ExecutionYear.REVERSE_COMPARATOR_BY_YEAR)
                        .collect(Collectors.toList()));

        model.addAttribute("CourseFunctionCost_degreeCurricularPlan_options", DegreeCurricularPlan
                .readNotEmptyDegreeCurricularPlans().stream().sorted(DegreeCurricularPlan.COMPARATOR_BY_PRESENTATION_NAME)
                .collect(Collectors.toList()));

        model.addAttribute("CourseFunctionCost_competenceCourses_options", CompetenceCourse.readBolonhaCompetenceCourses()
                .stream().sorted(CompetenceCourse.COMPETENCE_COURSE_COMPARATOR_BY_NAME).collect(Collectors.toList()));

        return jspPage("search");
    }

    private Stream<CourseFunctionCost> getSearchUniverseSearchCourseFunctionCostDataSet() {
        return CourseFunctionCost.findAll();
    }

    private List<CourseFunctionCost> filterSearchCourseFunctionCost(ExecutionYear executionYear,
            DegreeCurricularPlan degreeCurricularPlan, CompetenceCourse competenceCourses) {

        return getSearchUniverseSearchCourseFunctionCostDataSet()
                .filter(courseFunctionCost -> executionYear == null || executionYear == courseFunctionCost.getExecutionYear())
                .filter(courseFunctionCost -> degreeCurricularPlan == null
                        || degreeCurricularPlan == courseFunctionCost.getDegreeCurricularPlan())
                .filter(courseFunctionCost -> competenceCourses == null
                        || competenceCourses == courseFunctionCost.getCompetenceCourses()).collect(Collectors.toList());
    }

    private static final String _SEARCH_TO_DELETE_ACTION_URI = "/search/delete/";
    public static final String SEARCH_TO_DELETE_ACTION_URL = CONTROLLER_URL + _SEARCH_TO_DELETE_ACTION_URI;

    @RequestMapping(value = _SEARCH_TO_DELETE_ACTION_URI + "{oid}", method = RequestMethod.POST)
    public String processSearchToDeleteAction(@PathVariable("oid") CourseFunctionCost courseFunctionCost, Model model,
            RedirectAttributes redirectAttributes) {
        setCourseFunctionCost(courseFunctionCost, model);
        try {
            courseFunctionCost.delete();

            addInfoMessage(academicTreasuryBundle("label.CourseFunctionCost.delete.success"), model);
            return redirect("/academictreasury/managecoursefunctioncost/coursefunctioncost/", model, redirectAttributes);
        } catch (DomainException ex) {
            addErrorMessage(ex.getLocalizedMessage(), model);
        }

        return jspPage("search");
    }

//				
    private static final String _CREATE_URI = "/create";
    public static final String CREATE_URL = CONTROLLER_URL + _CREATE_URI;

    @RequestMapping(value = _CREATE_URI, method = RequestMethod.GET)
    public String create(Model model) {
        CourseFunctionCostBean bean = new CourseFunctionCostBean();

        model.addAttribute("courseFunctionCostBean", bean);
        model.addAttribute("courseFunctionCostBeanJson", getBeanJson(bean));

        return jspPage("create");
    }

    private static final String _CREATEPOSTBACK_URI = "/createpostback";
    public static final String CREATEPOSTBACK_URL = CONTROLLER_URL + _CREATEPOSTBACK_URI;

    @RequestMapping(value = _CREATEPOSTBACK_URI, method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public @ResponseBody String createpostback(@RequestParam(value = "bean", required = false) CourseFunctionCostBean bean,
            Model model) {

        bean.updateData();

        return getBeanJson(bean);
    }

    @RequestMapping(value = _CREATE_URI, method = RequestMethod.POST)
    public String create(@RequestParam(value = "bean", required = false) CourseFunctionCostBean bean, Model model,
            RedirectAttributes redirectAttributes) {

        try {
            CourseFunctionCost courseFunctionCost = CourseFunctionCost.create(bean);
            model.addAttribute("courseFunctionCost", courseFunctionCost);

            return redirect(SEARCH_URL, model, redirectAttributes);
        } catch (DomainException de) {
            addErrorMessage(de.getLocalizedMessage(), model);
            return create(model);
        }
    }

    private String jspPage(final String page) {
        return JSP_PATH + "/" + page;
    }

}
