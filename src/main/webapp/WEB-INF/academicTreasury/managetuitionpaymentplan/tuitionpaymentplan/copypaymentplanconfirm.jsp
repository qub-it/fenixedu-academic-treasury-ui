<%@page import="org.fenixedu.academictreasury.domain.tuition.TuitionCalculationType"%>
<%@page import="org.fenixedu.academictreasury.ui.managetuitionpaymentplan.TuitionPaymentPlanController"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="datatables" uri="http://github.com/dandelion/datatables"%>
<%@ taglib prefix="joda" uri="http://www.joda.org/joda/time/tags" %>

<spring:url var="datatablesUrl" value="/javaScript/dataTables/media/js/jquery.dataTables.latest.min.js"/>
<spring:url var="datatablesBootstrapJsUrl" value="/javaScript/dataTables/media/js/jquery.dataTables.bootstrap.min.js"></spring:url>
<script type="text/javascript" src="${datatablesUrl}"></script>
<script type="text/javascript" src="${datatablesBootstrapJsUrl}"></script>
<spring:url var="datatablesCssUrl" value="/CSS/dataTables/dataTables.bootstrap.min.css"/>

<link rel="stylesheet" href="${datatablesCssUrl}"/>
<spring:url var="datatablesI18NUrl" value="/javaScript/dataTables/media/i18n/${portal.locale.language}.json"/>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CSS/dataTables/dataTables.bootstrap.min.css"/>

${portal.angularToolkit()} 

<link href="${pageContext.request.contextPath}/static/academicTreasury/css/dataTables.responsive.css" rel="stylesheet"/>
<script src="${pageContext.request.contextPath}/static/academicTreasury/js/dataTables.responsive.js"></script>
<link href="${pageContext.request.contextPath}/webjars/datatables-tools/2.2.4/css/dataTables.tableTools.css" rel="stylesheet"/>
<script src="${pageContext.request.contextPath}/webjars/datatables-tools/2.2.4/js/dataTables.tableTools.js"></script>
<link href="${pageContext.request.contextPath}/webjars/select2/4.0.0-rc.2/dist/css/select2.min.css" rel="stylesheet" />
<script src="${pageContext.request.contextPath}/webjars/select2/4.0.0-rc.2/dist/js/select2.min.js"></script>						
<script type="text/javascript" src="${pageContext.request.contextPath}/webjars/bootbox/4.4.0/bootbox.js" ></script>
<script src="${pageContext.request.contextPath}/static/academicTreasury/js/omnis.js"></script>

<script src="${pageContext.request.contextPath}/webjars/angular-sanitize/1.3.11/angular-sanitize.js"></script>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/webjars/angular-ui-select/0.11.2/select.min.css" />
<script src="${pageContext.request.contextPath}/webjars/angular-ui-select/0.11.2/select.min.js"></script>

<script>

angular.module('angularAppCopyTuitionPaymentPlan', ['ngSanitize', 'ui.select','bennuToolkit']).controller('CopyTuitionPaymentPlanController', ['$scope', function($scope) {

 	$scope.object=${tuitionPaymentPlanBeanJson};
	$scope.postBack = createAngularPostbackFunction($scope); 

	//Begin here of Custom Screen business JS - code
	
	$scope.copyPaymentPlan = function() {
		$("#createPaymentPlanform").attr("action", $("#copyPaymentPlanURL").attr("value"));
		$("#createPaymentPlanform").submit();
	}
	
	$scope.backToChooseExecutionYearAndDegreeCurricularPlans = function() {
		$("#createPaymentPlanform").attr("action", $("#backUrl").attr("value"));
		$("#createPaymentPlanform").submit();
	}
 	
}]);
</script>


<%-- TITLE --%>
<div class="page-header">
	<h1>
		<spring:message code="label.manageTuitionPaymentPlan.copyPaymentPlan" />
	</h1>
	
	<h3>
		<spring:message code="label.manageTuitionPaymentPlan.copyPaymentPlan.confirm" />
	</h3>
</div>


<c:if test="${not empty infoMessages}">
	<div class="alert alert-info" role="alert">

		<c:forEach items="${infoMessages}" var="message">
			<p>
				<span class="glyphicon glyphicon glyphicon-ok-sign"
					aria-hidden="true">&nbsp;</span> ${message}
			</p>
		</c:forEach>

	</div>
