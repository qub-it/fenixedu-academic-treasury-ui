/**
 * This file was created by Quorum Born IT <http://www.qub-it.com/> and its 
 * copyright terms are bind to the legal agreement regulating the FenixEdu@ULisboa 
 * software development project between Quorum Born IT and ServiÃ§os Partilhados da
 * Universidade de Lisboa:
 *  - Copyright Â© 2015 Quorum Born IT (until any Go-Live phase)
 *  - Copyright Â© 2015 Universidade de Lisboa (after any Go-Live phase)
 *
 * Contributors: xpto@qub-it.com
 *
 * 
 * This file is part of FenixEdu Academictreasury.
 *
 * FenixEdu Academictreasury is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FenixEdu Academictreasury is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FenixEdu Academictreasury.  If not, see <http://www.gnu.org/licenses/>.
 */
		
package org.fenixedu.academictreasury.dto.reports;

import org.fenixedu.academictreasury.domain.reports.DebtReportRequestType;
import org.fenixedu.bennu.IBean;
import org.joda.time.LocalDate;


public class DebtReportRequestBean implements IBean {
	
	private DebtReportRequestType type;
	private org.joda.time.LocalDate beginDate;
	private org.joda.time.LocalDate endDate;
	private String decimalSeparator;
	private boolean includeAnnuledEntries;
	
	public DebtReportRequestBean(){
	    this.type = DebtReportRequestType.INVOICE_ENTRIES;
	    this.beginDate = new LocalDate();
	    this.endDate = new LocalDate();
	    this.decimalSeparator = ",";
	    this.includeAnnuledEntries = true;
	}

    /* GETTERS & SETTERS */
    
    public DebtReportRequestType getType() {
        return type;
    }

    public void setType(DebtReportRequestType type) {
        this.type = type;
    }

    public org.joda.time.LocalDate getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(org.joda.time.LocalDate beginDate) {
        this.beginDate = beginDate;
    }

    public org.joda.time.LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(org.joda.time.LocalDate endDate) {
        this.endDate = endDate;
    }
    
    public String getDecimalSeparator() {
        return decimalSeparator;
    }
    
    public void setDecimalSeparator(String decimalSeparator) {
        this.decimalSeparator = decimalSeparator;
    }
	
    public boolean isIncludeAnnuledEntries() {
        return includeAnnuledEntries;
    }
    
    public void setIncludeAnnuledEntries(boolean includeAnnuledEntries) {
        this.includeAnnuledEntries = includeAnnuledEntries;
    }
}
