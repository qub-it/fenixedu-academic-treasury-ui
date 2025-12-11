package org.fenixedu.academictreasury.ui;

import org.fenixedu.treasury.domain.FinantialInstitution;
import org.fenixedu.treasury.services.accesscontrol.TreasuryAccessControlAPI;
import org.fenixedu.treasury.ui.TreasuryBaseController;
import org.fenixedu.treasury.util.TreasuryConstants;
import org.springframework.ui.Model;

import static org.fenixedu.treasury.util.TreasuryConstants.treasuryBundle;

public class AcademicTreasuryBaseController extends TreasuryBaseController {

    protected void assertUserIsManager(Model model) {
        final String loggedUsername = TreasuryConstants.getAuthenticatedUsername();
        
        if (TreasuryAccessControlAPI.isManager(loggedUsername)) {
            return;
        } else {
            addErrorMessage(treasuryBundle("error.authorization.not.manager"), model);
            throw new SecurityException(treasuryBundle("error.authorization.not.manager"));
        }
    }

    protected void assertUserIsBackOfficeMember(Model model) {
        final String loggedUsername = TreasuryConstants.getAuthenticatedUsername();

        if (TreasuryAccessControlAPI.isBackOfficeMember(loggedUsername)) {
            return;
        } else {
            addErrorMessage(treasuryBundle("error.authorization.not.backoffice"), model);
            throw new SecurityException(treasuryBundle("error.authorization.not.backoffice"));
        }
    }

    protected void assertUserIsFrontOfficeMember(Model model) {
        final String loggedUsername = TreasuryConstants.getAuthenticatedUsername();

        if (TreasuryAccessControlAPI.isFrontOfficeMember(loggedUsername)) {
            return;
        } else {
            addErrorMessage(treasuryBundle("error.authorization.not.frontoffice"), model);
            throw new SecurityException(treasuryBundle("error.authorization.not.frontoffice"));
        }
    }

    protected void assertUserIsBackOfficeMember(FinantialInstitution finantialInstitution, Model model) {
        final String loggedUsername = TreasuryConstants.getAuthenticatedUsername();

        if (TreasuryAccessControlAPI.isBackOfficeMember(loggedUsername, finantialInstitution)) {
            return;
        } else {
            addErrorMessage(treasuryBundle("error.authorization.not.backoffice"), model);
            throw new SecurityException(treasuryBundle("error.authorization.not.backoffice"));
        }
    }

    protected void assertUserIsFrontOfficeMember(FinantialInstitution finantialInstitution, Model model) {
        final String loggedUsername = TreasuryConstants.getAuthenticatedUsername();

        if (TreasuryAccessControlAPI.isFrontOfficeMember(loggedUsername, finantialInstitution)) {
            return;
        } else {
            addErrorMessage(treasuryBundle("error.authorization.not.frontoffice"), model);
            throw new SecurityException(treasuryBundle("error.authorization.not.frontoffice"));
        }
    }
    
}
