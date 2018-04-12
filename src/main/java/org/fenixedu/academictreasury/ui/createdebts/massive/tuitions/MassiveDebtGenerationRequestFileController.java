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
package org.fenixedu.academictreasury.ui.createdebts.massive.tuitions;

import static org.fenixedu.academictreasury.util.Constants.academicTreasuryBundle;

import java.io.IOException;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.fenixedu.academictreasury.domain.debtGeneration.requests.MassiveDebtGenerationRequestFile;
import org.fenixedu.academictreasury.domain.debtGeneration.requests.MassiveDebtGenerationRequestFileBean;
import org.fenixedu.academictreasury.domain.exceptions.AcademicTreasuryDomainException;
import org.fenixedu.academictreasury.ui.AcademicTreasuryBaseController;
import org.fenixedu.academictreasury.ui.AcademicTreasuryController;
import org.fenixedu.academictreasury.util.Constants;
import org.fenixedu.bennu.core.domain.exceptions.DomainException;
import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.treasury.domain.debt.DebtAccount;
import org.fenixedu.treasury.ui.accounting.managecustomer.CustomerController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.JstlView;
import org.springframework.web.servlet.view.RedirectView;

import com.google.common.base.Strings;

@BennuSpringController(value = CustomerController.class)
@SpringFunctionality(app = AcademicTreasuryController.class, title = "label.title.massivetuitiondebtcreation",
        accessGroup = "treasuryManagers")
@RequestMapping(MassiveDebtGenerationRequestFileController.CONTROLLER_URL)
public class MassiveDebtGenerationRequestFileController extends AcademicTreasuryBaseController {

    public static final String CONTROLLER_URL = "/academictreasury/massivedebtgenerationrequestfile";
    private static final String JSP_PATH = "academicTreasury/massivedebtgenerationrequestfile";

    @RequestMapping
    public String home(Model model) {
        return "forward:" + SEARCH_URL;
    }

    private static final String _SEARCH_URI = "/search";
    public static final String SEARCH_URL = CONTROLLER_URL + _SEARCH_URI;

    @RequestMapping(value = _SEARCH_URI, method = RequestMethod.GET)
    public String search(final Model model) {

        model.addAttribute("requestFiles", MassiveDebtGenerationRequestFile.findAllActive()
                .sorted(MassiveDebtGenerationRequestFile.COMPARE_BY_CREATION_DATE).collect(Collectors.toList()));

        return jspPage("search");
    }

    private static final String _CREATE_URI = "/create";
    public static final String CREATE_URL = CONTROLLER_URL + _CREATE_URI;

    @RequestMapping(value = _CREATE_URI, method = RequestMethod.GET)
    public String create(final Model model) {
        final MassiveDebtGenerationRequestFileBean bean = new MassiveDebtGenerationRequestFileBean();
        return _createFirstPage(bean, model);
    }

    private static final String _BACKTOCREATE_URI = "/backtocreate";
    public static final String BACKTOCREATE_URL = CONTROLLER_URL + _BACKTOCREATE_URI;

    @RequestMapping(value = _BACKTOCREATE_URI, method = RequestMethod.POST)
    public String backTocreate(@PathVariable("debtAccountId") final DebtAccount debtAccount,
            @RequestParam(value = "bean", required = false) final MassiveDebtGenerationRequestFileBean bean, final Model model) {
        return _createFirstPage(bean, model);
    }

    public String _createFirstPage(final MassiveDebtGenerationRequestFileBean bean, final Model model) {
        model.addAttribute("bean", bean);
        model.addAttribute("beanJson", getBeanJson(bean));

        return jspPage("create");
    }

    private static final String _CREATEPOSTBACK_URI = "/createpostback";
    public static final String CREATEPOSTBACK_URL = CONTROLLER_URL + _CREATEPOSTBACK_URI;

