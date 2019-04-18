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
package org.fenixedu.academictreasury.ui.managetuitionpaymentplangroup;

import static org.fenixedu.academictreasury.util.AcademicTreasuryConstants.academicTreasuryBundle;

import java.util.stream.Collectors;

import org.fenixedu.academictreasury.domain.exceptions.AcademicTreasuryDomainException;
import org.fenixedu.academictreasury.domain.settings.AcademicTreasurySettings;
import org.fenixedu.academictreasury.domain.tuition.TuitionPaymentPlanGroup;
import org.fenixedu.academictreasury.ui.AcademicTreasuryBaseController;
import org.fenixedu.academictreasury.ui.AcademicTreasuryController;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.treasury.domain.Product;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

//@Component("org.fenixedu.academicTreasury.ui.manageTuitionPaymentPlanGroup")
@SpringFunctionality(app = AcademicTreasuryController.class, title = "label.title.manageTuitionPaymentPlanGroup",
        accessGroup = "treasuryManagers")
@RequestMapping(value = TuitionPaymentPlanGroupController.CONTROLLER)
public class TuitionPaymentPlanGroupController extends AcademicTreasuryBaseController {

    public static final String CONTROLLER = "/academictreasury/managetuitionpaymentplangroup/tuitionpaymentplangroup";
    public static final String JSP_PATH = "academicTreasury/managetuitionpaymentplangroup/tuitionpaymentplangroup";

