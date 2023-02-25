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

import static org.fenixedu.academictreasury.util.AcademicTreasuryConstants.academicTreasuryBundle;

import java.util.List;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.DegreeCurricularPlan;
import org.fenixedu.academic.domain.ExecutionInterval;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academictreasury.domain.tuition.TuitionInstallmentTariff;
import org.fenixedu.academictreasury.domain.tuition.TuitionPaymentPlan;
import org.fenixedu.academictreasury.domain.tuition.TuitionPaymentPlanGroup;
import org.fenixedu.academictreasury.dto.tariff.AcademicTariffBean;
import org.fenixedu.academictreasury.dto.tariff.TuitionPaymentPlanBean;
import org.fenixedu.academictreasury.ui.AcademicTreasuryBaseController;
import org.fenixedu.academictreasury.util.AcademicTreasuryConstants;
import org.fenixedu.bennu.core.domain.exceptions.DomainException;
import org.fenixedu.bennu.core.i18n.BundleUtil;
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

import com.google.common.base.Strings;

//@Component("org.fenixedu.academicTreasury.ui.manageTuitionPaymentPlan") <-- Use for duplicate controller name disambiguation
//@SpringFunctionality(app = AcademicTreasuryController.class, title = "label.title.manageTuitionPaymentPlan",accessGroup = "logged")// CHANGE_ME accessGroup = "group1 | group2 | groupXPTO"
//or
@BennuSpringController(value = FinantialEntityController.class)
@RequestMapping(TuitionPaymentPlanController.CONTROLLER_URL)
@Deprecated
public class TuitionPaymentPlanController extends AcademicTreasuryBaseController {

    public static final String CONTROLLER_URL = "/academictreasury/managetuitionpaymentplan/tuitionpaymentplan";
    private static final String JSP_PATH = "academicTreasury/managetuitionpaymentplan/tuitionpaymentplan";

    @RequestMapping
    public String home(Model model) {
        return "forward:" + CONTROLLER_URL + "/";
    }

    private TuitionPaymentPlan getTuitionPaymentPlan(Model model) {
        return (TuitionPaymentPlan) model.asMap().get("tuitionPaymentPlan");
    }

    private void setTuitionPaymentPlan(TuitionPaymentPlan tuitionPaymentPlan, Model model) {
        model.addAttribute("tuitionPaymentPlan", tuitionPaymentPlan);
    }

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

        model.addAttribute(
                "searchtuitionpaymentplanResultsDataSet",
                TuitionPaymentPlan.findSortedByPaymentPlanOrder(
                        TuitionPaymentPlanGroup.findUniqueDefaultGroupForRegistration().get(), degreeCurricularPlan,
                        executionYear).collect(Collectors.toList()));

