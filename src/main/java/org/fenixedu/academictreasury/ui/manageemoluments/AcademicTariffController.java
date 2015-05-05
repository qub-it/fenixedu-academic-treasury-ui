package org.fenixedu.academictreasury.ui.manageemoluments;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.degree.DegreeType;
import org.fenixedu.academictreasury.domain.tariff.AcademicTariff;
import org.fenixedu.academictreasury.dto.tariff.AcademicTariffBean;
import org.fenixedu.academictreasury.ui.AcademicTreasuryBaseController;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.domain.exceptions.DomainException;
import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.fenixedu.treasury.domain.FinantialEntity;
import org.fenixedu.treasury.domain.Product;
import org.fenixedu.treasury.domain.tariff.DueDateCalculationType;
import org.fenixedu.treasury.domain.tariff.InterestType;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import pt.ist.fenixframework.Atomic;

@BennuSpringController(value = FinantialEntityController.class)
@RequestMapping("/academictreasury/manageemoluments/academictariff")
public class AcademicTariffController extends AcademicTreasuryBaseController {

    @RequestMapping(value = "/viewemolumenttariffs/{finantialEntityId}/{productId}")
    public String viewEmolumentTariffs(@PathVariable("finantialEntityId") final FinantialEntity finantialEntity,
            @PathVariable("productId") final Product product, Model model) {

        model.addAttribute("finantialEntity", finantialEntity);
        model.addAttribute("product", product);
        model.addAttribute("viewemolumenttariffsResultsDataSet", filterViewEmolumentTariffs(finantialEntity, product));

        return "academicTreasury/manageemoluments/academictariff/viewemolumenttariffs";
    }

    private List<AcademicTariff> getSearchUniverseViewEmolumentTariffsDataSet(final FinantialEntity finantialEntity,
            final Product product) {
        return new ArrayList<AcademicTariff>(AcademicTariff.find(finantialEntity, product).collect(
                Collectors.<AcademicTariff> toSet()));
    }

    private List<AcademicTariff> filterViewEmolumentTariffs(final FinantialEntity finantialEntity, final Product product) {
        return getSearchUniverseViewEmolumentTariffsDataSet(finantialEntity, product).stream().collect(Collectors.toList());
    }

    @RequestMapping(value = "/viewEmolumentTariffs/view/{oid}")
    public String processViewEmolumentTariffsToViewAction(@PathVariable("oid") AcademicTariff academicTariff, Model model) {
        return String.format("redirect:/academictreasury/manageemoluments/academictariff/viewemolumenttariff/%s",
                academicTariff.getExternalId());
    }

    @RequestMapping(value = "/viewEmolumentTariffs/delete/{oid}, method = RequestMethod.POST")
    public String processViewEmolumentTariffsToDeleteAction(@PathVariable("oid") AcademicTariff academicTariff, Model model) {
        setAcademicTariff(academicTariff, model);
        try {
            //call the Atomic delete function
            //deleteAcademicTariff(academicTariff);

            addInfoMessage("Sucess deleting AcademicTariff ...", model);
            return "redirect:/academictreasury/manageemoluments/product/searchemoluments";
        } catch (DomainException ex) {
            //Add error messages to the list
            addErrorMessage("Error deleting the AcademicTariff due to " + ex.getLocalizedMessage(), model);
        }

        //The default mapping is the same Search screen
        return "academicTreasury/manageemoluments/academictariff/viewemolumenttariffs";
    }

    @RequestMapping(value = "/createemolumenttariff/{finantialEntityId}/{productId}", method = RequestMethod.GET)
    public String createemolumenttariff(@PathVariable("finantialEntityId") final FinantialEntity finantialEntity,
            @PathVariable("productId") final Product product, final Model model) {
        return _createemolumenttariff(finantialEntity, product, new AcademicTariffBean(), model);
    }

    @RequestMapping(value = "/createemolumenttariffpostback/{finantialEntityId}/{productId}", method = RequestMethod.POST)
    public String createemolumenttariffpostback(@PathVariable("finantialEntityId") final FinantialEntity finantialEntity,
            @PathVariable("productId") final Product product,
            @RequestParam(value = "academicTariffBean", required = true) final AcademicTariffBean academicTariffBean,
            final Model model) {

        return _createemolumenttariff(finantialEntity, product, academicTariffBean, model);
    }

