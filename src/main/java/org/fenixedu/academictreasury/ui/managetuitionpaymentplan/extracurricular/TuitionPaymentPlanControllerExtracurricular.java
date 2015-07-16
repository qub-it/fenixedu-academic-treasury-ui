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
package org.fenixedu.academictreasury.ui.managetuitionpaymentplan.extracurricular;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.DegreeCurricularPlan;
import org.fenixedu.academic.domain.ExecutionDegree;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academictreasury.domain.tuition.TuitionPaymentPlan;
import org.fenixedu.academictreasury.domain.tuition.TuitionPaymentPlanGroup;
import org.fenixedu.academictreasury.dto.tariff.TuitionPaymentPlanBean;
import org.fenixedu.academictreasury.ui.AcademicTreasuryBaseController;
import org.fenixedu.academictreasury.ui.AcademicTreasuryController;
import org.fenixedu.academictreasury.util.Constants;
import org.fenixedu.bennu.core.domain.exceptions.DomainException;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
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
import com.google.common.collect.Lists;

import edu.emory.mathcs.backport.java.util.Collections;

@SpringFunctionality(app = AcademicTreasuryController.class, title = "label.title.manageTuitionPaymentPlanExtracurricular",
        accessGroup = "treasuryBackOffice")
@RequestMapping(TuitionPaymentPlanControllerExtracurricular.CONTROLLER_URL)
public class TuitionPaymentPlanControllerExtracurricular extends AcademicTreasuryBaseController {

    public static final String CONTROLLER_URL = "/academictreasury/tuitionpaymentplanextracurricular";
    private static final String JSP_PATH = "academicTreasury/tuitionpaymentplanextracurricular";

    @RequestMapping
    public String home(Model model) {
        return "forward:" + CHOOSEFINANTIALENTITY_URL;
    }

    private static final String _CHOOSEFINANTIALENTITY_URI = "/choosefinantialentity";
    public static final String CHOOSEFINANTIALENTITY_URL = CONTROLLER_URL + _CHOOSEFINANTIALENTITY_URI;

    @RequestMapping(value = _CHOOSEFINANTIALENTITY_URI)
    public String chooseFinantialEntity(final Model model) {

        model.addAttribute("choosefinantialentityResultsDataSet",
                FinantialEntity.findWithBackOfficeAccessFor(Authenticate.getUser()).sorted(FinantialEntity.COMPARE_BY_NAME)
                        .collect(Collectors.toList()));

        model.addAttribute("executionYear", ExecutionYear.readCurrentExecutionYear());

        return jspPage("choosefinantialentity");
    }

    private static final String _CHOOSEDEGREECURRICULARPLAN_URI = "/choosedegreecurricularplan";
    public static final String CHOOSEDEGREECURRICULARPLAN_URL = CONTROLLER_URL + _CHOOSEDEGREECURRICULARPLAN_URI;

