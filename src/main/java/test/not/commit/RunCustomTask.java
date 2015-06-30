package test.not.commit;

import org.fenixedu.academic.domain.administrativeOffice.AdministrativeOffice;
import org.fenixedu.academic.domain.administrativeOffice.AdministrativeOfficeType;
import org.fenixedu.academic.domain.util.email.UnitBasedSender;
import org.fenixedu.bennu.scheduler.custom.CustomTask;

public class RunCustomTask extends CustomTask {

    @Override
    public void runTask() throws Exception {
        UnitBasedSender.newInstance(AdministrativeOffice.readByAdministrativeOfficeType(AdministrativeOfficeType.DEGREE).getUnit());
    }

}
