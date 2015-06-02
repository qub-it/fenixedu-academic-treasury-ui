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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.fenixedu.academic.domain.DegreeCurricularPlan;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academictreasury.domain.tuition.TuitionPaymentPlan;
import org.fenixedu.academictreasury.domain.tuition.TuitionPaymentPlanGroup;
import org.fenixedu.academictreasury.dto.tariff.TuitionPaymentPlanBean;
import org.fenixedu.academictreasury.ui.AcademicTreasuryBaseController;
import org.fenixedu.academictreasury.ui.AcademicTreasuryController;
import org.fenixedu.bennu.core.domain.exceptions.DomainException;
import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.fenixedu.treasury.domain.FinantialEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import pt.ist.fenixframework.Atomic;

//@Component("org.fenixedu.academicTreasury.ui.manageTuitionPaymentPlan") <-- Use for duplicate controller name disambiguation
//@SpringFunctionality(app = AcademicTreasuryController.class, title = "label.title.manageTuitionPaymentPlan",accessGroup = "logged")// CHANGE_ME accessGroup = "group1 | group2 | groupXPTO"
//or
@BennuSpringController(value = AcademicTreasuryController.class)
@RequestMapping(TuitionPaymentPlanController.CONTROLLER_URL)
public class TuitionPaymentPlanController extends AcademicTreasuryBaseController {

    public static final String CONTROLLER_URL = "/academictreasury/managetuitionpaymentplan/tuitionpaymentplan";
    private static final String JSP_PATH = "academicTreasury/managetuitionpaymentplan/tuitionpaymentplan";

    @RequestMapping
    public String home(Model model) {
        //this is the default behaviour, for handling in a Spring Functionality
        return "forward:" + CONTROLLER_URL + "/";
    }

    // @formatter: off

    /*
    * This should be used when using AngularJS in the JSP
    */

    //private TuitionPaymentPlan getTuitionPaymentPlanBean(Model model)
    //{
    //  return (TuitionPaymentPlan)model.asMap().get("tuitionPaymentPlanBean");
    //}
    //              
    //private void setTuitionPaymentPlanBean (TuitionPaymentPlanBean bean, Model model)
    //{
    //  model.addAttribute("tuitionPaymentPlanBeanJson", getBeanJson(bean));
    //  model.addAttribute("tuitionPaymentPlanBean", bean);
    //}

    // @formatter: on

    private TuitionPaymentPlan getTuitionPaymentPlan(Model model) {
        return (TuitionPaymentPlan) model.asMap().get("tuitionPaymentPlan");
    }

    private void setTuitionPaymentPlan(TuitionPaymentPlan tuitionPaymentPlan, Model model) {
        model.addAttribute("tuitionPaymentPlan", tuitionPaymentPlan);
    }

    @Atomic
    public void deleteTuitionPaymentPlan(TuitionPaymentPlan tuitionPaymentPlan) {
        // CHANGE_ME: Do the processing for deleting the tuitionPaymentPlan
        // Do not catch any exception here

        // tuitionPaymentPlan.delete();
    }

//              
    private static final String _SEARCH_URI = "/";
    public static final String SEARCH_URL = CONTROLLER_URL + _SEARCH_URI;

    @RequestMapping(value = _SEARCH_URI + "{finantialEntityId}/{executionYearId}/{degreeCurricularPlanId}")
    public String search(@PathVariable("finantialEntityId") final FinantialEntity finantialEntity,
            @PathVariable("executionYearId") final ExecutionYear executionYear,
            @PathVariable("degreeCurricularPlanId") final DegreeCurricularPlan degreeCurricularPlan, final Model model) {

        //add the results dataSet to the model
        model.addAttribute("finantialEntity", finantialEntity);
        model.addAttribute("executionYear", executionYear);
        model.addAttribute("degreeCurricularPlan", degreeCurricularPlan);

        model.addAttribute("searchtuitionpaymentplanResultsDataSet", TuitionPaymentPlan.findSortedByPaymentPlanOrder(
                TuitionPaymentPlanGroup.findUniqueDefaultGroupForRegistration().get(), degreeCurricularPlan, executionYear).collect(Collectors.toSet()));

        return jspPage("search");
    }

    private static final String _SEARCH_TO_DELETE_ACTION_URI = "/search/delete/";
    public static final String SEARCH_TO_DELETE_ACTION_URL = CONTROLLER_URL + _SEARCH_TO_DELETE_ACTION_URI;

