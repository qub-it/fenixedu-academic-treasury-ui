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
            code="label.manageacademicdebtgenerationrule.createAcademicDebtGenerationRule" />
        <small></small>
    </h1>
</div>

<%-- NAVIGATION --%>
<div class="well well-sm" style="display: inline-block">
    <span class="glyphicon glyphicon-arrow-left" aria-hidden="true"></span>&nbsp;<a
        class=""
        href="${pageContext.request.contextPath}/academictreasury/manageacademicdebtgenerationrule/academicdebtgenerationrule/"><spring:message
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

 	$scope.object=angular.fromJson('${academicDebtGenerationRuleBeanJson}');
	$scope.postBack = createAngularPostbackFunction($scope); 
 	
	 $scope.booleanvalues = [ {
         name : '<spring:message code="label.no"/>',
         value : false
     }, {
         name : '<spring:message code="label.yes"/>',
         value : true
     } ];
}]);
</script>

<div
	ng-app="angularAppAcademicDebtGenerationRule"
	ng-controller="AcademicDebtGenerationRuleController">

<h3><spring:message code="label.AcademicDebtGenerationRule.associated.products" /></h3>


<c:choose>
	<c:when test="${not empty academicDebtGenerationRuleBean.entries}">
		<table id="searchacademicdebtgenerationruleTable"
			class="table responsive table-bordered table-hover" width="100%">
			<thead>
				<tr>
					<%--!!!  Field names here --%>
					<th><spring:message
							code="label.AcademicDebtGenerationRuleEntry.product" /></th>
					<th><spring:message
							code="label.AcademicDebtGenerationRuleEntry.createDebt" /></th>
					<%-- Operations Column --%>
					<th></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="entry" items="${academicDebtGenerationRuleBean.entries}" varStatus="loopStatus">
					<tr>
						<td>
							<p>${entry.product.name.content}</p>
						</td>
						<td>
							<c:if test="${entry.createDebt}">
								<p><strong><spring:message code="label.true" /></strong></p>
							</c:if>
	
							<c:if test="${not entry.createDebt}">
								<p><strong><spring:message code="label.false" /></strong></p>
							</c:if>
						</td>
						<td>
							<form name='form' method="post" class="form-horizontal"
								action='${pageContext.request.contextPath}<%= AcademicDebtGenerationRuleController.REMOVEPRODUCT_URL %>/${loopStatus.index}'>
								<input name="bean" type="hidden" value="{{ object }}" />
								
								<input type="submit" class="btn btn-xs btn-default" role="button" value="<spring:message code="label.delete" />" />
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

