package org.fenixedu.academictreasury.ui.exemptions.requests;

import org.fenixedu.treasury.dto.ITreasuryBean;
import org.fenixedu.treasury.domain.exemption.TreasuryExemptionType;

public class ExemptionsGenerationRequestFileBean implements ITreasuryBean {

    private TreasuryExemptionType treasuryExemptionType;

    public TreasuryExemptionType getTreasuryExemptionType() {
        return treasuryExemptionType;
    }
    
    public void setTreasuryExemptionType(TreasuryExemptionType treasuryExemptionType) {
        this.treasuryExemptionType = treasuryExemptionType;
    }
    
}
