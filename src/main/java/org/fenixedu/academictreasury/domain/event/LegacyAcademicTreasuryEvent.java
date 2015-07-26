package org.fenixedu.academictreasury.domain.event;

import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.treasury.domain.Product;
import org.fenixedu.treasury.domain.debt.DebtAccount;

public class LegacyAcademicTreasuryEvent extends LegacyAcademicTreasuryEvent_Base {

    protected LegacyAcademicTreasuryEvent(DebtAccount debtAccount, Product product, Degree degree, ExecutionYear executionYear) {
        super();
        this.setDebtAccount(debtAccount);
        this.setProduct(product);
        this.setExecutionYear(executionYear);
        this.setDegree(degree);
    }

    public static LegacyAcademicTreasuryEvent create(final DebtAccount debtAccount, final Product product, final Degree degree,
            final ExecutionYear executionYear) {
        return new LegacyAcademicTreasuryEvent(debtAccount, product, degree, executionYear);
    }

    @Override
    public boolean isAcademicServiceRequestEvent() {
        return false;
    }

    @Override
    public boolean isAcademicTax() {
        return false;
    }

    @Override
    public boolean isForAcademicServiceRequest() {
        return false;
    }

    @Override
    public boolean isForExtracurricularTuition() {
        return false;
    }

    @Override
    public boolean isForAcademicTax() {
        return false;
    }

    @Override
    public boolean isForImprovementTax() {
        return false;
    }

    @Override
    public boolean isForRegistrationTuition() {
        return false;
    }

    @Override
    public boolean isForStandaloneTuition() {
        return false;
    }

    @Override
    public boolean isImprovementTax() {
        return false;
    }

    @Override
    public boolean isLegacy() {
        return true;
    }

    @Override
    public boolean isUrgentRequest() {
        return false;
    }

    @Override
    public boolean isTuitionEvent() {
        return false;
    }

}
