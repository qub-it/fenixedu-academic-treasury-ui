package org.fenixedu.academictreasury.domain.tuition.importation;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.poi.ss.formula.functions.Rows;
import org.fenixedu.academic.domain.CurricularYear;
import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.DegreeCurricularPlan;
import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.candidacy.IngressionType;
import org.fenixedu.academic.domain.degree.DegreeType;
import org.fenixedu.academic.domain.student.RegistrationRegimeType;
import org.fenixedu.academic.domain.student.StatuteType;
import org.fenixedu.academictreasury.domain.exceptions.AcademicTreasuryDomainException;
import org.fenixedu.academictreasury.domain.tuition.EctsCalculationType;
import org.fenixedu.academictreasury.domain.tuition.TuitionCalculationType;
import org.fenixedu.academictreasury.domain.tuition.TuitionPaymentPlan;
import org.fenixedu.academictreasury.domain.tuition.TuitionPaymentPlanGroup;
import org.fenixedu.academictreasury.dto.tariff.AcademicTariffBean;
import org.fenixedu.academictreasury.dto.tariff.TuitionPaymentPlanBean;
import org.fenixedu.academictreasury.util.Constants;
import org.fenixedu.academictreasury.util.ExcelSheet;
import org.fenixedu.academictreasury.util.ExcelUtils;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.treasury.domain.FinantialEntity;
import org.fenixedu.treasury.domain.FinantialInstitution;
import org.fenixedu.treasury.domain.Product;
import org.fenixedu.treasury.domain.tariff.DueDateCalculationType;
import org.fenixedu.treasury.domain.tariff.InterestType;
import org.fenixedu.treasury.services.accesscontrol.TreasuryAccessControlAPI;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.iver.cit.gvsig.fmap.rendering.FInterval;

import edu.emory.mathcs.backport.java.util.Arrays;
import pt.ist.fenixframework.Atomic;

public class TuitionPaymentPlanImportFile extends TuitionPaymentPlanImportFile_Base {

    private static final int MAX_COLS = 13;

    // @formatter:off
    /* ***********************
     * PAYMENT PLAN PROPERTIES
     * ***********************
     */
    // @formatter:on

    private static final int DEFAULT_PAYMENT_PLAN_IDX = 0;
    private static final int DEGREE_TYPE_IDX = 1;
    private static final int DEGREE_CODE_IDX = 2;
    private static final int DEGREE_NAME_IDX = 3;
    private static final int DCP_NAME_IDX = 4;
    private static final int REGIME_TYPE_IDX = 5;
    private static final int INGRESSION_IDX = 6;
    private static final int CURRICULAR_YEAR_IDX = 7;
    private static final int SEMESTER_IDX = 8;
    private static final int STATUTE_IDX = 9;
    private static final int FIRST_TIME_STUDENT_IDX = 10;
    private static final int CUSTOM_IDX = 11;
    private static final int PAYMENT_PLAN_NAME_IDX = 12;

    // @formatter:off
    /* **********************
     * INSTALLMENT PROPERTIES
     * **********************
     */
    // @formatter:on

    private static final int PRODUCT_IDX = 0;
    private static final int TUITION_CALCULATION_TYPE_IDX = 1;
    private static final int ECTS_CALCULATION_TYPE_IDX = 2;
    private static final int FIXED_AMOUNT_IDX = 3;
    private static final int FACTOR_IDX = 4;
    private static final int TOTAL_ECTS_OR_UNITS_IDX = 5;
    private static final int BEGIN_DATE_IDX = 6;
    private static final int DUE_DATE_CALCULATION_TYPE_IDX = 7;
    private static final int FIXED_DUE_DATE_IDX = 8;
    private static final int NUMBER_OF_DAYS_AFTER_CREATION_IDX = 9;
    private static final int BLOCK_ACADEMICAL_ACTS_IDX = 10;
    private static final int APPLY_INTERESTS_IDX = 11;
    private static final int INTEREST_TYPE_IDX = 12;

    private static final String YES = "Y";
    private static final String NO = "N";
    private static final String INSTALLMENTS_MARK = "INSTALLMENTS";

    protected TuitionPaymentPlanImportFile() {
        super();

        setBennu(Bennu.getInstance());
    }

