package org.fenixedu.academictreasury.services.reports.dataproviders;

import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.treasury.services.reports.dataproviders.AbstractDataProvider;

import com.qubit.terra.docs.util.IDocumentFieldsData;
import com.qubit.terra.docs.util.IFieldsExporter;
import com.qubit.terra.docs.util.IReportDataProvider;

public class RegistrationDataProvider extends AbstractDataProvider implements IReportDataProvider {

    protected static final String REGISTRATION_KEY = "registration";

    private Registration registration;

    public RegistrationDataProvider(final Registration registration) {
        this.registration = registration;
        registerKey(REGISTRATION_KEY, RegistrationDataProvider::handleRegistrationKey);
    }

    private static Object handleRegistrationKey(IReportDataProvider provider) {
        RegistrationDataProvider regisProvider = (RegistrationDataProvider) provider;
        return regisProvider.registration;
    }

    @Override
    public void registerFieldsAndImages(IDocumentFieldsData arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void registerFieldsMetadata(IFieldsExporter arg0) {
        // TODO Auto-generated method stub

    }

}
