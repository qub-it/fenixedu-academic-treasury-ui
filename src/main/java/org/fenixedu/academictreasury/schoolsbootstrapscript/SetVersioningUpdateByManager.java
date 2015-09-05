package org.fenixedu.academictreasury.schoolsbootstrapscript;

import org.fenixedu.bennu.scheduler.custom.CustomTask;
import org.fenixedu.treasury.domain.document.FinantialDocumentEntry;

public class SetVersioningUpdateByManager extends CustomTask {

    @Override
    public void runTask() throws Exception {
        
        FinantialDocumentEntry.findAll().forEach(l -> l.setVersioningCreator("qubIT_admin"));
        FinantialDocumentEntry.findAll().forEach(l -> l.setVersioningUpdatedBy("qubIT_admin"));
        
    }

}