<form id="productForm" name='form' method="post" class="form-horizontal"
	action='${pageContext.request.contextPath}<%= AcademicDebtGenerationRuleController.ADDPRODUCT_URL %>'>

	<input name="bean" type="hidden" value="{{ object }}" />

	<div class="panel panel-default">
		<div class="panel-body">
			<div class="form-group row">
				<div class="col-sm-2 control-label">
					<spring:message
						code="label.AcademicDebtGenerationRuleEntry.product" />
				</div>

				<div class="col-sm-4">
					<%-- Relation to side 1 drop down rendered in input --%>
					<ui-select id="academicDebtGenerationRule_product"
						class="" name="product"
						ng-model="$parent.object.product" theme="bootstrap"
						ng-disabled="disabled"> <ui-select-match>{{$select.selected.text}}</ui-select-match>
					<ui-select-choices
						repeat="product.id as product in object.productDataSource | filter: $select.search">
					<span ng-bind-html="product.text | highlight: $select.search"></span>
					</ui-select-choices> </ui-select>
				</div>
			</div>
			<div class="form-group row">
				<div class="col-sm-2 control-label">
					<spring:message
						code="label.AcademicDebtGenerationRuleEntry.createDebt" />
				</div>

				<div class="col-sm-2">
					<select id="academicDebtGenerationRule_createDebt"
						name="aggregateondebitnote" class=""
						ng-model="object.createDebt" ng-options="bvalue.value as bvalue.name for bvalue in booleanvalues">
					</select>
					<script>
		$("#academicDebtGenerationRule_createDebt").select2().select2('val', '<c:out value='${not empty param.createDebt ? param.createDebt : academicDebtGenerationRule.createDebt }'/>');
	</script>
                    </div>
                </div>
            </div>
            <div class="panel-footer">
                <input type="submit" class="btn btn-default"
                    role="button"
                    value="<spring:message code="label.add" />" />
            </div>
        </div>

    </form>

    <h3>
        <spring:message code="label.AcademicDebtGenerationRule.rules" />
    </h3>

    <form name='form' method="post" class="form-horizontal"
        action='${pageContext.request.contextPath}<%= AcademicDebtGenerationRuleController.CREATE_URL %>'>

        <input name="bean" type="hidden" value="{{ object }}" />

        <div class="panel panel-default">
            <div class="panel-body">
                <div class="form-group row">
                    <div class="col-sm-2 control-label">
                        <spring:message
                            code="label.AcademicDebtGenerationRule.executionYear" />
                    </div>

                    <div class="col-sm-4">
                        <%-- Relation to side 1 drop down rendered in input --%>
                        <ui-select
                            id="academicDebtGenerationRule_executionYear"
                            class="" name="executionyear"
                            ng-model="$parent.object.executionYear"
                            theme="bootstrap" ng-disabled="disabled">
                        <ui-select-match>{{$select.selected.text}}</ui-select-match>
                        <ui-select-choices
                            repeat="executionYear.id as executionYear in object.executionYearDataSource | filter: $select.search">
                        <span
                            ng-bind-html="executionYear.text | highlight: $select.search"></span>
                        </ui-select-choices> </ui-select>
                    </div>
                </div>
                
                <div class="form-group row">
                    <div class="col-sm-2 control-label">
                        <spring:message
                            code="label.AcademicDebtGenerationRule.aggregateOnDebitNote" />
                    </div>

                    <div class="col-sm-2">
                        <select
                            id="academicDebtGenerationRule_aggregateOnDebitNote"
                            name="aggregateondebitnote"
                            class="form-control"
                            ng-model="object.aggregateOnDebitNote" ng-options="bvalue.value as bvalue.name for bvalue in booleanvalues">>
                           </option>
                        </select>
                        <script>
		$("#academicDebtGenerationRule_aggregateOnDebitNote").select2().select2('val', '<c:out value='${not empty param.aggregateondebitnote ? param.aggregateondebitnote : academicDebtGenerationRuleBean.aggregateOnDebitNote }'/>');
	</script>
                    </div>
                </div>
                <div class="form-group row"
                    ng-show="(object.aggregateOnDebitNote === true) || (object.aggregateOnDebitNote == 'true')">
                    <div class="col-sm-2 control-label">
                        <spring:message
                            code="label.AcademicDebtGenerationRule.aggregateAllOrNothing" />
                    </div>

                    <div class="col-sm-2">
                        <select
                            id="academicDebtGenerationRule_aggregateAllOrNothing"
                            name="aggregateallornothing"
                            class="form-control"
                            ng-model="object.aggregateAllOrNothing" ng-options="bvalue.value as bvalue.name for bvalue in booleanvalues">>
                        </select>
                        <script>
		$("#academicDebtGenerationRule_aggregateAllOrNothing").select2().select2('val', '<c:out value='${not empty param.aggregateallornothing ? param.aggregateallornothing : academicDebtGenerationRuleBean.aggregateAllOrNothing }'/>');
	</script>
                    </div>
                </div>
                <div class="form-group row"
                    ng-show="(object.aggregateOnDebitNote === true) || (object.aggregateOnDebitNote == 'true')">
                    <div class="col-sm-2 control-label">
                        <spring:message
                            code="label.AcademicDebtGenerationRule.closeDebitNote" />
                    </div>

                    <div class="col-sm-2">
                        <select
                            id="academicDebtGenerationRule_closeDebitNote"
                            name="closedebitnote" class="form-control"
                            ng-model="object.closeDebitNote" ng-options="bvalue.value as bvalue.name for bvalue in booleanvalues">>
                        </select>
                        <script>
		$("#academicDebtGenerationRule_closeDebitNote").select2().select2('val', '<c:out value='${not empty param.closedebitnote ? param.closedebitnote : academicDebtGenerationRuleBean.closeDebitNote }'/>');
	</script>
                    </div>
                </div>
                <div class="form-group row"
                    ng-show="object.aggregateOnDebitNote == 'true' && object.closeDebitNote == 'true'">
                    <div class="col-sm-2 control-label">
                        <spring:message
                            code="label.AcademicDebtGenerationRule.createPaymentReferenceCode" />
                    </div>

                    <div class="col-sm-2">
                        <select
                            id="academicDebtGenerationRule_createPaymentReferenceCode"
                            name="createpaymentreferencecode"
                            class="form-control"
                            ng-model="object.createPaymentReferenceCode" ng-options="bvalue.value as bvalue.name for bvalue in booleanvalues">>
                        </select>
                        <script>
		$("#academicDebtGenerationRule_createPaymentReferenceCode").select2().select2('val', '<c:out value='${not empty param.createpaymentreferencecode ? param.createpaymentreferencecode : academicDebtGenerationRuleBean.createPaymentReferenceCode }'/>');
	</script>
                    </div>
                </div>
                
                
                <div class="form-group row"
                	ng-show="object.createPaymentReferenceCode == 'true' && object.createPaymentReferenceCode == 'true'">
                    <div class="col-sm-2 control-label">
                        <spring:message
                            code="label.AcademicDebtGenerationRule.paymentCodePool" />
                    </div>

                    <div class="col-sm-4">
                        <%-- Relation to side 1 drop down rendered in input --%>
                        <ui-select
                            id="academicDebtGenerationRule_paymentCodePool"
                            class="" name="paymentcodepool"
                            ng-model="$parent.object.paymentCodePool"
                            theme="bootstrap" ng-disabled="disabled">
                        <ui-select-match>{{$select.selected.text}}</ui-select-match>
                        <ui-select-choices
                            repeat="paymentCodePool.id as paymentCodePool in object.paymentCodePoolDataSource | filter: $select.search">
                        <span
                            ng-bind-html="paymentCodePool.text | highlight: $select.search"></span>
                        </ui-select-choices> </ui-select>
                    </div>
                </div>
                
            </div>
            <div class="panel-footer">
                <input type="submit" class="btn btn-default"
                    role="button"
                    value="<spring:message code="label.submit" />" />
            </div>
        </div>
    </form>

</div>

<script>
$(document).ready(function() {

	});
</script>
