package org.fenixedu.academictreasury.domain.debtGeneration;

import java.util.Comparator;

import org.fenixedu.academictreasury.domain.exceptions.AcademicTreasuryDomainException;
import org.fenixedu.bennu.core.domain.Bennu;
import org.joda.time.DateTime;

public class AcademicDebtGenerationLog extends AcademicDebtGenerationLog_Base {

    public static Comparator<AcademicDebtGenerationLog> COMPARATOR_BY_CREATION_DATE =
            new Comparator<AcademicDebtGenerationLog>() {

                @Override
                public int compare(AcademicDebtGenerationLog o1, AcademicDebtGenerationLog o2) {
                    if (o1.getCreationDate().isBefore(o2.getCreationDate())) {
                        return -1;
                    }
                    if (o1.getCreationDate().isEqual(o2.getCreationDate())) {
                        return 0;
                    }
                    return 1;
                }
            };

    protected AcademicDebtGenerationLog(final AcademicDebtGenerationRule rule, final String log, final DateTime creationDate) {
        super();

        super.setBennu(Bennu.getInstance());
        super.setAcademicDebtGenerationRule(rule);
        super.setLog(log);
        super.setCreationDate(creationDate);

        checkRules();
    }

    private void checkRules() {
        if (getAcademicDebtGenerationRule() == null) {
            throw new AcademicTreasuryDomainException("error.AcademicDebtGenerationLog.academicDebtGenerationRule.required");
        }
    }

    public static AcademicDebtGenerationLog create(final AcademicDebtGenerationRule rule, final String log,
            final DateTime creationDate) {
        return new AcademicDebtGenerationLog(rule, log, creationDate);
    }

}
