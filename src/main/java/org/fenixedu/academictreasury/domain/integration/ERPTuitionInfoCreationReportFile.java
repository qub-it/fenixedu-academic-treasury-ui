package org.fenixedu.academictreasury.domain.integration;

import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.treasury.domain.accesscontrol.TreasuryAccessControl;

import pt.ist.fenixframework.Atomic;

public class ERPTuitionInfoCreationReportFile extends ERPTuitionInfoCreationReportFile_Base {
    
    public ERPTuitionInfoCreationReportFile() {
        super();
        setBennu(Bennu.getInstance());
    }
    
    public ERPTuitionInfoCreationReportFile(final String displayName, final String filename, final byte[] content) {
        this();
        
        init(displayName, filename, content);
    }

    @Override
    public boolean isAccessible(User arg0) {
        return TreasuryAccessControl.getInstance().isBackOfficeMember(arg0);
    }
    
    
    // @formatter:off
    /* ********
     * SERVICES
     * ********
     */
    // @formatter:on
    
    @Atomic
    public static final ERPTuitionInfoCreationReportFile create(final String displayName, final String filename, final byte[] content) {
        return new ERPTuitionInfoCreationReportFile(displayName, filename, content);
    }
    
}
