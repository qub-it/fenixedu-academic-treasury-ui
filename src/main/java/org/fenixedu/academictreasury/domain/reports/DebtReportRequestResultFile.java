package org.fenixedu.academictreasury.domain.reports;

import org.fenixedu.academictreasury.domain.exceptions.AcademicTreasuryDomainException;
import org.fenixedu.academictreasury.util.Constants;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.treasury.services.accesscontrol.TreasuryAccessControlAPI;
import org.joda.time.DateTime;

public class DebtReportRequestResultFile extends DebtReportRequestResultFile_Base {

    protected DebtReportRequestResultFile(final DebtReportRequest request, final byte[] content) {
        super();

        setBennu(Bennu.getInstance());
        setDebtReportRequest(request);

        final String filename =
                Constants.bundle(String.format("label.DebtReportRequestResultFile.%s.filename", 
                        request.getType().name()), new DateTime().toString("YYYYMMddHHmmss"));

        init(filename, filename, content);
        checkRules();
    }

    private void checkRules() {
        if(getDebtReportRequest() == null) {
            throw new AcademicTreasuryDomainException("error.DebtReportRequestResultFile.debtReportRequest.required");
        }
    }

    @Override
    public boolean isAccessible(final User user) {
        return TreasuryAccessControlAPI.isBackOfficeMember(user);
    }

    public static DebtReportRequestResultFile create(final DebtReportRequest request, final byte[] content) {
        return new DebtReportRequestResultFile(request, content);
    }
}
