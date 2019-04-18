package org.fenixedu.academictreasury.domain.exceptions;

import javax.ws.rs.core.Response.Status;

import org.fenixedu.academictreasury.util.AcademicTreasuryConstants;
import org.fenixedu.bennu.core.domain.exceptions.DomainException;

public class AcademicTreasuryDomainException extends DomainException {

    private static final long serialVersionUID = 1L;

    public AcademicTreasuryDomainException(String key, String... args) {
        super(AcademicTreasuryConstants.BUNDLE.replace('.', '/'), key, args);
    }

    public AcademicTreasuryDomainException(Status status, String key, String... args) {
        super(status, AcademicTreasuryConstants.BUNDLE.replace('.', '/'), key, args);
    }

    public AcademicTreasuryDomainException(Throwable cause, String key, String... args) {
        super(cause, AcademicTreasuryConstants.BUNDLE.replace('.', '/'), key, args);
    }

    public AcademicTreasuryDomainException(Throwable cause, Status status, String key, String... args) {
        super(cause, status, AcademicTreasuryConstants.BUNDLE.replace('.', '/'), key, args);
    }
    
}
