package org.fenixedu.academictreasury.ui.exemptions.requests;

import java.io.IOException;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.fenixedu.academictreasury.domain.exemptions.requests.ExemptionsGenerationRequestFile;
import org.fenixedu.academictreasury.ui.AcademicTreasuryBaseController;
import org.fenixedu.academictreasury.ui.AcademicTreasuryController;
import org.fenixedu.academictreasury.util.Constants;
import org.fenixedu.bennu.core.domain.exceptions.DomainException;
import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.treasury.domain.exemption.TreasuryExemptionType;
import org.fenixedu.treasury.ui.accounting.managecustomer.CustomerController;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@BennuSpringController(value = CustomerController.class)
@SpringFunctionality(app = AcademicTreasuryController.class, title = "label.ExemptionsGenerationRequestFile.title",
        accessGroup = "treasuryManagers")
@RequestMapping(ExemptionsGenerationRequestFileController.CONTROLLER_URL)
public class ExemptionsGenerationRequestFileController extends AcademicTreasuryBaseController {

    public static final String CONTROLLER_URL = "/academictreasury/exemptionsgenerationrequestfile";
    private static final String JSP_PATH = "academicTreasury/exemptionsgenerationrequestfile";

    @RequestMapping
    public String home(Model model) {
        return "forward:" + SEARCH_URL;
    }

    private static final String _SEARCH_URI = "/search";
    public static final String SEARCH_URL = CONTROLLER_URL + _SEARCH_URI;

    @RequestMapping(value = _SEARCH_URI, method = RequestMethod.GET)
    public String search(final Model model) {

        model.addAttribute("requestFiles", ExemptionsGenerationRequestFile.findAll()
                .sorted(ExemptionsGenerationRequestFile.COMPARE_BY_CREATION_DATE).collect(Collectors.toList()));

        return jspPage("search");
    }

    private static final String _CREATE_URI = "/create";
    public static final String CREATE_URL = CONTROLLER_URL + _CREATE_URI;

    @RequestMapping(value = _CREATE_URI, method = RequestMethod.GET)
    public String create(final Model model) {
        return _createFirstPage(model);
    }

    public String _createFirstPage(final Model model) {
        model.addAttribute("treasuryExemptionTypesSet",
                TreasuryExemptionType.findAll().sorted(TreasuryExemptionType.COMPARE_BY_NAME).collect(Collectors.toList()));

        return jspPage("create");
    }

    @RequestMapping(value = _CREATE_URI, method = RequestMethod.POST)
    public String create(@RequestParam(value = "treasuryExemptionTypeId", required = false) final TreasuryExemptionType treasuryExemptionType,
            @RequestParam(value = "requestFile", required = true) MultipartFile requestFile, final Model model,
            final RedirectAttributes redirectAttributes) {

        try {
            byte[] content = requestFile.getBytes();

            if (treasuryExemptionType == null) {
                addErrorMessage("error.ExemptionsGenerationRequestFile.treasuryExemptionType.required", model);
                return _createFirstPage(model);
            }

            ExemptionsGenerationRequestFile.readExcel(treasuryExemptionType, content);

            ExemptionsGenerationRequestFile file =
                    ExemptionsGenerationRequestFile.create(treasuryExemptionType, requestFile.getOriginalFilename(), content);

            return redirect(CONFIRMDEBTCREATION_URL + "/" + file.getExternalId(), model, redirectAttributes);
        } catch (final Exception e) {
            addErrorMessage(e.getLocalizedMessage(), model);

            return _createFirstPage(model);
        }
    }

    private static final String _CONFIRMDEBTCREATION_URI = "/confirmdebtcreation";
    public static final String CONFIRMDEBTCREATION_URL = CONTROLLER_URL + _CONFIRMDEBTCREATION_URI;

    @RequestMapping(value = _CONFIRMDEBTCREATION_URI + "/{fileId}", method = RequestMethod.GET)
    public String confirmdebtcreation(
            @PathVariable("fileId") final ExemptionsGenerationRequestFile exemptionsGenerationRequestFile, final Model model,
            final RedirectAttributes redirectAttributes) {

        try {
            model.addAttribute("exemptionsGenerationRequestFile", exemptionsGenerationRequestFile);
            model.addAttribute("rows", ExemptionsGenerationRequestFile.readExcel(
                    exemptionsGenerationRequestFile.getTreasuryExemptionType(), exemptionsGenerationRequestFile.getContent()));

            model.addAttribute("requestFile", exemptionsGenerationRequestFile);
            model.addAttribute("processable", true);
        } catch (DomainException de) {
            addErrorMessage(de.getLocalizedMessage(), model);
        }

        return jspPage("confirm");
    }

    private static final String _PROCESSREQUEST_URI = "/processrequest";
    public static final String PROCESSREQUEST_URL = CONTROLLER_URL + _PROCESSREQUEST_URI;

    @RequestMapping(value = _PROCESSREQUEST_URI + "/{fileId}", method = RequestMethod.POST)
    public String processrequest(@PathVariable("fileId") final ExemptionsGenerationRequestFile exemptionsGenerationRequestFile,
            final Model model, final RedirectAttributes redirectAttributes) {

        try {
            exemptionsGenerationRequestFile.process();

            addInfoMessage(Constants.bundle("label.MassiveDebtGenerationRequestFile.success"), model);
            return redirect(SEARCH_URL, model, redirectAttributes);
        } catch (final Exception e) {
            addErrorMessage(e.getLocalizedMessage(), model);
            return confirmdebtcreation(exemptionsGenerationRequestFile, model, redirectAttributes);
        }
    }

    private static final String _DOWNLOAD_URI = "/download";
    public static final String DOWNLOAD_URL = CONTROLLER_URL + _DOWNLOAD_URI;

    @RequestMapping(value = _DOWNLOAD_URI + "/{fileId}", method = RequestMethod.GET)
    public void download(@PathVariable("fileId") final ExemptionsGenerationRequestFile exemptionsGenerationRequestFile,
            final HttpServletRequest request, final HttpServletResponse response, final Model model) {

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-disposition", "attachment; filename=" + exemptionsGenerationRequestFile.getFilename());

        try {
            response.getOutputStream().write(exemptionsGenerationRequestFile.getContent());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String jspPage(final String page) {
        return JSP_PATH + "/" + page;
    }

}
