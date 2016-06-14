package org.fenixedu.academictreasury.domain.debtGeneration;

import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Stream;

import org.fenixedu.academictreasury.domain.exceptions.AcademicTreasuryDomainException;
import org.fenixedu.bennu.core.domain.Bennu;

import com.google.common.base.Strings;

public class AcademicDebtGenerationRuleType extends AcademicDebtGenerationRuleType_Base {
    
    public static Comparator<AcademicDebtGenerationRuleType> COMPARE_BY_ORDER_NUMBER = new Comparator<AcademicDebtGenerationRuleType>() {

        @Override
        public int compare(final AcademicDebtGenerationRuleType o1, final AcademicDebtGenerationRuleType o2) {
            int c = Integer.compare(o1.getOrderNumber(), o2.getOrderNumber());
            
            return c != 0 ? c : o1.getExternalId().compareTo(o2.getExternalId());
        }
        
    };
    
    protected AcademicDebtGenerationRuleType() {
        super();
        setBennu(Bennu.getInstance());
    }
    
    public AcademicDebtGenerationRuleType(final String code, final String name, final String strategyImplementation, final int orderNumber) {
        this();
        setCode(code);
        setName(name);
        setStrategyImplementation(strategyImplementation);
        setOrderNumber(orderNumber);
        
        checkRules();
    }
    
    private void checkRules() {
        if(Strings.isNullOrEmpty(getCode())) {
            throw new AcademicTreasuryDomainException("error.AcademicDebtGenerationRuleType.code.required");
        }
        
        if(findByCodeIgnoresCase(getCode()).count() > 1) {
            throw new AcademicTreasuryDomainException("error.AcademicDebtGenerationRuleType.code.already.exists");
        }

        if(Strings.isNullOrEmpty(getName())) {
            throw new AcademicTreasuryDomainException("error.AcademicDebtGenerationRuleType.name.required");
        }
        
        strategyImplementation();
        
        if(findAll().filter(i -> i.getOrderNumber() == getOrderNumber()).count() > 1) {
            throw new AcademicTreasuryDomainException("error.AcademicDebtGenerationRuleType.orderNumber.already.exists");
        }
        
        if(findAll().filter(l -> l.getStrategyImplementation().equals(getStrategyImplementation())).count() > 1) {
            throw new AcademicTreasuryDomainException("error.AcademicDebtGenerationRuleType.strategyImplementation.already.exists");
        }
    }

    public IAcademicDebtGenerationRuleStrategy strategyImplementation() {
        try {
            return (IAcademicDebtGenerationRuleStrategy) Class.forName(getStrategyImplementation()).newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    // @formatter: off
    /************
     * SERVICES *
     ************/
    // @formatter: on
    
    public static Stream<AcademicDebtGenerationRuleType> findAll() {
        return Bennu.getInstance().getAcademicDebtGenerationRuleTypesSet().stream();
    }
    
    public static Optional<AcademicDebtGenerationRuleType> findByCode(final String code) {
        return findAll().filter(l -> l.getCode().equals(code)).findAny();
    }
    
    private static Stream<AcademicDebtGenerationRuleType> findByCodeIgnoresCase(final String code) {
        return findAll().filter(l -> l.getCode().toLowerCase().equals(code.toLowerCase()));
    }
    
    public static AcademicDebtGenerationRuleType create(final String code, final String name, final String strategyImplementation, final int orderNumber) {
        return new AcademicDebtGenerationRuleType(code, name, strategyImplementation, orderNumber);
    }

}
