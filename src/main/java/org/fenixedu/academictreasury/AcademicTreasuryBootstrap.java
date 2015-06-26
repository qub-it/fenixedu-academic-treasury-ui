package org.fenixedu.academictreasury;

import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.organizationalStructure.Party;
import org.fenixedu.academictreasury.domain.customer.PersonCustomer;
import org.fenixedu.bennu.core.domain.Bennu;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

public class AcademicTreasuryBootstrap {

    public static void process() {

        final CustomersPersonThread customersPersonThread = new CustomersPersonThread();
        customersPersonThread.start();

        try {
            customersPersonThread.join();
        } catch (InterruptedException e) {
        }

    }

    private static final class CustomersPersonThread extends Thread {

        @Override
        public void run() {
            createMissingPersonCustomersForStudents();
        }

        @Atomic(mode = TxMode.READ)
        private void createMissingPersonCustomersForStudents() {

            for (Party party : Bennu.getInstance().getPartysSet()) {
                if (!party.isPerson()) {
                    continue;
                }

                final Person person = (Person) party;

                if (person.getStudent() == null) {
                    continue;
                }

                try {
                    createMissingPersonCustomer(person);
                } catch (final Exception e) {
                    e.printStackTrace();
                }
            }

        }

        @Atomic(mode = TxMode.WRITE)
        private void createMissingPersonCustomer(final Person person) {
            if (PersonCustomer.findUnique(person).isPresent()) {
                return;
            }

            PersonCustomer.create(person);
        }

    }

}