</c:if>
<c:if test="${not empty warningMessages}">
	<div class="alert alert-warning" role="alert">

		<c:forEach items="${warningMessages}" var="message">
			<p>
				<span class="glyphicon glyphicon-exclamation-sign"
					aria-hidden="true">&nbsp;</span> ${message}
			</p>
		</c:forEach>

	</div>
</c:if>
<c:if test="${not empty errorMessages}">
	<div class="alert alert-danger" role="alert">

		<c:forEach items="${errorMessages}" var="message">
			<p>
				<span class="glyphicon glyphicon-exclamation-sign"
					aria-hidden="true">&nbsp;</span> ${message}
			</p>
		</c:forEach>

	</div>
</c:if>

<div class="panel panel-primary">
	<div class="panel-heading">
		<h3 class="panel-title">
			<spring:message code="label.details" />
		</h3>
	</div>
	<div class="panel-body">
		<table class="table">
			<tbody>
				<tr>
					<th scope="row" class="col-xs-3"><spring:message
							code="label.TuitionPaymentPlan.executionYear" /></th>
					<td><c:out value='${tuitionPaymentPlanBean.executionYear.qualifiedName}' /></td>
				</tr>
				<tr>
					<th scope="row" class="col-xs-3"><spring:message
							code="label.TuitionPaymentPlan.degreeCurricularPlans" /></th>
					<td>
						<c:forEach items="${tuitionPaymentPlanBean.degreeCurricularPlans}" var="dcp">
							<p>${dcp.getPresentationName(tuitionPaymentPlanBean.executionYear)}</p>
						</c:forEach>
					</td>
				</tr>

				<c:if test="${tuitionPaymentPlanBean.defaultPaymentPlan}">
					<tr>
						<th scope="row" class="col-xs-3"><spring:message
								code="label.TuitionPaymentPlan.defaultPaymentPlan" /></th>
						<td><spring:message code="label.true" /></td>
					</tr>
				</c:if>
				
				<c:if test="${tuitionPaymentPlanBean.getRegistrationRegimeType() != null}">
					<tr>
						<th scope="row" class="col-xs-3"><spring:message
								code="label.TuitionPaymentPlan.registrationRegimeType" /></th>
						<td><c:out value="${tuitionPaymentPlanBean.registrationRegimeType.localizedName}" /></td>
					</tr>
				</c:if>
				
				<c:if test="${tuitionPaymentPlanBean.getRegistrationProtocol() != null}">
					<tr>
						<th scope="row" class="col-xs-3"><spring:message
								code="label.TuitionPaymentPlan.registrationProtocol" /></th>
						<td><c:out value="${tuitionPaymentPlanBean.registrationProtocol.description.content}" /></td>
					</tr>
				</c:if>
				
				<c:if test="${tuitionPaymentPlanBean.getIngression() != null}">
					<tr>
						<th scope="row" class="col-xs-3"><spring:message
								code="label.TuitionPaymentPlan.ingression" /></th>
						<td><c:out value="${tuitionPaymentPlanBean.ingression.localizedName}" /></td>
					</tr>
				</c:if>
				
				<c:if test="${tuitionPaymentPlanBean.getCurricularYear() != null}">
					<tr>
						<th scope="row" class="col-xs-3"><spring:message
								code="label.TuitionPaymentPlan.curricularYear" /></th>
						<td><c:out value="${tuitionPaymentPlanBean.curricularYear.year}" /></td>
					</tr>
				</c:if>
										
				<c:if test="${tuitionPaymentPlanBean.getExecutionSemester() != null}">
					<tr>
						<th scope="row" class="col-xs-3"><spring:message code="label.TuitionPaymentPlan.curricularSemester" /></th>
						<td><c:out value="${tuitionPaymentPlanBean.executionSemester.childOrder}" /></td>
					</tr>
				</c:if>

				<c:if test="${tuitionPaymentPlanBean.getStatuteType() != null}">
					<tr>
						<th scope="row" class="col-xs-3"><spring:message
								code="label.TuitionPaymentPlan.statuteType" /></th>
						<td><c:out value="${tuitionPaymentPlanBean.statuteType.name.content}" /></td>
					</tr>
				</c:if>
					
				<c:if test="${tuitionPaymentPlanBean.firstTimeStudent}">
					<tr>
						<th scope="row" class="col-xs-3"><spring:message
								code="label.TuitionPaymentPlan.firstTimeStudent" /></th>
						<td><spring:message code="label.true" /></td>
					</tr>
				</c:if>
					
				<c:if test="${tuitionPaymentPlanBean.customized}">
					<tr>
						<th scope="row" class="col-xs-3"><spring:message
								code="label.TuitionPaymentPlan.customized" /></th>
						<td><spring:message code="label.true" /></td>
					</tr>
					
					<tr>
						<th scope="row" class="col-xs-3"><spring:message
								code="label.TuitionPaymentPlan.customizedName" /></th>
						<td>${tuitionPaymentPlanBean.name}</td>
					</tr>
				</c:if>
				
			</tbody>
		</table>
		
		<form method="post" class="form-horizontal">
		</form>
	</div>
