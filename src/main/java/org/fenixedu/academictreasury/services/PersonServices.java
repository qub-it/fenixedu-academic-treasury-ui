package org.fenixedu.academictreasury.services;

import java.util.Optional;

import org.fenixedu.academic.domain.Person;
import org.fenixedu.academictreasury.domain.academicalAct.AcademicActBlockingSuspension;
import org.fenixedu.academictreasury.domain.customer.PersonCustomer;
import org.fenixedu.academictreasury.domain.event.AcademicTreasuryEvent;
import org.fenixedu.bennu.signals.DomainObjectEvent;
import org.joda.time.LocalDate;

import pt.ist.fenixframework.Atomic;

import com.google.common.eventbus.Subscribe;

public class PersonServices {

    @Atomic
    public static PersonCustomer createPersonCustomer(Person p) {
        Optional<? extends PersonCustomer> findUnique = PersonCustomer.findUnique(p);
        if (findUnique.isPresent()) {
            return findUnique.get();
        }
        return PersonCustomer.create(p);
    }

    public static boolean isAcademicalActsBlocked(final Person person, final LocalDate when) {
        if (!PersonCustomer.findUnique(person).isPresent()) {
            return false;
        }

        if (AcademicActBlockingSuspension.isBlockingSuspended(person, when)) {
            return false;
        }

        return AcademicTreasuryEvent.find(PersonCustomer.findUnique(person).get()).filter(l -> l.isBlockingAcademicalActs(when))
                .count() > 0;
    }

    @Subscribe
    public void newPersonEvent(final DomainObjectEvent<Person> event) {
        createPersonCustomer(event.getInstance());
    }
}
