package org.fenixedu.academictreasury.ui.manageemoluments;

import static org.fenixedu.academictreasury.util.AcademicTreasuryConstants.academicTreasuryBundle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.degree.DegreeType;
import org.fenixedu.academictreasury.domain.tariff.AcademicTariff;
import org.fenixedu.academictreasury.dto.tariff.AcademicTariffBean;
import org.fenixedu.academictreasury.ui.AcademicTreasuryBaseController;
import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.fenixedu.treasury.domain.FinantialEntity;
import org.fenixedu.treasury.domain.Product;
import org.fenixedu.treasury.domain.settings.TreasurySettings;
import org.fenixedu.treasury.domain.tariff.DueDateCalculationType;
import org.fenixedu.treasury.domain.tariff.InterestRateType;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

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
        return new ArrayList<AcademicTariff>(
                AcademicTariff.find(finantialEntity, product).collect(Collectors.<AcademicTariff> toSet()));
    }

    private List<AcademicTariff> filterViewEmolumentTariffs(final FinantialEntity finantialEntity, final Product product) {
        return getSearchUniverseViewEmolumentTariffsDataSet(finantialEntity, product).stream().collect(Collectors.toList());
    }

    @RequestMapping(value = "/viewEmolumentTariffs/view/{oid}")
    public String processViewEmolumentTariffsToViewAction(@PathVariable("oid") AcademicTariff academicTariff, Model model) {
        return String.format("redirect:/academictreasury/manageemoluments/academictariff/viewemolumenttariff/%s",
                academicTariff.getExternalId());
    }

    @RequestMapping(value = "/viewEmolumentTariffs/delete/{finantialEntityId}/{productId}/{academicTariffId}",
            method = RequestMethod.POST)
    public String processViewEmolumentTariffsToDeleteAction(
            @PathVariable("finantialEntityId") final FinantialEntity finantialEntity,
            @PathVariable("productId") final Product product,
            @PathVariable("academicTariffId") final AcademicTariff academicTariff, Model model) {
        try {
            academicTariff.delete();

            addInfoMessage(academicTreasuryBundle("label.AcademicTariff.delete.success"), model);
        } catch (Exception ex) {
            addErrorMessage(ex.getLocalizedMessage(), model);
        }

        return String.format("redirect:/academictreasury/manageemoluments/academictariff/viewemolumenttariffs/%s/%s",
                finantialEntity.getExternalId(), product.getExternalId());
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

        model.addAttribute("AcademicTariff_degreeType_options", DegreeType.all().sorted(new Comparator<DegreeType>() {

            @Override
            public int compare(final DegreeType o1, final DegreeType o2) {
                int c = o1.getName().getContent().compareTo(o2.getName().getContent());

                return c != 0 ? c : o1.getExternalId().compareTo(o2.getExternalId());
            }

        }).collect(Collectors.<DegreeType> toList()));

        model.addAttribute("AcademicTariff_degree_options",
                Degree.readAllMatching(Predicate.<DegreeType> isEqual(academicTariffBean.getDegreeType())).stream()
                        .sorted(Degree.COMPARATOR_BY_NAME).collect(Collectors.toList()));

        model.addAttribute("AcademicTariff_cycleType_options", academicTariffBean.getDegree() != null ? academicTariffBean
                .getDegreeType().getCycleTypes() : Collections.emptyList());

        model.addAttribute("AcademicTariff_dueDateCalculationType_options", Arrays.asList(DueDateCalculationType.values()));
        model.addAttribute("AcademicTariff_interestType_options",
                TreasurySettings.getInstance().getAvailableInterestRateTypesSet().stream()
                        .sorted(InterestRateType.COMPARE_BY_NAME).collect(Collectors.toList()));

        return "academicTreasury/manageemoluments/academictariff/createemolumenttariff";
    }

    @RequestMapping(value = "/createemolumenttariff/{finantialEntityId}/{productId}", method = RequestMethod.POST)
    public String createemolumenttariff(@PathVariable("finantialEntityId") final FinantialEntity finantialEntity,
            @PathVariable("productId") final Product product,
            @RequestParam(value = "academicTariffBean", required = false) final AcademicTariffBean bean, final Model model) {

        try {

            bean.resetFields();

            AcademicTariff academicTariff = AcademicTariff.create(finantialEntity, product, bean);

            return String.format("redirect:/academictreasury/manageemoluments/academictariff/viewemolumenttariffs/%s/%s",
                    finantialEntity.getExternalId(), product.getExternalId(), academicTariff.getExternalId());

        } catch (Exception de) {
            addErrorMessage(de.getLocalizedMessage(), model);
            return _createemolumenttariff(finantialEntity, product, bean, model);
        }
    }

    @RequestMapping(value = "/updateemolumenttariff/{finantialEntityId}/{productId}/{academicTariffId}",
            method = RequestMethod.GET)
    public String updateemolumenttariff(@PathVariable("finantialEntityId") final FinantialEntity finantialEntity,
            @PathVariable("productId") final Product product,
            @PathVariable("academicTariffId") final AcademicTariff academicTariff, final Model model) {

        return _updateemolumenttariff(finantialEntity, product, academicTariff, new AcademicTariffBean(academicTariff), model);
    }

    public String _updateemolumenttariff(final FinantialEntity finantialEntity, final Product product,
            final AcademicTariff academicTariff, final AcademicTariffBean bean, final Model model) {

        model.addAttribute("finantialEntity", finantialEntity);
        model.addAttribute("product", product);
        model.addAttribute("academicTariff", academicTariff);

        model.addAttribute("academicTariffBean", bean);
        model.addAttribute("academicTariffBeanJson", getBeanJson(bean));

        model.addAttribute("AcademicTariff_interestType_options", TreasurySettings.getInstance().getAvailableInterestRateTypesSet().stream()
                .sorted(InterestRateType.COMPARE_BY_NAME).collect(Collectors.toList()));

        return "academicTreasury/manageemoluments/academictariff/updateemolumenttariff";
    }

    @RequestMapping(value = "/updateemolumenttariffpostback/{finantialEntityId}/{productId}/{academicTariffId}",
            method = RequestMethod.POST)
    public String updateemolumenttariffpostback(@PathVariable("finantialEntityId") final FinantialEntity finantialEntity,
            @PathVariable("productId") final Product product,
            @PathVariable("academicTariffId") final AcademicTariff academicTariff,
            @RequestParam(value = "academicTariffBean", required = false) final AcademicTariffBean bean, final Model model) {

        return _updateemolumenttariff(finantialEntity, product, academicTariff, bean, model);
    }

    @RequestMapping(value = "/updateemolumenttariff/{finantialEntityId}/{productId}/{academicTariffId}",
            method = RequestMethod.POST)
    public String updateemolumenttariff(@PathVariable("finantialEntityId") final FinantialEntity finantialEntity,
            @PathVariable("productId") final Product product,
            @PathVariable("academicTariffId") final AcademicTariff academicTariff,
            @RequestParam(value = "academicTariffBean", required = false) final AcademicTariffBean academicTariffBean,
            final Model model) {

        try {
            academicTariff.edit(academicTariffBean);

            return String.format("redirect:/academictreasury/manageemoluments/academictariff/viewemolumenttariffs/%s/%s",
                    finantialEntity.getExternalId(), product.getExternalId(), academicTariff.getExternalId());

        } catch (Exception de) {
            addErrorMessage(de.getLocalizedMessage(), model);

            return _updateemolumenttariff(finantialEntity, product, academicTariff, academicTariffBean, model);
        }
    }

}