    @RequestMapping(value = _SEARCH_TO_DELETE_ACTION_URI + "{oid}", method = RequestMethod.POST)
    public String processSearchToDeleteAction(@PathVariable("oid") TuitionPaymentPlan tuitionPaymentPlan, Model model,
            RedirectAttributes redirectAttributes) {
        setTuitionPaymentPlan(tuitionPaymentPlan, model);
        try {
            //call the Atomic delete function
            deleteTuitionPaymentPlan(tuitionPaymentPlan);

            addInfoMessage("Sucess deleting TuitionPaymentPlan ...", model);
            return redirect("/academictreasury/managetuitionpaymentplan/tuitionpaymentplan/", model, redirectAttributes);
        } catch (DomainException ex) {
            addErrorMessage(ex.getLocalizedMessage(), model);
        }

        //The default mapping is the same Search screen
        return "academicTreasury/managetuitionpaymentplan/tuitionpaymentplan/search";
    }

//              
    private static final String _CREATECHOOSEDEGREECURRICULARPLANS_URI = "/createchoosedegreecurricularplans";
    public static final String CREATECHOOSEDEGREECURRICULARPLANS_URL = CONTROLLER_URL + _CREATECHOOSEDEGREECURRICULARPLANS_URI;

    @RequestMapping(value = _CREATECHOOSEDEGREECURRICULARPLANS_URI + "/{finantialEntityId}/{executionYearId}",
            method = RequestMethod.GET)
    public String createchoosedegreecurricularplans(@PathVariable("finantialEntityId") final FinantialEntity finantialEntity,
            @PathVariable("executionYearId") final ExecutionYear executionYear, final Model model) {

        final TuitionPaymentPlanBean bean =
                new TuitionPaymentPlanBean(null, TuitionPaymentPlanGroup.findUniqueDefaultGroupForRegistration().get(),
                        finantialEntity, executionYear);

        model.addAttribute("finantialEntity", finantialEntity);
        model.addAttribute("executionYear", executionYear);
        model.addAttribute("bean", bean);
        model.addAttribute("tuitionPaymentPlanBeanJson", getBeanJson(bean));

        return jspPage("createchoosedegreecurricularplans");
    }

    private static final String _CREATECHOOSEDEGREECURRICULARPLANSPOSTBACK_URI = "/createchoosedegreecurricularplanspostback";
    public static final String CREATECHOOSEDEGREECURRICULARPLANSPOSTBACK_URL = CONTROLLER_URL
            + _CREATECHOOSEDEGREECURRICULARPLANSPOSTBACK_URI;

