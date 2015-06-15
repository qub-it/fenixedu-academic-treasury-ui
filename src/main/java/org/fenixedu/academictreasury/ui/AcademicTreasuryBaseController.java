package org.fenixedu.academictreasury.ui;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.fenixedu.bennu.BeanConverterService;
import org.fenixedu.bennu.DomainObjectAdapter;
import org.fenixedu.bennu.IBean;
import org.fenixedu.bennu.LocalizedStringAdapter;
import org.fenixedu.commons.i18n.LocalizedString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import pt.ist.fenixframework.DomainObject;

import com.fatboyindustrial.gsonjodatime.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

public class AcademicTreasuryBaseController {
    private static final String ERROR_MESSAGES = "errorMessages";
    private static final String WARNING_MESSAGES = "warningMessages";
    private static final String INFO_MESSAGES = "infoMessages";

    //The HTTP Request that can be used internally in the controller
    protected @Autowired HttpServletRequest request;

    //The entity in the Model

    // The list of INFO messages that can be showed on View
    protected void addInfoMessage(String message, Model model) {
        ((List<String>) model.asMap().get(INFO_MESSAGES)).add(message);
    }

    // The list of WARNING messages that can be showed on View
    protected void addWarningMessage(String message, Model model) {
        ((List<String>) model.asMap().get(WARNING_MESSAGES)).add(message);
    }

    // The list of ERROR messages that can be showed on View
    protected void addErrorMessage(String message, Model model) {
        ((List<String>) model.asMap().get(ERROR_MESSAGES)).add(message);
    }

    protected void clearMessages(Model model) {
        model.addAttribute(INFO_MESSAGES, new ArrayList<String>());
        model.addAttribute(WARNING_MESSAGES, new ArrayList<String>());
        model.addAttribute(ERROR_MESSAGES, new ArrayList<String>());
    }

    protected String redirect(String destinationAction, Model model, RedirectAttributes redirectAttributes) {
        if (model.containsAttribute(INFO_MESSAGES)) {
            redirectAttributes.addFlashAttribute(INFO_MESSAGES, model.asMap().get(INFO_MESSAGES));
        }
        if (model.containsAttribute(WARNING_MESSAGES)) {
            redirectAttributes.addFlashAttribute(WARNING_MESSAGES, model.asMap().get(WARNING_MESSAGES));
        }
        if (model.containsAttribute(ERROR_MESSAGES)) {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGES, model.asMap().get(ERROR_MESSAGES));
        }

        return "redirect:" + destinationAction;
    }

    @ModelAttribute
    protected void addModelProperties(Model model) {
        if (!model.containsAttribute(INFO_MESSAGES)) {
            model.addAttribute(INFO_MESSAGES, new ArrayList<String>());
        }
        if (!model.containsAttribute(WARNING_MESSAGES)) {
            model.addAttribute(WARNING_MESSAGES, new ArrayList<String>());
        }
        if (!model.containsAttribute(ERROR_MESSAGES)) {
            model.addAttribute(ERROR_MESSAGES, new ArrayList<String>());
        }

        // Add here more attributes to the Model
        // model.addAttribute(<attr1Key>, <attr1Value>);
        // ....
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        GenericConversionService conversionService = (GenericConversionService) binder.getConversionService();
        if (!conversionService.canConvert(String.class, IBean.class)) {
            conversionService.addConverter(new BeanConverterService());
        }
    }

    protected String getBeanJson(IBean bean) {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(LocalizedString.class, new LocalizedStringAdapter());
        builder.registerTypeHierarchyAdapter(DomainObject.class, new DomainObjectAdapter());
        Gson gson = Converters.registerAll(builder).create();

        // CREATING JSON TREE TO ADD CLASSNAME ATTRIBUTE MUST DO THIS AUTOMAGICALLY
        JsonElement jsonTree = gson.toJsonTree(bean);
        jsonTree.getAsJsonObject().addProperty("classname", bean.getClass().getName());
        return jsonTree.toString();
    }

}
