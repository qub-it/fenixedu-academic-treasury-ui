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
package org.fenixedu.academictreasury.ui.managefinantialentityadministrativeoffice;

import java.util.stream.Collectors;

import org.fenixedu.academic.domain.administrativeOffice.AdministrativeOffice;
import org.fenixedu.academictreasury.ui.AcademicTreasuryBaseController;
import org.fenixedu.academictreasury.ui.AcademicTreasuryController;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.domain.exceptions.DomainException;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.treasury.domain.FinantialEntity;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import pt.ist.fenixframework.Atomic;

@Component("org.fenixedu.academictreasury.ui.managefinantialentityadministrativeoffice")
@SpringFunctionality(app = AcademicTreasuryController.class, title = "label.title.manageFinantialEntityAdministrativeOffice",
        accessGroup = "treasuryManagers")
@RequestMapping(FinantialEntityController.CONTROLLER_URL)
public class FinantialEntityController extends AcademicTreasuryBaseController {

    public static final String CONTROLLER_URL = "/academicTreasury/managefinantialentityadministrativeoffice/finantialentity";
    public static final String JSP_PATH = "academicTreasury/managefinantialentityadministrativeoffice/finantialentity";

    @RequestMapping
    public String home(Model model) {
        return "forward:" + CONTROLLER_URL + "/";
    }

    private static final String _SEARCH_URI = "/";
    public static final String SEARCH_URL = CONTROLLER_URL + _SEARCH_URI;

    @RequestMapping(value = _SEARCH_URI)
    public String search(Model model) {
        model.addAttribute("searchfinantialentityResultsDataSet",
                FinantialEntity.findAll().sorted(FinantialEntity.COMPARE_BY_NAME).collect(Collectors.toList()));

        return jspPage("search");
    }

    private static final String _SEARCH_TO_VIEW_ACTION_URI = "/search/view/";
    public static final String SEARCH_TO_VIEW_ACTION_URL = CONTROLLER_URL + _SEARCH_TO_VIEW_ACTION_URI;

    @RequestMapping(value = _SEARCH_TO_VIEW_ACTION_URI + "{oid}", method=RequestMethod.GET)
    public String processSearchToViewAction(@PathVariable("oid") FinantialEntity finantialEntity, Model model,
            RedirectAttributes redirectAttributes) {
        
        return redirect(READ_URL + finantialEntity.getExternalId(), model, redirectAttributes);
    }

    private static final String _READ_URI = "/read/";
    public static final String READ_URL = CONTROLLER_URL + _READ_URI;

    @RequestMapping(value = _READ_URI + "{oid}", method=RequestMethod.GET)
    public String read(@PathVariable("oid") FinantialEntity finantialEntity, Model model) {
        model.addAttribute("finantialEntity", finantialEntity);

        return jspPage("read");
    }

    private static final String _UPDATE_URI = "/update/";
    public static final String UPDATE_URL = CONTROLLER_URL + _UPDATE_URI;

    @RequestMapping(value = _UPDATE_URI + "{oid}", method = RequestMethod.GET)
    public String update(@PathVariable("oid") FinantialEntity finantialEntity, Model model) {
        model.addAttribute("finantialEntity", finantialEntity);
        
        model.addAttribute("FinantialEntity_administrativeOffice_options", Bennu.getInstance().getAdministrativeOfficesSet());

        return jspPage("update");
    }

    @RequestMapping(value = _UPDATE_URI + "{oid}", method = RequestMethod.POST)
    public String update(@PathVariable("oid") FinantialEntity finantialEntity, @RequestParam(value = "administrativeoffice",
            required = false) AdministrativeOffice administrativeOffice, Model model, RedirectAttributes redirectAttributes) {

        try {
            associateFinantialEntityWithAdministrativeOffice(finantialEntity, administrativeOffice);

            return redirect(READ_URL + finantialEntity.getExternalId(), model, redirectAttributes);
        } catch (DomainException de) {
            addErrorMessage(de.getLocalizedMessage(), model);

            return update(finantialEntity, model);
        }
    }

    @Atomic
    private void associateFinantialEntityWithAdministrativeOffice(final FinantialEntity finantialEntity,
            final AdministrativeOffice administrativeOffice) {
        finantialEntity.setAdministrativeOffice(administrativeOffice);
    }

    private String jspPage(final String page) {
        return JSP_PATH + "/" + page;
    }
    
}
