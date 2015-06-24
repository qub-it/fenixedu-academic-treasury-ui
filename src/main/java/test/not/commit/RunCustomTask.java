package test.not.commit;

import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.groups.DynamicGroup;
import org.fenixedu.bennu.scheduler.custom.CustomTask;

public class RunCustomTask extends CustomTask {

    private static final String TREASURY_MANAGERS = "treasuryManagers";

    private static final String TREASURY_BACK_OFFICE = "treasuryBackOffice";

    private static final String TREASURY_FRONT_OFFICE = "treasuryFrontOffice";


    @Override
    public void runTask() throws Exception {
        DynamicGroup.get(TREASURY_FRONT_OFFICE).mutator().grant(User.findByUsername("manager"));
        DynamicGroup.get(TREASURY_BACK_OFFICE).mutator().grant(User.findByUsername("manager"));
        DynamicGroup.get(TREASURY_MANAGERS).mutator().grant(User.findByUsername("manager"));
    }

}
