<%@page
    import="org.fenixedu.academictreasury.ui.manageacademicdebtgenerationrule.AcademicDebtGenerationRuleController"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>
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
            code="label.manageacademicdebtgenerationrule.editDegreeCurricularPlans" />
        <small><c:out value="${academicDebtGenerationRuleType.name}" />&nbsp;[<c:out value="${executionYear.qualifiedName}" />]</small>
    </h1>
</div>

<%-- NAVIGATION --%>
<div class="well well-sm" style="display: inline-block">
    <span class="glyphicon glyphicon-arrow-left" aria-hidden="true"></span>&nbsp;<a
        class=""
        href="${pageContext.request.contextPath}<%= AcademicDebtGenerationRuleController.SEARCH_URL %>/${academicDebtGenerationRuleType.externalId}/${executionYear.externalId}"><spring:message
            code="label.event.back" /></a> &nbsp;
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

<script>

angular.module('angularAppAcademicDebtGenerationRule', ['ngSanitize', 'ui.select']).controller('AcademicDebtGenerationRuleController', ['$scope', function($scope) {

	 $scope.booleanvalues = [ {
         name : '<spring:message code="label.no"/>',
         value : false
     }, {
         name : '<spring:message code="label.yes"/>',
         value : true
     } ];
	 
 	$scope.object=${academicDebtGenerationRuleBeanJson};
	$scope.postBack = createAngularPostbackFunction($scope); 
 	
	 
	$scope.onDegreeTypeChange = function(degreeType, model) {
		$('#degree-type-form').attr('action', $("#degree-type-postback").attr('value'));
		$('#degree-type-form').submit();
	}
	
	$scope.onExecutionYearChange = function(executionYear, model) {
		$('#execution-year-select-form').attr('action', $("#execution-year-postback").attr('value'));
		$('#execution-year-select-form').submit();
	}
	
	$scope.toggleDegreeCurricularPlans = function toggleSelection(dcpId) {
		var idx = $scope.object.degreeCurricularPlansToAdd.indexOf(dcpId);
		
		// is currently selected
		if (idx > -1) {
		  $scope.object.degreeCurricularPlansToAdd.splice(idx, 1);
		} else {
			// is newly selected
		  $scope.object.degreeCurricularPlansToAdd.push(dcpId);
		}
	};

	
	
	
}]);
</script>

<div ng-app="angularAppAcademicDebtGenerationRule" ng-controller="AcademicDebtGenerationRuleController">

    <h3><spring:message code="label.AcademicDebtGenerationRule.degreeCurricularPlans" /></h3>
    
<c:choose>
	<c:when test="${not empty academicDebtGenerationRuleBean.degreeCurricularPlans}">
		<table id="searchacademicdebtgenerationruleTable"
			class="table responsive table-bordered table-hover" width="100%">
			<thead>
				<tr>
					<%--!!!  Field names here --%>
					<th><spring:message
							code="label.AcademicDebtGenerationRuleEntry.degreeType" /></th>
					<th><spring:message
							code="label.AcademicDebtGenerationRuleEntry.degreeCurricularPlan" /></th>
					<%-- Operations Column --%>
					<th></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="dcp" items="${academicDebtGenerationRuleBean.degreeCurricularPlans}" varStatus="loopStatus">
					<tr>
						<td>
							<p><c:out value="${dcp.degree.degreeType.name.content}" /></p>
						</td>
						<td>
							<p><c:out value="[${dcp.degree.code}] ${dcp.getPresentationName(academicDebtGenerationRuleBean.executionYear)}" /></p>
						</td>
						<td>
							<form name='form' method="post" class="form-horizontal"
								action='${pageContext.request.contextPath}<%= AcademicDebtGenerationRuleController.EDIT_DCP_REMOVEDEGREECURRICULARPLAN_URL %>/${academicDebtGenerationRuleType.externalId}/${executionYear.externalId}/${academicDebtGenerationRule.externalId}/${loopStatus.index}'>
								<input name="bean" type="hidden" value="{{ object }}" />
								
								<button class="btn btn-xs btn-default">
									<span class="glyphicon glyphicon-trash" aria-hidden="true"></span>
									<spring:message code="label.delete" />
								</button>
							</form>
						</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</c:when>
	<c:otherwise>
		<div class="alert alert-warning" role="alert">
			<p>
				<span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true">&nbsp;</span>
				<spring:message code="label.noResultsFound" />
			</p>
		</div>
	</c:otherwise>