    @RequestMapping
    public String home(Model model) {
        return String.format("forward:%s/", CONTROLLER);
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String search(Model model) {

        model.addAttribute("searchtuitionpaymentplangroupResultsDataSet",
                TuitionPaymentPlanGroup.findAll().collect(Collectors.<TuitionPaymentPlanGroup> toSet()));
        return jspPage("search");
    }

    @RequestMapping(value = "/search/view/{tuitionPaymentPlanGroupId}")
    public String processSearchToViewAction(
            @PathVariable("tuitionPaymentPlanGroupId") TuitionPaymentPlanGroup tuitionPaymentPlanGroup, Model model,
            RedirectAttributes redirectAttributes) {

        return redirect(route("/read", tuitionPaymentPlanGroup.getExternalId()), model, redirectAttributes);
    }

    @RequestMapping(value = "/search/delete/{tuitionPaymentPlanGroupid}", method = RequestMethod.POST)
    public String processSearchToDeleteAction(
            @PathVariable("tuitionPaymentPlanGroupid") TuitionPaymentPlanGroup tuitionPaymentPlanGroup, Model model,
            RedirectAttributes redirectAttributes) {
        setTuitionPaymentPlanGroup(tuitionPaymentPlanGroup, model);

        try {
            tuitionPaymentPlanGroup.delete();

            addInfoMessage(academicTreasuryBundle("label.TuitionPaymentPlanGroup.delete.sucess"), model);

            return redirect(route("/"), model, redirectAttributes);
        } catch (AcademicTreasuryDomainException tde) {
            addErrorMessage(academicTreasuryBundle("label.error.delete") + tde.getLocalizedMessage(), model);
        } catch (Exception ex) {
            addErrorMessage(academicTreasuryBundle("label.error.delete") + ex.getLocalizedMessage(), model);
        }

        return jspPage("search");
    }

    @RequestMapping(value = "/read/{tuitionPaymentPlanGroupId}")
    public String read(@PathVariable("tuitionPaymentPlanGroupId") TuitionPaymentPlanGroup tuitionPaymentPlanGroup, Model model) {
        setTuitionPaymentPlanGroup(tuitionPaymentPlanGroup, model);
        return jspPage("read");
    }

    @RequestMapping(value = "/create", method = RequestMethod.GET)
    public String create(Model model) {
        model.addAttribute("products", AcademicTreasurySettings.getInstance().getTuitionProductGroup().getProductsSet());

        return jspPage("create");
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public String create(@RequestParam(value = "code", required = false) final String code, @RequestParam(value = "name",
            required = false) final LocalizedString name,
            @RequestParam(value = "forRegistration", required = false) boolean forRegistration, @RequestParam(
                    value = "forStandalone", required = false) boolean forStandalone, @RequestParam(value = "forExtracurricular",
                    required = false) boolean forExtracurricular,
            @RequestParam(value = "currentProduct", required = false) final Product currentProduct, final Model model,
            final RedirectAttributes redirectAttributes) {

        try {
            final TuitionPaymentPlanGroup group =
                    TuitionPaymentPlanGroup
                            .create(code, name, forRegistration, forStandalone, forExtracurricular, currentProduct);

            addInfoMessage(academicTreasuryBundle("label.TuitionPaymentPlanGroup.creation.success"), model);
            return redirect(route("/read", group.getExternalId()), model, redirectAttributes);
        } catch (AcademicTreasuryDomainException tde) {
            addErrorMessage(academicTreasuryBundle("label.error.create") + tde.getLocalizedMessage(), model);
        } catch (Exception ex) {
            addErrorMessage(academicTreasuryBundle("label.error.create") + ex.getLocalizedMessage(), model);
        }
        return create(model);
    }

    @RequestMapping(value = "/update/{tuitionPaymentPlanGroupId}", method = RequestMethod.GET)
    public String update(@PathVariable("tuitionPaymentPlanGroupId") TuitionPaymentPlanGroup tuitionPaymentPlanGroup, Model model) {
        setTuitionPaymentPlanGroup(tuitionPaymentPlanGroup, model);

        model.addAttribute("products", AcademicTreasurySettings.getInstance().getTuitionProductGroup().getProductsSet());

        return jspPage("/update");
    }

    @RequestMapping(value = "/update/{tuitionPaymentPlanGroupId}", method = RequestMethod.POST)
    public String update(@PathVariable("tuitionPaymentPlanGroupId") TuitionPaymentPlanGroup tuitionPaymentPlanGroup,
            @RequestParam(value = "code", required = false) String code,
            @RequestParam(value = "name", required = false) LocalizedString name, @RequestParam(value = "forRegistration",
                    required = false) boolean forRegistration,
            @RequestParam(value = "forStandalone", required = false) boolean forStandalone, @RequestParam(
                    value = "forExtracurricular", required = false) boolean forExtracurricular, @RequestParam(
                    value = "currentProduct", required = false) final Product currentProduct,

            final Model model, final RedirectAttributes redirectAttributes) {

        setTuitionPaymentPlanGroup(tuitionPaymentPlanGroup, model);

        try {
            tuitionPaymentPlanGroup.edit(code, name, forRegistration, forStandalone, forExtracurricular, currentProduct);

            return redirect(route("/read", tuitionPaymentPlanGroup.getExternalId()), model, redirectAttributes);
        } catch (AcademicTreasuryDomainException tde) {
            addErrorMessage(academicTreasuryBundle("label.error.update") + tde.getLocalizedMessage(), model);
        } catch (Exception ex) {
            addErrorMessage(academicTreasuryBundle("label.error.update") + ex.getLocalizedMessage(), model);
        }
        return update(tuitionPaymentPlanGroup, model);
    }

    private String jspPage(final String page) {
        return JSP_PATH + "/" + page;
    }

    private String route(final String action, final String... ids) {
        StringBuilder sb = new StringBuilder(CONTROLLER);
        sb.append(action).append("/");

        for (final String id : ids) {
            sb.append(id).append("/");
        }

        sb.delete(sb.length() - 1, sb.length());

        return sb.toString();
    }

    private void setTuitionPaymentPlanGroup(TuitionPaymentPlanGroup tuitionPaymentPlanGroup, Model model) {
        model.addAttribute("tuitionPaymentPlanGroup", tuitionPaymentPlanGroup);
    }

}
