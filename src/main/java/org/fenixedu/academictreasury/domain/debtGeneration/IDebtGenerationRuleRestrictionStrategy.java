package org.fenixedu.academictreasury.domain.debtGeneration;

import org.fenixedu.academic.domain.student.Registration;

public interface IDebtGenerationRuleRestrictionStrategy {
    
    public boolean isToApply(final AcademicDebtGenerationRule rule, final Registration registration);
    
}
