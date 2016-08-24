package org.fenixedu.academictreasury.ui.exemptions.requests;

import org.fenixedu.bennu.IBean;
import org.fenixedu.treasury.domain.exemption.TreasuryExemptionType;

public class ExemptionsGenerationRequestFileBean implements IBean {

    private TreasuryExemptionType treasuryExemptionType;

    public TreasuryExemptionType getTreasuryExemptionType() {
        return treasuryExemptionType;
    }
    
    public void setTreasuryExemptionType(TreasuryExemptionType treasuryExemptionType) {
        this.treasuryExemptionType = treasuryExemptionType;
    }
    
}