</div>

<h3>
	<bean:message code="label.manageTuitionPaymentPlan.installments" />
</h3>

<datatables:table id="installments" row="installment" data="${tuitionPaymentPlanBean.tuitionInstallmentBeans}" 
	cssClass="table responsive table-bordered table-hover" cdn="false" cellspacing="2">
	
	<datatables:column cssStyle="width:8%">
		<datatables:columnHead ><p style="align: center"><spring:message code="label.TuitionInstallmentTariff.installmentOrder" /></p></datatables:columnHead>
		<p style="align: center"><c:out value="${installment.installmentOrder}" /></p>
	</datatables:column>
	
	<datatables:column className="dt-center" cssStyle="width:40%">
		<datatables:columnHead ><p style="align: center"><spring:message code="label.TuitionInstallmentTariff.amount" /></p></datatables:columnHead>

		<p><strong>
			<c:out value="${installment.tuitionInstallmentProduct.name.content}" />
		</strong></p>
		
		<c:choose>
			<c:when test="${installment.tuitionCalculationType.fixedAmount}" >
				<p>	
					<em><spring:message code="TuitionCalculationType.FIXED_AMOUNT" />: </em>
					<c:out value="${finantialEntity.finantialInstitution.currency.getValueFor(installment.fixedAmount)}" />
				</p>
			</c:when>
			<c:when test="${installment.tuitionCalculationType.ects}">
				<p>
					<em>
						<c:out value="${installment.tuitionCalculationType.descriptionI18N.content}" />
						&nbsp;
						[<c:out value="${installment.ectsCalculationType.descriptionI18N.content}" />]:
						&nbsp;
					</em>

					<c:if test="${installment.ectsCalculationType.fixedAmount}">
						<spring:message code="label.TuitionInstallmentTariff.amountPerEcts" 
							arguments="${finantialEntity.finantialInstitution.currency.getValueFor(installment.amountPerEctsOrUnit)}" />
					</c:if>
					<c:if test="${installment.ectsCalculationType.dependentOnDefaultPaymentPlan}">
						<spring:message code="label.TuitionInstallmentTariff.defaultPaymentPlanIndexed.ectsParameters"
							arguments="${installment.factor},${installment.totalEctsOrUnits}" />
					</c:if>
				</p>
			</c:when>
			<c:when test="${installment.tuitionCalculationType.units}">
				<p>
					<strong>
						<c:out value="${installment.tuitionCalculationType.descriptionI18N.content}" />
						&nbsp;
						[<c:out value="${installment.ectsCalculationType.descriptionI18N.content}" />]
					</strong>
				</p>

				<c:if test="${installment.ectsCalculationType.fixedAmount}">
					<p>&nbsp;</p>
					
					<p><spring:message code="label.TuitionInstallmentTariff.amountPerUnits" 
						arguments="${finantialEntity.finantialInstitution.currency.getValueFor(installment.amountPerEctsOrUnit)}" /></p>
				</c:if>
				<c:if test="${installment.ectsCalculationType.dependentOnDefaultPaymentPlan}">
					<p><em><spring:message code="label.TuitionInstallmentTariff.defaultPaymentPlanIndexed.unitsParameters"
						arguments="${installment.factor},${installment.totalEctsOrUnits}" /></em></p>
				</c:if>
			</c:when>
		</c:choose>
		
			<c:if test="${installment.academicalActBlockingOff}"> 
			<p><span class="label label-warning">
					<spring:message code="label.TuitionPaymentPlan.academicalActBlockingOff" />
			</span></p>
		</c:if>
		
	</datatables:column>

	<datatables:column cssStyle="width:10%">
		<datatables:columnHead><p style="align: center"><spring:message code="label.TuitionInstallmentTariff.beginDate" /></p></datatables:columnHead>
		<p style="align: center"><joda:format value="${installment.beginDate}" style="S-" /></p>
	</datatables:column>
	
	<datatables:column cssStyle="width:15%">
		<datatables:columnHead ><p style="align: center"><spring:message code="label.TuitionInstallmentTariff.dueDate" /></p></datatables:columnHead>

		<c:choose>
			<c:when test="${installment.dueDateCalculationType.noDueDate}">
				<spring:message code="label.TuitionInstallmentTariff.noDueDate" />
			</c:when>
			<c:when test="${installment.dueDateCalculationType.fixedDate}">
				<joda:format value="${installment.fixedDueDate}" style="S-" />
			</c:when>
			<c:when test="${installment.dueDateCalculationType.daysAfterCreation}">
				<spring:message code="label.TuitionInstallmentTariff.daysAfterCreation" arguments="${installment.numberOfDaysAfterCreationForDueDate}" />
			</c:when>
			<c:when test="${installment.dueDateCalculationType.bestOfFixedDateAndDaysAfterCreation}">
				<p>
					<joda:format var="fixedDueDate" value="${installment.fixedDueDate}" style="S-" />
					<spring:message code="label.TuitionInstallmentTariff.bestOfFixedDateAndDaysAfterCreation" 
						arguments="${fixedDueDate},${installment.numberOfDaysAfterCreationForDueDate}" />
				</p>
			</c:when>				
		</c:choose>
	</datatables:column>

	<datatables:column cssStyle="width:25%">
		<datatables:columnHead ><p style="align: center"><spring:message code="label.TuitionInstallmentTariff.interests" /></p></datatables:columnHead>
		<c:if test="${not installment.applyInterests}">
			<p><strong><spring:message code="label.TuitionInstallmentTariff.interests.not.applied" /></strong></p>
		</c:if>
		<c:if test="${installment.applyInterests}">
			<p><strong>[<c:out value="${installment.interestType.descriptionI18N.content}" />]</strong></p>
			
			<c:choose>
				
				<c:when test="${installment.interestType.fixedAmount}">
					<p>
						<strong><spring:message code="label.TuitionInstallmentTariff.interestFixedAmount" />:&nbsp;</strong>
						<c:out value="${finantialEntity.finantialInstitution.currency.getValueFor(installment.interestFixedAmount)}" />
					</p>
				</c:when>
			</c:choose>
		</c:if>
	</datatables:column>
	