    @RequestMapping(value = _CHOOSEDEGREECURRICULARPLAN_URI + "/{finantialEntityId}/{executionYearId}")
    public String chooseDegreeCurricularPlan(@PathVariable("finantialEntityId") FinantialEntity finantialEntity,
            @PathVariable("executionYearId") final ExecutionYear executionYear, final Model model) {

        List<DegreeCurricularPlan> degreeCurricularPlanList =
                Lists.newArrayList(ExecutionDegree.getAllByExecutionYear(executionYear).stream()
                        .map(e -> e.getDegreeCurricularPlan()).collect(Collectors.toList()));

        Collections.sort(degreeCurricularPlanList,
                DegreeCurricularPlan.DEGREE_CURRICULAR_PLAN_COMPARATOR_BY_DEGREE_TYPE_AND_EXECUTION_DEGREE_AND_DEGREE_CODE);

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
                        TuitionPaymentPlanGroup.findUniqueDefaultGroupForExtracurricular().get(), degreeCurricularPlan,
                        executionYear).collect(Collectors.toList()));

        return jspPage("search");
    }

    private static final String _SEARCH_TO_DELETE_ACTION_URI = "/search/delete";
    public static final String SEARCH_TO_DELETE_ACTION_URL = CONTROLLER_URL + _SEARCH_TO_DELETE_ACTION_URI;

    @RequestMapping(value = _SEARCH_TO_DELETE_ACTION_URI
            + "/{finantialEntityId}/{executionYearId}/{degreeCurricularPlanId}/{tuitionPaymentPlanId}",
            method = RequestMethod.GET)
    public String processSearchToDeleteAction(@PathVariable("finantialEntityId") final FinantialEntity finantialEntity,
            @PathVariable("executionYearId") final ExecutionYear executionYear,
            @PathVariable("degreeCurricularPlanId") final DegreeCurricularPlan degreeCurricularPlan,
            @PathVariable("tuitionPaymentPlanId") TuitionPaymentPlan tuitionPaymentPlan, final Model model,
            final RedirectAttributes redirectAttributes) {
        try {

            tuitionPaymentPlan.delete();

            addInfoMessage(BundleUtil.getString(Constants.BUNDLE, "label.TuitionPaymentPlan.deletion.success"), model);
        } catch (DomainException ex) {
            addErrorMessage(ex.getLocalizedMessage(), model);
        }

        return redirect(String.format(SEARCH_URL + "/%s/%s/%s", finantialEntity.getExternalId(), executionYear.getExternalId(),
                degreeCurricularPlan.getExternalId()), model, redirectAttributes);
    }

    private static final String _CREATECHOOSEDEGREECURRICULARPLANS_URI = "/createchoosedegreecurricularplans";
    public static final String CREATECHOOSEDEGREECURRICULARPLANS_URL = CONTROLLER_URL + _CREATECHOOSEDEGREECURRICULARPLANS_URI;

    @RequestMapping(value = _CREATECHOOSEDEGREECURRICULARPLANS_URI + "/{finantialEntityId}/{executionYearId}",
            method = RequestMethod.GET)
    public String createchoosedegreecurricularplans(@PathVariable("finantialEntityId") final FinantialEntity finantialEntity,
            @PathVariable("executionYearId") final ExecutionYear executionYear, final Model model) {

        if (!TuitionPaymentPlanGroup.findUniqueDefaultGroupForExtracurricular().isPresent()) {
            addInfoMessage(BundleUtil.getString(Constants.BUNDLE,
                    "label.TuitionPaymentPlanGroup.defaultGroupForExtracurricular.required"), model);
            return chooseDegreeCurricularPlan(finantialEntity, executionYear, model);
        }

        final TuitionPaymentPlanBean bean =
                new TuitionPaymentPlanBean(null, TuitionPaymentPlanGroup.findUniqueDefaultGroupForExtracurricular().get(),
                        finantialEntity, executionYear);

        bean.setTuitionInstallmentProduct(TuitionPaymentPlanGroup.findUniqueDefaultGroupForExtracurricular().get()
                .getCurrentProduct());

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
            addErrorMessage(BundleUtil.getString(Constants.BUNDLE, "error.TuitionPaymentPlan.choose.degree.curricular.plans"),
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

    private static final String _CREATEPAYMENTPLAN_URI = "/createpaymentplan";
    public static final String CREATEPAYMENTPLAN_URL = CONTROLLER_URL + _CREATEPAYMENTPLAN_URI;

    @RequestMapping(value = _CREATEPAYMENTPLAN_URI + "/{finantialEntityId}/{executionYearId}", method = RequestMethod.POST)
    public String createinsertinstallments(@PathVariable("finantialEntityId") final FinantialEntity finantialEntity,
            @PathVariable("executionYearId") final ExecutionYear executionYear,
            @RequestParam(value = "bean", required = false) final TuitionPaymentPlanBean bean, final Model model,
            final RedirectAttributes redirectAttributes) {

        try {
            if (bean.isCustomized() && Strings.isNullOrEmpty(bean.getName())) {
                addErrorMessage(
                        BundleUtil.getString(Constants.BUNDLE, "error.TuitionPaymentPlan.custom.payment.plan.name.required"),
                        model);
                return createdefinestudentconditions(finantialEntity, executionYear, bean, model);
            }

            final List<String> errorMessages = bean.addInstallment();

            if (!errorMessages.isEmpty()) {
                for (final String error : errorMessages) {
                    addErrorMessage(BundleUtil.getString(Constants.BUNDLE, error), model);
                }

                return createdefinestudentconditions(finantialEntity, executionYear, bean, model);
            }

            TuitionPaymentPlan.create(bean);

            return redirect(
                    String.format("%s/%s/%s", CHOOSEDEGREECURRICULARPLAN_URL, finantialEntity.getExternalId(),
                            executionYear.getExternalId()), model, redirectAttributes);
        } catch (final DomainException de) {
            addErrorMessage(de.getLocalizedMessage(), model);
            bean.getTuitionCalculationTypeDataSource().clear();

            return createdefinestudentconditions(finantialEntity, executionYear, bean, model);
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

            tuitionPaymentPlan.orderUp();

            addInfoMessage(BundleUtil.getString(Constants.BUNDLE, "label.TuitionPaymentPlan.order.up.success"), model);
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

            tuitionPaymentPlan.orderDown();

            addInfoMessage(BundleUtil.getString(Constants.BUNDLE, "label.TuitionPaymentPlan.order.down.success"), model);
        } catch (DomainException ex) {
            addErrorMessage(ex.getLocalizedMessage(), model);
        }

        return redirect(String.format(SEARCH_URL + "/%s/%s/%s", finantialEntity.getExternalId(), executionYear.getExternalId(),
                degreeCurricularPlan.getExternalId()), model, redirectAttributes);
    }

    private String jspPage(final String page) {
        return JSP_PATH + "/" + page;
    }

}
