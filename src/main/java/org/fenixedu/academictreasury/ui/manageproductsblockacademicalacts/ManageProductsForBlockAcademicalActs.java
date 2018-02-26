package org.fenixedu.academictreasury.ui.manageproductsblockacademicalacts;

import java.util.List;
import java.util.stream.Collectors;

import org.fenixedu.academictreasury.domain.settings.AcademicTreasurySettings;
import org.fenixedu.academictreasury.ui.AcademicTreasuryBaseController;
import org.fenixedu.academictreasury.ui.AcademicTreasuryController;
import org.fenixedu.bennu.core.domain.exceptions.DomainException;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.treasury.domain.Product;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@SpringFunctionality(app = AcademicTreasuryController.class, title = "label.title.academictreasury.ManageProductsForBlockAcademicalActs", 
    accessGroup = "treasuryBackOffice")
@RequestMapping(ManageProductsForBlockAcademicalActs.CONTROLLER_URL)
public class ManageProductsForBlockAcademicalActs extends AcademicTreasuryBaseController {

    public static final String CONTROLLER_URL = "/academicTreasury/manageproductsblockacademicalacts/";
    public static final String JSP_PATH = "academicTreasury/manageproductsblockacademicalacts/";
    
    @RequestMapping
    public String home(Model model) {
        return "forward:" + CONTROLLER_URL + "/search";
    }

    private static final String _SEARCH_URI = "/search";
    public static final String SEARCH_URL = CONTROLLER_URL + _SEARCH_URI;

    @RequestMapping(value = _SEARCH_URI)
    public String search(Model model) {
        final AcademicTreasurySettings academicTreasurySettings = AcademicTreasurySettings.getInstance();
        
        model.addAttribute("academicTreasurySettings", academicTreasurySettings);
        
        model.addAttribute("blockingproducts", Product.findAll().filter(p -> academicTreasurySettings.isAcademicalActBlocking(p)). collect(Collectors.toSet()));
        model.addAttribute("nonblockingproducts", Product.findAll().filter(p -> !academicTreasurySettings.isAcademicalActBlocking(p)).collect(Collectors.toSet()));
        
        return jspPage("search");
    }
    
    private static final String _ADD_PRODUCTS_FOR_BLOCKING_URI = "/addproductsforblocking";
    public static final String ADD_PRODUCTS_BLOCKING_URL = CONTROLLER_URL + _ADD_PRODUCTS_FOR_BLOCKING_URI;

    @RequestMapping(value = _ADD_PRODUCTS_FOR_BLOCKING_URI, method=RequestMethod.GET)
    public String addproductsforblocking(Model model) {
        final AcademicTreasurySettings academicTreasurySettings = AcademicTreasurySettings.getInstance();
        
        model.addAttribute("nonblockingproducts", Product.findAll().filter(p -> !academicTreasurySettings.isAcademicalActBlocking(p)).collect(Collectors.toSet()));

        return jspPage("addproductsforblocking");
    }
    
    @RequestMapping(value = _ADD_PRODUCTS_FOR_BLOCKING_URI, method=RequestMethod.POST)
    public String addproductsforblocking(@RequestParam("products") final List<Product> products, final Model model, final RedirectAttributes redirectAttributes) {
        
        try {
            
            return redirect("/", model, redirectAttributes);
        } catch(final DomainException e) {
            addErrorMessage(e.getLocalizedMessage(), model);
            return jspPage("addproductsforblocking");
        }
        
    }
    
    private static final String _REMOVE_PRODUCTS_FOR_BLOCKING_URI = "/removeproductsforblocking";
    public static final String REMOVE_PRODUCTS_FOR_BLOCKING_URL = CONTROLLER_URL + _REMOVE_PRODUCTS_FOR_BLOCKING_URI;
    
    @RequestMapping(value = _REMOVE_PRODUCTS_FOR_BLOCKING_URI, method=RequestMethod.GET)
    public String removeproductsforblocking(final Model model) {
        return jspPage("removeproductsforblocking");
    }

    @RequestMapping(value = _REMOVE_PRODUCTS_FOR_BLOCKING_URI, method=RequestMethod.POST)
    public String removeproductsforblocking(@RequestParam("productIds") final List<Product> products, final Model model, final RedirectAttributes redirectAttributes) {
        
        try {

            return redirect("/", model, redirectAttributes);
        } catch(final DomainException e) {
            addErrorMessage(e.getLocalizedMessage(), model);
            return jspPage("removeproductsforblocking");
        }
        
    }
    
    private String jspPage(final String page) {
        return JSP_PATH + "/" + page;
    }
    
}