    @RequestMapping(value = _CREATECHOOSEDEGREECURRICULARPLANSPOSTBACK_URI + "/{finantialEntityId}/{executionYearId}",
            method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public @ResponseBody ResponseEntity<String> createchoosedegreecurricularplanspostback(
            @PathVariable("finantialEntityId") final FinantialEntity finantialEntity,
            @PathVariable("executionYearId") final ExecutionYear executionYear,
            @RequestParam("bean") final TuitionPaymentPlanBean bean, final Model model) {

        bean.updateData();

        return new ResponseEntity<String>(getBeanJson(bean), HttpStatus.OK);
    }

    private static final String _CREATEDEFINESTUDENTCONDITIONS_URI = "/createdefinestudentconditions";
    public static final String CREATEDEFINESTUDENTCONDITIONS_URL = CONTROLLER_URL + _CREATEDEFINESTUDENTCONDITIONS_URI;

    @RequestMapping(value = _CREATEDEFINESTUDENTCONDITIONS_URI + "/{finantialEntityId}/{executionYearId}",
            method = RequestMethod.POST)
    public String createdefinestudentconditions(@PathVariable("finantialEntityId") final FinantialEntity finantialEntity,
            @PathVariable("executionYearId") final ExecutionYear executionYear,
            @RequestParam("bean") final TuitionPaymentPlanBean bean, final Model model) {

        model.addAttribute("finantialEntity", finantialEntity);
        model.addAttribute("executionYear", executionYear);
        model.addAttribute("bean", bean);
        model.addAttribute("tuitionPaymentPlanBeanJson", getBeanJson(bean));

        return jspPage("createdefinestudentconditions");
    }

    private static final String _CREATEDEFINESTUDENTCONDITIONSPOSTBACK_URI = "/createdefinestudentconditionspostback";
    public static final String CREATEDEFINESTUDENTCONDITIONSPOSTBACK_URL = CONTROLLER_URL
            + _CREATEDEFINESTUDENTCONDITIONSPOSTBACK_URI;

    @RequestMapping(value = _CREATEDEFINESTUDENTCONDITIONSPOSTBACK_URI + "/{finantialEntityId}/{executionYearId}",
            method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public @ResponseBody ResponseEntity<String> createdefinestudentconditionspostback(
            @PathVariable("finantialEntityId") final FinantialEntity finantialEntity,
            @PathVariable("executionYearId") final ExecutionYear executionYear,
            @RequestParam("bean") final TuitionPaymentPlanBean bean, final Model model) {

        return new ResponseEntity<String>(getBeanJson(bean), HttpStatus.OK);
    }

    private static final String _CREATEINSERTINSTALLMENTS_URI = "/createinsertinstallments";
    public static final String CREATEINSERTINSTALLMENTS_URL = CONTROLLER_URL + _CREATEINSERTINSTALLMENTS_URI;

    @RequestMapping(value = _CREATEINSERTINSTALLMENTS_URI + "/{finantialEntityId}/{executionYearId}", method = RequestMethod.POST)
    public String createinsertinstallments(@PathVariable("finantialEntityId") final FinantialEntity finantialEntity,
            @PathVariable("executionYearId") final ExecutionYear executionYear,
            @RequestParam("bean") final TuitionPaymentPlanBean bean, final Model model) {

        model.addAttribute("finantialEntity", finantialEntity);
        model.addAttribute("executionYear", executionYear);
        model.addAttribute("bean", bean);
        model.addAttribute("tuitionPaymentPlanBeanJson", getBeanJson(bean));

        return jspPage("createinsertinstallments");
    }

    private static final String _CREATEINSERTINSTALLMENTSPOSTBACK_URI = "/createinsertinstallmentspostback";
    public static final String CREATEINSERTINSTALLMENTSPOSTBACK_URL = CONTROLLER_URL + _CREATEINSERTINSTALLMENTSPOSTBACK_URI;

    @RequestMapping(value = _CREATEINSERTINSTALLMENTSPOSTBACK_URI + "/{finantialEntityId}/{executionYearId}",
            method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public @ResponseBody ResponseEntity<String> createinsertinstallmentspostback(
            @PathVariable("finantialEntityId") final FinantialEntity finantialEntity,
            @PathVariable("executionYearId") final ExecutionYear executionYear,
            @RequestParam("bean") final TuitionPaymentPlanBean bean, final Model model) {

        return new ResponseEntity<String>(getBeanJson(bean), HttpStatus.OK);
    }

    private static final String _ADDINSTALLMENTSPOSTBACK_URI = "/addinstallmentspostback";
    public static final String ADDINSTALLMENTSPOSTBACK_URL = CONTROLLER_URL + _ADDINSTALLMENTSPOSTBACK_URI;

    @RequestMapping(value = _ADDINSTALLMENTSPOSTBACK_URI + "/{finantialEntityId}/{executionYearId}", method = RequestMethod.POST,
            produces = "application/json;charset=UTF-8")
    public String addinstallmentspostback(@PathVariable("finantialEntityId") final FinantialEntity finantialEntity,
            @PathVariable("executionYearId") final ExecutionYear executionYear,
            @RequestParam("bean") final TuitionPaymentPlanBean bean, final Model model) {

        bean.addInstallment();
        bean.resetInstallmentFields();

        model.addAttribute("finantialEntity", finantialEntity);
        model.addAttribute("executionYear", executionYear);
        model.addAttribute("bean", bean);
        model.addAttribute("tuitionPaymentPlanBeanJson", getBeanJson(bean));

        return jspPage("createinsertinstallments");
    }

//    public @ResponseBody ResponseEntity<String> addinstallmentspostback(@PathVariable("finantialEntityId") final FinantialEntity finantialEntity,
//            @PathVariable("executionYearId") final ExecutionYear executionYear,
//            @RequestParam("bean") final TuitionPaymentPlanBean bean, final Model model) {
//
//        return new ResponseEntity<String>(getBeanJson(bean), HttpStatus.OK);
//    }

    private static final String _CREATEPAYMENTPLAN_URI = "/createpaymentplan";
    public static final String CREATEPAYMENTPLAN_URL = CONTROLLER_URL + _CREATEPAYMENTPLAN_URI;

    @RequestMapping(value = _CREATEPAYMENTPLAN_URI + "/{finantialEntityId}/{executionYearId}", method = RequestMethod.POST)
    public String createinsertinstallments(@PathVariable("finantialEntityId") final FinantialEntity finantialEntity,
            @PathVariable("executionYearId") final ExecutionYear executionYear,
            @RequestParam("bean") final TuitionPaymentPlanBean bean, final Model model,
            final RedirectAttributes redirectAttributes) {

        try {
            TuitionPaymentPlan.create(bean);

            //Success Validation
            //Add the bean to be used in the View

            return redirect(
                    String.format("%s/%s/%s", DegreeCurricularPlanController.CHOOSEDEGREECURRICULARPLAN_URL,
                            finantialEntity.getExternalId(), executionYear.getExternalId()), model, redirectAttributes);
        } catch (final DomainException de) {
            addErrorMessage(de.getLocalizedMessage(), model);
            return createinsertinstallments(finantialEntity, executionYear, bean, model);
        }
    }

    private String jspPage(final String page) {
        return JSP_PATH + "/" + page;
    }

}
