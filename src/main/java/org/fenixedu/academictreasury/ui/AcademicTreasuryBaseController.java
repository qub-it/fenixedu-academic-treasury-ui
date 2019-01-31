package org.fenixedu.academictreasury.ui;

import static org.fenixedu.treasury.util.TreasuryConstants.treasuryBundle;

import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.treasury.domain.FinantialInstitution;
import org.fenixedu.treasury.domain.accesscontrol.TreasuryAccessControl;
import org.fenixedu.treasury.ui.TreasuryBaseController;
import org.fenixedu.treasury.util.TreasuryConstants;
import org.springframework.ui.Model;

public class AcademicTreasuryBaseController extends TreasuryBaseController {

    protected void assertUserIsManager(Model model) {
        if (TreasuryAccessControl.getInstance().isManager(Authenticate.getUser())) {
            return;
        } else {
            addErrorMessage(treasuryBundle("error.authorization.not.manager"), model);
            throw new SecurityException(treasuryBundle("error.authorization.not.manager"));
        }
    }

    protected void assertUserIsBackOfficeMember(Model model) {
        if (TreasuryAccessControl.getInstance().isBackOfficeMember(Authenticate.getUser())) {
            return;
        } else {
            addErrorMessage(treasuryBundle("error.authorization.not.backoffice"), model);
            throw new SecurityException(treasuryBundle("error.authorization.not.backoffice"));
        }
    }

    protected void assertUserIsFrontOfficeMember(Model model) {
        if (TreasuryAccessControl.getInstance().isFrontOfficeMember(Authenticate.getUser())) {
            return;
        } else {
            addErrorMessage(treasuryBundle("error.authorization.not.frontoffice"), model);
            throw new SecurityException(treasuryBundle("error.authorization.not.frontoffice"));
        }
    }

    protected void assertUserIsBackOfficeMember(FinantialInstitution finantialInstitution, Model model) {
        if (TreasuryAccessControl.getInstance().isBackOfficeMember(Authenticate.getUser(), finantialInstitution)) {
            return;
        } else {
            addErrorMessage(treasuryBundle("error.authorization.not.backoffice"), model);
            throw new SecurityException(treasuryBundle("error.authorization.not.backoffice"));
        }
    }

    protected void assertUserIsFrontOfficeMember(FinantialInstitution finantialInstitution, Model model) {
        if (TreasuryAccessControl.getInstance().isFrontOfficeMember(Authenticate.getUser(), finantialInstitution)) {
            return;
        } else {
            addErrorMessage(treasuryBundle("error.authorization.not.frontoffice"), model);
            throw new SecurityException(treasuryBundle("error.authorization.not.frontoffice"));
        }
    }
}
