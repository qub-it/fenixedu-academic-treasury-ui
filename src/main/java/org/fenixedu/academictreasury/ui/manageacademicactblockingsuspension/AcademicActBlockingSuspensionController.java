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
package org.fenixedu.academictreasury.ui.manageacademicactblockingsuspension;

import static org.fenixedu.academictreasury.util.AcademicTreasuryConstants.academicTreasuryBundle;

import java.util.stream.Collectors;

import org.fenixedu.academic.domain.Person;
import org.fenixedu.academictreasury.domain.academicalAct.AcademicActBlockingSuspension;
import org.fenixedu.academictreasury.domain.customer.PersonCustomer;
import org.fenixedu.academictreasury.ui.AcademicTreasuryBaseController;
import org.fenixedu.academictreasury.util.AcademicTreasuryConstants;
import org.fenixedu.bennu.core.domain.exceptions.DomainException;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.fenixedu.treasury.domain.debt.DebtAccount;
import org.fenixedu.treasury.ui.accounting.managecustomer.CustomerController;
import org.joda.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@BennuSpringController(value = CustomerController.class)
@RequestMapping(AcademicActBlockingSuspensionController.CONTROLLER_URL)
public class AcademicActBlockingSuspensionController extends AcademicTreasuryBaseController {

    public static final String CONTROLLER_URL =
            "/academictreasury/manageacademicactblockingsuspension/academicactblockingsuspension";

    private static final String JSP_PATH = "academicTreasury/manageacademicactblockingsuspension/academicactblockingsuspension";

    @RequestMapping
    public String home(Model model) {
        return "forward:" + CONTROLLER_URL + "/";
    }

    private void setAcademicActBlockingSuspension(AcademicActBlockingSuspension academicActBlockingSuspension, Model model) {
        model.addAttribute("academicActBlockingSuspension", academicActBlockingSuspension);
    }

    private static final String _SEARCH_URI = "/search/";
    public static final String SEARCH_URL = CONTROLLER_URL + _SEARCH_URI;

    @RequestMapping(value = _SEARCH_URI + "{debtAccountId}")
    public String search(@PathVariable("debtAccountId") final DebtAccount debtAccount, final Model model) {

        final PersonCustomer customer = (PersonCustomer) debtAccount.getCustomer();
        final Person person = customer.isActive() ? customer.getPerson() : customer.getPersonForInactivePersonCustomer();
        model.addAttribute(
                "searchacademicactblockingsuspensionResultsDataSet",
                AcademicActBlockingSuspension.find(person).sorted(AcademicActBlockingSuspension.COMPARE_BY_BEGIN_DATE)
                        .collect(Collectors.toList()));

        model.addAttribute("debtAccount", debtAccount);

        return jspPage("search");
    }

    private static final String _SEARCH_TO_VIEW_ACTION_URI = "/search/view/";
    public static final String SEARCH_TO_VIEW_ACTION_URL = CONTROLLER_URL + _SEARCH_TO_VIEW_ACTION_URI;

    @RequestMapping(value = _SEARCH_TO_VIEW_ACTION_URI + "{debtAccountId}/{oid}")
    public String processSearchToViewAction(@PathVariable("debtAccountId") final DebtAccount debtAccount,
            @PathVariable("oid") AcademicActBlockingSuspension academicActBlockingSuspension, Model model,
            RedirectAttributes redirectAttributes) {

        // CHANGE_ME Insert code here for processing viewAction
        // If you selected multiple exists you must choose which one to use below	 
        return redirect(READ_URL + debtAccount.getExternalId() + "/" + academicActBlockingSuspension.getExternalId(), model, redirectAttributes);
    }

    private static final String _SEARCH_TO_DELETE_ACTION_URI = "/search/delete/";
    public static final String SEARCH_TO_DELETE_ACTION_URL = CONTROLLER_URL + _SEARCH_TO_DELETE_ACTION_URI;

