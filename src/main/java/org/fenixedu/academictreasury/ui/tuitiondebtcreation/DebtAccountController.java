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
//package org.fenixedu.academicTreasury.ui.tuitiondebtcreation;
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
//import org.fenixedu.treasury.domain.debt.DebtAccount;
//
////@Component("org.fenixedu.academicTreasury.ui.TuitionDebtCreation") <-- Use for duplicate controller name disambiguation
////@SpringFunctionality(app = AcademicTreasuryController.class, title = "label.title.TuitionDebtCreation",accessGroup = "logged")// CHANGE_ME accessGroup = "group1 | group2 | groupXPTO"
////or
//@BennuSpringController(value=AcademicTreasuryController.class) 
//@RequestMapping(DebtAccountController.CONTROLLER_URL)
//public class DebtAccountController extends AcademicTreasuryBaseController {
//	
//	
//		public static final String CONTROLLER_URL ="/academictreasury/tuitiondebtcreation/debtaccount"; 
////
//
//				
//	
//				@RequestMapping
//				public String home(Model model) {
//					//this is the default behaviour, for handling in a Spring Functionality
//					return "forward:" + CONTROLLER_URL + "/";
//				}
//				
//				// @formatter: off
//				
//				/*
////				* This should be used when using AngularJS in the JSP
//				*/
//				
//				//private DebtAccount getDebtAccountBean(Model model)
//				//{
//				//	return (DebtAccount)model.asMap().get("debtAccountBean");
//				//}
//				//				
//				//private void setDebtAccountBean (DebtAccountBean bean, Model model)
//				//{
//				//	model.addAttribute("debtAccountBeanJson", getBeanJson(bean));
//        		//	model.addAttribute("debtAccountBean", bean);
//				//}
//								
//				// @formatter: on
//
//				private DebtAccount getDebtAccount(Model model)
//				{
//					return (DebtAccount)model.asMap().get("debtAccount");
//				}
//								
//				private void setDebtAccount(DebtAccount debtAccount, Model model)
//				{
//					model.addAttribute("debtAccount", debtAccount);
//				}
//								
//				@Atomic
//				public void deleteDebtAccount(DebtAccount debtAccount) {
//					// CHANGE_ME: Do the processing for deleting the debtAccount
//					// Do not catch any exception here
//					
//					// debtAccount.delete();
//				}
//
////				
//					private static final String _READ_URI ="/read/";
//					public static final String  READ_URL = CONTROLLER_URL + _READ_URI;
//					@RequestMapping(value = _READ_URI + "{oid}")
//					public String read(@PathVariable("oid") DebtAccount debtAccount, Model model) {
//						setDebtAccount(debtAccount,model);
//						return "academicTreasury/tuitiondebtcreation/debtaccount/read";
//					}
//
////
//
//				//
//				// This is the EventtuitionDebtCreation Method for Screen read
//				//
//		@RequestMapping(value = "/read/{oid}/tuitiondebtcreation")
//		public String processReadToTuitionDebtCreation(@PathVariable("oid") DebtAccount debtAccount, Model model, RedirectAttributes redirectAttributes)
//		{
//				setDebtAccount(debtAccount,model);
////
//						/* Put here the logic for processing Event tuitionDebtCreation 	*/
//					 	//doSomething();
//				 
//					// Now choose what is the Exit Screen	 
//			return redirect("/academictreasury/tuitiondebtcreation/tuitiondebtcreationbean/create/"+ getDebtAccount(model).getExternalId(), model, redirectAttributes);
//				}
//				  
//}
