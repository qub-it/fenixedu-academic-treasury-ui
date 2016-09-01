package org.fenixedu.academictreasury.domain.debtGeneration;

import java.util.stream.Stream;

import org.fenixedu.academictreasury.domain.exceptions.AcademicTreasuryDomainException;
import org.fenixedu.bennu.core.domain.Bennu;

public class DebtGenerationRuleRestriction extends DebtGenerationRuleRestriction_Base {
    
    public DebtGenerationRuleRestriction() {
        super();

        setBennu(Bennu.getInstance());
    }
    
    protected DebtGenerationRuleRestriction(final String name, final String strategyImplementation) {
        this();
        
        setName(name);
        setStrategyImplementation(strategyImplementation);
    }
    
    public IDebtGenerationRuleRestrictionStrategy strategyImplementation() {
        try {
            final Class<IDebtGenerationRuleRestrictionStrategy> clazz = (Class<IDebtGenerationRuleRestrictionStrategy>) Class.forName(getStrategyImplementation());
            final IDebtGenerationRuleRestrictionStrategy strategy = clazz.newInstance();

            return strategy;
        } catch (final ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
    
    public void delete() {
        if(!getAcademicDebtGenerationRulesSet().isEmpty()) {
            throw new AcademicTreasuryDomainException("error.DebtGenerationRuleRestriction.academicDebtGenerationRulesSet.not.empty");
        }
        
        setBennu(null);
        
        deleteDomainObject();
    }
    
    // @formatter:off
    /* ********
     * SERVICES
     * ********
     */
    // @formatter:on

    public static Stream<DebtGenerationRuleRestriction> findAll() {
        return Bennu.getInstance().getDebtGenerationRuleRestrictionsSet().stream();
    }
    
    public static DebtGenerationRuleRestriction create(final String name, final String strategyImplementation) {
        return new DebtGenerationRuleRestriction(name, strategyImplementation);
    }
    
}