    @RequestMapping(value = _SEARCH_TO_DELETE_ACTION_URI + "{debtAccountId}/{oid}", method = RequestMethod.POST)
    public String processSearchToDeleteAction(@PathVariable("debtAccountId") final DebtAccount debtAccount,
            @PathVariable("oid") final AcademicActBlockingSuspension academicActBlockingSuspension, Model model,
            RedirectAttributes redirectAttributes) {

        try {

            academicActBlockingSuspension.delete();

            return redirect(SEARCH_URL + debtAccount.getExternalId(), model, redirectAttributes);
        } catch (final DomainException ex) {
            addErrorMessage(ex.getLocalizedMessage(), model);
        }

        return search(debtAccount, model);
    }

    private static final String _CREATE_URI = "/create";
    public static final String CREATE_URL = CONTROLLER_URL + _CREATE_URI;

    @RequestMapping(value = _CREATE_URI + "/{debtAccountId}", method = RequestMethod.GET)
    public String create(@PathVariable("debtAccountId") final DebtAccount debtAccount, final Model model) {
        
        model.addAttribute("debtAccount", debtAccount);
        
        return jspPage("create");
    }

    @RequestMapping(value = _CREATE_URI + "/{debtAccountId}", method = RequestMethod.POST)
    public String create(
            @PathVariable("debtAccountId") final DebtAccount debtAccount,
            @RequestParam(value = "begindate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate beginDate,
            @RequestParam(value = "enddate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam(value = "reason", required = false) java.lang.String reason, Model model,
            RedirectAttributes redirectAttributes) {

        try {

            AcademicActBlockingSuspension.create(((PersonCustomer) debtAccount.getCustomer()).getPerson(), beginDate, endDate, reason);

            addInfoMessage(academicTreasuryBundle("label.AcademicActBlockingSuspension.created.success"), model);
            return redirect(SEARCH_URL + debtAccount.getExternalId(), model, redirectAttributes);
        } catch (DomainException de) {
            addErrorMessage(de.getLocalizedMessage(), model);

            return create(debtAccount, model);
        }
    }

    private static final String _READ_URI = "/read/";
    public static final String READ_URL = CONTROLLER_URL + _READ_URI;

    @RequestMapping(value = _READ_URI + "{debtAccountId}/{oid}")
    public String read(
            @PathVariable("debtAccountId") final DebtAccount debtAccount,
            @PathVariable("oid") final AcademicActBlockingSuspension academicActBlockingSuspension, final Model model) {
        setAcademicActBlockingSuspension(academicActBlockingSuspension, model);

        model.addAttribute("debtAccount", debtAccount);

        return jspPage("read");
    }

    private static final String _UPDATE_URI = "/update/";
    public static final String UPDATE_URL = CONTROLLER_URL + _UPDATE_URI;

    @RequestMapping(value = _UPDATE_URI + "{debtAccountId}/{oid}", method = RequestMethod.GET)
    public String update(
            @PathVariable("debtAccountId") final DebtAccount debtAccount,
            @PathVariable("oid") final AcademicActBlockingSuspension academicActBlockingSuspension, final Model model) {
        setAcademicActBlockingSuspension(academicActBlockingSuspension, model);

        model.addAttribute("debtAccount", debtAccount);
        
        return jspPage("update");
    }

    @RequestMapping(value = _UPDATE_URI + "{debtAccountId}/{oid}", method = RequestMethod.POST)
    public String update(
            @PathVariable("debtAccountId") final DebtAccount debtAccount,
            @PathVariable("oid") final AcademicActBlockingSuspension academicActBlockingSuspension,
            @RequestParam(value = "begindate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate beginDate,
            @RequestParam(value = "enddate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam(value = "reason", required = false) java.lang.String reason, Model model,
            RedirectAttributes redirectAttributes) {

        setAcademicActBlockingSuspension(academicActBlockingSuspension, model);

        try {

            academicActBlockingSuspension.edit(beginDate, endDate, reason);

            return redirect(
                    String.format(READ_URL + "%s/%s", debtAccount.getExternalId(), academicActBlockingSuspension.getExternalId()),
                    model, redirectAttributes);
        } catch (DomainException de) {
            addErrorMessage(de.getLocalizedMessage(), model);

            return update(debtAccount, academicActBlockingSuspension, model);
        }
    }

    private String jspPage(final String page) {
        return JSP_PATH + "/" + page;
    }

}
