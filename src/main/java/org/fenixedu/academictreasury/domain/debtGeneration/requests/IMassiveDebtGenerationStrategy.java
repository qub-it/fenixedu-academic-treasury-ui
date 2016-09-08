package org.fenixedu.academictreasury.domain.debtGeneration.requests;

import java.util.List;

public interface IMassiveDebtGenerationStrategy<T> {

    public void checkRules(final MassiveDebtGenerationRequestFile file);

    public void process(final MassiveDebtGenerationRequestFile file);

    public List<T> readExcel(final byte[] content, final MassiveDebtGenerationRequestFileBean bean);

    public boolean isExecutionYearRequired();

    public boolean isForAcademicTaxRequired();

    public boolean isDebtDateRequired();

    public boolean isReasonRequired();
    
    public boolean isFinantialInstitutionRequired();

    public String viewUrl();

    public String dataDescription(final MassiveDebtGenerationRequestFile file);

}
