///**
// * This file was created by Quorum Born IT <http://www.qub-it.com/> and its 
// * copyright terms are bind to the legal agreement regulating the FenixEdu@ULisboa 
// * software development project between Quorum Born IT and ServiÃ§os Partilhados da
// * Universidade de Lisboa:
// *  - Copyright Â© 2015 Quorum Born IT (until any Go-Live phase)
// *  - Copyright Â© 2015 Universidade de Lisboa (after any Go-Live phase)
// *
// * Contributors: xpto@qub-it.com
// *
// * 
// * This file is part of FenixEdu AcademicTreasury.
// *
// * FenixEdu AcademicTreasury is free software: you can redistribute it and/or modify
// * it under the terms of the GNU Lesser General Public License as published by
// * the Free Software Foundation, either version 3 of the License, or
// * (at your option) any later version.
// *
// * FenixEdu AcademicTreasury is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// * GNU Lesser General Public License for more details.
// *
// * You should have received a copy of the GNU Lesser General Public License
// * along with FenixEdu AcademicTreasury.  If not, see <http://www.gnu.org/licenses/>.
// */
//package org.fenixedu.academictreasury.ui.tuitiondebtcreation;
//
//import javax.servlet.http.HttpServletRequest;
//import java.util.List;
//import java.util.ArrayList;
//import java.util.stream.Stream;
//import org.joda.time.DateTime;
//import java.util.stream.Collectors;
//import org.fenixedu.bennu.spring.portal.SpringApplication;
//import org.fenixedu.bennu.spring.portal.SpringFunctionality;
//import org.springframework.stereotype.Component;
//import org.springframework.format.annotation.DateTimeFormat;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.ModelAttribute;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.fenixedu.bennu.spring.portal.BennuSpringController;
//import org.fenixedu.bennu.core.domain.exceptions.DomainException;
//import org.springframework.web.servlet.mvc.support.RedirectAttributes;
//import org.fenixedu.bennu.core.domain.Bennu;
//import org.fenixedu.bennu.core.i18n.BundleUtil;
//import org.fenixedu.bennu.AcademicTreasurySpringConfiguration;
//import pt.ist.fenixframework.Atomic;
//
//import org.fenixedu.academicTreasury.ui.AcademicTreasuryBaseController;
//import org.fenixedu.academicTreasury.ui.AcademicTreasuryController;
//import org.fenixedu.academicTreasury.domain.dto.tuition.TuitionDebtCreationBean;
//
////@Component("org.fenixedu.academicTreasury.ui.TuitionDebtCreation") <-- Use for duplicate controller name disambiguation
////@SpringFunctionality(app = AcademicTreasuryController.class, title = "label.title.TuitionDebtCreation",accessGroup = "logged")// CHANGE_ME accessGroup = "group1 | group2 | groupXPTO"
////or
//@BennuSpringController(value = AcademicTreasuryController.class)
//@RequestMapping(TuitionDebtCreationBeanController.CONTROLLER_URL)
//public class TuitionDebtCreationBeanController extends AcademicTreasuryBaseController {
//
//    public static final String CONTROLLER_URL = "/academictreasury/tuitiondebtcreation/tuitiondebtcreationbean";
//
////
//
//    @RequestMapping
//    public String home(Model model) {
//        //this is the default behaviour, for handling in a Spring Functionality
//        return "forward:" + CONTROLLER_URL + "/";
//    }
//
//    // @formatter: off
//
//    /*
//    * This should be used when using AngularJS in the JSP
//    */
//
//    //private TuitionDebtCreationBean getTuitionDebtCreationBeanBean(Model model)
//    //{
//    //	return (TuitionDebtCreationBean)model.asMap().get("tuitionDebtCreationBeanBean");
//    //}
//    //				
//    //private void setTuitionDebtCreationBeanBean (TuitionDebtCreationBeanBean bean, Model model)
//    //{
//    //	model.addAttribute("tuitionDebtCreationBeanBeanJson", getBeanJson(bean));
//    //	model.addAttribute("tuitionDebtCreationBeanBean", bean);
//    //}
//
//    // @formatter: on
//
//    private TuitionDebtCreationBean getTuitionDebtCreationBean(Model model) {
//        return (TuitionDebtCreationBean) model.asMap().get("tuitionDebtCreationBean");
//    }
//
//    private void setTuitionDebtCreationBean(TuitionDebtCreationBean tuitionDebtCreationBean, Model model) {
//        model.addAttribute("tuitionDebtCreationBean", tuitionDebtCreationBean);
//    }
//
//    @Atomic
//    public void deleteTuitionDebtCreationBean(TuitionDebtCreationBean tuitionDebtCreationBean) {
//        // CHANGE_ME: Do the processing for deleting the tuitionDebtCreationBean
//        // Do not catch any exception here
//
//        // tuitionDebtCreationBean.delete();
//    }
//
////				
//    private static final String _CREATE_URI = "/create";
//    public static final String CREATE_URL = CONTROLLER_URL + _create_URI;
//
//    @RequestMapping(value = _CREATE_URI, method = RequestMethod.GET)
//    public String create(Model model) {
//        model.addAttribute("TuitionDebtCreationBean_executionYear_options",
//                new ArrayList<org.fenixedu.academic.domain.ExecutionYear>()); // CHANGE_ME - MUST DEFINE RELATION
//        //model.addAttribute("TuitionDebtCreationBean_executionYear_options", org.fenixedu.academic.domain.ExecutionYear.findAll()); // CHANGE_ME - MUST DEFINE RELATION
//        model.addAttribute("TuitionDebtCreationBean_registration_options",
//                new ArrayList<org.fenixedu.academic.domain.student.Registration>()); // CHANGE_ME - MUST DEFINE RELATION
//        //model.addAttribute("TuitionDebtCreationBean_registration_options", org.fenixedu.academic.domain.student.Registration.findAll()); // CHANGE_ME - MUST DEFINE RELATION
//        model.addAttribute("TuitionDebtCreationBean_tuitionPaymentPlans_options",
//                new ArrayList<org.fenixedu.academicTreasury.domain.tuition.TuitionPaymentPlan>()); // CHANGE_ME - MUST DEFINE RELATION
//        //model.addAttribute("TuitionDebtCreationBean_tuitionPaymentPlans_options", org.fenixedu.academicTreasury.domain.tuition.TuitionPaymentPlan.findAll()); // CHANGE_ME - MUST DEFINE RELATION
//
//        //IF ANGULAR, initialize the Bean
//        //TuitionDebtCreationBeanBean bean = new TuitionDebtCreationBeanBean();
//        //this.setTuitionDebtCreationBeanBean(bean, model);
//
//        return "academicTreasury/tuitiondebtcreation/tuitiondebtcreationbean/create";
//    }
//
////
////               THIS SHOULD BE USED ONLY WHEN USING ANGULAR 
////
////						// @formatter: off
////			
////				private static final String _CREATEPOSTBACK_URI ="/createpostback";
////				public static final String  CREATEPOSTBACK_URL = CONTROLLER_URL + _createPOSTBACK_URI;
////    			@RequestMapping(value = _CREATEPOSTBACK_URI, method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
////  			  	public @ResponseBody String createpostback(@RequestParam(value = "bean", required = false) TuitionDebtCreationBeanBean bean,
////            		Model model) {
////
////        			// Do validation logic ?!?!
////        			this.setTuitionDebtCreationBeanBean(bean, model);
////        			return getBeanJson(bean);
////    			}
////    			
////    			@RequestMapping(value = CREATE, method = RequestMethod.POST)
////  			  	public String create(@RequestParam(value = "bean", required = false) TuitionDebtCreationBeanBean bean,
////            		Model model, RedirectAttributes redirectAttributes ) {
////
////					/*
////					*  Creation Logic
////					*/
////					
////					try
////					{
////
////				     	TuitionDebtCreationBean tuitionDebtCreationBean = createTuitionDebtCreationBean(... get properties from bean ...,model);
////				    	
////					//Success Validation
////				     //Add the bean to be used in the View
////					model.addAttribute("tuitionDebtCreationBean",tuitionDebtCreationBean);
////				    return redirect("/academictreasury/tuitiondebtcreation/tuitiondebtcreationbean/confirmtuitiondebtcreation/" + getTuitionDebtCreationBean(model).getExternalId(), model, redirectAttributes);
////					}
////					catch (DomainException de)
////					{
////
////						/*
////						 * If there is any error in validation 
////					     *
////					     * Add a error / warning message
////					     * 
////					     * addErrorMessage(BundleUtil.getString(AcademicTreasurySpringConfiguration.BUNDLE, "label.error.create") + de.getLocalizedMessage(),model);
////					     * addWarningMessage(" Warning creating due to "+ ex.getLocalizedMessage(),model); */
////						
////						addErrorMessage(BundleUtil.getString(AcademicTreasurySpringConfiguration.BUNDLE, "label.error.create") + de.getLocalizedMessage(),model);
////				     	return create(model);
////					}
////    			}
////						// @formatter: on
//
////				
//    @RequestMapping(value = _CREATE_URI, method = RequestMethod.POST)
//    public String create(
//            @RequestParam(value = "debtdate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ") org.joda.time.LocalDate debtDate,
//            @RequestParam(value = "executionyear", required = false) org.fenixedu.academic.domain.ExecutionYear executionYear,
//            @RequestParam(value = "registration", required = false) org.fenixedu.academic.domain.student.Registration registration,
//            @RequestParam(value = "infered", required = false) boolean infered,
//            @RequestParam(value = "tuitionpaymentplans", required = false) org.fenixedu.academicTreasury.domain.tuition.TuitionPaymentPlan tuitionPaymentPlans,
//            Model model, RedirectAttributes redirectAttributes) {
//        /*
//        *  Creation Logic
//        */
//
//        try {
//
//            TuitionDebtCreationBean tuitionDebtCreationBean =
//                    createTuitionDebtCreationBean(debtDate, executionYear, registration, infered, tuitionPaymentPlans);
//
//            //Success Validation
//            //Add the bean to be used in the View
//            model.addAttribute("tuitionDebtCreationBean", tuitionDebtCreationBean);
//            return redirect("/academictreasury/tuitiondebtcreation/tuitiondebtcreationbean/confirmtuitiondebtcreation/"
//                    + getTuitionDebtCreationBean(model).getExternalId(), model, redirectAttributes);
//        } catch (DomainException de) {
//
//            // @formatter: off
//            /*
//             * If there is any error in validation 
//             *
//             * Add a error / warning message
//             * 
//             * addErrorMessage(BundleUtil.getString(AcademicTreasurySpringConfiguration.BUNDLE, "label.error.create") + de.getLocalizedMessage(),model);
//             * addWarningMessage(" Warning creating due to "+ ex.getLocalizedMessage(),model); */
//            // @formatter: on
//
//            addErrorMessage(
//                    BundleUtil.getString(AcademicTreasurySpringConfiguration.BUNDLE, "label.error.create")
//                            + de.getLocalizedMessage(), model);
//            return create(model);
//        }
//    }
//
//    @Atomic
//    public TuitionDebtCreationBean createTuitionDebtCreationBean(org.joda.time.LocalDate debtDate,
//            org.fenixedu.academic.domain.ExecutionYear executionYear,
//            org.fenixedu.academic.domain.student.Registration registration, boolean infered,
//            org.fenixedu.academicTreasury.domain.tuition.TuitionPaymentPlan tuitionPaymentPlans) {
//
//        // @formatter: off
//
//        /*
//         * Modify the creation code here if you do not want to create
//         * the object with the default constructor and use the setter
//         * for each field
//         * 
//         */
//
//        // CHANGE_ME It's RECOMMENDED to use "Create service" in DomainObject
//        //TuitionDebtCreationBean tuitionDebtCreationBean = tuitionDebtCreationBean.create(fields_to_create);
//
//        //Instead, use individual SETTERS and validate "CheckRules" in the end
//        // @formatter: on
//
//        TuitionDebtCreationBean tuitionDebtCreationBean = new TuitionDebtCreationBean();
//        tuitionDebtCreationBean.setDebtDate(debtDate);
//        tuitionDebtCreationBean.setExecutionYear(executionYear);
//        tuitionDebtCreationBean.setRegistration(registration);
//        tuitionDebtCreationBean.setInfered(infered);
//        tuitionDebtCreationBean.setTuitionPaymentPlans(tuitionPaymentPlans);
//
//        return tuitionDebtCreationBean;
//    }
//
////				
//    private static final String _CONFIRMTUITIONDEBTCREATION_URI = "/confirmtuitiondebtcreation";
//    public static final String CONFIRMTUITIONDEBTCREATION_URL = CONTROLLER_URL + _confirmtuitiondebtcreation_URI;
//
//    @RequestMapping(value = _CONFIRMTUITIONDEBTCREATION_URI, method = RequestMethod.GET)
//    public String confirmtuitiondebtcreation(Model model) {
//        model.addAttribute("TuitionDebtCreationBean_executionYear_options",
//                new ArrayList<org.fenixedu.academic.domain.ExecutionYear>()); // CHANGE_ME - MUST DEFINE RELATION
//        //model.addAttribute("TuitionDebtCreationBean_executionYear_options", org.fenixedu.academic.domain.ExecutionYear.findAll()); // CHANGE_ME - MUST DEFINE RELATION
//        model.addAttribute("TuitionDebtCreationBean_registration_options",
//                new ArrayList<org.fenixedu.academic.domain.student.Registration>()); // CHANGE_ME - MUST DEFINE RELATION
//        //model.addAttribute("TuitionDebtCreationBean_registration_options", org.fenixedu.academic.domain.student.Registration.findAll()); // CHANGE_ME - MUST DEFINE RELATION
//        model.addAttribute("TuitionDebtCreationBean_tuitionPaymentPlans_options",
//                new ArrayList<org.fenixedu.academicTreasury.domain.tuition.TuitionPaymentPlan>()); // CHANGE_ME - MUST DEFINE RELATION
//        //model.addAttribute("TuitionDebtCreationBean_tuitionPaymentPlans_options", org.fenixedu.academicTreasury.domain.tuition.TuitionPaymentPlan.findAll()); // CHANGE_ME - MUST DEFINE RELATION
//
//        //IF ANGULAR, initialize the Bean
//        //TuitionDebtCreationBeanBean bean = new TuitionDebtCreationBeanBean();
//        //this.setTuitionDebtCreationBeanBean(bean, model);
//
//        return "academicTreasury/tuitiondebtcreation/tuitiondebtcreationbean/confirmtuitiondebtcreation";
//    }
//
////
////               THIS SHOULD BE USED ONLY WHEN USING ANGULAR 
////
////						// @formatter: off
////			
////				private static final String _CONFIRMTUITIONDEBTCREATIONPOSTBACK_URI ="/confirmtuitiondebtcreationpostback";
////				public static final String  CONFIRMTUITIONDEBTCREATIONPOSTBACK_URL = CONTROLLER_URL + _confirmtuitiondebtcreationPOSTBACK_URI;
////    			@RequestMapping(value = _CONFIRMTUITIONDEBTCREATIONPOSTBACK_URI, method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
////  			  	public @ResponseBody String confirmtuitiondebtcreationpostback(@RequestParam(value = "bean", required = false) TuitionDebtCreationBeanBean bean,
////            		Model model) {
////
////        			// Do validation logic ?!?!
////        			this.setTuitionDebtCreationBeanBean(bean, model);
////        			return getBeanJson(bean);
////    			}
////    			
////    			@RequestMapping(value = CONFIRMTUITIONDEBTCREATION, method = RequestMethod.POST)
////  			  	public String confirmtuitiondebtcreation(@RequestParam(value = "bean", required = false) TuitionDebtCreationBeanBean bean,
////            		Model model, RedirectAttributes redirectAttributes ) {
////
////					/*
////					*  Creation Logic
////					*/
////					
////					try
////					{
////
////				     	TuitionDebtCreationBean tuitionDebtCreationBean = createTuitionDebtCreationBean(... get properties from bean ...,model);
////				    	
////					//Success Validation
////				     //Add the bean to be used in the View
////					model.addAttribute("tuitionDebtCreationBean",tuitionDebtCreationBean);
////				    return redirect("/academictreasury/tuitiondebtcreation/debtaccount/read/" + getTuitionDebtCreationBean(model).getExternalId(), model, redirectAttributes);
////					}
////					catch (DomainException de)
////					{
////
////						/*
////						 * If there is any error in validation 
////					     *
////					     * Add a error / warning message
////					     * 
////					     * addErrorMessage(BundleUtil.getString(AcademicTreasurySpringConfiguration.BUNDLE, "label.error.create") + de.getLocalizedMessage(),model);
////					     * addWarningMessage(" Warning creating due to "+ ex.getLocalizedMessage(),model); */
////						
////						addErrorMessage(BundleUtil.getString(AcademicTreasurySpringConfiguration.BUNDLE, "label.error.create") + de.getLocalizedMessage(),model);
////				     	return confirmtuitiondebtcreation(model);
////					}
////    			}
////						// @formatter: on
//
////				
//    @RequestMapping(value = _CONFIRMTUITIONDEBTCREATION_URI, method = RequestMethod.POST)
//    public String confirmtuitiondebtcreation(
//            @RequestParam(value = "debtdate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ") org.joda.time.LocalDate debtDate,
//            @RequestParam(value = "executionyear", required = false) org.fenixedu.academic.domain.ExecutionYear executionYear,
//            @RequestParam(value = "registration", required = false) org.fenixedu.academic.domain.student.Registration registration,
//            @RequestParam(value = "infered", required = false) boolean infered,
//            @RequestParam(value = "tuitionpaymentplans", required = false) org.fenixedu.academicTreasury.domain.tuition.TuitionPaymentPlan tuitionPaymentPlans,
//            Model model, RedirectAttributes redirectAttributes) {
//        /*
//        *  Creation Logic
//        */
//
//        try {
//
//            TuitionDebtCreationBean tuitionDebtCreationBean =
//                    createTuitionDebtCreationBean(debtDate, executionYear, registration, infered, tuitionPaymentPlans);
//
//            //Success Validation
//            //Add the bean to be used in the View
//            model.addAttribute("tuitionDebtCreationBean", tuitionDebtCreationBean);
//            return redirect("/academictreasury/tuitiondebtcreation/debtaccount/read/"
//                    + getTuitionDebtCreationBean(model).getExternalId(), model, redirectAttributes);
//        } catch (DomainException de) {
//
//            // @formatter: off
//            /*
//             * If there is any error in validation 
//             *
//             * Add a error / warning message
//             * 
//             * addErrorMessage(BundleUtil.getString(AcademicTreasurySpringConfiguration.BUNDLE, "label.error.create") + de.getLocalizedMessage(),model);
//             * addWarningMessage(" Warning creating due to "+ ex.getLocalizedMessage(),model); */
//            // @formatter: on
//
//            addErrorMessage(
//                    BundleUtil.getString(AcademicTreasurySpringConfiguration.BUNDLE, "label.error.create")
//                            + de.getLocalizedMessage(), model);
//            return confirmtuitiondebtcreation(model);
//        }
//    }
//
//    @Atomic
//    public TuitionDebtCreationBean createTuitionDebtCreationBean(org.joda.time.LocalDate debtDate,
//            org.fenixedu.academic.domain.ExecutionYear executionYear,
//            org.fenixedu.academic.domain.student.Registration registration, boolean infered,
//            org.fenixedu.academicTreasury.domain.tuition.TuitionPaymentPlan tuitionPaymentPlans) {
//
//        // @formatter: off
//
//        /*
//         * Modify the creation code here if you do not want to create
//         * the object with the default constructor and use the setter
//         * for each field
//         * 
//         */
//
//        // CHANGE_ME It's RECOMMENDED to use "Create service" in DomainObject
//        //TuitionDebtCreationBean tuitionDebtCreationBean = tuitionDebtCreationBean.create(fields_to_create);
//
//        //Instead, use individual SETTERS and validate "CheckRules" in the end
//        // @formatter: on
//
//        TuitionDebtCreationBean tuitionDebtCreationBean = new TuitionDebtCreationBean();
//        tuitionDebtCreationBean.setDebtDate(debtDate);
//        tuitionDebtCreationBean.setExecutionYear(executionYear);
//        tuitionDebtCreationBean.setRegistration(registration);
//        tuitionDebtCreationBean.setInfered(infered);
//        tuitionDebtCreationBean.setTuitionPaymentPlans(tuitionPaymentPlans);
//
//        return tuitionDebtCreationBean;
//    }
//}
