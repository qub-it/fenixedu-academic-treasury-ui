package org.fenixedu.academictreasury.domain.debtGeneration.requests;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.StudentCurricularPlan;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.student.Student;
import org.fenixedu.academictreasury.domain.emoluments.AcademicTax;
import org.fenixedu.academictreasury.domain.exceptions.AcademicTreasuryDomainException;
import org.fenixedu.academictreasury.domain.tuition.TuitionPaymentPlan;
import org.fenixedu.academictreasury.domain.tuition.TuitionPaymentPlanGroup;
import org.fenixedu.academictreasury.services.AcademicTaxServices;
import org.fenixedu.academictreasury.services.TuitionServices;
import org.fenixedu.academictreasury.util.ExcelUtils;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.domain.exceptions.DomainException;
import org.fenixedu.treasury.services.accesscontrol.TreasuryAccessControlAPI;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import pt.ist.fenixframework.Atomic;

public class MassiveDebtGenerationRequestFile extends MassiveDebtGenerationRequestFile_Base {

    public static final Comparator<MassiveDebtGenerationRequestFile> COMPARE_BY_CREATION_DATE =
            new Comparator<MassiveDebtGenerationRequestFile>() {

                @Override
                public int compare(final MassiveDebtGenerationRequestFile o1, final MassiveDebtGenerationRequestFile o2) {
                    int c = o1.getVersioningCreationDate().compareTo(o2.getVersioningCreationDate());

                    return c != 0 ? c : o1.getExternalId().compareTo(o2.getExternalId());
                }
            };

    private static final int MAX_COLS = 5;

    private static final int STUDENT_NUMBER_IDX = 0;
    private static final int STUDENT_NAME_IDX = 1;
    private static final int DEGREE_IDX = 2;
    private static final int DCP_IDX = 3;
    private static final int TUITION_PLAN_IDX = 4;

    protected MassiveDebtGenerationRequestFile() {
        super();

        setBennu(Bennu.getInstance());
    }

    protected MassiveDebtGenerationRequestFile(final TuitionPaymentPlanGroup tuitionPaymentPlanGroup,
            final AcademicTax academicTax, final ExecutionYear executionYear, final LocalDate debtDate, final String filename,
            final byte[] content) {
        this();

        init(filename, filename, content);

        setTuitionPaymentPlanGroup(tuitionPaymentPlanGroup);
        setAcademicTax(academicTax);
        setExecutionYear(executionYear);
        setDebtDate(debtDate);

        checkRules();
    }

    private void checkRules() {
        if (getExecutionYear() == null) {
            throw new AcademicTreasuryDomainException("error.MassiveDebtGenerationRequestFile.executionYear.required");
        }

        if (getTuitionPaymentPlanGroup() == null && getAcademicTax() == null) {
            throw new AcademicTreasuryDomainException(
                    "error.MassiveDebtGenerationRequestFile.tuitionPaymentPlanGroup.or.academic.tax.required");
        }

        if (getTuitionPaymentPlanGroup() != null && getAcademicTax() != null) {
            throw new AcademicTreasuryDomainException(
                    "error.MassiveDebtGenerationRequestFile.tuitionPaymentPlanGroup.and.academic.tax.specified");
        }

        if (getDebtDate() == null) {
            throw new AcademicTreasuryDomainException("error.MassiveDebtGenerationRequestFile.debtDate.required");
        }
    }

