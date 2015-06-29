<%@page import="org.fenixedu.academictreasury.ui.tuitiondebtcreation.standalone.OtherTuitionDebtCreationBeanController"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>
<%@ taglib prefix="datatables" uri="http://github.com/dandelion/datatables"%>
<%@ taglib prefix="joda" uri="http://www.joda.org/joda/time/tags" %>

<spring:url var="datatablesUrl"
	value="/javaScript/dataTables/media/js/jquery.dataTables.latest.min.js" />
<spring:url var="datatablesBootstrapJsUrl"
	value="/javaScript/dataTables/media/js/jquery.dataTables.bootstrap.min.js"></spring:url>
<script type="text/javascript" src="${datatablesUrl}"></script>
<script type="text/javascript" src="${datatablesBootstrapJsUrl}"></script>
<spring:url var="datatablesCssUrl"
	value="/CSS/dataTables/dataTables.bootstrap.min.css" />

<link rel="stylesheet" href="${datatablesCssUrl}" />
<spring:url var="datatablesI18NUrl"
	value="/javaScript/dataTables/media/i18n/${portal.locale.language}.json" />
<link rel="stylesheet" type="text/css"
	href="${pageContext.request.contextPath}/CSS/dataTables/dataTables.bootstrap.min.css" />

<!-- Choose ONLY ONE:  bennuToolkit OR bennuAngularToolkit -->
${portal.angularToolkit()}
<%--${portal.toolkit()}--%>

<link
	href="${pageContext.request.contextPath}/static/academicTreasury/css/dataTables.responsive.css"
	rel="stylesheet" />
<script
	src="${pageContext.request.contextPath}/static/academicTreasury/js/dataTables.responsive.js"></script>
<link
	href="${pageContext.request.contextPath}/webjars/datatables-tools/2.2.4/css/dataTables.tableTools.css"
	rel="stylesheet" />
<script
	src="${pageContext.request.contextPath}/webjars/datatables-tools/2.2.4/js/dataTables.tableTools.js"></script>
<link
	href="${pageContext.request.contextPath}/webjars/select2/4.0.0-rc.2/dist/css/select2.min.css"
	rel="stylesheet" />
<script
	src="${pageContext.request.contextPath}/webjars/select2/4.0.0-rc.2/dist/js/select2.min.js"></script>
<script type="text/javascript"
	src="${pageContext.request.contextPath}/webjars/bootbox/4.4.0/bootbox.js"></script>
<script
	src="${pageContext.request.contextPath}/static/academicTreasury/js/omnis.js"></script>

<script
	src="${pageContext.request.contextPath}/webjars/angular-sanitize/1.3.11/angular-sanitize.js"></script>
<link rel="stylesheet" type="text/css"
	href="${pageContext.request.contextPath}/webjars/angular-ui-select/0.11.2/select.min.css" />
<script
	src="${pageContext.request.contextPath}/webjars/angular-ui-select/0.11.2/select.min.js"></script>


<%-- TITLE --%>
<div class="page-header">
	<h1>
		<spring:message
			code="label.TuitionDebtCreation.confirmTuitionDebtCreation" />
		<small></small>
	</h1>
</div>

<%-- NAVIGATION --%>

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

<script>

angular.module('angularAppTuitionDebtCreationBean', ['ngSanitize', 'ui.select','bennuToolkit']).controller('TuitionDebtCreationBeanController', ['$scope', function($scope) {

 	$scope.object=angular.fromJson('${tuitionDebtCreationBeanJson}');
	$scope.postBack = createAngularPostbackFunction($scope); 

}]);

function backToCreate() {
	$('#form').attr('action', $("#backURL").attr('value'));
	$('#form').submit();
}

</script>

