package test.not.commit;

import org.fenixedu.academic.domain.administrativeOffice.AdministrativeOffice;
import org.fenixedu.academic.domain.util.email.UnitBasedSender;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.groups.NobodyGroup;
import org.fenixedu.bennu.scheduler.custom.CustomTask;
import org.fenixedu.treasury.domain.AdhocCustomer;
import org.fenixedu.treasury.domain.Customer;

public class RunCustomTask extends CustomTask {

    @Override
    public void runTask() throws Exception {
        new UnitBasedSender(AdministrativeOffice.readDegreeAdministrativeOffice().getUnit(), "noemail@noemail.z", NobodyGroup.get());
    }
}
