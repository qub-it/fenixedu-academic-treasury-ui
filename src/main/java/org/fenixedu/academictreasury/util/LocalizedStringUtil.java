/**
 * This file was created by Quorum Born IT <http://www.qub-it.com/> and its 
 * copyright terms are bind to the legal agreement regulating the FenixEdu@ULisboa 
 * software development project between Quorum Born IT and Serviços Partilhados da
 * Universidade de Lisboa:
 *  - Copyright © 2015 Quorum Born IT (until any Go-Live phase)
 *  - Copyright © 2015 Universidade de Lisboa (after any Go-Live phase)
 *
 * Contributors: ricardo.pedro@qub-it.com, anil.mamede@qub-it.com
 * 
 *
 * 
 * This file is part of FenixEdu Treasury.
 *
 * FenixEdu Treasury is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FenixEdu Treasury is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FenixEdu Treasury.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.fenixedu.academictreasury.util;

import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.fenixedu.commons.i18n.LocalizedString;

public class LocalizedStringUtil {

    public static boolean isTrimmedEmpty(final LocalizedString localizedString) {
        if(localizedString == null || localizedString.getLocales().isEmpty()) {
            return true;
        }
        
        boolean empty = true;
        for (final Locale locale : localizedString.getLocales()) {
            empty &= isTrimmedEmpty(localizedString.getContent(locale));
        }
        
        return empty;
    }

    @Deprecated
    /**
     * Replace with Guava
     */
    public static boolean isTrimmedEmpty(final String content) {
        return content == null || StringUtils.isEmpty(StringUtils.trimToEmpty(content));
    }

    public static boolean isEqualToAnyLocale(final LocalizedString localizedString, final String value) {
        return localizedString.getLocales().stream().filter(l -> localizedString.getContent(l).equals(value)).count() > 0;
    }
    
    public static boolean isEqualToAnyLocaleIgnoreCase(final LocalizedString localizedString, final String value) {
        return localizedString.getLocales().stream().filter(l -> localizedString.getContent(l).equalsIgnoreCase(value)).count() > 0;
    }
    
}