    @Atomic
    public void process() {
        List<MassiveDebtGenerationRowResult> rowResult =
                readExcel(getContent(), getTuitionPaymentPlanGroup(), getAcademicTax(), getExecutionYear(), getDebtDate());

        for (final MassiveDebtGenerationRowResult row : rowResult) {
            try {
                if (getTuitionPaymentPlanGroup() != null) {
                    boolean createdTuition =
                            TuitionServices.createTuitionForRegistration(row.getStudentCurricularPlan().getRegistration(),
                                    getExecutionYear(), getDebtDate(), true, row.getTuitionPaymentPlan());

                    if (!createdTuition) {
                        final Integer registrationNumber = row.getStudentCurricularPlan().getRegistration().getNumber();
                        final String studentName = row.getStudentCurricularPlan().getRegistration().getStudent().getName();
                        final String tuitionPaymentPlanName = row.getTuitionPaymentPlan().getConditionsDescription().getContent();

                        throw new AcademicTreasuryDomainException("error.MassiveDebtGenerationRequestFile.tuition.not.created",
                                String.valueOf(registrationNumber), studentName, tuitionPaymentPlanName);
                    }
                } else if (getAcademicTax() != null) {
                    boolean createdAcademicTax =
                            AcademicTaxServices.createAcademicTax(row.getStudentCurricularPlan().getRegistration(),
                                    getExecutionYear(), getAcademicTax(), getDebtDate(), true);

                    if (!createdAcademicTax) {
                        final Integer registrationNumber = row.getStudentCurricularPlan().getRegistration().getNumber();
                        final String studentName = row.getStudentCurricularPlan().getRegistration().getStudent().getName();

                        throw new AcademicTreasuryDomainException(
                                "error.MassiveDebtGenerationRequestFile.academicTax.not.created",
                                String.valueOf(registrationNumber), studentName);
                    }
                }
            } catch (final DomainException e) {
                throw new AcademicTreasuryDomainException("error.MassiveDebtGenerationRequestFile.debt.not.created",
                        String.valueOf(row.getRowNum()), String.valueOf(row.getStudentCurricularPlan().getRegistration().getNumber()),
                        row.getStudentCurricularPlan().getRegistration().getName(), e.getLocalizedMessage());
            }
        }

        setWhenProcessed(new DateTime());
    }

    @Override
    public boolean isAccessible(final User user) {
        return TreasuryAccessControlAPI.isBackOfficeMember(user);
    }

    // @formatter:off
    /* ********
     * SERVICES
     * ********
     */
    // @formatter:on

    public static Stream<MassiveDebtGenerationRequestFile> findAll() {
        return Bennu.getInstance().getMassiveDebtGenerationRequestFilesSet().stream();
    }

    @Atomic
    public static MassiveDebtGenerationRequestFile create(final TuitionPaymentPlanGroup tuitionPaymentPlanGroup,
            final AcademicTax academicTax, final ExecutionYear executionYear, final LocalDate debtDate, final String filename,
            final byte[] content) {
        return new MassiveDebtGenerationRequestFile(tuitionPaymentPlanGroup, academicTax, executionYear, debtDate, filename,
                content);
    }

