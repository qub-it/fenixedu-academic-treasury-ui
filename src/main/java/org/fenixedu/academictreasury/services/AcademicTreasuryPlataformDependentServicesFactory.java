package org.fenixedu.academictreasury.services;

public class AcademicTreasuryPlataformDependentServicesFactory {

    private static IAcademicTreasuryPlatformDependentServices _impl;
    
    public static IAcademicTreasuryPlatformDependentServices implementation() {
        return _impl;
    }
    
    public static synchronized void registerImplementation(IAcademicTreasuryPlatformDependentServices impl) {
        _impl = impl;
    }
	
}
