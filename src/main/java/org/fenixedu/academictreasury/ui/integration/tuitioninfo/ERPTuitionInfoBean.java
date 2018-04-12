package org.fenixedu.academictreasury.ui.integration.tuitioninfo;

import static org.fenixedu.academictreasury.util.Constants.academicTreasuryBundle;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academictreasury.domain.customer.PersonCustomer;
import org.fenixedu.academictreasury.domain.event.AcademicTreasuryEvent;
import org.fenixedu.academictreasury.domain.integration.tuitioninfo.ERPTuitionInfo;
import org.fenixedu.academictreasury.domain.integration.tuitioninfo.ERPTuitionInfoSettings;
import org.fenixedu.academictreasury.domain.integration.tuitioninfo.ERPTuitionInfoType;
import org.fenixedu.academictreasury.domain.integration.tuitioninfo.ERPTuitionInfoTypeAcademicEntry;
import org.fenixedu.academictreasury.util.Constants;
import org.fenixedu.treasury.dto.ITreasuryBean;
import org.fenixedu.treasury.dto.TreasuryTupleDataSourceBean;
import org.fenixedu.treasury.domain.debt.DebtAccount;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class ERPTuitionInfoBean implements ITreasuryBean {

    private List<TreasuryTupleDataSourceBean> executionYearDataSource = Lists.newArrayList();
    private List<TreasuryTupleDataSourceBean> erpTuitionInfoTypeDataSource = Lists.newArrayList();
    
    private DebtAccount debtAccount = null;
    private ExecutionYear executionYear = null;
    private ERPTuitionInfoType erpTuitionInfoType = null;
    
    private String pendingErpTuitionInfoMessage;
    
    public ERPTuitionInfoBean(final DebtAccount debtAccount) {
        this.debtAccount = debtAccount;
        
        updateData();
    }
    
    private Set<AcademicTreasuryEvent> tuitionAcademicTreasuryEvents() {
        if(this.executionYear == null) {
            return Sets.newHashSet();
        }
        
        return ((PersonCustomer) debtAccount.getCustomer()).getAssociatedPerson().getAcademicTreasuryEventSet().stream()
            .filter(e -> e.isForRegistrationTuition() || e.isForStandaloneTuition() || e.isForExtracurricularTuition())
            .filter(e-> e.getExecutionYear() == executionYear)
            .collect(Collectors.toSet());
    }
    
    private List<TreasuryTupleDataSourceBean> executionYearDataSource() {
        final List<TreasuryTupleDataSourceBean> result = ExecutionYear.readNotClosedExecutionYears().stream()
                .filter(e -> ERPTuitionInfoSettings.getInstance().getActiveExecutionYearsSet().contains(e))
                .sorted(ExecutionYear.REVERSE_COMPARATOR_BY_YEAR).collect(Collectors.toList()).stream()
                .map(l -> new TreasuryTupleDataSourceBean(l.getExternalId(), l.getQualifiedName()))
                .collect(Collectors.toList());

        result.add(0, Constants.SELECT_OPTION);

        return result;
    }

    private List<TreasuryTupleDataSourceBean> erpTuitionInfoTypeDataSource() {
        if(executionYear == null) {
            return Lists.newArrayList();
        }
        
        final List<TreasuryTupleDataSourceBean> result = ERPTuitionInfoType.findActiveForExecutionYear(executionYear)
                .filter(t -> isForAnyOfTreasuryEvent(t))
                .map(t -> new TreasuryTupleDataSourceBean(t.getExternalId(), t.getErpTuitionInfoProduct().getName()))
                .sorted(TreasuryTupleDataSourceBean.COMPARE_BY_TEXT)
                .collect(Collectors.toList());
                

        result.add(0, Constants.SELECT_OPTION);
        
        return result;
    }
    
    private boolean isForAnyOfTreasuryEvent(final ERPTuitionInfoType t) {
        for (final AcademicTreasuryEvent ev : tuitionAcademicTreasuryEvents()) {
            for (final ERPTuitionInfoTypeAcademicEntry entry : t.getErpTuitionInfoTypeAcademicEntriesSet()) {
                if(entry.isForStandalone() || entry.isForExtracurricular()) {
                    return true;
                }
                
                if(entry.isAppliedForRegistration(ev.getRegistration(), this.executionYear)) {
                    return true;
                }
            }
        }
        
        return false;
    }

    public void updateData() {
        this.executionYearDataSource = executionYearDataSource();
        this.erpTuitionInfoTypeDataSource = erpTuitionInfoTypeDataSource();
        
        this.pendingErpTuitionInfoMessage = "";
        if(getErpTuitionInfoType() != null) {
            ERPTuitionInfo pendingErpTuitionInfo = ERPTuitionInfo.findUniquePendingToExport((PersonCustomer) getDebtAccount().getCustomer(), getErpTuitionInfoType()).orElse(null);
            
            if(pendingErpTuitionInfo != null) {
                this.pendingErpTuitionInfoMessage = academicTreasuryBundle("error.ERPTuitionInfo.pending.to.export", pendingErpTuitionInfo.getUiDocumentNumber());
            }
        }
        
    }
    
    // @formatter:off
    /* *****************
     * GETTERS & SETTERS
     * *****************
     */
    // @formatter:on
    
    public DebtAccount getDebtAccount() {
        return debtAccount;
    }
    
    public ExecutionYear getExecutionYear() {
        return executionYear;
    }
    
    public ERPTuitionInfoType getErpTuitionInfoType() {
        return erpTuitionInfoType;
    }
    
    public List<TreasuryTupleDataSourceBean> getErpTuitionInfoTypeDataSource() {
        return erpTuitionInfoTypeDataSource;
    }
    
    public List<TreasuryTupleDataSourceBean> getExecutionYearDataSource() {
        return executionYearDataSource;
    }

    public String getpendingErpTuitionInfoMessage() {
        return pendingErpTuitionInfoMessage;
    }
    
}
