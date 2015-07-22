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
package org.fenixedu.academictreasury.ui.manageregistrationprotocolpaygratuities;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.fenixedu.academic.domain.student.RegistrationProtocol;
import org.fenixedu.academictreasury.ui.AcademicTreasuryBaseController;
import org.fenixedu.academictreasury.ui.AcademicTreasuryController;
import org.fenixedu.bennu.core.domain.exceptions.DomainException;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import pt.ist.fenixframework.Atomic;

//@Component("org.fenixedu.academictreasury.ui.manageRegistrationProtocolPayGratuities") <-- Use for duplicate controller name disambiguation
@SpringFunctionality(app = AcademicTreasuryController.class, title = "label.title.manageRegistrationProtocolPayGratuities",accessGroup = "treasuryManagers")
//@BennuSpringController(value=AcademicTreasuryController.class) 
@RequestMapping(RegistrationProtocolController.CONTROLLER_URL)
public class RegistrationProtocolController extends AcademicTreasuryBaseController {
	
	
		public static final String CONTROLLER_URL ="/academictreasury/manageregistrationprotocolpaygratuities/registrationprotocol"; 
//

				
	
				@RequestMapping
				public String home(Model model) {
					//this is the default behaviour, for handling in a Spring Functionality
					return "forward:" + CONTROLLER_URL + "/";
				}
				
				// @formatter: off
				
				/*
				* This should be used when using AngularJS in the JSP
				*/
				
				//private RegistrationProtocol getRegistrationProtocolBean(Model model)
				//{
				//	return (RegistrationProtocol)model.asMap().get("registrationProtocolBean");
				//}
				//				
				//private void setRegistrationProtocolBean (RegistrationProtocolBean bean, Model model)
				//{
				//	model.addAttribute("registrationProtocolBeanJson", getBeanJson(bean));
        		//	model.addAttribute("registrationProtocolBean", bean);
				//}
								
				// @formatter: on

				private RegistrationProtocol getRegistrationProtocol(Model model)
				{
					return (RegistrationProtocol)model.asMap().get("registrationProtocol");
				}
								
				private void setRegistrationProtocol(RegistrationProtocol registrationProtocol, Model model)
				{
					model.addAttribute("registrationProtocol", registrationProtocol);
				}
								
				@Atomic
				public void deleteRegistrationProtocol(RegistrationProtocol registrationProtocol) {
					// CHANGE_ME: Do the processing for deleting the registrationProtocol
					// Do not catch any exception here
					
					// registrationProtocol.delete();
				}

//				
					private static final String _SEARCH_URI ="/";
					public static final String  SEARCH_URL = CONTROLLER_URL + _SEARCH_URI;
					@RequestMapping(value = _SEARCH_URI)
					public String search( Model model) {
							List<RegistrationProtocol> searchregistrationprotocolResultsDataSet = filterSearchRegistrationProtocol(  );
						
						//add the results dataSet to the model
						model.addAttribute("searchregistrationprotocolResultsDataSet",searchregistrationprotocolResultsDataSet);
						return "academicTreasury/manageregistrationprotocolpaygratuities/registrationprotocol/search";
					}
					
				private Stream<RegistrationProtocol> getSearchUniverseSearchRegistrationProtocolDataSet() {
					//
					//The initialization of the result list must be done here
					//
					//
					// return RegistrationProtocol.findAll(); //CHANGE_ME
					return new ArrayList<RegistrationProtocol>().stream();
				}
				
