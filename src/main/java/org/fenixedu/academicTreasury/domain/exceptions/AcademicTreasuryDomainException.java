package org.fenixedu.academicTreasury.domain.exceptions;

import javax.ws.rs.core.Response.Status;

import org.fenixedu.bennu.FenixeduAcademicTreasurySpringConfiguration;
import org.fenixedu.bennu.core.domain.exceptions.DomainException;

public class AcademicTreasuryDomainException extends DomainException {

    private static final long serialVersionUID = 1L;

    public AcademicTreasuryDomainException(String key, String... args) {
        super(FenixeduAcademicTreasurySpringConfiguration.BUNDLE, key, args);
    }

    public AcademicTreasuryDomainException(Status status, String key, String... args) {
        super(status, FenixeduAcademicTreasurySpringConfiguration.BUNDLE, key, args);
    }

    public AcademicTreasuryDomainException(Throwable cause, String key, String... args) {
        super(cause, FenixeduAcademicTreasurySpringConfiguration.BUNDLE, key, args);
    }

    public AcademicTreasuryDomainException(Throwable cause, Status status, String key, String... args) {
        super(cause, status, FenixeduAcademicTreasurySpringConfiguration.BUNDLE, key, args);
    }
    
}
