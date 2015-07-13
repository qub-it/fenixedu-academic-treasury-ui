package org.fenixedu.academictreasury.domain.listeners;

import org.fenixedu.treasury.domain.document.DebitEntry;

import pt.ist.fenixframework.FenixFramework;
import pt.ist.fenixframework.dml.DeletionListener;

public class DebitEntryDeletionListener {
    
    public static void attach() {
        
        FenixFramework.getDomainModel().registerDeletionListener(DebitEntry.class, new DeletionListener<DebitEntry>() {

            @Override
            public void deleting(final DebitEntry debitEntry) {
                debitEntry.setCurricularCourse(null);
                debitEntry.setExecutionSemester(null);
                debitEntry.setEvaluationSeason(null);
            }
        });
    }
    
}
