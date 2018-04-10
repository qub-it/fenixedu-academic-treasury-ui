package org.fenixedu.academictreasury.schoolsbootstrapscript;

import java.util.stream.Collectors;

import org.fenixedu.bennu.scheduler.custom.CustomTask;
import org.fenixedu.treasury.domain.Product;
import org.fenixedu.treasury.domain.VatExemptionReason;
import org.fenixedu.treasury.domain.VatType;

public class SetVatExemptionOnMissingProducts extends CustomTask {

    @Override
    public void runTask() throws Exception {
        doIt();
        
        throw new RuntimeException("abort");
    }

    private void doIt() {
        for (Product product : Product.findAll().collect(Collectors.toSet())) {
            if(product.getVatExemptionReason() != null) {
                continue;
            }
            
            if(product.getVatType() != VatType.findByCode("ISE")) {
                continue;
            }
            
            taskLog("C\tTAX EXEMPTION\t%s\t%s\t%s\n", product.getCode(), product.getVatType().getCode(), product.getVatExemptionReason());
            
            product.setVatExemptionReason(VatExemptionReason.findByCode("M07"));
            
        }
    }

}
