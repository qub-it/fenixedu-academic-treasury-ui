<%@page import="org.fenixedu.academictreasury.ui.academictaxdebtcreation.AcademicTaxDebtCreationBeanController"%>
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
			code="label.AcademicTaxDebtCreation.confirmAcademicTaxDebtCreation" />
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

angular.module('angularAppAcademicTaxDebtCreationBean', ['ngSanitize', 'ui.select','bennuToolkit']).controller('AcademicTaxDebtCreationBeanController', ['$scope', function($scope) {

 	$scope.object=angular.fromJson('${academicTaxDebtCreationBeanJson}');
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
            <spring:message code="label.AcademicTaxDebtCreationBean.academicTax" />
        </h3>
    </div>
    <div class="panel-body">
    	<form method="post" class="form-horizontal">
           <table class="table">
               <tbody>
                   <tr>
                       <th scope="row" class="col-xs-3"><spring:message code="label.AcademicTaxDebtCreationBean.executionYear" /></th>
                       <td><c:out value='${bean.executionYear.qualifiedName}' /></td>
                   </tr>
                   <tr>
                       <th scope="row" class="col-xs-3"><spring:message code="label.AcademicTaxDebtCreationBean.registration" /></th>
                       <td><c:out value='${bean.registration.degree.getPresentationNameI18N(executionYear).content}' /></td>
                   </tr>
                   <tr>
                       <th scope="row" class="col-xs-3"><spring:message code="label.AcademicTaxDebtCreationBean.academicTax" /></th>
                       <td>
                       	<c:out value='${bean.academicTax.product.name.content}' />
                       </td>
                   </tr>
                   <tr>
                       <th scope="row" class="col-xs-3"><spring:message code="label.AcademicTaxDebtCreationBean.debtDate" /></th>
					<td><joda:format value="${bean.debtDate}" style="S-" /></td>
                   </tr>
                   
                   <c:if test="${not empty debt}">
					<tr className="dt-center" cssStyle="width:40%">
						<th scope="row" class="col-xs-3"><spring:message code="label.AcademicTaxDebtCreationBean.debtDescription" /></th>
						
						<td><c:out value='${debt.description.content}' /></td>
					</tr>
							
					<tr cssStyle="width:15%">
						<th scope="row" class="col-xs-3"><spring:message code="label.AcademicTaxDebtCreationBean.dueDate" /></th>
			
						<td><joda:format value="${debt.dueDate}" style="S-" /></td>
					</tr>
						
					<tr className="dt-center" cssStyle="width:10%">
						<th scope="row" class="col-xs-3"><spring:message code="label.AcademicTaxDebtCreationBean.vat" /></th>
				
						<td><c:out value='${debt.vatRate}' /></td>
					</tr>
			
					<tr className="dt-center" cssStyle="width:40%">
						<th scope="row" class="col-xs-3"><spring:message code="label.AcademicTaxDebtCreationBean.amount" /></th>
				
						<td><c:out value='${debtAccount.finantialInstitution.currency.getValueFor(debt.amount)}' /></th>
					</tr>
				</c:if>
               </body>
           </table>
          </form>
    </div>
</div>

<form id="form" name='form' method="post" class="form-horizontal"
	ng-app="angularAppAcademicTaxDebtCreationBean"
	ng-controller="AcademicTaxDebtCreationBeanController"
	action='${pageContext.request.contextPath}<%= AcademicTaxDebtCreationBeanController.CONFIRMACADEMICTAXDEBTCREATION_URL %>/${debtAccount.externalId}'>
	
	<input id="backURL" type="hidden" name="backURL" value='${pageContext.request.contextPath}<%= AcademicTaxDebtCreationBeanController.BACKTOCREATE_URL %>/${debtAccount.externalId}' />

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
