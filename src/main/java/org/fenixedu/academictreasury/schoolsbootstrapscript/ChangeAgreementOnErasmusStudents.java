package org.fenixedu.academictreasury.schoolsbootstrapscript;

import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.student.RegistrationProtocol;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.scheduler.custom.CustomTask;

import pt.ist.fenixframework.FenixFramework;

public class ChangeAgreementOnErasmusStudents extends CustomTask {

    @Override
    public void runTask() throws Exception {
        int count = 0;
        for (final Registration registration : Bennu.getInstance().getRegistrationsSet()) {
            if(!registration.isActive()) {
                continue;
            }
            
            if(!registration.getIngressionType().getCode().equals("15")) {
                continue;
            }
            
            getLogger().info(String.format("Change student %d -> '%s' to Erasmus agreement ", 
                    registration.getNumber(), registration.getStudent().getName()));
            
            registration.setRegistrationProtocol((RegistrationProtocol) FenixFramework.getDomainObject("282836481343490"));
            count++;
        }
        
        getLogger().info(String.format("Modified %d/%d", count, Bennu.getInstance().getRegistrationsSet().size()));
    }
}