<div class="panel panel-primary">
    <div class="panel-heading">
        <h3 class="panel-title">
            <spring:message code="label.TuitionDebtCreationBean.tuitionPaymentPlan" />
        </h3>
    </div>
    <div class="panel-body">
        <form method="post" class="form-horizontal">
            <table class="table">
                <tbody>
                    <tr>
                        <th scope="row" class="col-xs-3"><spring:message code="label.TuitionDebtCreationBean.executionYear" /></th>
                        <td><c:out value='${tuitionPaymentPlan.executionYear.qualifiedName}' /></td>
                    </tr>
                    <tr>
                        <th scope="row" class="col-xs-3"><spring:message code="label.TuitionDebtCreationBean.registration" /></th>
                        <td><c:out value='${tuitionPaymentPlan.degreeCurricularPlan.degree.getPresentationNameI18N(executionYear).content}' /></td>
                    </tr>
                    <tr>
                        <th scope="row" class="col-xs-3"><spring:message code="label.TuitionDebtCreationBean.tuitionPaymentPlan" /></th>
                        <td>
                        	<c:out value='${tuitionPaymentPlan.name.content}' />
                        	&nbsp;
                        	(<c:out value='${tuitionPaymentPlan.conditionsDescription.content}' />)
                        </td>
                    </tr>
                    <tr>
                        <th scope="row" class="col-xs-3"><spring:message code="label.TuitionDebtCreationBean.debtDate" /></th>
						<td><joda:format value="${bean.debtDate}" style="S-" /></td>
                    </tr>
                </tbody>
            </table>
        </form>
    </div>
</div>

<div class="panel panel-primary">
    <div class="panel-heading">
        <h3 class="panel-title">
            <spring:message code="label.manageTuitionPaymentPlan.installments" />
        </h3>
    </div>
    <div class="panel-body">
		<c:if test="${empty installments}">
			<p><em><spring:message code="label.TuitionInstallmentTariff.installments.empty" /></em></p>
		</c:if>
		
		<c:if test="${not empty installments}">
		
			<datatables:table id="installments" row="installment" data="${installments}" 
				cssClass="table responsive table-bordered table-hover" cdn="false" cellspacing="2">
				
				<datatables:column cssStyle="width:10%">
					<datatables:columnHead ><spring:message code="label.TuitionInstallmentTariff.installmentOrder" /></datatables:columnHead>
		
					<p><c:out value="${installment.installmentOrder}" /></p>
				</datatables:column>
				
				<datatables:column className="dt-center" cssStyle="width:40%">
					<datatables:columnHead ><spring:message code="label.TuitionInstallmentTariff.description" /></datatables:columnHead>
					
					<p><c:out value='${installment.description.content}' /></p>
				</datatables:column>
						
				<datatables:column cssStyle="width:15%">
					<datatables:columnHead ><spring:message code="label.TuitionInstallmentTariff.dueDate" /></datatables:columnHead>
		
					<p><joda:format value="${installment.dueDate}" style="S-" /></p>
				</datatables:column>
					
				<datatables:column className="dt-center" cssStyle="width:10%">
					<datatables:columnHead ><spring:message code="label.TuitionInstallmentTariff.vat" /></datatables:columnHead>
			
					<p><c:out value='${installment.vatRate}' /></p>
				</datatables:column>
		
				<datatables:column className="dt-center" cssStyle="width:40%">
					<datatables:columnHead ><spring:message code="label.TuitionInstallmentTariff.amount" /></datatables:columnHead>
			
					<p><c:out value='${debtAccount.finantialInstitution.currency.getValueFor(installment.amount)}' /></p>
				</datatables:column>
			</datatables:table>
		</c:if>
	</div>
</div>

<form id="form" name='form' method="post" class="form-horizontal"
	ng-app="angularAppTuitionDebtCreationBean"
	ng-controller="TuitionDebtCreationBeanController"
	action='${pageContext.request.contextPath}<%= OtherTuitionDebtCreationBeanController.CONFIRMTUITIONDEBTCREATION_URL %>/${debtAccount.externalId}'>
	
	<input id="backURL" type="hidden" name="backURL" value='${pageContext.request.contextPath}<%= OtherTuitionDebtCreationBeanController.BACKTOCREATE_URL %>/${debtAccount.externalId}' />

	<input name="bean" type="hidden" value="{{ object }}" />

	<div class="panel panel-default">
		<div class="panel-footer">
			<input type="button" class="btn btn-default" role="button" value="<spring:message code="label.back" />" onclick="backToCreate()" />
			<input type="submit" class="btn btn-default" role="button" value="<spring:message code="label.confirm" />" />
		</div>
	</div>
</form>

<script>
	$(document).ready(function() {});
</script>
