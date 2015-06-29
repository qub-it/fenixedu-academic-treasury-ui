package test.not.commit;

import java.util.Locale;

import org.fenixedu.academic.domain.serviceRequests.ServiceRequestType;
import org.fenixedu.academic.domain.serviceRequests.ServiceRequestTypeOption;
import org.fenixedu.academic.domain.serviceRequests.documentRequests.AcademicServiceRequestType;
import org.fenixedu.academic.domain.serviceRequests.documentRequests.DocumentRequestType;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.bennu.scheduler.custom.CustomTask;
import org.fenixedu.commons.i18n.LocalizedString;

public class RunCustomTask extends CustomTask {

    @Override
    public void runTask() throws Exception {
        ServiceRequestTypeOption.create(
                "DETAILED",
                BundleUtil.getLocalizedString("resources.AcademicOfficeResources", ServiceRequestTypeOption.class.getSimpleName()
                        + ".detailed"), true);

        for (final AcademicServiceRequestType academicServiceRequestType : AcademicServiceRequestType.values()) {
            if (academicServiceRequestType == AcademicServiceRequestType.DOCUMENT) {
                continue;
            }

            ServiceRequestType.createLegacy(academicServiceRequestType.name(), new LocalizedString(new Locale("PT", "pt"),
                    academicServiceRequestType.getLocalizedName()), academicServiceRequestType, null, true);
        }

        for (final DocumentRequestType documentRequestType : DocumentRequestType.values()) {
            ServiceRequestType.createLegacy(
                    documentRequestType.name(),
                    BundleUtil.getLocalizedString("resources.EnumerationResources",
                            "DocumentRequestType." + documentRequestType.name()), AcademicServiceRequestType.DOCUMENT,
                    documentRequestType, true);
        }
    }

}
