package org.fenixedu.academictreasury.domain.reports;

import static org.fenixedu.academictreasury.util.Constants.academicTreasuryBundle;

import org.fenixedu.academictreasury.domain.exceptions.AcademicTreasuryDomainException;
import org.fenixedu.academictreasury.util.Constants;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.treasury.services.accesscontrol.TreasuryAccessControlAPI;
import org.joda.time.DateTime;

import pt.ist.fenixframework.FenixFramework;

public class DebtReportRequestResultErrorsFile extends DebtReportRequestResultErrorsFile_Base {

    protected DebtReportRequestResultErrorsFile(final DebtReportRequest request, final byte[] content) {
        super();

        setBennu(Bennu.getInstance());
        setDebtReportRequest(request);

        final String filename =
                academicTreasuryBundle("label.DebtReportRequestResultErrorsFile.filename", new DateTime().toString("YYYYMMddHHmmss"));

        init(filename, filename, content);
        checkRules();
    }

    private void checkRules() {
        if (getDebtReportRequest() == null) {
            throw new AcademicTreasuryDomainException("error.DebtReportRequestResultErrorsFile.debtReportRequest.required");
        }
    }

    @Override
    public boolean isAccessible(final User user) {
        return TreasuryAccessControlAPI.isBackOfficeMember(user);
    }

    public static DebtReportRequestResultErrorsFile create(final DebtReportRequest request, final byte[] content) {
        return new DebtReportRequestResultErrorsFile(request, content);
    }

}