</c:choose>
    
    <form id="degree-type-form" name='form' method="post" class="form-horizontal"
        action='${pageContext.request.contextPath}<%= AcademicDebtGenerationRuleController.EDIT_DCP_ADDDEGREECURRICULARPLANS_URL %>/${academicDebtGenerationRuleType.externalId}/${executionYear.externalId}/${academicDebtGenerationRule.externalId}'>

        <input name="bean" type="hidden" value="{{ object }}" />

		<input id="degree-type-postback" type="hidden" name="postback"
			value='${pageContext.request.contextPath}<%= AcademicDebtGenerationRuleController.EDIT_DCP_CHOOSEDEGREETYPEPOSTBACK_URL %>/${academicDebtGenerationRuleType.externalId}/${executionYear.externalId}/${academicDebtGenerationRule.externalId}' />

        <div class="panel panel-default">
            <div class="panel-body">
            
				<div class="form-group row">
					<div class="col-sm-2 control-label"><spring:message code="label.AcademicDebtGenerationRule.degreeType"/></div> 
					
					<div class="col-sm-6">
						<ui-select id="academicDebtGenerationRule_degreeType" name="degreeType" ng-model="$parent.object.degreeType" theme="bootstrap" ng-disabled="disabled" 
							on-select="onDegreeTypeChange($product, $model)" >
							<ui-select-match>{{$select.selected.text}}</ui-select-match>
							<ui-select-choices repeat="degreeType.id as degreeType in object.degreeTypeDataSource | filter: $select.search">
								<span ng-bind-html="degreeType.text | highlight: $select.search"></span>
							</ui-select-choices>
						</ui-select>				
					</div>
				</div>		
				<div class="form-group row">
					<div class="col-sm-2 control-label"><spring:message code="label.AcademicDebtGenerationRule.degreeCurricularPlans"/></div> 
					<div class="col-sm-6">
	                    <div ng-hide="object.degreeCurricularPlanDataSource" class="alert alert-warning">
	                        <spring:message code="label.AcademicDebtGenerationRule.degreeCurricularPlanDataSource.is.empty"/>
	                    </div>
						<div ng-repeat="dcp in object.degreeCurricularPlanDataSource" >
	                        <div class="checkbox">
	        					<input class="checkbox pull-left"  name="{{dcp.id}}" type="checkbox" id="{{dcp.id}}"
	        					ng-checked="object.degreeCurricularPlans.indexOf(dcp.id) > -1"
	        					ng-click="toggleDegreeCurricularPlans(dcp.id)" />
	        					<span><label for="{{dcp.id}}">{{dcp.text}}</label></span>
	                        </div>
	                    </div>
					</div>
				</div>		
				<div class="form-group row">
					<div class="col-sm-8">
			            <input type="submit" class="btn btn-default" role="button" value="<spring:message code="label.add" />" style="float:right;"/>
					</div>
				</div>
            
			</div>
		</div>
    </form>
    
    <form id="form" name='form' method="post" class="form-horizontal"
        action='${pageContext.request.contextPath}<%= AcademicDebtGenerationRuleController.EDIT_DEGREECURRICULARPLANS_URL %>/${academicDebtGenerationRuleType.externalId}/${executionYear.externalId}/${academicDebtGenerationRule.externalId}'>

        <input name="bean" type="hidden" value="{{ object }}" />

        <div class="panel panel-default">
            <div class="panel-footer">
                <input type="submit" class="btn btn-default"
                    role="button"
                    value="<spring:message code="label.submit" />" />
            </div>
        </div>
	</div>    

</div>