		private List<RegistrationProtocol> filterSearchRegistrationProtocol() {
			
			return getSearchUniverseSearchRegistrationProtocolDataSet()
				.collect(Collectors.toList());				
		}
		
		
				private static final String _SEARCH_TO_VIEW_ACTION_URI ="/search/view/";
				public static final String  SEARCH_TO_VIEW_ACTION_URL = CONTROLLER_URL + _SEARCH_TO_VIEW_ACTION_URI;
				@RequestMapping(value = _SEARCH_TO_VIEW_ACTION_URI + "{oid}")
				public String processSearchToViewAction(@PathVariable("oid") RegistrationProtocol registrationProtocol,  Model model, RedirectAttributes redirectAttributes) {
		
			// CHANGE_ME Insert code here for processing viewAction
			// If you selected multiple exists you must choose which one to use below	 
		return redirect("/academictreasury/manageregistrationprotocolpaygratuities/registrationprotocol/read" + "/" + registrationProtocol.getExternalId(), model, redirectAttributes);
		}
		
		
		
		
//				
					private static final String _READ_URI ="/read/";
					public static final String  READ_URL = CONTROLLER_URL + _READ_URI;
					@RequestMapping(value = _READ_URI + "{oid}")
					public String read(@PathVariable("oid") RegistrationProtocol registrationProtocol, Model model) {
						setRegistrationProtocol(registrationProtocol,model);
						return "academicTreasury/manageregistrationprotocolpaygratuities/registrationprotocol/read";
					}

//				
				private static final String _UPDATE_URI ="/update/";
				public static final String  UPDATE_URL = CONTROLLER_URL + _UPDATE_URI;
				@RequestMapping(value = _UPDATE_URI + "{oid}", method = RequestMethod.GET)
				public String update(@PathVariable("oid") RegistrationProtocol registrationProtocol, Model model) {
					setRegistrationProtocol(registrationProtocol, model);
					return "academicTreasury/manageregistrationprotocolpaygratuities/registrationprotocol/update";
				}
					 		
//

//               THIS SHOULD BE USED ONLY WHEN USING ANGULAR 
//
//						// @formatter: off
//			
//				private static final String _UPDATEPOSTBACK_URI ="/updatepostback/";
//				public static final String  UPDATEPOSTBACK_URL = CONTROLLER_URL + _updatePOSTBACK_URI;
//    			@RequestMapping(value = _UPDATEPOSTBACK_URI + "{oid}", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
//  			  	public @ResponseBody String updatepostback(@PathVariable("oid") RegistrationProtocol registrationProtocol, @RequestParam(value = "bean", required = false) RegistrationProtocolBean bean,
//            		Model model) {
//
//        			// Do validation logic ?!?!
//        			this.setRegistrationProtocolBean(bean, model);
//        			return getBeanJson(bean);
//    			} 
//    			
//    			@RequestMapping(value = _UPDATE_URI + "{oid}", method = RequestMethod.POST)
//  			  	public String update(@PathVariable("oid") RegistrationProtocol registrationProtocol, @RequestParam(value = "bean", required = false) RegistrationProtocolBean bean,
//            		Model model, RedirectAttributes redirectAttributes ) {
//					setRegistrationProtocol(registrationProtocol,model);
//
//				     try
//				     {
//					/*
//					*  UpdateLogic here
//					*/
//				    		
//						updateRegistrationProtocol( .. get fields from bean..., model);
//
//					/*Succes Update */
//
//				    return redirect("/academictreasury/manageregistrationprotocolpaygratuities/registrationprotocol/read/" + getRegistrationProtocol(model).getExternalId(), model, redirectAttributes);
//					}
//					catch (DomainException de) 
//					{
//				
//						/*
//					 	* If there is any error in validation 
//				     	*
//				     	* Add a error / warning message
//				     	* 
//				     	* addErrorMessage(BundleUtil.getString(AcademictreasurySpringConfiguration.BUNDLE, "label.error.update") + de.getLocalizedMessage(),model);
//				     	* addWarningMessage(" Warning updating due to " + de.getLocalizedMessage(),model);
//				     	*/
//										     
//				     	addErrorMessage(BundleUtil.getString(AcademictreasurySpringConfiguration.BUNDLE, "label.error.update") + de.getLocalizedMessage(),model);
//				     	return update(registrationProtocol,model);
//					 
//
//					}
//				}
//						// @formatter: on    			
//				
				@RequestMapping(value = _UPDATE_URI + "{oid}", method = RequestMethod.POST)
				public String update(@PathVariable("oid") RegistrationProtocol registrationProtocol, @RequestParam(value="description", required=false) 
									org.fenixedu.commons.i18n.LocalizedString 
									  description
				,@RequestParam(value="paygratuity", required=false) 
									java.lang.Boolean 
									  payGratuity
				, Model model, RedirectAttributes redirectAttributes) {
					
					setRegistrationProtocol(registrationProtocol,model);

				     try
				     {
					/*
					*  UpdateLogic here
					*/
				    		
						updateRegistrationProtocol( description ,  payGratuity ,  model);

					/*Succes Update */

return redirect("/academictreasury/manageregistrationprotocolpaygratuities/registrationprotocol/read/" + getRegistrationProtocol(model).getExternalId(), model, redirectAttributes);
					}
					catch (DomainException de) 
					{
						// @formatter: off
				
						/*
					 	* If there is any error in validation 
				     	*
				     	* Add a error / warning message
				     	* 
				     	* addErrorMessage(BundleUtil.getString(AcademictreasurySpringConfiguration.BUNDLE, "label.error.update") + de.getLocalizedMessage(),model);
				     	* addWarningMessage(" Warning updating due to " + de.getLocalizedMessage(),model);
				     	*/
						// @formatter: on
										     
				     	addErrorMessage(de.getLocalizedMessage(), model);
				     	return update(registrationProtocol,model);
					 

					}
				}
				
				@Atomic
				public void updateRegistrationProtocol(  org.fenixedu.commons.i18n.LocalizedString description 
				, java.lang.Boolean payGratuity 
				 ,  Model model) {
	
	// @formatter: off				
						/*
						 * Modify the update code here if you do not want to update
						 * the object with the default setter for each field
						 */

						 // CHANGE_ME It's RECOMMENDED to use "Edit service" in DomainObject
						//getRegistrationProtocol(model).edit(fields_to_edit);
						
						//Instead, use individual SETTERS and validate "CheckRules" in the end
	// @formatter: on
	
						 getRegistrationProtocol(model).setPayGratuity(payGratuity);
				}
				
}
