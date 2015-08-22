package org.fenixedu.academictreasury.schoolsbootstrapscript;

import java.util.stream.Collectors;

import org.fenixedu.bennu.scheduler.custom.CustomTask;
import org.fenixedu.treasury.domain.Product;
import org.fenixedu.treasury.domain.VatExemptionReason;

public class SetVatExemptionOnMissingProducts extends CustomTask {

    @Override
    public void runTask() throws Exception {
        for (Product product : Product.findAll().collect(Collectors.toSet())) {
            if(product.getVatExemptionReason() != null) {
                continue;
            }
            
            product.setVatExemptionReason(VatExemptionReason.findByCode("M07"));
        }
    }

}
