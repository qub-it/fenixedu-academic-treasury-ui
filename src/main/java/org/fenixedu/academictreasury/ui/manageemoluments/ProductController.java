package org.fenixedu.academictreasury.ui.manageemoluments;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.fenixedu.academictreasury.services.EmolumentServices;
import org.fenixedu.academictreasury.ui.AcademicTreasuryBaseController;
import org.fenixedu.bennu.core.domain.exceptions.DomainException;
import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.treasury.domain.FinantialEntity;
import org.fenixedu.treasury.domain.Product;
import org.fenixedu.treasury.domain.VatType;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import pt.ist.fenixframework.Atomic;

@Component("org.fenixedu.academictreasury.ui.manageemoluments")
@BennuSpringController(value = FinantialEntityController.class)
@RequestMapping("/academictreasury/manageemoluments/product")
public class ProductController extends AcademicTreasuryBaseController {

    @Atomic
    public void deleteProduct(final Product product) {
        // CHANGE_ME: Do the processing for deleting the product
        // Do not catch any exception here

        // product.delete();
    }

    @RequestMapping(value = "/searchemoluments/{finantialEntityId}")
    public String searchEmoluments(@PathVariable("finantialEntityId") final FinantialEntity finantialEntity, final Model model) {
        List<Product> searchemolumentsResultsDataSet = filterSearchEmoluments();

        //add the results dataSet to the model
        model.addAttribute("searchemolumentsResultsDataSet", searchemolumentsResultsDataSet);
        model.addAttribute("finantialEntity", finantialEntity);

        return "academicTreasury/manageemoluments/product/searchemoluments";
    }

    private List<Product> getSearchUniverseSearchEmolumentsDataSet() {
        return new ArrayList<Product>(EmolumentServices.findEmoluments().collect(Collectors.toSet()));
    }

    private List<Product> filterSearchEmoluments() {
        return getSearchUniverseSearchEmolumentsDataSet().stream().collect(Collectors.toList());
    }

    @RequestMapping(value = "/searchemoluments/view/{finantialEntityId}/{productId}")
    public String processSearchEmolumentsToViewAction(@PathVariable("finantialEntityId") final FinantialEntity finantialEntity,
            @PathVariable("productId") final Product product, final Model model) {
        return String.format("redirect:/academictreasury/manageemoluments/academictariff/viewemolumenttariffs/%s/%s",
                finantialEntity.getExternalId(), product.getExternalId());
    }

    @RequestMapping(value = "/createemolument/{finantialEntityId}", method = RequestMethod.GET)
    public String createemolument(@PathVariable("finantialEntityId") final FinantialEntity finantialEntity, final Model model) {
        model.addAttribute("finantialEntity", finantialEntity);
        model.addAttribute("vatType_options", VatType.findAll().collect(Collectors.toSet()));

        return "academicTreasury/manageemoluments/product/createemolument";
    }

    @RequestMapping(value = "/createemolument/{finantialEntityId}", method = RequestMethod.POST)
    public String createemolument(@PathVariable("finantialEntityId") final FinantialEntity finantialEntity, @RequestParam(
            value = "code", required = false) final java.lang.String code,
            @RequestParam(value = "name", required = false) final LocalizedString name, @RequestParam(value = "vattype",
                    required = false) final VatType vatType, final Model model) {

        try {
            final Product product = EmolumentServices.createEmolument(code, name, vatType);

            model.addAttribute("product", product);
            return String.format("redirect:/academictreasury/manageemoluments/product/searchemoluments/%s",
                    finantialEntity.getExternalId());
        } catch (final DomainException de) {
            addErrorMessage(de.getLocalizedMessage(), model);
            return createemolument(finantialEntity, model);
        }
    }

}