        return jspPage("search");
    }

    private static final String _SEARCH_TO_DELETE_ACTION_URI = "/search/delete";
    public static final String SEARCH_TO_DELETE_ACTION_URL = CONTROLLER_URL + _SEARCH_TO_DELETE_ACTION_URI;

    @RequestMapping(value = _SEARCH_TO_DELETE_ACTION_URI
            + "/{finantialEntityId}/{executionYearId}/{degreeCurricularPlanId}/{tuitionPaymentPlanId}",
            method = RequestMethod.POST)
    public String processSearchToDeleteAction(@PathVariable("finantialEntityId") final FinantialEntity finantialEntity,
            @PathVariable("executionYearId") final ExecutionYear executionYear,
            @PathVariable("degreeCurricularPlanId") final DegreeCurricularPlan degreeCurricularPlan,
            @PathVariable("tuitionPaymentPlanId") TuitionPaymentPlan tuitionPaymentPlan, final Model model,
            final RedirectAttributes redirectAttributes) {
        try {

            tuitionPaymentPlan.delete();

            addInfoMessage(academicTreasuryBundle("label.TuitionPaymentPlan.deletion.success"), model);
        } catch (DomainException ex) {
            addErrorMessage(ex.getLocalizedMessage(), model);
        }

        return redirect(String.format(SEARCH_URL + "/%s/%s/%s", finantialEntity.getExternalId(), executionYear.getExternalId(),
                degreeCurricularPlan.getExternalId()), model, redirectAttributes);
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

        return _createchoosedegreecurricularplans(finantialEntity, executionYear, model, bean);
    }

    private String _createchoosedegreecurricularplans(final FinantialEntity finantialEntity, final ExecutionYear executionYear,
            final Model model, final TuitionPaymentPlanBean bean) {

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

        if (bean.getDegreeType() == null || bean.getDegreeCurricularPlans().isEmpty()) {
            addErrorMessage(BundleUtil.getString(AcademicTreasuryConstants.BUNDLE, "error.TuitionPaymentPlan.choose.degree.curricular.plans"),
                    model);

            return _createchoosedegreecurricularplans(finantialEntity, executionYear, model, bean);
        }

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

        List<String> messages = bean.validateStudentConditions();
        if (!messages.isEmpty()) {
            for (String m : messages) {
                addErrorMessage(academicTreasuryBundle(m), model);
            }

            return createdefinestudentconditions(finantialEntity, executionYear, bean, model);
        }

        if (bean.isCustomized() && Strings.isNullOrEmpty(bean.getName())) {
            addErrorMessage(academicTreasuryBundle("error.TuitionPaymentPlan.custom.payment.plan.name.required"), model);
            return createdefinestudentconditions(finantialEntity, executionYear, bean, model);
        }

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

        final List<String> errorMessages = bean.addInstallment();

        if (!errorMessages.isEmpty()) {
            for (final String error : errorMessages) {
                addErrorMessage(academicTreasuryBundle(error), model);
            }
        } else {
            bean.resetInstallmentFields();
        }

        model.addAttribute("finantialEntity", finantialEntity);
        model.addAttribute("executionYear", executionYear);
        model.addAttribute("bean", bean);
        model.addAttribute("tuitionPaymentPlanBeanJson", getBeanJson(bean));

        return jspPage("createinsertinstallments");
    }

    private static final String _REMOVEINSTALLMENT_URI = "/removeinstallment";
    public static final String REMOVEINSTALLMENT_URL = CONTROLLER_URL + _REMOVEINSTALLMENT_URI;

    @RequestMapping(value = _REMOVEINSTALLMENT_URI + "/{finantialEntityId}/{executionYearId}/{installmentNumber}",
            method = RequestMethod.POST)
    public String addinstallmentspostback(@PathVariable("finantialEntityId") final FinantialEntity finantialEntity,
            @PathVariable("executionYearId") final ExecutionYear executionYear,
            @PathVariable("installmentNumber") final int installmentNumber,
            @RequestParam("bean") final TuitionPaymentPlanBean bean, final Model model) {

        try {
            bean.removeInstallment(installmentNumber);
        } catch (final DomainException de) {
            addErrorMessage(de.getLocalizedMessage(), model);
        }

        bean.resetInstallmentFields();

        model.addAttribute("finantialEntity", finantialEntity);
        model.addAttribute("executionYear", executionYear);
        model.addAttribute("bean", bean);
        model.addAttribute("tuitionPaymentPlanBeanJson", getBeanJson(bean));

        return jspPage("createinsertinstallments");
    }

    private static final String _CREATEPAYMENTPLAN_URI = "/createpaymentplan";
    public static final String CREATEPAYMENTPLAN_URL = CONTROLLER_URL + _CREATEPAYMENTPLAN_URI;

    @RequestMapping(value = _CREATEPAYMENTPLAN_URI + "/{finantialEntityId}/{executionYearId}", method = RequestMethod.POST)
    public String createinsertinstallments(@PathVariable("finantialEntityId") final FinantialEntity finantialEntity,
            @PathVariable("executionYearId") final ExecutionYear executionYear,
            @RequestParam(value = "bean", required = false) final TuitionPaymentPlanBean bean, final Model model,
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

    private static final String BACKTODEGREECURRICULARPLAN_TO_CHOOSE_ACTION_URI = "/backtochoosedegreecurricularplans";
    public static final String BACKTODEGREECURRICULARPLAN_TO_CHOOSE_ACTION_URL = CONTROLLER_URL
            + BACKTODEGREECURRICULARPLAN_TO_CHOOSE_ACTION_URI;

    @RequestMapping(value = BACKTODEGREECURRICULARPLAN_TO_CHOOSE_ACTION_URI + "/{finantialEntityId}/{executionYearId}")
    public String processBackToChooseDegreeCurricularPlansAction(
            @PathVariable("finantialEntityId") final FinantialEntity finantialEntity,
            @PathVariable("executionYearId") final ExecutionYear executionYear,
            @RequestParam("bean") final TuitionPaymentPlanBean bean, final Model model) {

        model.addAttribute("finantialEntity", finantialEntity);
        model.addAttribute("executionYear", executionYear);
        model.addAttribute("bean", bean);
        model.addAttribute("tuitionPaymentPlanBeanJson", getBeanJson(bean));

        return jspPage("createchoosedegreecurricularplans");
    }

    private static final String BACKTODEFINE_STUDENT_CONDITIONS_ACTION_URI = "/backtodefinestudentconditions";
    public static final String BACKTODEFINE_STUDENT_CONDITIONS_ACTION_URL = CONTROLLER_URL
            + BACKTODEFINE_STUDENT_CONDITIONS_ACTION_URI;

    @RequestMapping(value = BACKTODEFINE_STUDENT_CONDITIONS_ACTION_URI + "/{finantialEntityId}/{executionYearId}",
            method = RequestMethod.POST)
    public String processBackToDefineStudentConditionsAction(
            @PathVariable("finantialEntityId") final FinantialEntity finantialEntity,
            @PathVariable("executionYearId") final ExecutionYear executionYear,
            @RequestParam("bean") final TuitionPaymentPlanBean bean, final Model model) {

        model.addAttribute("finantialEntity", finantialEntity);
        model.addAttribute("executionYear", executionYear);
        model.addAttribute("bean", bean);
        model.addAttribute("tuitionPaymentPlanBeanJson", getBeanJson(bean));

        return jspPage("createdefinestudentconditions");
    }

    private static final String _ORDER_UP_ACTION_URI = "/paymentplanorderup";
    public static final String ORDER_UP_ACTION_URL = CONTROLLER_URL + _ORDER_UP_ACTION_URI;

    @RequestMapping(value = _ORDER_UP_ACTION_URI
            + "/{finantialEntityId}/{executionYearId}/{degreeCurricularPlanId}/{tuitionPaymentPlanId}",
            method = RequestMethod.GET)
    public String processOrderUpAction(@PathVariable("finantialEntityId") final FinantialEntity finantialEntity,
            @PathVariable("executionYearId") final ExecutionYear executionYear,
            @PathVariable("degreeCurricularPlanId") final DegreeCurricularPlan degreeCurricularPlan,
            @PathVariable("tuitionPaymentPlanId") TuitionPaymentPlan tuitionPaymentPlan, final Model model,
            final RedirectAttributes redirectAttributes) {
        try {

//            tuitionPaymentPlan.orderUp();

            addInfoMessage(academicTreasuryBundle("label.TuitionPaymentPlan.order.up.success"), model);
        } catch (DomainException ex) {
            addErrorMessage(ex.getLocalizedMessage(), model);
        }

        return redirect(String.format(SEARCH_URL + "/%s/%s/%s", finantialEntity.getExternalId(), executionYear.getExternalId(),
                degreeCurricularPlan.getExternalId()), model, redirectAttributes);
    }

    private static final String _ORDER_DOWN_ACTION_URI = "/paymentplanorderdown";
    public static final String ORDER_DOWN_ACTION_URL = CONTROLLER_URL + _ORDER_DOWN_ACTION_URI;

    @RequestMapping(value = _ORDER_DOWN_ACTION_URI
            + "/{finantialEntityId}/{executionYearId}/{degreeCurricularPlanId}/{tuitionPaymentPlanId}",
            method = RequestMethod.GET)
    public String processOrderDownAction(@PathVariable("finantialEntityId") final FinantialEntity finantialEntity,
            @PathVariable("executionYearId") final ExecutionYear executionYear,
            @PathVariable("degreeCurricularPlanId") final DegreeCurricularPlan degreeCurricularPlan,
            @PathVariable("tuitionPaymentPlanId") TuitionPaymentPlan tuitionPaymentPlan, final Model model,
            final RedirectAttributes redirectAttributes) {
        try {

//            tuitionPaymentPlan.orderDown();

            addInfoMessage(academicTreasuryBundle("label.TuitionPaymentPlan.order.down.success"), model);
        } catch (DomainException ex) {
            addErrorMessage(ex.getLocalizedMessage(), model);
        }

        return redirect(String.format(SEARCH_URL + "/%s/%s/%s", finantialEntity.getExternalId(), executionYear.getExternalId(),
                degreeCurricularPlan.getExternalId()), model, redirectAttributes);
    }

    /* *****************
     * COPY PAYMENT PLAN
     * *****************
     */

    private static final String _COPY_PAYMENT_PLAN_CHOOSE_EXECUTION_YEAR_DEGREE_CURRICULAR_PLANS_URI =
            "/copypaymentplanchooseexecutionyeardegreecurricularplans";
    public static final String COPY_PAYMENT_PLAN_CHOOSE_EXECUTION_YEAR_DEGREE_CURRICULAR_PLANS_URL = CONTROLLER_URL
            + _COPY_PAYMENT_PLAN_CHOOSE_EXECUTION_YEAR_DEGREE_CURRICULAR_PLANS_URI;

    @RequestMapping(value = _COPY_PAYMENT_PLAN_CHOOSE_EXECUTION_YEAR_DEGREE_CURRICULAR_PLANS_URI
            + "/{finantialEntityId}/{executionYearId}/{degreeCurricularPlanId}/{tuitionPaymentPlanId}",
            method = RequestMethod.GET)
    public String copyPaymentPlanChooseExecutionYearDegreeCurricularPlans(
            @PathVariable("finantialEntityId") final FinantialEntity finantialEntity,
            @PathVariable("executionYearId") final ExecutionYear executionYear,
            @PathVariable("degreeCurricularPlanId") final DegreeCurricularPlan degreeCurricularPlan,
            @PathVariable("tuitionPaymentPlanId") TuitionPaymentPlan tuitionPaymentPlan, final Model model) {
        final TuitionPaymentPlanBean tuitionPaymentPlanBean = new TuitionPaymentPlanBean(tuitionPaymentPlan);

        return _copyPaymentPlanChooseExecutionYearDegreeCurricularPlans(finantialEntity, executionYear, degreeCurricularPlan,
                model, tuitionPaymentPlanBean);
    }

    private String _copyPaymentPlanChooseExecutionYearDegreeCurricularPlans(
            @PathVariable("finantialEntityId") final FinantialEntity finantialEntity,
            @PathVariable("executionYearId") final ExecutionYear executionYear,
            @PathVariable("degreeCurricularPlanId") final DegreeCurricularPlan degreeCurricularPlan, final Model model,
            final TuitionPaymentPlanBean tuitionPaymentPlanBean) {
        model.addAttribute("tuitionPaymentPlanBean", tuitionPaymentPlanBean);
        model.addAttribute("tuitionPaymentPlanBeanJson", getBeanJson(tuitionPaymentPlanBean));

        model.addAttribute("finantialEntity", finantialEntity);
        model.addAttribute("executionYear", executionYear);
        model.addAttribute("degreeCurricularPlan", degreeCurricularPlan);

        return jspPage("copypaymentplanchooseexecutionyeardegreecurricularplans");
    }

    private static final String _COPY_PAYMENT_PLAN_CHOOSE_EXECUTION_YEAR_DEGREE_CURRICULAR_PLANS_POSTBACK_URI =
            "/copypaymentplanchooseexecutionyeardegreecurricularplanspostback";
    public static final String COPY_PAYMENT_PLAN_CHOOSE_EXECUTION_YEAR_DEGREE_CURRICULAR_PLANS_POSTBACK__URL = CONTROLLER_URL
            + _COPY_PAYMENT_PLAN_CHOOSE_EXECUTION_YEAR_DEGREE_CURRICULAR_PLANS_POSTBACK_URI;

    @RequestMapping(value = _COPY_PAYMENT_PLAN_CHOOSE_EXECUTION_YEAR_DEGREE_CURRICULAR_PLANS_POSTBACK_URI
            + "/{finantialEntityId}/{executionYearId}/{degreeCurricularPlanId}", method = RequestMethod.POST,
            produces = "application/json;charset=UTF-8")
    public @ResponseBody ResponseEntity<String> copyPaymentPlanChooseExecutionYearDegreeCurricularPlansPostback(
            @PathVariable("finantialEntityId") final FinantialEntity finantialEntity,
            @PathVariable("executionYearId") final ExecutionYear executionYear,
            @PathVariable("degreeCurricularPlanId") final DegreeCurricularPlan degreeCurricularPlan,
            @RequestParam("bean") final TuitionPaymentPlanBean bean, final Model model) {

        bean.updateData();

        return new ResponseEntity<String>(getBeanJson(bean), HttpStatus.OK);
    }

    private static final String _COPY_PAYMENT_PLAN_CONFIRM_URI = "/copypaymentplanconfirm";
    public static final String COPY_PAYMENT_PLAN_CONFIRM_URL = CONTROLLER_URL + _COPY_PAYMENT_PLAN_CONFIRM_URI;

    @RequestMapping(value = _COPY_PAYMENT_PLAN_CONFIRM_URI + "/{finantialEntityId}/{executionYearId}/{degreeCurricularPlanId}",
            method = RequestMethod.POST)
    public String copyPaymentPlanConfirm(@PathVariable("finantialEntityId") final FinantialEntity finantialEntity,
            @PathVariable("executionYearId") final ExecutionYear executionYear,
            @PathVariable("degreeCurricularPlanId") final DegreeCurricularPlan degreeCurricularPlan,
            @RequestParam("bean") final TuitionPaymentPlanBean bean, final Model model,
            final RedirectAttributes redirectAttributes) {

        bean.updateDatesBasedOnSelectedExecutionYear();

        model.addAttribute("tuitionPaymentPlanBean", bean);
        model.addAttribute("tuitionPaymentPlanBeanJson", getBeanJson(bean));

        model.addAttribute("finantialEntity", finantialEntity);
        model.addAttribute("executionYear", executionYear);
        model.addAttribute("degreeCurricularPlan", degreeCurricularPlan);

        return jspPage("copypaymentplanconfirm");
    }

    private static final String _COPY_PAYMENT_PLAN_URI = "/copypaymentplan";
    public static final String COPY_PAYMENT_PLAN_URL = CONTROLLER_URL + _COPY_PAYMENT_PLAN_URI;

    @RequestMapping(value = _COPY_PAYMENT_PLAN_URI + "/{finantialEntityId}/{executionYearId}/{degreeCurricularPlanId}",
            method = RequestMethod.POST)
    public String copyPaymentPlan(@PathVariable("finantialEntityId") final FinantialEntity finantialEntity,
            @PathVariable("executionYearId") final ExecutionYear executionYear,
            @PathVariable("degreeCurricularPlanId") final DegreeCurricularPlan degreeCurricularPlan,
            @RequestParam("bean") final TuitionPaymentPlanBean bean, final Model model,
            final RedirectAttributes redirectAttributes) {

        try {
            // Copy tuition payment plan
            TuitionPaymentPlan.create(bean);

            //Success Validation
            //Add the bean to be used in the View
            addInfoMessage(academicTreasuryBundle("label.TuitionPaymentPlan.copy.success"), model);

            return redirect(
                    String.format("%s/%s/%s", DegreeCurricularPlanController.CHOOSEDEGREECURRICULARPLAN_URL,
                            finantialEntity.getExternalId(), bean.getExecutionYear().getExternalId()), model, redirectAttributes);
        } catch (DomainException ex) {
            addErrorMessage(ex.getLocalizedMessage(), model);
            return copyPaymentPlanConfirm(finantialEntity, executionYear, degreeCurricularPlan, bean, model, redirectAttributes);
        }
    }

    private static final String _CANCEL_COPY_PAYMENT_PLAN_URI = "/cancelcopypaymentplan";
    public static final String CANCEL_COPY_PAYMENT_PLAN_URL = CONTROLLER_URL + _CANCEL_COPY_PAYMENT_PLAN_URI;

    @RequestMapping(value = _CANCEL_COPY_PAYMENT_PLAN_URI + "/{finantialEntityId}/{executionYearId}/{degreeCurricularPlanId}",
            method = RequestMethod.POST)
    public String cancelCopyPaymentPlan(@PathVariable("finantialEntityId") final FinantialEntity finantialEntity,
            @PathVariable("executionYearId") final ExecutionYear executionYear,
            @PathVariable("degreeCurricularPlanId") final DegreeCurricularPlan degreeCurricularPlan,
            @RequestParam("bean") final TuitionPaymentPlanBean bean, final Model model,
            final RedirectAttributes redirectAttributes) {

        return redirect(String.format("%s/%s/%s/%s", SEARCH_URL, finantialEntity.getExternalId(), executionYear.getExternalId(),
                degreeCurricularPlan.getExternalId()), model, redirectAttributes);

    }

    private static final String _BACK_COPY_PAYMENT_PLAN_CHOOSE_EXECUTION_YEAR_URI = "/backcopypaymentplanchooseexecutionyear";
    public static final String BACK_COPY_PAYMENT_PLAN_CHOOSE_EXECUTION_YEAR_URL = CONTROLLER_URL
            + _BACK_COPY_PAYMENT_PLAN_CHOOSE_EXECUTION_YEAR_URI;

    @RequestMapping(value = _BACK_COPY_PAYMENT_PLAN_CHOOSE_EXECUTION_YEAR_URI
            + "/{finantialEntityId}/{executionYearId}/{degreeCurricularPlanId}", method = RequestMethod.POST)
    public String backCopyPaymentPlanChooseExecutionYear(
            @PathVariable("finantialEntityId") final FinantialEntity finantialEntity,
            @PathVariable("executionYearId") final ExecutionYear executionYear,
            @PathVariable("degreeCurricularPlanId") final DegreeCurricularPlan degreeCurricularPlan,
            @RequestParam("bean") final TuitionPaymentPlanBean bean, final Model model) {

        return _copyPaymentPlanChooseExecutionYearDegreeCurricularPlans(finantialEntity, executionYear, degreeCurricularPlan,
                model, bean);
    }

    /* ************************
     * EDIT TUITION INSTALLMENT
     * ************************
     */

    private static final String _EDIT_TUITION_INSTALLMENT_URI = "/edittuitioninstallment";
    public static final String EDIT_TUITION_INSTALLMENT_URL = CONTROLLER_URL + _EDIT_TUITION_INSTALLMENT_URI;

    @RequestMapping(value = _EDIT_TUITION_INSTALLMENT_URI + "/{tuitionInstallmentTariffId}", method = RequestMethod.GET)
    public String editTuitionInstallment(@PathVariable("tuitionInstallmentTariffId") final TuitionInstallmentTariff tuitionInstallmentTariff, 
            final Model model) {

        return _editTuitionInstallment(tuitionInstallmentTariff, model, new AcademicTariffBean(tuitionInstallmentTariff));
    }

    private String _editTuitionInstallment(final TuitionInstallmentTariff tuitionInstallmentTariff, final Model model,
            final AcademicTariffBean academicTariffBean) {

        model.addAttribute("tuitionInstallmentTariff", tuitionInstallmentTariff);
        model.addAttribute("bean", academicTariffBean);
        model.addAttribute("academicTariffBeanJson", getBeanJson(academicTariffBean));

        final FinantialEntity finantialEntity = tuitionInstallmentTariff.getFinantialEntity();
        final ExecutionInterval executionYear = tuitionInstallmentTariff.getTuitionPaymentPlan().getExecutionYear();
        final DegreeCurricularPlan degreeCurricularPlan =
                tuitionInstallmentTariff.getTuitionPaymentPlan().getDegreeCurricularPlan();
        
        model.addAttribute("finantialEntity", finantialEntity);
        model.addAttribute("executionYear", executionYear);
        model.addAttribute("degreeCurricularPlan", degreeCurricularPlan);
        
        return jspPage("edittuitioninstallment");
    }

    @RequestMapping(value = _EDIT_TUITION_INSTALLMENT_URI + "/{tuitionInstallmentTariffId}", method = RequestMethod.POST)
    public String editTuitionInstallment(@PathVariable("tuitionInstallmentTariffId") final TuitionInstallmentTariff tuitionInstallmentTariff, 
            final Model model, @RequestParam("bean") final AcademicTariffBean bean, 
            final RedirectAttributes redirectAttributes) {

        try {
            tuitionInstallmentTariff.edit(bean);
            
            //Success Validation
            //Add the bean to be used in the View
            addInfoMessage(academicTreasuryBundle("label.TuitionPaymentPlan.edit.success"), model);

            final FinantialEntity finantialEntity = tuitionInstallmentTariff.getFinantialEntity();
            final ExecutionInterval executionYear = tuitionInstallmentTariff.getTuitionPaymentPlan().getExecutionYear();
            final DegreeCurricularPlan degreeCurricularPlan =
                    tuitionInstallmentTariff.getTuitionPaymentPlan().getDegreeCurricularPlan();

            return redirect(String.format("%s/%s/%s/%s", TuitionPaymentPlanController.SEARCH_URL, finantialEntity.getExternalId(),
                    executionYear.getExternalId(), degreeCurricularPlan.getExternalId()), model, redirectAttributes);
            
        } catch (final DomainException ex) {
            addErrorMessage(ex.getLocalizedMessage(), model);
            return _editTuitionInstallment(tuitionInstallmentTariff, model, bean);
        }
    }

    private String jspPage(final String page) {
        return JSP_PATH + "/" + page;
    }

}
