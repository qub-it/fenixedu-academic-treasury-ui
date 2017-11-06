package org.fenixedu.academictreasury.domain.integration.tuitioninfo.exceptions;

import javax.ws.rs.core.Response.Status;

import org.fenixedu.academictreasury.domain.exceptions.AcademicTreasuryDomainException;

public class ERPTuitionInfoNoDifferencesException extends AcademicTreasuryDomainException {

    private static final long serialVersionUID = 1L;

    public ERPTuitionInfoNoDifferencesException(String key, String... args) {
        super(key, args);
    }

    public ERPTuitionInfoNoDifferencesException(Status status, String key, String... args) {
        super(status, key, args);
    }

    public ERPTuitionInfoNoDifferencesException(Throwable cause, String key, String... args) {
        super(cause, key, args);
    }

    public ERPTuitionInfoNoDifferencesException(Throwable cause, Status status, String key, String... args) {
        super(cause, status, key, args);
    }
    
}