    protected String _createemolumenttariff(@PathVariable("finantialEntityId") final FinantialEntity finantialEntity,
            @PathVariable("productId") final Product product,
            @RequestParam(value = "academicTariffBean", required = true) final AcademicTariffBean academicTariffBean,
            final Model model) {
        
        academicTariffBean.resetFields();
        
        model.addAttribute("finantialEntity", finantialEntity);
        model.addAttribute("product", product);

        model.addAttribute("academicTariffBean", academicTariffBean);
        model.addAttribute("academicTariffBeanJson", getBeanJson(academicTariffBean));

        model.addAttribute("AcademicTariff_administrativeOffice_options", Bennu.getInstance().getAdministrativeOfficesSet());
        model.addAttribute("AcademicTariff_degreeType_options", DegreeType.all().collect(Collectors.<DegreeType> toSet()));
        model.addAttribute("AcademicTariff_degree_options",
                Degree.readAllMatching(Predicate.<DegreeType> isEqual(academicTariffBean.getDegreeType())));
        model.addAttribute("AcademicTariff_cycleType_options", academicTariffBean.getDegree() != null ? academicTariffBean
                .getDegreeType().getCycleTypes() : Collections.emptyList());
        model.addAttribute("AcademicTariff_dueDateCalculationType_options", Arrays.asList(DueDateCalculationType.values()));
        model.addAttribute("AcademicTariff_interestType_options", Arrays.asList(InterestType.values()));
        
        return "academicTreasury/manageemoluments/academictariff/createemolumenttariff";
    }

    @RequestMapping(value = "/createemolumenttariff/{finantialEntityId}/{productId}", method = RequestMethod.POST)
    public String createemolumenttariff(@PathVariable("finantialEntityId") final FinantialEntity finantialEntity,
            @PathVariable("productId") final Product product,
            @RequestParam(value = "academicTariffBean", required = false) final AcademicTariffBean bean, final Model model) {


        try {

            bean.resetFields();
            AcademicTariff academicTariff = AcademicTariff.create(finantialEntity, product, null, bean);

            return String.format("redirect:/academictreasury/manageemoluments/academictariff/viewemolumenttariffs/%s/%s",
                    finantialEntity.getExternalId(), product.getExternalId(), academicTariff.getExternalId());
            
        } catch (DomainException de) {
            addErrorMessage(de.getLocalizedMessage(), model);
            return _createemolumenttariff(finantialEntity, product, bean, model);
        }
    }

    @RequestMapping(value = "/viewemolumenttariff/{oid}")
    public String viewemolumenttariff(@PathVariable("oid") AcademicTariff academicTariff, Model model) {
        setAcademicTariff(academicTariff, model);
        return "academicTreasury/manageemoluments/academictariff/viewemolumenttariff";
    }

    @RequestMapping(value = "/updateemolumenttariff/{oid}", method = RequestMethod.GET)
    public String updateemolumenttariff(@PathVariable("oid") AcademicTariff academicTariff, Model model) {
        setAcademicTariff(academicTariff, model);
        return "academicTreasury/manageemoluments/academictariff/updateemolumenttariff";
    }

//				
    @RequestMapping(value = "/updateemolumenttariff/{oid}", method = RequestMethod.POST)
    public String updateemolumenttariff(
            @PathVariable("oid") AcademicTariff academicTariff,
            @RequestParam(value = "begindate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ") org.joda.time.DateTime beginDate,
            @RequestParam(value = "enddate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ") org.joda.time.DateTime endDate,
            Model model) {

        setAcademicTariff(academicTariff, model);

        try {
            /*
            *  UpdateLogic here
            */

            updateAcademicTariff(beginDate, endDate, model);

            /*Succes Update */

            return "redirect:/academictreasury/manageemoluments/academictariff/viewemolumenttariff/"
                    + getAcademicTariff(model).getExternalId();
        } catch (DomainException de) {
            // @formatter: off

            /*
            * If there is any error in validation 
            *
            * Add a error / warning message
            * 
            * addErrorMessage(" Error updating due to " + de.getLocalizedMessage(),model);
            * addWarningMessage(" Warning updating due to " + de.getLocalizedMessage(),model);
            */
            // @formatter: on

            addErrorMessage(" Error updating due to " + de.getLocalizedMessage(), model);
            return updateemolumenttariff(academicTariff, model);

        }
    }

    @Atomic
    public void updateAcademicTariff(org.joda.time.DateTime beginDate, org.joda.time.DateTime endDate, Model m) {

        // @formatter: off				
        /*
         * Modify the update code here if you do not want to update
         * the object with the default setter for each field
         */

        // CHANGE_ME It's RECOMMENDED to use "Edit service" in DomainObject
        //getAcademicTariff(m).edit(fields_to_edit);

        //Instead, use individual SETTERS and validate "CheckRules" in the end
        // @formatter: on

        getAcademicTariff(m).setBeginDate(beginDate);
        getAcademicTariff(m).setEndDate(endDate);
    }

    private AcademicTariff getAcademicTariff(Model m) {
        return (AcademicTariff) m.asMap().get("academicTariff");
    }

    private void setAcademicTariff(AcademicTariff academicTariff, Model m) {
        m.addAttribute("academicTariff", academicTariff);
    }

}
