package org.fenixedu.academictreasury.ui.createdebts;

import org.fenixedu.academictreasury.ui.AcademicTreasuryController;
import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.fenixedu.treasury.domain.debt.DebtAccount;
import org.fenixedu.treasury.ui.accounting.managecustomer.CustomerController;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@BennuSpringController(value = CustomerController.class)
@RequestMapping(CreateDebtsController.CONTROLLER_URL)
public class CreateDebtsController extends AcademicTreasuryController {

    public static final String CONTROLLER_URL = "/academictreasury/createdebts";
    private static final String JSP_PATH = "academicTreasury/createdebts";

    private static final String _OPERATIONS_URI = "/operations";
    public static final String OPERATIONS_URL = CONTROLLER_URL + _OPERATIONS_URI;

    @RequestMapping(value = _OPERATIONS_URI + "/{debtAccountId}", method = RequestMethod.GET)
    public String create(@PathVariable("debtAccountId") final DebtAccount debtAccount, final Model model) {
        
        model.addAttribute("debtAccount", debtAccount);
        
        return jspPage("operations");
    }

    private String jspPage(final String page) {
        return JSP_PATH + "/" + page;
    }

}
