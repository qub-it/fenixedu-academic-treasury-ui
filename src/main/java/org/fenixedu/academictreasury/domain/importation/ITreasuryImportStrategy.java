package org.fenixedu.academictreasury.domain.importation;

public interface ITreasuryImportStrategy<T> {
    
    public void process(final TreasuryImportFile file);
    
    public T readExcel(final byte[] content);

    public String viewUrl();

}
