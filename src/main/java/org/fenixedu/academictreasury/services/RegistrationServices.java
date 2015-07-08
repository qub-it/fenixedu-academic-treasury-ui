package org.fenixedu.academictreasury.services;

import java.util.Optional;

import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academictreasury.domain.customer.PersonCustomer;
import org.fenixedu.bennu.signals.DomainObjectEvent;

import pt.ist.fenixframework.Atomic;

import com.google.common.eventbus.Subscribe;

public class RegistrationServices {

    @Atomic
    public static PersonCustomer createPersonCustomer(Person p) {
        Optional<? extends PersonCustomer> findUnique = PersonCustomer.findUnique(p);
        if (findUnique.isPresent()) {
            return findUnique.get();
        }
        return PersonCustomer.create(p);
    }

    @Subscribe
    public void newRegistrationEvent(final DomainObjectEvent<Registration> event) {
        Registration reg = event.getInstance();
        if (reg.getStudent() != null) {
            createPersonCustomer(reg.getStudent().getPerson());
        }
    }
}
