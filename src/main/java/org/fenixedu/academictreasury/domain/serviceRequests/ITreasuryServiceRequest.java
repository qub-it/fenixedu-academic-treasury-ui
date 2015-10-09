/**
 * This file was created by Quorum Born IT <http://www.qub-it.com/> and its 
 * copyright terms are bind to the legal agreement regulating the FenixEdu@ULisboa 
 * software development project between Quorum Born IT and Serviços Partilhados da
 * Universidade de Lisboa:
 *  - Copyright © 2015 Quorum Born IT (until any Go-Live phase)
 *  - Copyright © 2015 Universidade de Lisboa (after any Go-Live phase)
 *
 * Contributors: diogo.simoes@qub-it.com
 *               jnpa@reitoria.ulisboa.pt
 *
 * 
 * This file is part of FenixEdu QubDocs.
 *
 * FenixEdu QubDocs is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FenixEdu QubDocs is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FenixEdu QubDocs.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.fenixedu.academictreasury.domain.serviceRequests;

import java.util.Locale;

import org.fenixedu.academic.domain.degreeStructure.CycleType;
import org.fenixedu.academic.domain.student.Registration;
import org.joda.time.DateTime;

/**
 * 
 * Defines the {@link AcademicServiceRequest} contract for FenixeduAcademicTreasury
 *
 */
public interface ITreasuryServiceRequest {

    public Registration getRegistration();

    public boolean hasRegistation();

    public Locale getLanguage();

    public boolean hasLanguage();

    public boolean isDetailed();

    public Integer getNumberOfUnits();

    public boolean hasNumberOfUnits();

    public boolean isUrgent();

    public Integer getNumberOfPages();

    public boolean hasNumberOfPages();

    public CycleType getCycleType();

    public boolean hasCycleType();

    public DateTime getRequestDate();

    public String getDescription();

}
