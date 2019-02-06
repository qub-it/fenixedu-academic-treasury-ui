package org.fenixedu.academictreasury.ui;

import static org.fenixedu.treasury.util.TreasuryConstants.treasuryBundle;

import org.fenixedu.treasury.domain.FinantialInstitution;
import org.fenixedu.treasury.domain.accesscontrol.TreasuryAccessControl;
import org.fenixedu.treasury.services.accesscontrol.TreasuryAccessControlAPI;
import org.fenixedu.treasury.services.integration.TreasuryPlataformDependentServicesFactory;
import org.fenixedu.treasury.ui.TreasuryBaseController;
import org.springframework.ui.Model;

public class AcademicTreasuryBaseController extends TreasuryBaseController {

    protected void assertUserIsManager(Model model) {
        final String loggedUsername = TreasuryPlataformDependentServicesFactory.implementation().getLoggedUsername();
        
        if (TreasuryAccessControlAPI.isManager(loggedUsername)) {
            return;
        } else {
            addErrorMessage(treasuryBundle("error.authorization.not.manager"), model);
            throw new SecurityException(treasuryBundle("error.authorization.not.manager"));
        }
    }

    protected void assertUserIsBackOfficeMember(Model model) {
        final String loggedUsername = TreasuryPlataformDependentServicesFactory.implementation().getLoggedUsername();

        if (TreasuryAccessControlAPI.isBackOfficeMember(loggedUsername)) {
            return;
        } else {
            addErrorMessage(treasuryBundle("error.authorization.not.backoffice"), model);
            throw new SecurityException(treasuryBundle("error.authorization.not.backoffice"));
        }
    }

    protected void assertUserIsFrontOfficeMember(Model model) {
        final String loggedUsername = TreasuryPlataformDependentServicesFactory.implementation().getLoggedUsername();

        if (TreasuryAccessControlAPI.isFrontOfficeMember(loggedUsername)) {
            return;
        } else {
            addErrorMessage(treasuryBundle("error.authorization.not.frontoffice"), model);
            throw new SecurityException(treasuryBundle("error.authorization.not.frontoffice"));
        }
    }

    protected void assertUserIsBackOfficeMember(FinantialInstitution finantialInstitution, Model model) {
        final String loggedUsername = TreasuryPlataformDependentServicesFactory.implementation().getLoggedUsername();

        if (TreasuryAccessControlAPI.isBackOfficeMember(loggedUsername, finantialInstitution)) {
            return;
        } else {
            addErrorMessage(treasuryBundle("error.authorization.not.backoffice"), model);
            throw new SecurityException(treasuryBundle("error.authorization.not.backoffice"));
        }
    }

    protected void assertUserIsFrontOfficeMember(FinantialInstitution finantialInstitution, Model model) {
        final String loggedUsername = TreasuryPlataformDependentServicesFactory.implementation().getLoggedUsername();

        if (TreasuryAccessControlAPI.isFrontOfficeMember(loggedUsername, finantialInstitution)) {
            return;
        } else {
            addErrorMessage(treasuryBundle("error.authorization.not.frontoffice"), model);
            throw new SecurityException(treasuryBundle("error.authorization.not.frontoffice"));
        }
    }
    
}
