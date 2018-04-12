package org.fenixedu.academictreasury.domain.integration.tuitioninfo;

import org.fenixedu.academictreasury.domain.integration.tuitioninfo.exporter.ERPTuitionInfoExporterForSAP;
import pt.ist.fenixframework.FenixFramework;
import org.fenixedu.treasury.domain.document.Series;

import pt.ist.fenixframework.Atomic;

public class ERPTuitionInfoSettings extends ERPTuitionInfoSettings_Base {
    
    public ERPTuitionInfoSettings() {
        super();
        setDomainRoot(FenixFramework.getDomainRoot());
    }
    
    @Atomic
    public void edit(final Series series) {
        setSeries(series);
    }
    
    public IERPTuitionInfoExporter exporter() {
        return new ERPTuitionInfoExporterForSAP();
    }
    
    // @formatter:off
    /* ********
     * SERVICES
     * ********
     */
    // @formatter:on
    
    @Atomic
    public static ERPTuitionInfoSettings getInstance() {
        if(FenixFramework.getDomainRoot().getErpTuitionInfoSettings() == null) {
            new ERPTuitionInfoSettings();
        }
        
        return FenixFramework.getDomainRoot().getErpTuitionInfoSettings();
    }
}
