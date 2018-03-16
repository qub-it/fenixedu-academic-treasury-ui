package org.fenixedu.academictreasury.domain.integration.tuitioninfo.exceptions;

import javax.ws.rs.core.Response.Status;

import org.fenixedu.academictreasury.domain.exceptions.AcademicTreasuryDomainException;

public class ERPTuitionInfoPendingException extends AcademicTreasuryDomainException {

    private static final long serialVersionUID = 1L;

    public ERPTuitionInfoPendingException(String key, String... args) {
        super(key, args);
    }

    public ERPTuitionInfoPendingException(Status status, String key, String... args) {
        super(status, key, args);
    }

    public ERPTuitionInfoPendingException(Throwable cause, String key, String... args) {
        super(cause, key, args);
    }

    public ERPTuitionInfoPendingException(Throwable cause, Status status, String key, String... args) {
        super(cause, status, key, args);
    }
    
}
