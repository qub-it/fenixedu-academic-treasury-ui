package test.not.commit;

import java.util.stream.Collectors;

import org.fenixedu.academictreasury.domain.customer.PersonCustomer;
import org.fenixedu.bennu.scheduler.custom.CustomTask;
import org.fenixedu.treasury.domain.FinantialInstitution;
import org.fenixedu.treasury.domain.debt.DebtAccount;

public class RunCustomTask extends CustomTask {

    @Override
    public void runTask() throws Exception {
        
        for (FinantialInstitution finantialInstitution : FinantialInstitution.findAll().collect(Collectors.toSet())) {
            for (final PersonCustomer personCustomer : PersonCustomer.findAll().collect(Collectors.<PersonCustomer> toSet())) {
                DebtAccount.create(finantialInstitution, personCustomer);
            }
        }
        
    }
}
