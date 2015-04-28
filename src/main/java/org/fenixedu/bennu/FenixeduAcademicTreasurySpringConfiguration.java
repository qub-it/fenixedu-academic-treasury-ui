package org.fenixedu.bennu;

import org.fenixedu.bennu.spring.BennuSpringModule;

@BennuSpringModule(basePackages = "org.fenixedu.academictreasury", bundles = "AcademicTreasuryResources")
public class FenixeduAcademicTreasurySpringConfiguration {

    public static final String BUNDLE = "resources/AcademicTreasuryResources";
}
