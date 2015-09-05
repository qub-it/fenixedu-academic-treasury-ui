package org.fenixedu.academictreasury.schoolsbootstrapscript;

import java.util.stream.Collectors;

import org.fenixedu.bennu.scheduler.custom.CustomTask;
import org.fenixedu.treasury.domain.document.FinantialDocument;

import com.google.common.base.Strings;

public class MoveTemporaryDocumentOriginNumberToDocumentObservations extends CustomTask {

    @Override
    public void runTask() throws Exception {
        int count = 0;
        
        for (final FinantialDocument finantialDocument : FinantialDocument.findAll().collect(Collectors.<FinantialDocument> toSet())) {
            if(Strings.isNullOrEmpty(finantialDocument.getOriginDocumentNumber())) {
                continue;
            }
            
            if(!finantialDocument.getOriginDocumentNumber().contains("_")) {
                continue;
            }
            
            getLogger().info(String.format("Change %s ", finantialDocument.getOriginDocumentNumber()));
            
            finantialDocument.setDocumentObservations(finantialDocument.getOriginDocumentNumber());
            finantialDocument.setOriginDocumentNumber("");
            
            count++;
        }
        
        getLogger().info(String.format("Changed %d/%d", count, FinantialDocument.findAll().count()));
    }

}
