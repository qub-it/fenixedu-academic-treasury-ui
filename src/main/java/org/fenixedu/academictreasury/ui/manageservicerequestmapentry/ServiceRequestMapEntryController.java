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
package org.fenixedu.academictreasury.ui.manageservicerequestmapentry;

import static org.fenixedu.academictreasury.util.AcademicTreasuryConstants.academicTreasuryBundle;

import java.util.stream.Collectors;

import org.fenixedu.academic.domain.serviceRequests.AcademicServiceRequestSituationType;
import org.fenixedu.academic.domain.serviceRequests.ServiceRequestType;
import org.fenixedu.academictreasury.domain.emoluments.ServiceRequestMapEntry;
import org.fenixedu.academictreasury.ui.AcademicTreasuryBaseController;
import org.fenixedu.academictreasury.ui.AcademicTreasuryController;
import org.fenixedu.bennu.core.domain.exceptions.DomainException;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.treasury.domain.Product;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@SpringFunctionality(app = AcademicTreasuryController.class, title = "label.title.manageServiceRequestMapEntry",
        accessGroup = "treasuryManagers")
@RequestMapping("/academictreasury/manageservicerequestmapentry/servicerequestmapentry")
public class ServiceRequestMapEntryController extends AcademicTreasuryBaseController {

    @RequestMapping
    public String home(Model model) {
        return "forward:/academictreasury/manageservicerequestmapentry/servicerequestmapentry/";
    }

    @RequestMapping(value = "/")
    public String search(Model model) {
        model.addAttribute("searchservicerequestmapentryResultsDataSet",
                ServiceRequestMapEntry.findAll().collect(Collectors.toList()));

        return "academicTreasury/manageservicerequestmapentry/servicerequestmapentry/search";
    }

    @RequestMapping(value = "/search/delete/{serviceRequestMapEntryId}", method = RequestMethod.POST)
    public String processSearchToDeleteAction(
            @PathVariable("serviceRequestMapEntryId") final ServiceRequestMapEntry serviceRequestMapEntry, Model model,
            RedirectAttributes redirectAttributes) {
        model.addAttribute("serviceRequestMapEntry", serviceRequestMapEntry);

        try {
            serviceRequestMapEntry.delete();

            addInfoMessage(academicTreasuryBundle("label.ServiceRequestMapEntry.delete.success"), model);
        } catch (final DomainException e) {
            addErrorMessage(e.getLocalizedMessage(), model);
        }

        return redirect("/academictreasury/manageservicerequestmapentry/servicerequestmapentry/", model, redirectAttributes);
    }

    @RequestMapping(value = "/create", method = RequestMethod.GET)
    public String create(Model model) {

        model.addAttribute("ServiceRequestMapEntry_serviceRequestType_options",
                ServiceRequestType.findAll().collect(Collectors.toList()));

        model.addAttribute("ServiceRequestMapEntry_product_options", Product.findAllActive().collect(Collectors.toList()));
        model.addAttribute("ServiceRequestMapEntry_situationType_options", AcademicServiceRequestSituationType.values());
        return "academicTreasury/manageservicerequestmapentry/servicerequestmapentry/create";
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public String create(@RequestParam(value = "servicerequesttype", required = false) ServiceRequestType serviceRequestType,
            @RequestParam(value = "product", required = false) Product product,
            @RequestParam(value = "createEventOnSituation",
                    required = true) AcademicServiceRequestSituationType createEventOnSituationType,
            @RequestParam(value = "generatePaymentCode", required = true) boolean generatePaymentCode,
            @RequestParam(value = "debitEntryDescriptionExtensionFormat",
                    required = false) String debitEntryDescriptionExtensionFormat,
            Model model, RedirectAttributes redirectAttributes) {

        try {

            ServiceRequestMapEntry.create(product, serviceRequestType, createEventOnSituationType, generatePaymentCode,
                    debitEntryDescriptionExtensionFormat);

            addInfoMessage(academicTreasuryBundle("label.ServiceRequestMapEntry.create.success"), model);

            return redirect("/academictreasury/manageservicerequestmapentry/servicerequestmapentry/", model, redirectAttributes);
        } catch (DomainException de) {
            addErrorMessage(de.getLocalizedMessage(), model);

            return create(model);
        }
    }
}
