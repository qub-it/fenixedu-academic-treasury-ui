package org.fenixedu.academictreasury.services;

import org.fenixedu.academic.domain.Person;
import org.fenixedu.academictreasury.domain.customer.PersonCustomer;
import org.fenixedu.bennu.signals.DomainObjectEvent;

import pt.ist.fenixframework.Atomic;

import com.google.common.eventbus.Subscribe;

public class PersonServices {

    @Atomic
    public static PersonCustomer createPersonCustomer(Person p) {
        PersonCustomer result;
        result = PersonCustomer.create(p);

        //Do not create the DEbtAccout for now...
//        for (FinantialInstitution fin : Bennu.getInstance().getFi) {
//            FinantialInstitution finInst = fin.getImportedObject();
//            DebtAccount debtAccount = DebtAccount.findUnique(finInst, customer).orElse(null);
//            if (debtAccount == null) {
//                debtAccount = DebtAccount.create(finInst, customer);
//            }
//        }
//
        return result;
    }

    @Subscribe
    public void newPersonEvent(final DomainObjectEvent<Person> event) {
        createPersonCustomer(event.getInstance());
    }
}