    protected TuitionPaymentPlanImportFile(final FinantialEntity finantialEntity,
            final TuitionPaymentPlanGroup tuitionPaymentPlanGroup, final Product product, final ExecutionYear executionYear,
            final String filename, final byte[] content) {
        this();

        init(filename, filename, content);

        setFinantialEntity(finantialEntity);
        setProduct(product);
        setTuitionPaymentPlanGroup(tuitionPaymentPlanGroup);
        setExecutionYear(executionYear);
        
        checkRules();
    }

    private void checkRules() {
        
        if(getFinantialEntity() == null) {
            throw new AcademicTreasuryDomainException("error.TuitionPaymentPlanImportFile.finantialEntity.required");
        }
        
        if(getProduct() == null) {
            throw new AcademicTreasuryDomainException("error.TuitionPaymentPlanImportFile.product.required");
        }
        
        if(getTuitionPaymentPlanGroup() == null) {
            throw new AcademicTreasuryDomainException("error.TuitionPaymentPlanImportFile.tuitionPaymentPlanGroup.required");
        }
        
        if (getExecutionYear() == null) {
            throw new AcademicTreasuryDomainException("error.TuitionPaymentPlanImportFile.executionYear.required");
        }
    }

    @Override
    public boolean isAccessible(final User user) {
        return TreasuryAccessControlAPI.isBackOfficeMember(user);
    }

    public boolean isProcessed() {
        return getWhenProcessed() != null;
    }

    
    public List<TuitionPaymentPlanBean> readTuitions() {
        return readExcel(getFinantialEntity(), getTuitionPaymentPlanGroup(), getProduct(), getExecutionYear(), getContent());
    }

    
    @Atomic
    public void process() {
        if(isProcessed()) {
            throw new AcademicTreasuryDomainException("error.TuitionPaymentPlanImportFile.already.processed");
        }
        
        final List<TuitionPaymentPlanBean> tuitionPaymentPlanBeans = readTuitions();

        for (final TuitionPaymentPlanBean tuitionPaymentPlanBean : tuitionPaymentPlanBeans) {
            TuitionPaymentPlan.create(tuitionPaymentPlanBean);
        }
        
        setWhenProcessed(new DateTime());
    }
    
    
    // @formatter:off
    /* ********
     * SERVICES
     * ********
     */
    // @formatter:on
    
    public static Stream<TuitionPaymentPlanImportFile> findAll() {
        return Bennu.getInstance().getTuitionPaymentPlanImportFilesSet().stream();
    }

