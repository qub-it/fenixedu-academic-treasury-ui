package org.fenixedu.academictreasury.domain.importation;

import java.util.Comparator;
import java.util.stream.Stream;

import org.fenixedu.academictreasury.domain.exceptions.AcademicTreasuryDomainException;
import org.fenixedu.bennu.core.domain.Bennu;

import com.google.common.base.Strings;

import pt.ist.fenixframework.Atomic;

public class TreasuryImportType extends TreasuryImportType_Base {
    
    public static final Comparator<? super TreasuryImportType> COMPARE_BY_NAME = new Comparator<TreasuryImportType>() {

        @Override
        public int compare(final TreasuryImportType o1, final TreasuryImportType o2) {
            int c = o1.getName().compareTo(o2.getName());
            
            return c != 0 ? c : o1.getExternalId().compareTo(o2.getExternalId());
        }
    };

    protected TreasuryImportType() {
        super();
        setBennu(Bennu.getInstance());
    }
    
    protected TreasuryImportType(final String name, final String clazz) {
        this();
        
        setName(name);
        setClazz(clazz);
        
        checkRules();
    }
    
    @Atomic
    public void edit(final String name, final String clazz) {
        setName(name);
        setClazz(clazz);
        
        checkRules();
    }
    
    @Atomic
    public void delete() {
        if(!getTreasuryImportFilesSet().isEmpty()) {
            throw new AcademicTreasuryDomainException("error.TreasuryImportType.delete.impossible");
        }
        
        setBennu(null);
        deleteDomainObject();
    }

    private void checkRules() {
        if(getBennu() == null) {
            throw new AcademicTreasuryDomainException("error.TreasuryImportType.bennu.required");
        }
        
        if(Strings.isNullOrEmpty(getName())) {
            throw new AcademicTreasuryDomainException("error.TreasuryImportType.name.required");
        }
        
        if(Strings.isNullOrEmpty(getClazz())) {
            throw new AcademicTreasuryDomainException("error.TreasuryImportType.clazz.required");
        }
        
        implementation();
    }
    
    public ITreasuryImportStrategy<?> implementation() {
        try {
            return (ITreasuryImportStrategy<?>) Class.forName(getClazz()).newInstance();
        } catch (final InstantiationException | IllegalAccessException | ClassNotFoundException | ClassCastException e) {
            throw new AcademicTreasuryDomainException("error.TreasuryImportType.clazz.invalid");
        }
    }

    public static Stream<TreasuryImportType> findAll() {
        return Bennu.getInstance().getTreasuryImportTypesSet().stream();
    }
    
    public static Stream<TreasuryImportType> findByClassName(final String clazz) {
       return findAll().filter(t -> t.getClazz().equals(clazz));
    }

    @Atomic
    public static TreasuryImportType create(final String name, final String clazz) {
        return new TreasuryImportType(name, clazz);
    }

}
