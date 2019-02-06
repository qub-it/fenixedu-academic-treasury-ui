package org.fenixedu.academictreasury.domain.exceptions;

import javax.ws.rs.core.Response.Status;

import org.fenixedu.academictreasury.util.Constants;
import org.fenixedu.bennu.core.domain.exceptions.DomainException;

public class AcademicTreasuryDomainException extends DomainException {

    private static final long serialVersionUID = 1L;

    public AcademicTreasuryDomainException(String key, String... args) {
        super(Constants.BUNDLE.replace('.', '/'), key, args);
    }

    public AcademicTreasuryDomainException(Status status, String key, String... args) {
        super(status, Constants.BUNDLE.replace('.', '/'), key, args);
    }

    public AcademicTreasuryDomainException(Throwable cause, String key, String... args) {
        super(cause, Constants.BUNDLE.replace('.', '/'), key, args);
    }

    public AcademicTreasuryDomainException(Throwable cause, Status status, String key, String... args) {
        super(cause, status, Constants.BUNDLE.replace('.', '/'), key, args);
    }
    
}