    @RequestMapping(value = _CREATEPOSTBACK_URI, method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public @ResponseBody ResponseEntity<String> createpostback(
            @RequestParam(value = "bean", required = false) final MassiveDebtGenerationRequestFileBean bean, final Model model) {

        bean.updateData();
        return new ResponseEntity<String>(getBeanJson(bean), HttpStatus.OK);
    }

    @RequestMapping(value = _CREATE_URI, method = RequestMethod.POST)
    public String create(@RequestParam(value = "bean", required = false) final MassiveDebtGenerationRequestFileBean bean,
            @RequestParam(value = "requestFile", required = true) MultipartFile requestFile, final Model model,
            final RedirectAttributes redirectAttributes) {

        try {
            byte[] content = requestFile.getBytes();

            if (bean.getMassiveDebtGenerationType() == null) {
                throw new AcademicTreasuryDomainException(
                        "error.MassiveDebtGenerationRequestFile.massiveDebtGenerationType.required");
            }

            bean.getMassiveDebtGenerationType().implementation().readExcel(content, bean);

            MassiveDebtGenerationRequestFile file =
                    MassiveDebtGenerationRequestFile.create(bean, requestFile.getOriginalFilename(), content);

            return redirect(CONFIRMDEBTCREATION_URL + "/" + file.getExternalId(), model, redirectAttributes);
        } catch (final Exception e) {
            addErrorMessage(e.getLocalizedMessage(), model);

            return _createFirstPage(bean, model);
        }
    }

    private static final String _CONFIRMDEBTCREATION_URI = "/confirmdebtcreation";
    public static final String CONFIRMDEBTCREATION_URL = CONTROLLER_URL + _CONFIRMDEBTCREATION_URI;

    @RequestMapping(value = _CONFIRMDEBTCREATION_URI + "/{fileId}", method = RequestMethod.GET)
    public View confirmdebtcreation(
            @PathVariable("fileId") final MassiveDebtGenerationRequestFile massiveDebtGenerationRequestFile, final Model model,
            final RedirectAttributes redirectAttributes) {

        try {
            model.addAttribute("massiveDebtGenerationRequestFile", massiveDebtGenerationRequestFile);
            model.addAttribute("rows",
                    massiveDebtGenerationRequestFile.getMassiveDebtGenerationType().implementation().readExcel(
                            massiveDebtGenerationRequestFile.getContent(),
                            new MassiveDebtGenerationRequestFileBean(massiveDebtGenerationRequestFile)));

            model.addAttribute("requestFile", massiveDebtGenerationRequestFile);
            model.addAttribute("processable", true);
        } catch (DomainException de) {
            addErrorMessage(de.getLocalizedMessage(), model);
        }

        return new JstlView(massiveDebtGenerationRequestFile.getMassiveDebtGenerationType().implementation().viewUrl()) {
            @Override
            protected void exposeHelpers(HttpServletRequest request) throws Exception {
                setServletContext(request.getServletContext());
                super.exposeHelpers(request);
            }
        };
    }

    private static final String _PROCESSREQUEST_URI = "/processrequest";
    public static final String PROCESSREQUEST_URL = CONTROLLER_URL + _PROCESSREQUEST_URI;

    @RequestMapping(value = _PROCESSREQUEST_URI + "/{fileId}", method = RequestMethod.POST)
    public View processrequest(@PathVariable("fileId") final MassiveDebtGenerationRequestFile massiveDebtGenerationRequestFile,
            final Model model, final RedirectAttributes redirectAttributes) {

        try {
            massiveDebtGenerationRequestFile.process();

            addInfoMessage(academicTreasuryBundle("label.MassiveDebtGenerationRequestFile.success"), model);

            redirect(SEARCH_URL, model, redirectAttributes);

            if(Strings.isNullOrEmpty(request.getContextPath())) {
                return new RedirectView(SEARCH_URL);
            } else {
                return new RedirectView(request.getContextPath() + SEARCH_URL);
            }
            
        } catch (final Exception e) {
            addErrorMessage(e.getLocalizedMessage(), model);
            return confirmdebtcreation(massiveDebtGenerationRequestFile, model, redirectAttributes);
        }

    }

    private static final String _DOWNLOAD_URI = "/download";
    public static final String DOWNLOAD_URL = CONTROLLER_URL + _DOWNLOAD_URI;

    @RequestMapping(value = _DOWNLOAD_URI + "/{fileId}", method = RequestMethod.GET)
    public void download(@PathVariable("fileId") final MassiveDebtGenerationRequestFile massiveDebtGenerationRequestFile,
            final HttpServletRequest request, final HttpServletResponse response, final Model model) {

        response.setContentLength(massiveDebtGenerationRequestFile.getContent().length);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-disposition", "attachment; filename=" + massiveDebtGenerationRequestFile.getFilename());

        try {
            response.getOutputStream().write(massiveDebtGenerationRequestFile.getContent());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String jspPage(final String page) {
        return JSP_PATH + "/" + page;
    }

}
