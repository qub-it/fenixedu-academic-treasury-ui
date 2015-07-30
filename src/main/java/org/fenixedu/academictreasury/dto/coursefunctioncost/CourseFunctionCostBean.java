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

package org.fenixedu.academictreasury.dto.coursefunctioncost;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.CompetenceCourse;
import org.fenixedu.academic.domain.DegreeCurricularPlan;
import org.fenixedu.academic.domain.ExecutionDegree;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.bennu.IBean;
import org.fenixedu.bennu.TupleDataSourceBean;

import com.google.common.collect.Lists;

public class CourseFunctionCostBean implements IBean {

    private CompetenceCourse competenceCourses;
    private DegreeCurricularPlan degreeCurricularPlan;
    private ExecutionYear executionYear;
    private BigDecimal functionCost;

    private List<TupleDataSourceBean> executionYearDataSource;
    private List<TupleDataSourceBean> degreeCurricularPlanDataSource;
    private List<TupleDataSourceBean> competenceCoursesDataSource;

    public CourseFunctionCostBean() {

        updateData();
    }

    public void updateData() {
        executionYearDataSource =
                ExecutionYear.readOpenExecutionYears().stream().sorted(ExecutionYear.REVERSE_COMPARATOR_BY_YEAR)
                        .collect(Collectors.toList()).stream()
                        .map(l -> new TupleDataSourceBean(l.getExternalId(), l.getQualifiedName())).collect(Collectors.toList());

        if (executionYear == null) {
            degreeCurricularPlanDataSource = Lists.newArrayList();
            competenceCoursesDataSource = Lists.newArrayList();
            return;
        }

        final List<TupleDataSourceBean> result =
                ExecutionDegree.getAllByExecutionYear(getExecutionYear()).stream().map(e -> e.getDegreeCurricularPlan())
                        .map((dcp) -> new TupleDataSourceBean(dcp.getExternalId(), dcp.getPresentationName(getExecutionYear())))
                        .collect(Collectors.toList());

        degreeCurricularPlanDataSource = result.stream().sorted(TupleDataSourceBean.COMPARE_BY_TEXT).collect(Collectors.toList());

        if (degreeCurricularPlan == null) {
            competenceCoursesDataSource = Lists.newArrayList();
            return;
        }

        competenceCoursesDataSource =
                degreeCurricularPlan.getCompetenceCourses().stream()
                        .map(l -> new TupleDataSourceBean(l.getExternalId(), l.getNameI18N().getContent()))
                        .sorted(TupleDataSourceBean.COMPARE_BY_TEXT).collect(Collectors.toList());
    }

    public CompetenceCourse getCompetenceCourses() {
        return competenceCourses;
    }

    public void setCompetenceCourses(CompetenceCourse value) {
        competenceCourses = value;
    }

    public List<TupleDataSourceBean> getCompetenceCoursesDataSource() {
        return competenceCoursesDataSource;
    }

    public DegreeCurricularPlan getDegreeCurricularPlan() {
        return degreeCurricularPlan;
    }
    
    public void setDegreeCurricularPlan(DegreeCurricularPlan degreeCurricularPlan) {
        this.degreeCurricularPlan = degreeCurricularPlan;
    }

    public List<TupleDataSourceBean> getDegreeCurricularPlanDataSource() {
        return degreeCurricularPlanDataSource;
    }

    public ExecutionYear getExecutionYear() {
        return executionYear;
    }

    public void setExecutionYear(ExecutionYear value) {
        executionYear = value;
    }

    public List<TupleDataSourceBean> getExecutionYearDataSource() {
        return executionYearDataSource;
    }

    public BigDecimal getFunctionCost() {
        return functionCost;
    }

    public void setFunctionCost(BigDecimal value) {
        functionCost = value;
    }

}
