package org.fenixedu.academictreasury.ui.managetuitionpaymentplan;

import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academictreasury.domain.settings.AcademicTreasurySettings;
import org.fenixedu.academictreasury.domain.tuition.TuitionPaymentPlanGroup;
import org.fenixedu.academictreasury.domain.tuition.importation.TuitionPaymentPlanImportFile;
import org.fenixedu.academictreasury.ui.AcademicTreasuryBaseController;
import org.fenixedu.academictreasury.ui.AcademicTreasuryController;
import org.fenixedu.academictreasury.util.Constants;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.treasury.domain.FinantialEntity;
import org.fenixedu.treasury.domain.Product;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Lists;

import edu.emory.mathcs.backport.java.util.Collections;

@SpringFunctionality(app = AcademicTreasuryController.class, title = "label.title.importTuitionPaymentPlans",
        accessGroup = "treasuryManagers")// CHANGE_ME accessGroup = "group1 | group2 | groupXPTO"
@RequestMapping(ImportTuitionPaymentPlansController.CONTROLLER_URL)
public class ImportTuitionPaymentPlansController extends AcademicTreasuryBaseController {

    public static final String CONTROLLER_URL = "/academictreasury/importtuitionpaymentplans";
    private static final String JSP_PATH = "academicTreasury/importtuitionpaymentplans";

    @RequestMapping
    public String home(Model model) {
        return "forward:" + SEARCH_URL;
    }

    private static final String _SEARCH_URI = "/search";
    public static final String SEARCH_URL = CONTROLLER_URL + _SEARCH_URI;

    @RequestMapping(_SEARCH_URI)
    public String search(final Model model) {

        model.addAttribute("requestFiles", TuitionPaymentPlanImportFile.findAll().collect(Collectors.toList()));

        return jspPage(_SEARCH_URI);
    }

    private static final String _CREATE_URI = "/create";
    public static final String CREATE_URL = CONTROLLER_URL + _CREATE_URI;

    @RequestMapping(value = _CREATE_URI, method = RequestMethod.GET)
    public String create(final Model model) {

        final List<FinantialEntity> finantialEntityList =
                FinantialEntity.findAll().sorted(FinantialEntity.COMPARE_BY_NAME).collect(Collectors.toList());
        final List<ExecutionYear> executionYearList = Lists.newArrayList(ExecutionYear.readNotClosedExecutionYears());
        final List<Product> productList = AcademicTreasurySettings.getInstance().getTuitionProductGroup().getProductsSet()
                .stream().sorted(Product.COMPARE_BY_NAME).collect(Collectors.toList());
        final List<TuitionPaymentPlanGroup> tuitionPaymentPlanGroupList =
                TuitionPaymentPlanGroup.findAll().sorted(TuitionPaymentPlanGroup.COMPARE_BY_NAME).collect(Collectors.toList());

        Collections.sort(executionYearList, ExecutionYear.REVERSE_COMPARATOR_BY_YEAR);

        model.addAttribute("finantialEntityList", finantialEntityList);
        model.addAttribute("executionYearList", executionYearList);
        model.addAttribute("productList", productList);
        model.addAttribute("tuitionPaymentPlanGroupList", tuitionPaymentPlanGroupList);

        return jspPage(_CREATE_URI);
    }

    @RequestMapping(value = _CREATE_URI, method = RequestMethod.POST)
    public String createpost(@RequestParam("productId") final Product product,
            @RequestParam("finantialEntityId") final FinantialEntity finantialEntity,
            @RequestParam("tuitionPaymentPlanGroupId") final TuitionPaymentPlanGroup tuitionPaymentPlanGroup,
            @RequestParam("executionYearId") final ExecutionYear executionYear,
            @RequestParam(value = "requestFile", required = true) final MultipartFile requestFile, final Model model,
            final RedirectAttributes redirectAttributes) {

        try {
            byte[] content = requestFile.getBytes();

            TuitionPaymentPlanImportFile.readExcel(finantialEntity, tuitionPaymentPlanGroup, product, executionYear, content);

            final TuitionPaymentPlanImportFile tuitionPaymentPlanImportFile = TuitionPaymentPlanImportFile.create(finantialEntity,
                    tuitionPaymentPlanGroup, product, executionYear, requestFile.getOriginalFilename(), content);

            return redirect(VIEW_URL + "/" + tuitionPaymentPlanImportFile.getExternalId(), model, redirectAttributes);
        } catch (final Exception e) {
            e.printStackTrace();
            addErrorMessage(e.getLocalizedMessage(), model);
            return create(model);
        }
    }

    private static final String _VIEW_URI = "/view";
    public static final String VIEW_URL = CONTROLLER_URL + _VIEW_URI;

    @RequestMapping(_VIEW_URI + "/{tuitionPaymentPlanImportFileId}")
    public String view(
            @PathVariable("tuitionPaymentPlanImportFileId") final TuitionPaymentPlanImportFile tuitionPaymentPlanImportFile,
            final Model model) {

        model.addAttribute("tuitionPaymentPlanImportFile", tuitionPaymentPlanImportFile);
        model.addAttribute("tuitionPaymentPlanBeans", tuitionPaymentPlanImportFile.readTuitions());

        return jspPage(_VIEW_URI);
    }

    private static final String _PROCESS_URI = "/process";
    public static final String PROCESS_URL = CONTROLLER_URL + _PROCESS_URI;

    @RequestMapping(value = _PROCESS_URI + "/{tuitionPaymentPlanImportFileId}", method = RequestMethod.POST)
    public String process(
            @PathVariable("tuitionPaymentPlanImportFileId") final TuitionPaymentPlanImportFile tuitionPaymentPlanImportFile,
            final Model model, final RedirectAttributes redirectAttributes) {
        try {

            tuitionPaymentPlanImportFile.process();
            addInfoMessage(Constants.bundle("label.ImportTuitionPaymentPlans.process.success"), model);

            redirect(VIEW_URL + "/" + tuitionPaymentPlanImportFile.getExternalId(), model, redirectAttributes);
        } catch (final Exception e) {
            e.printStackTrace();
            addErrorMessage(e.getLocalizedMessage(), model);
        }

        return view(tuitionPaymentPlanImportFile, model);
    }

    private static final String _DOWNLOAD_URI = "/download";
    public static final String DOWNLOAD_URL = CONTROLLER_URL + _DOWNLOAD_URI;

    @RequestMapping(_DOWNLOAD_URI + "/{tuitionPaymentPlanImportFileId}")
    @ResponseBody
    public byte[] download(
            @PathVariable("tuitionPaymentPlanImportFileId") final TuitionPaymentPlanImportFile tuitionPaymentPlanImportFile,
            final HttpServletRequest request, final HttpServletResponse response, final Model model) {

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-disposition", "attachment; filename=" + tuitionPaymentPlanImportFile.getFilename());

        return tuitionPaymentPlanImportFile.getContent();
    }

    private String jspPage(final String page) {
        return JSP_PATH + page;
    }

}
