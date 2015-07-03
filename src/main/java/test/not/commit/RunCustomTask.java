package test.not.commit;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.administrativeOffice.AdministrativeOffice;
import org.fenixedu.academic.domain.degree.DegreeType;
import org.fenixedu.academictreasury.domain.tariff.AcademicTariff;
import org.fenixedu.academictreasury.dto.tariff.AcademicTariffBean;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.scheduler.custom.CustomTask;
import org.fenixedu.treasury.domain.FinantialEntity;
import org.fenixedu.treasury.domain.FinantialInstitution;
import org.fenixedu.treasury.domain.Product;
import org.fenixedu.treasury.domain.tariff.DueDateCalculationType;
import org.joda.time.format.DateTimeFormat;

import com.google.common.collect.Maps;

public class RunCustomTask extends CustomTask {

    private static final Map<String, String> FI_LOOKUP = Maps.newHashMap();
    private static final int NOT_APPLIED = -1;

    static {
        FI_LOOKUP.put("FMD", "5777777777");
//        FI_LOOKUP.put("FL", "999999992");
//        FI_LOOKUP.put("FF", "999999993");
//        FI_LOOKUP.put("RUL", "999999994");
//        FI_LOOKUP.put("FMV", "999999995");
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

    @Override
    public void runTask() throws Exception {
        final Locale PT = new Locale("PT", "pt");
        final Locale EN = new Locale("EN", "en");
        
        
    }
}
