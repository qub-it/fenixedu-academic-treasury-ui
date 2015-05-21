package org.fenixedu.academictreasury.dto.tariff;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import org.fenixedu.academic.domain.CurricularSemester;
import org.fenixedu.academic.domain.CurricularYear;
import org.fenixedu.academic.domain.DegreeCurricularPlan;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.candidacy.Ingression;
import org.fenixedu.academic.domain.student.RegistrationProtocol;
import org.fenixedu.academic.domain.student.RegistrationRegimeType;
import org.fenixedu.academictreasury.domain.tuition.TuitionPaymentPlanGroup;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.treasury.domain.FinantialEntity;
import org.fenixedu.treasury.domain.Product;
import org.fenixedu.treasury.domain.VatType;

import com.google.common.collect.Lists;

public class TuitionPaymentPlanBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private FinantialEntity finantialEntity;
    private Product product;
    private VatType vatType;
    private TuitionPaymentPlanGroup tuitionPaymentPlanGroup;
    private ExecutionYear executionYear;
    private Set<DegreeCurricularPlan> degreeCurricularPlan;

    private boolean defaultPaymentPlan;
    private RegistrationRegimeType registrationRegimeType;
    private RegistrationProtocol registrationProtocol;
    private Ingression ingression;
    private CurricularYear curricularYear;
    private CurricularSemester curricularSemester;
    private boolean firstTimeStudent;
    private boolean customized;
    private LocalizedString name;
    private boolean withLaboratorialClasses;
    
    public List<AcademicTariffBean> tuitionInstallmentBeans = Lists.newArrayList();
    
    public TuitionPaymentPlanBean() {
    }

    public FinantialEntity getFinantialEntity() {
        return finantialEntity;
    }
    
    public void setFinantialEntity(FinantialEntity finantialEntity) {
        this.finantialEntity = finantialEntity;
    }
    
    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
    
    public VatType getVatType() {
        return vatType;
    }
    
    public void setVatType(VatType vatType) {
        this.vatType = vatType;
    }

    public TuitionPaymentPlanGroup getTuitionPaymentPlanGroup() {
        return tuitionPaymentPlanGroup;
    }

    public void setTuitionPaymentPlanGroup(TuitionPaymentPlanGroup tuitionPaymentPlanGroup) {
        this.tuitionPaymentPlanGroup = tuitionPaymentPlanGroup;
    }

    public ExecutionYear getExecutionYear() {
        return executionYear;
    }

    public void setExecutionYear(ExecutionYear executionYear) {
        this.executionYear = executionYear;
    }

    public Set<DegreeCurricularPlan> getDegreeCurricularPlan() {
        return degreeCurricularPlan;
    }

    public void setDegreeCurricularPlan(Set<DegreeCurricularPlan> degreeCurricularPlan) {
        this.degreeCurricularPlan = degreeCurricularPlan;
    }

    public boolean isDefaultPaymentPlan() {
        return defaultPaymentPlan;
    }

    public void setDefaultPaymentPlan(boolean defaultPaymentPlan) {
        this.defaultPaymentPlan = defaultPaymentPlan;
    }

    public RegistrationRegimeType getRegistrationRegimeType() {
        return registrationRegimeType;
    }

    public void setRegistrationRegimeType(RegistrationRegimeType registrationRegimeType) {
        this.registrationRegimeType = registrationRegimeType;
    }

    public RegistrationProtocol getRegistrationProtocol() {
        return registrationProtocol;
    }

    public void setRegistrationProtocol(RegistrationProtocol registrationProtocol) {
        this.registrationProtocol = registrationProtocol;
    }

    public Ingression getIngression() {
        return ingression;
    }

    public void setIngression(Ingression ingression) {
        this.ingression = ingression;
    }

    public CurricularYear getCurricularYear() {
        return curricularYear;
    }

    public void setCurricularYear(CurricularYear curricularYear) {
        this.curricularYear = curricularYear;
    }

    public CurricularSemester getCurricularSemester() {
        return curricularSemester;
    }

    public void setCurricularSemester(CurricularSemester curricularSemester) {
        this.curricularSemester = curricularSemester;
    }

    public boolean isFirstTimeStudent() {
        return firstTimeStudent;
    }

    public void setFirstTimeStudent(boolean firstTimeStudent) {
        this.firstTimeStudent = firstTimeStudent;
    }

    public boolean isCustomized() {
        return customized;
    }

    public void setCustomized(boolean customized) {
        this.customized = customized;
    }

    public LocalizedString getName() {
        return name;
    }

    public void setName(LocalizedString name) {
        this.name = name;
    }

    public boolean isWithLaboratorialClasses() {
        return withLaboratorialClasses;
    }

    public void setWithLaboratorialClasses(boolean withLaboratorialClasses) {
        this.withLaboratorialClasses = withLaboratorialClasses;
    }
    
    public List<AcademicTariffBean> getTuitionInstallmentBeans() {
        return tuitionInstallmentBeans;
    }
    
    public void setTuitionInstallmentBeans(final List<AcademicTariffBean> tuitionInstallmentBeans) {
        this.tuitionInstallmentBeans = tuitionInstallmentBeans;
    }

}
