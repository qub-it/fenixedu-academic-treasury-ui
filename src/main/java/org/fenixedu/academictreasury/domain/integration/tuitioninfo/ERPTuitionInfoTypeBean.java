package org.fenixedu.academictreasury.domain.integration.tuitioninfo;

import java.io.Serializable;

import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.DegreeCurricularPlan;
import org.fenixedu.academic.domain.degree.DegreeType;
import org.fenixedu.treasury.dto.ITreasuryBean;

import com.sun.tools.javac.api.Formattable.LocalizedString;

public class ERPTuitionInfoTypeBean implements ITreasuryBean, Serializable {

    private static final long serialVersionUID = 1L;
    
    private String code;
    private LocalizedString name;
    
    private DegreeType degreeType;
    private Degree degree;
    private DegreeCurricularPlan degreeCurricularPlan;
    
    
    // @formatter:off
    /* *****************
     * GETTERS & SETTERS
     * *****************
     */
    // @formatter:on
    
    
    
}
