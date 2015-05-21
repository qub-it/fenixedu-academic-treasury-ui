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
//package org.fenixedu.academictreasury.ui.managetuitionpaymentplan;
//
//import org.fenixedu.academic.domain.DegreeCurricularPlan;
//import org.fenixedu.academic.domain.ExecutionYear;
//import org.fenixedu.academictreasury.domain.settings.AcademicTreasurySettings;
//import org.fenixedu.academictreasury.domain.tuition.TuitionPaymentPlan;
//import org.fenixedu.academictreasury.domain.tuition.TuitionPaymentPlanGroup;
//import org.fenixedu.academictreasury.ui.AcademicTreasuryBaseController;
//import org.fenixedu.academictreasury.ui.AcademicTreasuryController;
//import org.fenixedu.bennu.FenixeduAcademicTreasurySpringConfiguration;
//import org.fenixedu.bennu.core.domain.exceptions.DomainException;
//import org.fenixedu.bennu.core.i18n.BundleUtil;
//import org.fenixedu.bennu.spring.portal.BennuSpringController;
//import org.fenixedu.treasury.domain.FinantialEntity;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.servlet.mvc.support.RedirectAttributes;
//
//import pt.ist.fenixframework.Atomic;
//
////@Component("org.fenixedu.academicTreasury.ui.manageTuitionPaymentPlan") <-- Use for duplicate controller name disambiguation
////@SpringFunctionality(app = AcademicTreasuryController.class, title = "label.title.manageTuitionPaymentPlan",accessGroup = "logged")// CHANGE_ME accessGroup = "group1 | group2 | groupXPTO"
////or
//@BennuSpringController(value = AcademicTreasuryController.class)
//@RequestMapping(TuitionPaymentPlanController.CONTROLLER_URL)
//public class TuitionPaymentPlanController extends AcademicTreasuryBaseController {
//
//    public static final String CONTROLLER_URL = "/academictreasury/managetuitionpaymentplan/tuitionpaymentplan";
//    private static final String JSP_PAGE = "academicTreasury/managetuitionpaymentplan/tuitionpaymentplan";
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
//    //private TuitionPaymentPlan getTuitionPaymentPlanBean(Model model)
//    //{
//    //	return (TuitionPaymentPlan)model.asMap().get("tuitionPaymentPlanBean");
//    //}
//    //				
//    //private void setTuitionPaymentPlanBean (TuitionPaymentPlanBean bean, Model model)
//    //{
//    //	model.addAttribute("tuitionPaymentPlanBeanJson", getBeanJson(bean));
//    //	model.addAttribute("tuitionPaymentPlanBean", bean);
//    //}
//
//    // @formatter: on
//
//    private TuitionPaymentPlan getTuitionPaymentPlan(Model model) {
//        return (TuitionPaymentPlan) model.asMap().get("tuitionPaymentPlan");
//    }
//
//    private void setTuitionPaymentPlan(TuitionPaymentPlan tuitionPaymentPlan, Model model) {
//        model.addAttribute("tuitionPaymentPlan", tuitionPaymentPlan);
//    }
//
//    @Atomic
//    public void deleteTuitionPaymentPlan(TuitionPaymentPlan tuitionPaymentPlan) {
//        // CHANGE_ME: Do the processing for deleting the tuitionPaymentPlan
//        // Do not catch any exception here
//
//        // tuitionPaymentPlan.delete();
//    }
//
////				
//    private static final String _SEARCH_URI = "/";
//    public static final String SEARCH_URL = CONTROLLER_URL + _SEARCH_URI;
//
//    @RequestMapping(value = _SEARCH_URI + "/{finantialEntityId}/{executionYearId}/{degreeCurricularPlanId")
//    public String search(@PathVariable("finantialEntityId") final FinantialEntity finantialEntity,
//            @PathVariable("executionYearId") final ExecutionYear executionYear,
//            @PathVariable("degreeCurricularPlanId") final DegreeCurricularPlan degreeCurricularPlan, final Model model) {
//
//        final TuitionPaymentPlanGroup tuitionGroup =
//                AcademicTreasurySettings.getInstance().getTuitionPaymentPlanGroupForRegistration();
//        
//        model.addAttribute("searchtuitionpaymentplanResultsDataSet",
//                TuitionPaymentPlan.find(tuitionGroup, finantialEntity, executionYear, degreeCurricularPlan));
//
//        return jspPage("search");
//    }
//
//    private static final String _CREATECHOOSEDEGREECURRICULARPLANS_URI = "/createchoosedegreecurricularplans";
//    public static final String CREATECHOOSEDEGREECURRICULARPLANS_URL = CONTROLLER_URL + _CREATECHOOSEDEGREECURRICULARPLANS_URI;
//
//    @RequestMapping(value = _CREATECHOOSEDEGREECURRICULARPLANS_URI, method = RequestMethod.GET)
//    public String createchoosedegreecurricularplans(Model model) {
//
//        //IF ANGULAR, initialize the Bean
//        //TuitionPaymentPlanBean bean = new TuitionPaymentPlanBean();
//        //this.setTuitionPaymentPlanBean(bean, model);
//
//        return "academicTreasury/managetuitionpaymentplan/tuitionpaymentplan/createchoosedegreecurricularplans";
//    }
//
////
////               THIS SHOULD BE USED ONLY WHEN USING ANGULAR 
////
////						// @formatter: off
////			
////				private static final String _CREATECHOOSEDEGREECURRICULARPLANSPOSTBACK_URI ="/createchoosedegreecurricularplanspostback";
////				public static final String  CREATECHOOSEDEGREECURRICULARPLANSPOSTBACK_URL = CONTROLLER_URL + _createchoosedegreecurricularplansPOSTBACK_URI;
////    			@RequestMapping(value = _CREATECHOOSEDEGREECURRICULARPLANSPOSTBACK_URI, method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
////  			  	public @ResponseBody String createchoosedegreecurricularplanspostback(@RequestParam(value = "bean", required = false) TuitionPaymentPlanBean bean,
////            		Model model) {
////
////        			// Do validation logic ?!?!
////        			this.setTuitionPaymentPlanBean(bean, model);
////        			return getBeanJson(bean);
////    			}
////    			
////    			@RequestMapping(value = CREATECHOOSEDEGREECURRICULARPLANS, method = RequestMethod.POST)
////  			  	public String createchoosedegreecurricularplans(@RequestParam(value = "bean", required = false) TuitionPaymentPlanBean bean,
////            		Model model, RedirectAttributes redirectAttributes ) {
////
////					/*
////					*  Creation Logic
////					*/
////					
////					try
////					{
////
////				     	TuitionPaymentPlan tuitionPaymentPlan = createTuitionPaymentPlan(... get properties from bean ...,model);
////				    	
////					//Success Validation
////				     //Add the bean to be used in the View
////					model.addAttribute("tuitionPaymentPlan",tuitionPaymentPlan);
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
////				     	return createchoosedegreecurricularplans(model);
////					}
////    			}
////						// @formatter: on
//
////				
//    @RequestMapping(value = _CREATECHOOSEDEGREECURRICULARPLANS_URI, method = RequestMethod.POST)
//    public String createchoosedegreecurricularplans(Model model, RedirectAttributes redirectAttributes) {
//        /*
//        *  Creation Logic
//        */
//
//        try {
//
//            TuitionPaymentPlan tuitionPaymentPlan = createTuitionPaymentPlan();
//
//            //Success Validation
//            //Add the bean to be used in the View
//            model.addAttribute("tuitionPaymentPlan", tuitionPaymentPlan);
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
//                    BundleUtil.getString(FenixeduAcademicTreasurySpringConfiguration.BUNDLE, "label.error.create")
//                            + de.getLocalizedMessage(), model);
//            return createchoosedegreecurricularplans(model);
//        }
//    }
//
//    //
//    // This is the Eventchoose Method for Screen createchoosedegreecurricularplans
//    //
//    private static final String _CREATECHOOSEDEGREECURRICULARPLANS_TO_CHOOSE_URI = "/createchoosedegreecurricularplans/choose";
//    public static final String CREATECHOOSEDEGREECURRICULARPLANS_TO_CHOOSE_URL = CONTROLLER_URL
//            + _CREATECHOOSEDEGREECURRICULARPLANS_TO_CHOOSE_URI;
//
//    @RequestMapping(value = _CREATECHOOSEDEGREECURRICULARPLANS_TO_CHOOSE_URI)
//    public String processCreatechoosedegreecurricularplansToChoose(Model model) {
////
//        /* Put here the logic for processing Event choose 	*/
//        //doSomething();
//
//        // Now choose what is the Exit Screen	 
//        return redirect("/academictreasury/managetuitionpaymentplan/tuitionpaymentplan/createdefinestudentconditions", model,
//                redirectAttributes);
//    }
//
////				
//    private static final String _CREATEDEFINESTUDENTCONDITIONS_URI = "/createdefinestudentconditions";
//    public static final String CREATEDEFINESTUDENTCONDITIONS_URL = CONTROLLER_URL + _createdefinestudentconditions_URI;
//
//    @RequestMapping(value = _CREATEDEFINESTUDENTCONDITIONS_URI, method = RequestMethod.GET)
//    public String createdefinestudentconditions(Model model) {
//
//        //IF ANGULAR, initialize the Bean
//        //TuitionPaymentPlanBean bean = new TuitionPaymentPlanBean();
//        //this.setTuitionPaymentPlanBean(bean, model);
//
//        return "academicTreasury/managetuitionpaymentplan/tuitionpaymentplan/createdefinestudentconditions";
//    }
//
////
////               THIS SHOULD BE USED ONLY WHEN USING ANGULAR 
////
////						// @formatter: off
////			
////				private static final String _CREATEDEFINESTUDENTCONDITIONSPOSTBACK_URI ="/createdefinestudentconditionspostback";
////				public static final String  CREATEDEFINESTUDENTCONDITIONSPOSTBACK_URL = CONTROLLER_URL + _createdefinestudentconditionsPOSTBACK_URI;
////    			@RequestMapping(value = _CREATEDEFINESTUDENTCONDITIONSPOSTBACK_URI, method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
////  			  	public @ResponseBody String createdefinestudentconditionspostback(@RequestParam(value = "bean", required = false) TuitionPaymentPlanBean bean,
////            		Model model) {
////
////        			// Do validation logic ?!?!
////        			this.setTuitionPaymentPlanBean(bean, model);
////        			return getBeanJson(bean);
////    			}
////    			
////    			@RequestMapping(value = CREATEDEFINESTUDENTCONDITIONS, method = RequestMethod.POST)
////  			  	public String createdefinestudentconditions(@RequestParam(value = "bean", required = false) TuitionPaymentPlanBean bean,
////            		Model model, RedirectAttributes redirectAttributes ) {
////
////					/*
////					*  Creation Logic
////					*/
////					
////					try
////					{
////
////				     	TuitionPaymentPlan tuitionPaymentPlan = createTuitionPaymentPlan(... get properties from bean ...,model);
////				    	
////					//Success Validation
////				     //Add the bean to be used in the View
////					model.addAttribute("tuitionPaymentPlan",tuitionPaymentPlan);
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
////				     	return createdefinestudentconditions(model);
////					}
////    			}
////						// @formatter: on
//
////				
//    @RequestMapping(value = _CREATEDEFINESTUDENTCONDITIONS_URI, method = RequestMethod.POST)
//    public String createdefinestudentconditions(Model model, RedirectAttributes redirectAttributes) {
//        /*
//        *  Creation Logic
//        */
//
//        try {
//
//            TuitionPaymentPlan tuitionPaymentPlan = createTuitionPaymentPlan();
//
//            //Success Validation
//            //Add the bean to be used in the View
//            model.addAttribute("tuitionPaymentPlan", tuitionPaymentPlan);
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
//            return createdefinestudentconditions(model);
//        }
//    }
//
////  
//
//    //
//    // This is the Eventdefine Method for Screen createdefinestudentconditions
//    //
//    private static final String _CREATEDEFINESTUDENTCONDITIONS_TO_DEFINE_URI = "/createdefinestudentconditions/define";
//    public static final String CREATEDEFINESTUDENTCONDITIONS_TO_DEFINE_URL = CONTROLLER_URL
//            + _CREATEDEFINESTUDENTCONDITIONS_TO_DEFINE_URI;
//
//    @RequestMapping(value = _CREATEDEFINESTUDENTCONDITIONS_TO_DEFINE_URI)
//    public String processCreatedefinestudentconditionsToDefine(Model model) {
////
//        /* Put here the logic for processing Event define 	*/
//        //doSomething();
//
//        // Now choose what is the Exit Screen	 
//        return redirect("/academictreasury/managetuitionpaymentplan/tuitionpaymentplan/createinsertinstallments", model,
//                redirectAttributes);
//    }
//
////				
//    private static final String _CREATEINSERTINSTALLMENTS_URI = "/createinsertinstallments";
//    public static final String CREATEINSERTINSTALLMENTS_URL = CONTROLLER_URL + _createinsertinstallments_URI;
//
//    @RequestMapping(value = _CREATEINSERTINSTALLMENTS_URI, method = RequestMethod.GET)
//    public String createinsertinstallments(Model model) {
//
//        //IF ANGULAR, initialize the Bean
//        //TuitionPaymentPlanBean bean = new TuitionPaymentPlanBean();
//        //this.setTuitionPaymentPlanBean(bean, model);
//
//        return "academicTreasury/managetuitionpaymentplan/tuitionpaymentplan/createinsertinstallments";
//    }
//
////
////               THIS SHOULD BE USED ONLY WHEN USING ANGULAR 
////
////						// @formatter: off
////			
////				private static final String _CREATEINSERTINSTALLMENTSPOSTBACK_URI ="/createinsertinstallmentspostback";
////				public static final String  CREATEINSERTINSTALLMENTSPOSTBACK_URL = CONTROLLER_URL + _createinsertinstallmentsPOSTBACK_URI;
////    			@RequestMapping(value = _CREATEINSERTINSTALLMENTSPOSTBACK_URI, method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
////  			  	public @ResponseBody String createinsertinstallmentspostback(@RequestParam(value = "bean", required = false) TuitionPaymentPlanBean bean,
////            		Model model) {
////
////        			// Do validation logic ?!?!
////        			this.setTuitionPaymentPlanBean(bean, model);
////        			return getBeanJson(bean);
////    			}
////    			
////    			@RequestMapping(value = CREATEINSERTINSTALLMENTS, method = RequestMethod.POST)
////  			  	public String createinsertinstallments(@RequestParam(value = "bean", required = false) TuitionPaymentPlanBean bean,
////            		Model model, RedirectAttributes redirectAttributes ) {
////
////					/*
////					*  Creation Logic
////					*/
////					
////					try
////					{
////
////				     	TuitionPaymentPlan tuitionPaymentPlan = createTuitionPaymentPlan(... get properties from bean ...,model);
////				    	
////					//Success Validation
////				     //Add the bean to be used in the View
////					model.addAttribute("tuitionPaymentPlan",tuitionPaymentPlan);
////				    return redirect("/academictreasury/managetuitionpaymentplan/tuitionpaymentplan//" + getTuitionPaymentPlan(model).getExternalId(), model, redirectAttributes);
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
////				     	return createinsertinstallments(model);
////					}
////    			}
////						// @formatter: on
//
////				
//    @RequestMapping(value = _CREATEINSERTINSTALLMENTS_URI, method = RequestMethod.POST)
//    public String createinsertinstallments(Model model, RedirectAttributes redirectAttributes) {
//        /*
//        *  Creation Logic
//        */
//
//        try {
//
//            TuitionPaymentPlan tuitionPaymentPlan = createTuitionPaymentPlan();
//
//            //Success Validation
//            //Add the bean to be used in the View
//            model.addAttribute("tuitionPaymentPlan", tuitionPaymentPlan);
//            return redirect("/academictreasury/managetuitionpaymentplan/tuitionpaymentplan//"
//                    + getTuitionPaymentPlan(model).getExternalId(), model, redirectAttributes);
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
//            return createinsertinstallments(model);
//        }
//    }
//
//    @Atomic
//    public TuitionPaymentPlan createTuitionPaymentPlan() {
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
//        //TuitionPaymentPlan tuitionPaymentPlan = tuitionPaymentPlan.create(fields_to_create);
//
//        //Instead, use individual SETTERS and validate "CheckRules" in the end
//        // @formatter: on
//
//        TuitionPaymentPlan tuitionPaymentPlan = new TuitionPaymentPlan();
//
//        return tuitionPaymentPlan;
//    }
//
//    private String jspPage(final String page) {
//        return JSP_PATH + "/" + page;
//    }
//
//}
