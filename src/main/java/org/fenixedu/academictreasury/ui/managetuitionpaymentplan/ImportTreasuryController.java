package org.fenixedu.academictreasury.ui.managetuitionpaymentplan;

import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.fenixedu.academictreasury.domain.exceptions.AcademicTreasuryDomainException;
import org.fenixedu.academictreasury.domain.importation.TreasuryImportFile;
import org.fenixedu.academictreasury.domain.importation.TreasuryImportType;
import org.fenixedu.academictreasury.ui.AcademicTreasuryBaseController;
import org.fenixedu.academictreasury.ui.AcademicTreasuryController;
import org.fenixedu.academictreasury.util.Constants;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
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

@SpringFunctionality(app = AcademicTreasuryController.class, title = "label.title.importTreasury",
        accessGroup = "treasuryManagers")
@RequestMapping(ImportTreasuryController.CONTROLLER_URL)
public class ImportTreasuryController extends AcademicTreasuryBaseController {

    public static final String CONTROLLER_URL = "/academictreasury/importtreasury";
    private static final String JSP_PATH = "academicTreasury/importtreasury";

    @RequestMapping
    public String home(Model model) {
        return "forward:" + SEARCH_URL;
    }

    private static final String _SEARCH_URI = "/search";
    public static final String SEARCH_URL = CONTROLLER_URL + _SEARCH_URI;

    @RequestMapping(_SEARCH_URI)
    public String search(final Model model) {

        model.addAttribute("requestFiles", TreasuryImportFile.findAll().collect(Collectors.toList()));

        return jspPage(_SEARCH_URI);
    }

    private static final String _CREATE_URI = "/create";
    public static final String CREATE_URL = CONTROLLER_URL + _CREATE_URI;

    @RequestMapping(value = _CREATE_URI, method = RequestMethod.GET)
    public String create(final Model model) {

        model.addAttribute("treasuryImportTypes",
                TreasuryImportType.findAll().sorted(TreasuryImportType.COMPARE_BY_NAME).collect(Collectors.toList()));

        return jspPage(_CREATE_URI);
    }

    @RequestMapping(value = _CREATE_URI, method = RequestMethod.POST)
    public String createpost(@RequestParam("treasuryImportTypeId") final TreasuryImportType type,
            @RequestParam(value = "requestFile", required = true) final MultipartFile requestFile, final Model model,
            final RedirectAttributes redirectAttributes) {

        try {
            if (type == null) {
                throw new AcademicTreasuryDomainException("error.TreasuryImportFile.type.required");
            }

            byte[] content = requestFile.getBytes();

            type.implementation().readExcel(content);

            final TreasuryImportFile treasuryImportFile =
                    TreasuryImportFile.create(type, requestFile.getOriginalFilename(), content);

            return redirect(VIEW_URL + "/" + treasuryImportFile.getExternalId(), model, redirectAttributes);
        } catch (final Exception e) {
            e.printStackTrace();
            addErrorMessage(e.getLocalizedMessage(), model);
            return create(model);
        }
    }

    private static final String _VIEW_URI = "/view";
    public static final String VIEW_URL = CONTROLLER_URL + _VIEW_URI;

    @RequestMapping(_VIEW_URI + "/{treasuryImportFileId}")
    public View view(@PathVariable("treasuryImportFileId") final TreasuryImportFile treasuryImportFile, final Model model) {

        model.addAttribute("treasuryImportFile", treasuryImportFile);
        model.addAttribute("objects",
                treasuryImportFile.getTreasuryImportType().implementation().readExcel(treasuryImportFile.getContent()));
        
        return new JstlView(treasuryImportFile.getTreasuryImportType().implementation().viewUrl()) {
            @Override
            protected void exposeHelpers(HttpServletRequest request) throws Exception {
                setServletContext(request.getServletContext());
                super.exposeHelpers(request);
            }
        };
    }

    private static final String _PROCESS_URI = "/process";
    public static final String PROCESS_URL = CONTROLLER_URL + _PROCESS_URI;

    @RequestMapping(value = _PROCESS_URI + "/{treasuryImportFileId}", method = RequestMethod.POST)
    public View process(@PathVariable("treasuryImportFileId") final TreasuryImportFile treasuryImportFile, final Model model,
            final RedirectAttributes redirectAttributes) {
        try {

            treasuryImportFile.getTreasuryImportType().implementation().process(treasuryImportFile);
            addInfoMessage(Constants.bundle("label.ImportTreasury.process.success"), model);

            if("/".equals(request.getContextPath())) {
                return new RedirectView(VIEW_URL + "/" + treasuryImportFile.getExternalId());
            } else {
                return new RedirectView(request.getContextPath() + VIEW_URL + "/" + treasuryImportFile.getExternalId());
            }
        } catch (final Exception e) {
            e.printStackTrace();
            addErrorMessage(e.getLocalizedMessage(), model);
        }

        return view(treasuryImportFile, model);
    }

    private static final String _DOWNLOAD_URI = "/download";
    public static final String DOWNLOAD_URL = CONTROLLER_URL + _DOWNLOAD_URI;

    @RequestMapping(_DOWNLOAD_URI + "/{treasuryImportFileId}")
    @ResponseBody
    public byte[] download(@PathVariable("treasuryImportFileId") final TreasuryImportFile treasuryImportFile,
            final HttpServletRequest request, final HttpServletResponse response, final Model model) {

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-disposition", "attachment; filename=" + treasuryImportFile.getFilename());

        return treasuryImportFile.getContent();
    }

    private String jspPage(final String page) {
        return JSP_PATH + page;
    }

}