    public static TuitionPaymentPlanImportFile create(final FinantialEntity finantialEntity,
            final TuitionPaymentPlanGroup tuitionPaymentPlanGroup, final Product product, final ExecutionYear executionYear,
            final String filename, final byte[] content) {
        return new TuitionPaymentPlanImportFile(finantialEntity, tuitionPaymentPlanGroup, product, executionYear, filename, content);
    }
    
    
    public static List<TuitionPaymentPlanBean> readExcel(final FinantialEntity finantialEntity,
            final TuitionPaymentPlanGroup tuitionPaymentPlanGroup, final Product product, final ExecutionYear executionYear,
            final byte[] content) {
        try {
            if (tuitionPaymentPlanGroup == null) {
                throw new AcademicTreasuryDomainException("error.TuitionPaymentPlanImportFile.tuitionPaymentPlanGroup.required");
            }

            if (product == null) {
                throw new AcademicTreasuryDomainException("error.TuitionPaymentPlanImportFile.product.required");
            }

            if (executionYear == null) {
                throw new AcademicTreasuryDomainException("error.TuitionPaymentPlanImportFile.executionYear.required");
            }
            
            if(finantialEntity == null) {
                throw new AcademicTreasuryDomainException("error.TuitionPaymentPlanImportFile.finantialEntity.required");
            }

            final List<TuitionPaymentPlanBean> result = Lists.newArrayList();

            final List<ExcelSheet> excelSheets = ExcelUtils.readExcelSheets(new ByteArrayInputStream(content), MAX_COLS);

            boolean readInstallments = false;
            boolean readPaymentPlanHeader = false;
            boolean readInstallmentsHeader = false;
            for (final ExcelSheet sheet : excelSheets) {

                final List<TuitionPaymentPlanBean> paymentPlanBeansFromSheet = Lists.newArrayList();

                final List<AcademicTariffBean> installmentsList = Lists.newArrayList();

                int installmentOrder = 0;
                for (int r = 0; r < sheet.getRows().size(); r++) {

                    if (readInstallments) {

                        if (!readInstallmentsHeader) {
                            readInstallmentsHeader = true;
                            continue;
                        }

                        final List<String> row = sheet.getRows().get(r);

                        final AcademicTariffBean installmentBean = new AcademicTariffBean(installmentOrder);

                        final String productValue = row.get(PRODUCT_IDX);
                        final String tuitionCalculationTypeValue = row.get(TUITION_CALCULATION_TYPE_IDX);
                        final String ectsCalculationTypeValue = row.get(ECTS_CALCULATION_TYPE_IDX);
                        final String fixedAmountValue = row.get(FIXED_AMOUNT_IDX);
                        final String factorValue = row.get(FACTOR_IDX);
                        final String totalEctsOrUnitsValue = row.get(TOTAL_ECTS_OR_UNITS_IDX);
                        final String beginDateValue = row.get(BEGIN_DATE_IDX);
                        final String dueDateCalculationTypeValue = row.get(DUE_DATE_CALCULATION_TYPE_IDX);
                        final String fixedDueDateValue = row.get(FIXED_DUE_DATE_IDX);
                        final String numberOfDaysAfterCreationValue = row.get(NUMBER_OF_DAYS_AFTER_CREATION_IDX);
                        final String blockAcademicalActsValue = row.get(BLOCK_ACADEMICAL_ACTS_IDX);
                        final String applyInterestsValue = row.get(APPLY_INTERESTS_IDX);
                        final String interestsTypeValue = row.get(INTEREST_TYPE_IDX);

                        if (Strings.isNullOrEmpty(productValue)) {
                            continue;
                        }

                        final Product installmentProduct = Product.findByName(productValue).findFirst().orElse(null);

                        if (installmentProduct == null) {
                            throw new AcademicTreasuryDomainException(
                                    "error.TuitionPaymentPlanImportFile.installment.product.not.found", String.valueOf(r),
                                    productValue);
                        }

                        installmentBean.setTuitionInstallmentProduct(installmentProduct);

                        if (Strings.isNullOrEmpty(tuitionCalculationTypeValue)) {
                            throw new AcademicTreasuryDomainException(
                                    "error.TuitionPaymentPlanImportFile.installment.tuitionCalculationType.required",
                                    String.valueOf(r));
                        }

                        final TuitionCalculationType tuitionCalculationType = Lists.newArrayList(TuitionCalculationType.values())
                                .stream().filter(t -> t.getDescriptionI18N().getContent().equals(tuitionCalculationTypeValue))
                                .findFirst().orElse(null);

                        if (tuitionCalculationType == null) {
                            throw new AcademicTreasuryDomainException(
                                    "error.TuitionPaymentPlanImportFile.installment.tuitionCalculationType.not.found",
                                    String.valueOf(r), tuitionCalculationTypeValue);
                        }

                        installmentBean.setTuitionCalculationType(tuitionCalculationType);

                        EctsCalculationType ectsCalculationType = null;

                        if (tuitionCalculationType.isUnits() || tuitionCalculationType.isEcts()) {
                            if (Strings.isNullOrEmpty(ectsCalculationTypeValue)) {
                                throw new AcademicTreasuryDomainException(
                                        "error.TuitionPaymentPlanImportFile.installment.ectsCalculationType.required",
                                        String.valueOf(r));
                            }

                            ectsCalculationType = Lists.newArrayList(EctsCalculationType.values()).stream()
                                    .filter(e -> e.getDescriptionI18N().getContent().equals(ectsCalculationTypeValue)).findFirst()
                                    .orElse(null);

                            if (ectsCalculationType == null) {
                                throw new AcademicTreasuryDomainException(
                                        "error.TuitionPaymentPlanImportFile.installment.ectsCalculationType.not.found",
                                        String.valueOf(r), ectsCalculationTypeValue);
                            }
                        }

                        installmentBean.setEctsCalculationType(ectsCalculationType);

                        BigDecimal fixedAmount = null;
                        BigDecimal factor = null;
                        BigDecimal totalEctsOrUnits = null;;
                        if (tuitionCalculationType.isFixedAmount() || ectsCalculationType.isFixedAmount()) {

                            if (Strings.isNullOrEmpty(fixedAmountValue)) {
                                throw new AcademicTreasuryDomainException(
                                        "error.TuitionPaymentPlanImportFile.installment.fixedAmount.required", String.valueOf(r));
                            }

                            try {
                                fixedAmount = new BigDecimal(fixedAmountValue);
                            } catch (final NumberFormatException e) {
                                throw new AcademicTreasuryDomainException(
                                        "error.TuitionPaymentPlanImportFile.installment.fixedAmount.invalid", String.valueOf(r),
                                        fixedAmountValue);
                            }
                        } else {
                            if (Strings.isNullOrEmpty(factorValue)) {
                                throw new AcademicTreasuryDomainException(
                                        "error.TuitionPaymentPlanImportFile.installment.factor.required", String.valueOf(r));
                            }

                            if (Strings.isNullOrEmpty(totalEctsOrUnitsValue)) {
                                throw new AcademicTreasuryDomainException(
                                        "error.TuitionPaymentPlanImportFile.installment.totalEctsOrUnits.required",
                                        String.valueOf(r));
                            }

                            try {
                                factor = new BigDecimal(factorValue);
                            } catch (final NumberFormatException e) {
                                throw new AcademicTreasuryDomainException(
                                        "error.TuitionPaymentPlanImportFile.installment.factor.invalid", String.valueOf(r),
                                        factorValue);
                            }

                            try {
                                totalEctsOrUnits = new BigDecimal(totalEctsOrUnitsValue);
                            } catch (final NumberFormatException e) {
                                throw new AcademicTreasuryDomainException(
                                        "error.TuitionPaymentPlanImportFile.installment.totalEctsOrUnits.invalid",
                                        String.valueOf(r), totalEctsOrUnitsValue);
                            }
                        }

                        installmentBean.setFixedAmount(fixedAmount);
                        installmentBean.setFactor(factor);
                        installmentBean.setTotalEctsOrUnits(totalEctsOrUnits);

                        if (Strings.isNullOrEmpty(beginDateValue)) {
                            throw new AcademicTreasuryDomainException(
                                    "error.TuitionPaymentPlanImportFile.installment.beginDate.required", String.valueOf(r));
                        }

                        final LocalDate beginDate = DateTimeFormat.forPattern(Constants.STANDARD_DATE_FORMAT_YYYY_MM_DD)
                                .parseLocalDate(beginDateValue);

                        if (beginDate == null) {
                            throw new AcademicTreasuryDomainException(
                                    "error.TuitionPaymentPlanImportFile.installment.beginDate.invalid", String.valueOf(r),
                                    beginDateValue);
                        }

                        installmentBean.setBeginDate(beginDate);

                        if (Strings.isNullOrEmpty(dueDateCalculationTypeValue)) {
                            throw new AcademicTreasuryDomainException(
                                    "error.TuitionPaymentPlanImportFile.installment.dueDateCalculationType.required",
                                    String.valueOf(r));
                        }

                        DueDateCalculationType dueDateCalculationType = Lists.newArrayList(DueDateCalculationType.values())
                                .stream().filter(d -> d.getDescriptionI18N().getContent().equals(dueDateCalculationTypeValue))
                                .findFirst().orElse(null);

                        if (dueDateCalculationType == null) {
                            throw new AcademicTreasuryDomainException(
                                    "error.TuitionPaymentPlanImportFile.installment.dueDateCalculationType.invalid",
                                    String.valueOf(r), dueDateCalculationTypeValue);
                        }

                        installmentBean.setDueDateCalculationType(dueDateCalculationType);

                        if (!dueDateCalculationType.isFixedDate()
                                && !dueDateCalculationType.isBestOfFixedDateAndDaysAfterCreation()) {
                            throw new AcademicTreasuryDomainException(
                                    "error.TuitionPaymentPlanImportFile.installment.dueDateCalculationType.invalid",
                                    String.valueOf(r), dueDateCalculationTypeValue);
                        }

                        LocalDate fixedDueDate = null;

                        if (dueDateCalculationType.isBestOfFixedDateAndDaysAfterCreation()) {
                            if (Strings.isNullOrEmpty(fixedDueDateValue)) {
                                throw new AcademicTreasuryDomainException(
                                        "error.TuitionPaymentPlanImportFile.installment.fixedDueDate.required",
                                        String.valueOf(r));
                            }

                            fixedDueDate = DateTimeFormat.forPattern(Constants.STANDARD_DATE_FORMAT_YYYY_MM_DD)
                                    .parseLocalDate(fixedDueDateValue);

                            if (fixedDueDate == null) {
                                throw new AcademicTreasuryDomainException(
                                        "error.TuitionPaymentPlanImportFile.installment.fixedDueDate.invalid", String.valueOf(r),
                                        fixedDueDateValue);
                            }
                        }

                        installmentBean.setFixedDueDate(fixedDueDate);

                        if (Strings.isNullOrEmpty(numberOfDaysAfterCreationValue)) {
                            if (Strings.isNullOrEmpty(fixedDueDateValue)) {
                                throw new AcademicTreasuryDomainException(
                                        "error.TuitionPaymentPlanImportFile.installment.numberOfDaysAfterCreation.required",
                                        String.valueOf(r));
                            }
                        }

                        Integer numberOfDaysAfterCreationForDueDate = null;
                        try {
                            numberOfDaysAfterCreationForDueDate = Integer.parseInt(numberOfDaysAfterCreationValue);
                        } catch (final NumberFormatException e) {
                            throw new AcademicTreasuryDomainException(
                                    "error.TuitionPaymentPlanImportFile.installment.numberOfDaysAfterCreation.invalid",
                                    String.valueOf(r), numberOfDaysAfterCreationValue);
                        }

                        if (numberOfDaysAfterCreationForDueDate == null) {
                            throw new AcademicTreasuryDomainException(
                                    "error.TuitionPaymentPlanImportFile.installment.numberOfDaysAfterCreation.invalid",
                                    String.valueOf(r), numberOfDaysAfterCreationValue);
                        }

                        installmentBean.setNumberOfDaysAfterCreationForDueDate(numberOfDaysAfterCreationForDueDate);

                        if (Strings.isNullOrEmpty(blockAcademicalActsValue)) {
                            throw new AcademicTreasuryDomainException(
                                    "error.TuitionPaymentPlanImportFile.installment.blockAcademicalActs.required",
                                    String.valueOf(r));
                        }

                        if (!YES.equals(blockAcademicalActsValue) && !NO.equals(blockAcademicalActsValue)) {
                            throw new AcademicTreasuryDomainException(
                                    "error.TuitionPaymentPlanImportFile.installment.blockAcademicalActs.invalid",
                                    String.valueOf(r), blockAcademicalActsValue);
                        }

                        final boolean blockAcademicalActs = YES.equals(blockAcademicalActsValue) ? true : false;

                        if (Strings.isNullOrEmpty(applyInterestsValue)) {
                            throw new AcademicTreasuryDomainException(
                                    "error.TuitionPaymentPlanImportFile.installment.applyInterests.required", String.valueOf(r));
                        }

                        installmentBean.setAcademicalActBlockingOn(blockAcademicalActs);

                        if (!YES.equals(applyInterestsValue) && !NO.equals(applyInterestsValue)) {
                            throw new AcademicTreasuryDomainException(
                                    "error.TuitionPaymentPlanImportFile.installment.applyInterests.invalid", String.valueOf(r),
                                    applyInterestsValue);
                        }

                        final boolean applyInterests = YES.equals(applyInterestsValue) ? true : false;

                        installmentBean.setApplyInterests(applyInterests);

                        InterestType interestType = null;
                        if (applyInterests) {

                            if (Strings.isNullOrEmpty(interestsTypeValue)) {
                                throw new AcademicTreasuryDomainException(
                                        "error.TuitionPaymentPlanImportFile.installment.interestsType.required",
                                        String.valueOf(r));
                            }

                            interestType = Lists.newArrayList(InterestType.values()).stream()
                                    .filter(i -> i.getDescriptionI18N().getContent().equals(interestsTypeValue)).findFirst()
                                    .orElse(null);

                            if (interestType == null) {
                                throw new AcademicTreasuryDomainException(
                                        "error.TuitionPaymentPlanImportFile.installment.interestType.invalid", String.valueOf(r),
                                        interestsTypeValue);
                            }

                            if (!interestType.isFixedAmount() && !interestType.isGlobalRate()) {
                                throw new AcademicTreasuryDomainException(
                                        "error.TuitionPaymentPlanImportFile.installment.interestType.invalid", String.valueOf(r),
                                        interestsTypeValue);
                            }
                        }

                        installmentBean.setInterestType(interestType);

                        installmentsList.add(installmentBean);

                    } else {
                        if (!readPaymentPlanHeader) {
                            readPaymentPlanHeader = true;
                            continue;
                        }

                        final TuitionPaymentPlanBean tuitionPaymentPlanBean =
                                new TuitionPaymentPlanBean(product, tuitionPaymentPlanGroup, finantialEntity, executionYear);

                        final List<String> row = sheet.getRows().get(r);

                        final String defaultPaymentPlanValue = row.get(DEFAULT_PAYMENT_PLAN_IDX);
                        final String degreeTypeValue = row.get(DEGREE_TYPE_IDX);
                        final String degreeCodeValue = row.get(DEGREE_CODE_IDX);
                        final String degreeNameValue = row.get(DEGREE_NAME_IDX);
                        final String dcpValue = row.get(DCP_NAME_IDX);
                        final String regimeTypeValue = row.get(REGIME_TYPE_IDX);
                        final String ingressionTypeValue = row.get(INGRESSION_IDX);
                        final String curricularYearValue = row.get(CURRICULAR_YEAR_IDX);
                        final String semesterValue = row.get(SEMESTER_IDX);
                        final String statuteValue = row.get(STATUTE_IDX);
                        final String firstTimeStudentValue = row.get(FIRST_TIME_STUDENT_IDX);
                        final String customizedValue = row.get(CUSTOM_IDX);
                        final String tuitionPaymentPlanName = row.get(PAYMENT_PLAN_NAME_IDX);

                        if (Strings.isNullOrEmpty(defaultPaymentPlanValue)) {
                            continue;
                        }

                        if (INSTALLMENTS_MARK.equals(defaultPaymentPlanValue)) {
                            readInstallments = true;
                            continue;
                        }

                        if (!YES.equals(defaultPaymentPlanValue) && !NO.equals(defaultPaymentPlanValue)) {
                            throw new AcademicTreasuryDomainException(
                                    "error.TuitionPaymentPlanImportFile.invalid.default.payment.plan.value", String.valueOf(r),
                                    defaultPaymentPlanValue);
                        }

                        boolean defaultPaymentPlan = YES.equals(defaultPaymentPlanValue) ? true : false;

                        tuitionPaymentPlanBean.setDefaultPaymentPlan(defaultPaymentPlan);

                        if (Strings.isNullOrEmpty(degreeTypeValue)) {
                            throw new AcademicTreasuryDomainException("error.TuitionPaymentPlanImportFile.degreeType.required",
                                    String.valueOf(r));
                        }

                        final DegreeType degreeType = findDegreeTypeByName(degreeTypeValue);

                        if (degreeType == null) {
                            throw new AcademicTreasuryDomainException("error.TuitionPaymentPlanImportFile.degreeType.not.found",
                                    String.valueOf(r), degreeTypeValue);
                        }
                        
                        tuitionPaymentPlanBean.setDegreeType(degreeType);

                        Degree degree = null;

                        if (!Strings.isNullOrEmpty(degreeCodeValue)) {
                            degree = Degree.find(degreeCodeValue);

                            if (degree == null) {
                                throw new AcademicTreasuryDomainException("error.TuitionPaymentPlanImportFile.degree.not.found",
                                        String.valueOf(r), degreeCodeValue);
                            }

                            if (degree.getDegreeType() != degreeType) {
                                throw new AcademicTreasuryDomainException(
                                        "error.TuitionPaymentPlanImportFile.degree.not.of.degreeType", String.valueOf(r),
                                        degreeType.getName().getContent(), degreeCodeValue);
                            }

                            if (!degree.getPresentationNameI18N().getContent().equals(degreeNameValue)) {
                                throw new AcademicTreasuryDomainException(
                                        "error.TuitionPaymentPlanImportFile.degreeName.not.equals", String.valueOf(r),
                                        degreeNameValue, degree.getPresentationNameI18N().getContent());
                            }
                            
                        }

                        DegreeCurricularPlan degreeCurricularPlan = null;
                        if (!Strings.isNullOrEmpty(dcpValue)) {
                            if (degree == null) {
                                throw new AcademicTreasuryDomainException(
                                        "error.TuitionPaymentPlanImportFile.dcp.requires.degree.code", String.valueOf(r));
                            }

                            degreeCurricularPlan =
                                    degree.getDegreeCurricularPlansSet().stream()
                                            .filter(dcp -> dcp.getExecutionDegreeByAcademicInterval(
                                                    executionYear.getAcademicInterval()) != null)
                                    .filter(dcp -> dcp.getName().equals(dcpValue)).findFirst().orElse(null);

                            if (degreeCurricularPlan == null) {
                                throw new AcademicTreasuryDomainException("error.TuitionPaymentPlanImportFile.dcp.not.found",
                                        String.valueOf(r), degreeNameValue, dcpValue);
                            }
                        }

                        if (degreeCurricularPlan != null) {
                            
                            if(degreeCurricularPlan.getDegree().getAdministrativeOffice() != finantialEntity.getAdministrativeOffice()) {
                                throw new AcademicTreasuryDomainException(
                                        "error.TuitionPaymentPlanImportFile.not.allowed.to.add.degree", String.valueOf(r), degreeCurricularPlan.getDegree().getPresentationNameI18N().getContent());
                            }
                            
                            tuitionPaymentPlanBean.setDegreeCurricularPlans(Sets.newHashSet(degreeCurricularPlan));
                        } else {
                            tuitionPaymentPlanBean.setDegreeCurricularPlans(degreeType.getDegreeSet().stream()
                                    .filter(d -> d.getAdministrativeOffice() == finantialEntity.getAdministrativeOffice())
                                    .flatMap(d -> d.getExecutionDegreesForExecutionYear(executionYear).stream())
                                    .map(e -> e.getDegreeCurricularPlan()).collect(Collectors.toSet()));
                        }

                        RegistrationRegimeType regimeType = null;

                        if (!Strings.isNullOrEmpty(regimeTypeValue)) {
                            if (defaultPaymentPlan) {
                                throw new AcademicTreasuryDomainException(
                                        "error.TuitionPaymentPlanImportFile.defaultPaymentPlan.requires.regimeType.empty",
                                        String.valueOf(r));
                            }

                            regimeType = ((List<RegistrationRegimeType>) Arrays.asList(RegistrationRegimeType.values())).stream()
                                    .filter(t -> t.getLocalizedName().equals(regimeTypeValue)).findFirst().orElse(null);

                            if (regimeType == null) {
                                throw new AcademicTreasuryDomainException(
                                        "error.TuitionPaymentPlanImportFile.regimeType.not.found", String.valueOf(r),
                                        regimeTypeValue);
                            }
                        }

                        tuitionPaymentPlanBean.setRegistrationRegimeType(regimeType);

                        IngressionType ingressionType = null;

                        if (!Strings.isNullOrEmpty(ingressionTypeValue)) {
                            if (defaultPaymentPlan) {
                                throw new AcademicTreasuryDomainException(
                                        "error.TuitionPaymentPlanImportFile.defaultPaymentPlan.requires.ingressionType.empty",
                                        String.valueOf(r));
                            }

                            ingressionType = Bennu.getInstance().getIngressionTypesSet().stream()
                                    .filter(i -> i.getDescription().getContent().equals(ingressionTypeValue)).findFirst()
                                    .orElse(null);

                            if (ingressionType == null) {
                                throw new AcademicTreasuryDomainException(
                                        "error.TuitionPaymentPlanImportFile.ingression.type.not.found", String.valueOf(r),
                                        ingressionTypeValue);
                            }
                        }

                        tuitionPaymentPlanBean.setIngression(ingressionType);

                        CurricularYear curricularYear = null;

                        if (!Strings.isNullOrEmpty(curricularYearValue)) {
                            if (defaultPaymentPlan) {
                                throw new AcademicTreasuryDomainException(
                                        "error.TuitionPaymentPlanImportFile.defaultPaymentPlan.requires.curricularYearValue.empty",
                                        String.valueOf(r));
                            }

                            curricularYear = Bennu.getInstance().getCurricularYearsSet().stream()
                                    .filter(c -> c.getYear().toString().equals(curricularYearValue)).findFirst().orElse(null);

                            if (curricularYear == null) {
                                throw new AcademicTreasuryDomainException(
                                        "error.TuitionPaymentPlanImportFile.curricularYear.type.not.found", String.valueOf(r),
                                        curricularYearValue);
                            }
                        }

                        tuitionPaymentPlanBean.setCurricularYear(curricularYear);

                        ExecutionSemester executionSemester = null;

                        if (!Strings.isNullOrEmpty(semesterValue)) {
                            if (defaultPaymentPlan) {
                                throw new AcademicTreasuryDomainException(
                                        "error.TuitionPaymentPlanImportFile.defaultPaymentPlan.requires.semesterValue.empty",
                                        String.valueOf(r));
                            }

                            executionSemester = executionYear.getExecutionPeriodsSet().stream()
                                    .filter(e -> e.getName().equals(semesterValue)).findFirst().orElse(null);

                            if (executionSemester == null) {
                                throw new AcademicTreasuryDomainException(
                                        "error.TuitionPaymentPlanImportFile.executionSemester.not.found", String.valueOf(r),
                                        semesterValue);
                            }
                        }

                        tuitionPaymentPlanBean.setExecutionSemester(executionSemester);

                        StatuteType statuteType = null;

                        if (!Strings.isNullOrEmpty(statuteValue)) {
                            if (defaultPaymentPlan) {
                                throw new AcademicTreasuryDomainException(
                                        "error.TuitionPaymentPlanImportFile.defaultPaymentPlan.requires.statute.empty",
                                        String.valueOf(r));
                            }

                            statuteType = Bennu.getInstance().getStatuteTypesSet().stream()
                                    .filter(s -> s.getName().getContent().equals(statuteValue)).findFirst().orElse(null);

                            if (statuteType == null) {
                                throw new AcademicTreasuryDomainException(
                                        "error.TuitionPaymentPlanImportFile.statuteType.not.found", String.valueOf(r),
                                        statuteValue);
                            }
                        }

                        tuitionPaymentPlanBean.setStatuteType(statuteType);

                        Boolean firstTimeStudent = null;

                        if (!Strings.isNullOrEmpty(firstTimeStudentValue)) {
                            if (defaultPaymentPlan) {
                                throw new AcademicTreasuryDomainException(
                                        "error.TuitionPaymentPlanImportFile.defaultPaymentPlan.requires.firstTimeStudentValue.empty",
                                        String.valueOf(r));
                            }

                            if (!YES.equals(firstTimeStudentValue) && !NO.equals(firstTimeStudentValue)) {
                                throw new AcademicTreasuryDomainException(
                                        "error.TuitionPaymentPlanImportFile.invalid.firstTimeStudent.invalid", String.valueOf(r),
                                        firstTimeStudentValue);
                            }

                            firstTimeStudent = YES.equals(firstTimeStudentValue) ? true : false;
                        }

                        if (firstTimeStudent != null) {
                            tuitionPaymentPlanBean.setFirstTimeStudent(firstTimeStudent);
                        }

                        Boolean customized = null;

                        if (!Strings.isNullOrEmpty(customizedValue)) {
                            if (defaultPaymentPlan) {
                                throw new AcademicTreasuryDomainException(
                                        "error.TuitionPaymentPlanImportFile.defaultPaymentPlan.requires.customizedValue.empty",
                                        String.valueOf(r));
                            }

                            if (!YES.equals(customizedValue) && !NO.equals(customizedValue)) {
                                throw new AcademicTreasuryDomainException(
                                        "error.TuitionPaymentPlanImportFile.customized.invalid", String.valueOf(r),
                                        customizedValue);
                            }

                            customized = YES.equals(customizedValue) ? true : false;

                            if (customized && Strings.isNullOrEmpty(tuitionPaymentPlanName)) {
                                throw new AcademicTreasuryDomainException(
                                        "error.TuitionPaymentPlanImportFile.invalid.customized.requires.paymentPlanName",
                                        String.valueOf(r));
                            }
                        }

                        if (customized != null) {
                            tuitionPaymentPlanBean.setCustomized(customized);

                            if (customized) {
                                tuitionPaymentPlanBean.setName(tuitionPaymentPlanName);
                            }
                        }

                        paymentPlanBeansFromSheet.add(tuitionPaymentPlanBean);
                    }

                }

                for (final TuitionPaymentPlanBean paymentPlanBean : paymentPlanBeansFromSheet) {
                    paymentPlanBean.setTuitionInstallmentBeans(installmentsList);
                }

                result.addAll(paymentPlanBeansFromSheet);
            }

            return result;

        } catch (final IOException e) {
            throw new AcademicTreasuryDomainException("error.TuitionPaymentPlanImportFile.invalid.sheet");
        }
    }

    private static DegreeType findDegreeTypeByName(final String degreeTypeValue) {
        return DegreeType.all().filter(d -> d.getName().getContent().equals(degreeTypeValue)).findFirst().orElse(null);
    }

}
