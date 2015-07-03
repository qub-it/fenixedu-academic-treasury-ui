package org.fenixedu.academictreasury.schoolsbootstrapscript;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.administrativeOffice.AdministrativeOffice;
import org.fenixedu.academic.domain.degree.DegreeType;
import org.fenixedu.academic.domain.serviceRequests.ServiceRequestType;
import org.fenixedu.academic.domain.serviceRequests.ServiceRequestTypeOption;
import org.fenixedu.academic.domain.serviceRequests.documentRequests.AcademicServiceRequestType;
import org.fenixedu.academic.domain.serviceRequests.documentRequests.DocumentRequestType;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.bennu.scheduler.custom.CustomTask;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.treasury.domain.FinantialEntity;
import org.fenixedu.treasury.domain.FinantialInstitution;
import org.joda.time.format.DateTimeFormat;

import com.google.common.collect.Maps;

public class SchoolsBootstrapCustomTask extends CustomTask {

    
    private static final Map<String, String> FI_LOOKUP = Maps.newHashMap();
    private static final int NOT_APPLIED = -1;

    static {
        FI_LOOKUP.put("FMD", "5777777777");
//        FI_LOOKUP.put("FL", "999999992");
//        FI_LOOKUP.put("FF", "999999993");
//        FI_LOOKUP.put("RUL", "999999994");
//        FI_LOOKUP.put("FMV", "999999995");
    }

    @Override
    public void runTask() throws Exception {
        createDefaultServiceRequestTypes();
        
        createProducts();
        
        configureAcademicTreasurySettings();
        
        createTuitionPaymentPlanGroups();
        
        createAcademicTaxes();
        
        createServiceRequestTypesToProducts();
        
        
        createEmolumentTariffs();
        
        
        createTuitionForRegistrationTariffs();
        
        createStandaloneTariffs();
        
        createExtracurricularTariffs();
        
        
    }
    
    private List<FinantialInstitution> fromAcronymsToFinantialInstitutionList(final String acronyms) {
        String[] split = acronyms.split(",");

        final List<FinantialInstitution> result = new ArrayList<FinantialInstitution>();
        for (String acronym : split) {
            if (!FI_LOOKUP.containsKey(acronym.trim())) {
                continue;
            }

            result.add(FinantialInstitution.findUniqueByFiscalCode(FI_LOOKUP.get(acronym.trim())).get());
        }

        return result;
    }

    private FinantialEntity oneOfFinantialEntity(final String acronyms) {
        return FinantialEntity.findAll()
                .filter(l -> l.getAdministrativeOffice() == AdministrativeOffice.readDegreeAdministrativeOffice()).findFirst()
                .get();
    }

    private DegreeType findDegreeTypeByCode(final String code) {
        if (code.isEmpty()) {
            return null;
        }

        for (DegreeType degreeType : Bennu.getInstance().getDegreeTypeSet()) {
            if (code.equals(degreeType.getCode())) {
                return degreeType;
            }
        }

        return null;
    }

    private Degree findDegree(final String code) {
        if (code.isEmpty()) {
            return null;
        }

        return Degree.find(code);
    }

    private BigDecimal maximumAmount(int v) {
        if (v == NOT_APPLIED) {
            return BigDecimal.ZERO;
        }

        return new BigDecimal(v);
    }

    private org.joda.time.LocalDate fixedDueDate(final String v) {
        if (v.isEmpty()) {
            return null;
        }

        return DateTimeFormat.forPattern("dd/MM/yyyy").parseLocalDate(v);
    }

    private org.joda.time.LocalDate parseLocalDate(final String v) {
        if (v.isEmpty()) {
            return null;
        }

        return DateTimeFormat.forPattern("dd/MM/yyyy").parseLocalDate(v);
    }
    
    private Locale pt() {
        return new Locale("PT", "pt");
    }
    
    private Locale en() {
        return new Locale("EN", "en");
    }

    
    
    
    
    private static void createDefaultServiceRequestTypes() {
        ServiceRequestTypeOption.create(
                "DETAILED",
                BundleUtil.getLocalizedString("resources.AcademicAdminOffice", ServiceRequestTypeOption.class.getSimpleName()
                        + ".detailed"), true);

        for (final AcademicServiceRequestType academicServiceRequestType : AcademicServiceRequestType.values()) {
            if (academicServiceRequestType == AcademicServiceRequestType.DOCUMENT) {
                continue;
            } else if (academicServiceRequestType == AcademicServiceRequestType.DIPLOMA_REQUEST) {
                ServiceRequestType.createLegacy(academicServiceRequestType.name(), new LocalizedString(new Locale("PT", "pt"),
                        academicServiceRequestType.getLocalizedName()), academicServiceRequestType,
                        DocumentRequestType.DIPLOMA_REQUEST, true);
                continue;
            } else if (academicServiceRequestType == AcademicServiceRequestType.DIPLOMA_SUPPLEMENT_REQUEST) {
                ServiceRequestType.createLegacy(academicServiceRequestType.name(), new LocalizedString(new Locale("PT", "pt"),
                        academicServiceRequestType.getLocalizedName()), academicServiceRequestType,
                        DocumentRequestType.DIPLOMA_SUPPLEMENT_REQUEST, true);
                continue;
            } else if (academicServiceRequestType == AcademicServiceRequestType.REGISTRY_DIPLOMA_REQUEST) {
                ServiceRequestType.createLegacy(academicServiceRequestType.name(), new LocalizedString(new Locale("PT", "pt"),
                        academicServiceRequestType.getLocalizedName()), academicServiceRequestType,
                        DocumentRequestType.REGISTRY_DIPLOMA_REQUEST, true);
                continue;
            }

            ServiceRequestType.createLegacy(academicServiceRequestType.name(), new LocalizedString(new Locale("PT", "pt"),
                    academicServiceRequestType.getLocalizedName()), academicServiceRequestType, null, true);
        }

        for (final DocumentRequestType documentRequestType : DocumentRequestType.values()) {

            if (documentRequestType == DocumentRequestType.DIPLOMA_REQUEST) {
                continue;
            } else if (documentRequestType == DocumentRequestType.DIPLOMA_SUPPLEMENT_REQUEST) {
                continue;
            } else if (documentRequestType == DocumentRequestType.REGISTRY_DIPLOMA_REQUEST) {
                continue;
            }
            
            ServiceRequestType.createLegacy(
                    documentRequestType.name(),
                    BundleUtil.getLocalizedString("resources.EnumerationResources",
                            "DocumentRequestType." + documentRequestType.name()), AcademicServiceRequestType.DOCUMENT,
                    documentRequestType, true);
        }
    }
    
    
    private void createProducts() {
        // TODO Auto-generated method stub
        
    }

    
}