</datatables:table>

<form id="createPaymentPlanform" name='createPaymentPlanform' method="post" class="form-horizontal"
    ng-app="angularAppCopyTuitionPaymentPlan"
    ng-controller="CopyTuitionPaymentPlanController"
    action='${pageContext.request.contextPath}<%= TuitionPaymentPlanController.COPY_PAYMENT_PLAN_URL %>/${finantialEntity.externalId}/${executionYear.externalId}/${degreeCurricularPlan.externalId}'>
	
    <input id="copyPaymentPlanURL" type="hidden" name="copyPaymentPlanURL" 
        value="${pageContext.request.contextPath}<%= TuitionPaymentPlanController.COPY_PAYMENT_PLAN_URL %>/${finantialEntity.externalId}/${executionYear.externalId}/${degreeCurricularPlan.externalId}" />
	
    <input id="backUrl" type="hidden" name="backUrl" 
        value="${pageContext.request.contextPath}<%= TuitionPaymentPlanController.BACK_COPY_PAYMENT_PLAN_CHOOSE_EXECUTION_YEAR_URL %>/${finantialEntity.externalId}/${executionYear.externalId}/${degreeCurricularPlan.externalId}" />

    <input id="bean" name="bean" type="hidden" value="{{ object }}" />
    
    <div class="panel panel-default">
        <div class="panel-footer">
            <button type="button" class="btn btn-default" role="button" ng-click="backToChooseExecutionYearAndDegreeCurricularPlans()">
            <span class="glyphicon glyphicon-chevron-left"
                aria-hidden="true"></span> &nbsp;
            <spring:message code="label.back" />
            </button>
            <button type="submit" class="btn btn-primary" role="button" ng-click="copyPaymentPlan()">
            <span class="glyphicon glyphicon-ok"
                aria-hidden="true"></span>&nbsp;
                <spring:message code="label.finish" />
            </button>
        </div>
     </div>
</form>