    public static List<MassiveDebtGenerationRowResult> readExcel(final byte[] content,
            final TuitionPaymentPlanGroup tuitionPaymentPlanGroup, final AcademicTax academicTax,
            final ExecutionYear executionYear, final LocalDate debtDate) {
        try {

            if (executionYear == null) {
                throw new AcademicTreasuryDomainException("error.MassiveDebtGenerationRequestFile.executionYear.required");
            }

            if (tuitionPaymentPlanGroup == null && academicTax == null) {
                throw new AcademicTreasuryDomainException(
                        "error.MassiveDebtGenerationRequestFile.tuitionPaymentPlanGroup.or.academic.tax.required");
            }

            if (tuitionPaymentPlanGroup != null && academicTax != null) {
                throw new AcademicTreasuryDomainException(
                        "error.MassiveDebtGenerationRequestFile.tuitionPaymentPlanGroup.and.academic.tax.specified");
            }

            if (debtDate == null) {
                throw new AcademicTreasuryDomainException("error.MassiveDebtGenerationRequestFile.debtDate.required");
            }

            List<List<String>> data = ExcelUtils.readExcel(new ByteArrayInputStream(content), MAX_COLS);

            final List<MassiveDebtGenerationRowResult> result = Lists.newArrayList();

            int rowNum = 0;
            for (final List<String> row : data) {
                rowNum++;
                if (rowNum == 1) {
                    continue;
                }

                if (Strings.isNullOrEmpty(row.get(STUDENT_NUMBER_IDX))) {
                    throw new AcademicTreasuryDomainException("error.MassiveDebtGenerationRequest.student.number.invalid",
                            String.valueOf(rowNum));
                }

                if (Strings.isNullOrEmpty(row.get(STUDENT_NAME_IDX))) {
                    throw new AcademicTreasuryDomainException("error.MassiveDebtGenerationRequest.student.name.invalid",
                            String.valueOf(rowNum));
                }

                if (Strings.isNullOrEmpty(row.get(DEGREE_IDX))) {
                    throw new AcademicTreasuryDomainException("error.MassiveDebtGenerationRequest.degree.code.invalid",
                            String.valueOf(rowNum));
                }

                int registrationNumber = -1;
                try {
                    registrationNumber = Integer.parseInt(row.get(STUDENT_NUMBER_IDX).trim());
                } catch (final NumberFormatException e) {
                    throw new AcademicTreasuryDomainException("error.MassiveDebtGenerationRequest.student.number.invalid",
                            String.valueOf(rowNum));
                }

                final String studentName = row.get(STUDENT_NAME_IDX).trim();
                final String degreeCode = row.get(DEGREE_IDX).trim();
                final String dcpName = row.get(DCP_IDX).trim();
                final String tuitionPaymentPlanName = row.get(TUITION_PLAN_IDX) != null ? row.get(TUITION_PLAN_IDX).trim() : null;

                final Degree degree = Degree.find(degreeCode);

                if (degree == null) {
                    throw new AcademicTreasuryDomainException("error.MassiveDebtGenerationRequest.degree.not.found",
                            String.valueOf(rowNum));
                }

                final Registration registration =
                        findActiveRegistration(executionYear, registrationNumber, degree, dcpName, rowNum);

                if (registration == null) {
                    throw new AcademicTreasuryDomainException("error.MassiveDebtGenerationRequest.registration.not.found",
                            String.valueOf(rowNum));
                }

                if (!registration.getStudent().getName().equals(studentName)) {
                    throw new AcademicTreasuryDomainException("error.MassiveDebtGenerationRequest.student.name.not.equal",
                            String.valueOf(rowNum));
                }

                StudentCurricularPlan studentCurricularPlan = null;
                if (!Strings.isNullOrEmpty(dcpName)) {
                    studentCurricularPlan = registration.getStudentCurricularPlan(executionYear);
                } else {
                    studentCurricularPlan = registration.getLastStudentCurricularPlan();
                }

                if (!Strings.isNullOrEmpty(dcpName) && !studentCurricularPlan.getName().equals(dcpName)) {
                    throw new AcademicTreasuryDomainException("error.MassiveDebtGenerationRequest.dcp.name.not.equal",
                            String.valueOf(rowNum));
                }

                if (tuitionPaymentPlanGroup != null) {
                    TuitionPaymentPlan tuitionPaymentPlan = TuitionPaymentPlan
                            .find(tuitionPaymentPlanGroup, studentCurricularPlan.getDegreeCurricularPlan(), executionYear)
                            .filter(t -> t.getConditionsDescription().getContent().equals(tuitionPaymentPlanName)).findFirst()
                            .orElse(null);

                    if (tuitionPaymentPlan == null) {
                        throw new AcademicTreasuryDomainException(
                                "error.MassiveDebtGenerationRequest.tuition.payment.plan.not.found", String.valueOf(rowNum),
                                studentCurricularPlan.getName(), tuitionPaymentPlanName);
                    }

                    result.add(new MassiveDebtGenerationRowResult(rowNum, executionYear, studentCurricularPlan, tuitionPaymentPlan,
                            debtDate));
                } else if (academicTax != null) {
                    result.add(new MassiveDebtGenerationRowResult(rowNum, executionYear, studentCurricularPlan, academicTax, debtDate));
                } else {
                    throw new RuntimeException("error");
                }

            }

            return result;

        } catch (final IOException e) {
            throw new AcademicTreasuryDomainException("error.MassiveDebtGenerationRequest.invalid.sheet");
        }
    }

    private static Registration findActiveRegistration(final ExecutionYear executionYear, final int registrationNumber,
            final Degree degree, final String dcpName, final int rowNum) {
        Registration result = null;
        for (final Registration registration : Registration.readByNumber(registrationNumber)) {
            if (registration.getLastStateType() == null && !registration.getLastStateType().isActive()) {
                continue;
            }

            if (registration.getDegree() != degree) {
                continue;
            }

            if (!Strings.isNullOrEmpty(dcpName) && registration.getStudentCurricularPlan(executionYear) == null) {
                continue;
            } else if (Strings.isNullOrEmpty(dcpName) && registration.getLastStudentCurricularPlan() == null) {
                continue;
            }

            if (!Strings.isNullOrEmpty(dcpName)
                    && !registration.getStudentCurricularPlan(executionYear).getName().equals(dcpName)) {
                continue;
            }

            if (result != null) {
                throw new AcademicTreasuryDomainException("error.MassiveDebtGenerationRequest.found.more.than.one.registration",
                        String.valueOf(rowNum));
            }

            result = registration;
        }

        return result;
    }

}
