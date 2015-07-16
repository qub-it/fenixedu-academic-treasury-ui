package org.fenixedu.academictreasury.domain.debtGeneration;

import org.fenixedu.academictreasury.domain.exceptions.AcademicTreasuryDomainException;
import org.fenixedu.bennu.core.domain.Bennu;

public class AcademicDebtGenerationLog extends AcademicDebtGenerationLog_Base {
    
    protected AcademicDebtGenerationLog(final AcademicDebtGenerationRule rule, final String log) {
        super();
        
        super.setBennu(Bennu.getInstance());
        super.setAcademicDebtGenerationRule(rule);
        super.setLog(log);
        
        checkRules();
    }

    private void checkRules() {
        if(getAcademicDebtGenerationRule() == null) {
            throw new AcademicTreasuryDomainException("error.AcademicDebtGenerationLog.academicDebtGenerationRule.required");
        }
    }
    
    public static AcademicDebtGenerationLog create(final AcademicDebtGenerationRule rule, final String log) {
        return new AcademicDebtGenerationLog(rule, log);
    }
    
}
