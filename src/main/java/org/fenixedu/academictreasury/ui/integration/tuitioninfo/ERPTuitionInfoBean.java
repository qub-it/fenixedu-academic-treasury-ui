package org.fenixedu.academictreasury.ui.integration.tuitioninfo;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academictreasury.domain.customer.PersonCustomer;
import org.fenixedu.academictreasury.domain.event.AcademicTreasuryEvent;
import org.fenixedu.academictreasury.domain.integration.tuitioninfo.ERPTuitionInfoType;
import org.fenixedu.academictreasury.domain.integration.tuitioninfo.ERPTuitionInfoTypeAcademicEntry;
import org.fenixedu.academictreasury.util.Constants;
import org.fenixedu.bennu.IBean;
import org.fenixedu.bennu.TupleDataSourceBean;
import org.fenixedu.treasury.domain.debt.DebtAccount;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class ERPTuitionInfoBean implements IBean {

    private List<TupleDataSourceBean> executionYearDataSource = Lists.newArrayList();
    private List<TupleDataSourceBean> erpTuitionInfoTypeDataSource = Lists.newArrayList();
    
    private DebtAccount debtAccount = null;
    private ExecutionYear executionYear = null;
    private ERPTuitionInfoType erpTuitionInfoType = null;
    
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
    
    private List<TupleDataSourceBean> executionYearDataSource() {
        final List<TupleDataSourceBean> result = ExecutionYear.readNotClosedExecutionYears().stream()
                .sorted(ExecutionYear.REVERSE_COMPARATOR_BY_YEAR).collect(Collectors.toList()).stream()
                .map(l -> new TupleDataSourceBean(l.getExternalId(), l.getQualifiedName()))
                .collect(Collectors.toList());

        result.add(0, Constants.SELECT_OPTION);

        return result;
    }

    private List<TupleDataSourceBean> erpTuitionInfoTypeDataSource() {
        if(executionYear == null) {
            return Lists.newArrayList();
        }
        
        final List<TupleDataSourceBean> result = ERPTuitionInfoType.findActiveForExecutionYear(executionYear)
                .filter(t -> isForAnyOfTreasuryEvent(t))
                .map(t -> new TupleDataSourceBean(t.getExternalId(), t.getName()))
                .sorted(TupleDataSourceBean.COMPARE_BY_TEXT)
                .collect(Collectors.toList());
                

        result.add(0, Constants.SELECT_OPTION);
        
        return result;
    }
    
    private boolean isForAnyOfTreasuryEvent(final ERPTuitionInfoType t) {
        for (final AcademicTreasuryEvent ev : tuitionAcademicTreasuryEvents()) {
            for (final ERPTuitionInfoTypeAcademicEntry entry : t.getErpTuitionInfoTypeAcademicEntriesSet()) {
                if(entry.isAppliedOnAcademicTreasuryEvent(ev, this.executionYear)) {
                    return true;
                }
            }
        }
        
        return false;
    }

    public void updateData() {
        this.executionYearDataSource = executionYearDataSource();
        this.erpTuitionInfoTypeDataSource = erpTuitionInfoTypeDataSource();
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
    
    public List<TupleDataSourceBean> getErpTuitionInfoTypeDataSource() {
        return erpTuitionInfoTypeDataSource;
    }
    
    public List<TupleDataSourceBean> getExecutionYearDataSource() {
        return executionYearDataSource;
    }
}
