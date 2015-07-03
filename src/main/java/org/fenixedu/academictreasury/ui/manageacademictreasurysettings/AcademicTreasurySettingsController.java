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
package org.fenixedu.academictreasury.ui.manageacademictreasurysettings;

import java.util.stream.Collectors;

import org.fenixedu.academictreasury.domain.emoluments.AcademicTax;
import org.fenixedu.academictreasury.domain.settings.AcademicTreasurySettings;
import org.fenixedu.academictreasury.ui.AcademicTreasuryBaseController;
import org.fenixedu.academictreasury.ui.AcademicTreasuryController;
import org.fenixedu.bennu.core.domain.exceptions.DomainException;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.treasury.domain.Product;
import org.fenixedu.treasury.domain.ProductGroup;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

//@Component("org.fenixedu.academictreasury.ui.manageAcademicTreasurySettings") <-- Use for duplicate controller name disambiguation
@SpringFunctionality(app = AcademicTreasuryController.class, title = "label.title.manageAcademicTreasurySettings",
        accessGroup = "treasuryManagers")
@RequestMapping(AcademicTreasurySettingsController.CONTROLLER_URL)
public class AcademicTreasurySettingsController extends AcademicTreasuryBaseController {

    public static final String CONTROLLER_URL = "/academictreasury/manageacademictreasurysettings/academictreasurysettings";
    private static final String JSP_PATH = "academicTreasury/manageacademictreasurysettings/academictreasurysettings";

    @RequestMapping
    public String home(Model model) {
        return "forward:" + READ_URL;
    }

    private static final String _READ_URI = "/read/";
    public static final String READ_URL = CONTROLLER_URL + _READ_URI;

    @RequestMapping(value = _READ_URI)
    public String read(final Model model) {
        model.addAttribute("products", Product.findAllActive().sorted(Product.COMPARE_BY_NAME).collect(Collectors.toList()));

        model.addAttribute("academicTreasurySettings", AcademicTreasurySettings.getInstance());

        return jspPage("read");
    }

    private static final String _UPDATE_URI = "/update/";
    public static final String UPDATE_URL = CONTROLLER_URL + _UPDATE_URI;

    @RequestMapping(value = _UPDATE_URI, method = RequestMethod.GET)
    public String update(final Model model) {
        model.addAttribute("AcademicTreasurySettings_emolumentsProductGroup_options", ProductGroup.readAll());
        model.addAttribute("AcademicTreasurySettings_tuitionProductGroup_options", ProductGroup.readAll());
        model.addAttribute("AcademicTreasurySettings_improvementAcademicTax_options",
                AcademicTax.findAll().sorted(AcademicTax.COMPARE_BY_PRODUCT_NAME).collect(Collectors.toList()));

        model.addAttribute("academicTreasurySettings", AcademicTreasurySettings.getInstance());

        return jspPage("update");
    }

    @RequestMapping(value = _UPDATE_URI, method = RequestMethod.POST)
    public String update(
            @RequestParam(value = "emolumentsproductgroup", required = false) final ProductGroup emolumentsProductGroup,
            @RequestParam(value = "tuitionproductgroup", required = false) final ProductGroup tuitionProductGroup, @RequestParam(
                    value = "improvementacademictax", required = false) final AcademicTax improvementAcademicTax,
            final Model model, final RedirectAttributes redirectAttributes) {

        try {

            AcademicTreasurySettings.getInstance().edit(emolumentsProductGroup, tuitionProductGroup, improvementAcademicTax);

            return redirect(READ_URL, model, redirectAttributes);
        } catch (final DomainException de) {
            addErrorMessage(de.getLocalizedMessage(), model);

            return update(model);
        }
    }

    private static final String _ADDACADEMICALACTBLOCKINGPRODUCT_URI = "/addacademicalactblockingproduct/";
    public static final String ADDACADEMICALACTBLOCKINGPRODUCT_URL = CONTROLLER_URL + _ADDACADEMICALACTBLOCKINGPRODUCT_URI;

    @RequestMapping(value = _ADDACADEMICALACTBLOCKINGPRODUCT_URI + "{productId}", method = RequestMethod.GET)
    public String addAcademicalActBlockingProduct(@PathVariable("productId") final Product product, Model model) {

        try {

            AcademicTreasurySettings.getInstance().addAcademicalActBlockingProduct(product);

        } catch (final DomainException e) {
            addErrorMessage(e.getLocalizedMessage(), model);
        }

        return read(model);
    }

    private static final String _REMOVEACADEMICALACTBLOCKINGPRODUCT_URI = "/removeacademicalactblockingproduct/";
    public static final String REMOVEACADEMICALACTBLOCKINGPRODUCT_URL = CONTROLLER_URL + _REMOVEACADEMICALACTBLOCKINGPRODUCT_URI;

    @RequestMapping(value = _REMOVEACADEMICALACTBLOCKINGPRODUCT_URI + "{productId}", method = RequestMethod.GET)
    public String removeAcademicalActBlockingProduct(@PathVariable("productId") final Product product, Model model) {

        try {

            AcademicTreasurySettings.getInstance().removeAcademicalActBlockingProduct(product);

        } catch (final DomainException e) {
            addErrorMessage(e.getLocalizedMessage(), model);
        }

        return read(model);
    }

    private String jspPage(final String page) {
        return JSP_PATH + "/" + page;
    }

}
