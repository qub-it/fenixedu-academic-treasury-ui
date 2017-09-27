<%@page import="org.fenixedu.treasury.ui.accounting.managecustomer.DebtAccountController"%>
<%@page import="org.fenixedu.academictreasury.ui.integration.tuitioninfo.ERPTuitionInfoExportOperationController"%>
<%@page import="org.fenixedu.academictreasury.ui.integration.tuitioninfo.ERPTuitionInfoController"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<spring:url var="datatablesUrl" value="/javaScript/dataTables/media/js/jquery.dataTables.latest.min.js"/>
<spring:url var="datatablesBootstrapJsUrl" value="/javaScript/dataTables/media/js/jquery.dataTables.bootstrap.min.js"></spring:url>
<script type="text/javascript" src="${datatablesUrl}"></script>
<script type="text/javascript" src="${datatablesBootstrapJsUrl}"></script>
<spring:url var="datatablesCssUrl" value="/CSS/dataTables/dataTables.bootstrap.min.css"/>

<link rel="stylesheet" href="${datatablesCssUrl}"/>
<spring:url var="datatablesI18NUrl" value="/javaScript/dataTables/media/i18n/${portal.locale.language}.json"/>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CSS/dataTables/dataTables.bootstrap.min.css"/>

<!-- Choose ONLY ONE:  bennuToolkit OR bennuAngularToolkit -->
${portal.angularToolkit()} 
<%--${portal.toolkit()}--%>

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

<%-- TITLE --%>
<div class="page-header">
    <h1>
        <spring:message code="label.ERPTuitionInfo.create.title" />
        <small></small>
    </h1>
</div>

<%-- NAVIGATION --%>
<div class="well well-sm" style="display: inline-block">
    <span class="glyphicon glyphicon-arrow-left" aria-hidden="true"></span>
    &nbsp; <a class=""
        href="${pageContext.request.contextPath}<%= DebtAccountController.READ_URL %>${debtAccount.externalId}">
        <spring:message code="label.event.back" />
    </a> &nbsp;
</div>

<c:if test="${not empty infoMessages}">
    <div class="alert alert-info" role="alert">

        <c:forEach items="${infoMessages}" var="message">
            <p>
                <span class="glyphicon glyphicon glyphicon-ok-sign" aria-hidden="true">&nbsp;</span> ${message}
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

angular.module('myAngularApp', ['ngSanitize', 'ui.select','bennuToolkit']).controller('myController', ['$scope', function($scope) {

 	$scope.object=${beanJson};
	
	$scope.booleanvalues = [
		{
			name : '<spring:message code="label.no"/>',
			value : false
		},
		{
			name : '<spring:message code="label.yes"/>',
			value : true
		} ];
 	
 	$scope.postBack = createAngularPostbackFunction($scope); 
}]);

</script>

<div class="panel panel-primary">
    <div class="panel-heading">
        <h3 class="panel-title">
            <spring:message code="label.details" />
        </h3>
    </div>
    <div class="panel-body">
        <form method="post" class="form-horizontal">
            <table class="table">
                <tbody>
                    <tr>
                        <th scope="row" class="col-xs-3"><spring:message code="label.ERPTuitionInfo.customer" /></th>
                        <td><strong><c:out value='${customer.businessIdentification} - ${customer.name}' /></strong></td>
                    </tr>
                </tbody>
            </table>
        </form>
    </div>
</div>

<form id="form" name='form' method="post" class="form-horizontal" ng-app="myAngularApp" ng-controller="myController"
    action='${pageContext.request.contextPath}<%= ERPTuitionInfoController.CREATE_URL %>/${debtAccount.externalId}'>

	<input type="hidden" name="postback"
		value='${pageContext.request.contextPath}<%= ERPTuitionInfoController.CREATEPOSTBACK_URL %>/${debtAccount.externalId}' />

	<input name="bean" type="hidden" value="{{ object }}" />
	
    <div class="panel panel-default">
        <div class="panel-body">
    
 			<div class="form-group row">
				<div class="col-sm-2 control-label">
					<spring:message code="label.ERPTuitionInfo.executionYear" />
				</div> 
				
				<div class="col-sm-8">
					<ui-select id="erpTuitionInfoBean_executionYear" name="executionYear" ng-model="$parent.object.executionYear" 
						theme="bootstrap" ng-disabled="disabled" on-select="postBack($model)" >
						<ui-select-match>{{$select.selected.text}}</ui-select-match>
						<ui-select-choices repeat="executionYear.id as executionYear in object.executionYearDataSource | filter: $select.search">
							<span ng-bind-html="executionYear.text | highlight: $select.search"></span>
						</ui-select-choices>
					</ui-select>				
				</div>
			</div>

			<div class="form-group row">
				<div class="col-sm-2 control-label">
					<spring:message code="label.ERPTuitionInfo.erpTuitionInfoType" />
				</div> 
				
				<div class="col-sm-8">
					<ui-select id="erpTuitionInfoBean_erpTuitionInfoType" name="erpTuitionInfoType" ng-model="$parent.object.erpTuitionInfoType" 
						theme="bootstrap" ng-disabled="disabled" on-select="postBack($model)" >
						<ui-select-match>{{$select.selected.text}}</ui-select-match>
						<ui-select-choices repeat="erpTuitionInfoType.id as erpTuitionInfoType in object.erpTuitionInfoTypeDataSource | filter: $select.search">
							<span ng-bind-html="erpTuitionInfoType.text | highlight: $select.search"></span>
						</ui-select-choices>
					</ui-select>				
				</div>
			</div>

        </div>
        <div class="panel-footer">
            <input type="submit" class="btn btn-default" role="button" value="<spring:message code="label.submit" />" />
        </div>
    </div>
</form>

<script>
	$(document).ready(function() {
	});
</script>
