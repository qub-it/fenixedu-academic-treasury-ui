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

<script type="text/javascript">
      function processDelete(externalId) {
        url = "${pageContext.request.contextPath}<%= TuitionPaymentPlanController.REMOVEINSTALLMENT_URL %>/${finantialEntity.externalId}/${executionYear.externalId}/" + externalId;
        $("#deleteForm").attr("action", url);
        $('#removebean').attr('value', $('#bean').attr('value')); 
        $('#deleteModal').modal('toggle');
        
      }
</script>

<div class="modal fade" id="deleteModal">
    <div class="modal-dialog">
        <div class="modal-content">
            <form id="deleteForm" action="#" method="POST">
                <input id="removebean" name="bean" type="hidden" value="" />
                <div class="modal-header">
                    <button type="button" class="close"
                        data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                    <h4 class="modal-title">
                        <spring:message code="label.confirmation" />
                    </h4>
                </div>
                <div class="modal-body">
                    <p>
                        <spring:message
                            code="label.createinsertinstallments.confirmDelete" />
                    </p>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default"
                        data-dismiss="modal">
                        <spring:message code="label.close" />
                    </button>
                    <button id="deleteButton" class="btn btn-warning"
                        type="submit">
                        <spring:message code="label.delete" />
                    </button>
                </div>
            </form>
        </div>
        <!-- /.modal-content -->
    </div>
    <!-- /.modal-dialog -->
</div>
<!-- /.modal -->

<%-- TITLE --%>
<div class="page-header">
	<h1><spring:message code="label.manageTuitionPaymentPlan.createTuitionPaymentPlan" /></h1>
	<h3><spring:message code="label.manageTuitionPaymentPlan.createInsertInstallments" /></h3>
</div>

<c:if test="${not empty infoMessages}">
	<div class="alert alert-info" role="alert">
		
		<c:forEach items="${infoMessages}" var="message"> 
			<p> <span class="glyphicon glyphicon glyphicon-ok-sign" aria-hidden="true">&nbsp;</span>
						${message}
					</p>
		</c:forEach>
		
	</div>	
</c:if>
<c:if test="${not empty warningMessages}">
	<div class="alert alert-warning" role="alert">
		
		<c:forEach items="${warningMessages}" var="message"> 
			<p> <span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true">&nbsp;</span>
						${message}
					</p>
		</c:forEach>
		
	</div>	
</c:if>
<c:if test="${not empty errorMessages}">
	<div class="alert alert-danger" role="alert">
		
		<c:forEach items="${errorMessages}" var="message"> 
			<p> <span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true">&nbsp;</span>
						${message}
					</p>
		</c:forEach>
		
	</div>	
</c:if>

<script>

angular.module('angularAppTuitionInstallmentTariff', ['ngSanitize', 'ui.select','bennuToolkit']).controller('TuitionInstallmentTariffController', ['$scope', function($scope) {

 	$scope.object=${tuitionPaymentPlanBeanJson};
	$scope.postBack = createAngularPostbackFunction($scope); 

	//Begin here of Custom Screen business JS - code
	
	$scope.createPaymentPlan = function() {
		$("#createPaymentPlanform").attr("action", $("#createPaymentPlanURL").attr("value"));
		$("#createPaymentPlanform").submit();
	}
	
	$scope.backToDefineStudentConditions = function() {
		$("#createPaymentPlanform").attr("action", $("#backUrl").attr("value"));
		$("#createPaymentPlanform").submit();
	}
	
	$scope.booleanvalues = [{name : '<spring:message code="label.no"/>', value : false},
							{name : '<spring:message code="label.yes"/>', value : true}];
 	
}]);
</script>

<h3><spring:message code="label.manageTuitionPaymentPlan.installments" /></h3>

<c:if test="${empty bean.tuitionInstallmentBeans}">
	<p><em><spring:message code="label.TuitionInstallmentTariff.installments.empty" /></em></p>
</c:if>

<c:if test="${not empty bean.tuitionInstallmentBeans}">

	<datatables:table id="installments" row="installment" data="${bean.tuitionInstallmentBeans}" 
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
								arguments="${finantialEntity.finantialInstitution.currency.getValueFor(installment.amountPerEctsOrUnit, 3)}" />
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
							arguments="${finantialEntity.finantialInstitution.currency.getValueFor(installment.amountPerEctsOrUnit, 3)}" /></p>
					</c:if>
					<c:if test="${installment.ectsCalculationType.dependentOnDefaultPaymentPlan}">
						<p><em><spring:message code="label.TuitionInstallmentTariff.defaultPaymentPlanIndexed.unitsParameters"
							arguments="${installment.factor},${installment.totalEctsOrUnits}" /></em></p>
					</c:if>
				</c:when>
			</c:choose>
			
			<c:choose>
				<c:when test="${installment.applyMaximumAmount}">
					<p>
						<em><spring:message code="label.TuitionPaymentPlan.maximumAmount" />:&nbsp;</em>
						<c:out value="${finantialEntity.finantialInstitution.currency.getValueFor(installment.maximumAmount)}" />
					</p>
				</c:when>
			</c:choose>

 			<c:if test="${installment.academicalActBlockingOff}"> 
				<p><span class="label label-warning">
						<spring:message code="label.TuitionPaymentPlan.academicalActBlockingOff" />
				</span></p>
			</c:if>
			
 			<c:if test="${installment.blockAcademicActsOnDebt}"> 
				<p><span class="label label-warning">
						<spring:message code="label.TuitionPaymentPlan.blockAcademicActsOnDebt" />
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
		
		<datatables:column>
				<button type="submit" class="btn btn-warning" role="button" onClick="javascript:processDelete('${installment.installmentOrder}')">
                        <span class="glyphicon glyphicon-trash" aria-hidden="true"></span>&nbsp;
                        <spring:message code="label.delete" />
                </button>
		</datatables:column>
	</datatables:table>
</c:if>

<h3><spring:message code="label.manageTuitionPaymentPlan.installments.new" /></h3>


<form name='form' method="post" class="form-horizontal"
	ng-app="angularAppTuitionInstallmentTariff"
	ng-controller="TuitionInstallmentTariffController"
	action='${pageContext.request.contextPath}<%= TuitionPaymentPlanController.ADDINSTALLMENTSPOSTBACK_URL %>/${finantialEntity.externalId}/${executionYear.externalId}'>

	<input type="hidden" name="postback"
		value='${pageContext.request.contextPath}<%= TuitionPaymentPlanController.CREATEINSERTINSTALLMENTSPOSTBACK_URL %>/${finantialEntity.externalId}/${executionYear.externalId}' />

	<input id="bean" name="bean" type="hidden" value="{{ object }}" />

	<div class="panel panel-default">
		<div class="panel-body">
			<div class="form-group row">
				<div class="col-sm-2 control-label">
					<spring:message code="label.TuitionInstallmentTariff.product" />
				</div>
				
				<div class="col-sm-6">
					<ui-select id="tuitionInstallmentTariff_tuitionInstallmentProduct"
						name="tuitioninstallmentproduct"
						ng-model="$parent.object.tuitionInstallmentProduct" theme="bootstrap">
						<ui-select-match>{{$select.selected.text}}</ui-select-match>
						<ui-select-choices
							repeat="tuitionInstallmentProduct.id as tuitionInstallmentProduct in object.tuitionInstallmentProductDataSource | filter: $select.search">
						<span
							ng-bind-html="tuitionInstallmentProduct.text | highlight: $select.search"></span>
						</ui-select-choices>
					</ui-select>
				</div>
				
			</div>
					
			<div class="form-group row">
				<div class="col-sm-2 control-label">
					<spring:message code="label.TuitionInstallmentTariff.tuitionCalculationType" />
				</div>

				<div class="col-sm-6">
					<ui-select id="tuitionInstallmentTariff_tuitionCalculationType"
						name="tuitioncalculationtype"
						ng-model="$parent.object.tuitionCalculationType" theme="bootstrap"
						required>
						<ui-select-match>{{$select.selected.text}}</ui-select-match>
						<ui-select-choices
							repeat="tuitionCalculationType.id as tuitionCalculationType in object.tuitionCalculationTypeDataSource | filter: $select.search">
						<span
							ng-bind-html="tuitionCalculationType.text | highlight: $select.search"></span>
						</ui-select-choices>
					</ui-select>
				</div>
			</div>
			<div class="form-group row" ng-show='object.tuitionCalculationType == "ECTS" || object.tuitionCalculationType == "UNITS"'>
				<div class="col-sm-2 control-label">
					<span ng-show='object.tuitionCalculationType == "ECTS"'>
						<spring:message code="label.TuitionInstallmentTariff.ectsCalculationType"
							arguments="<%= TuitionCalculationType.ECTS.getDescriptionI18N().getContent() %>" />
					</span>
					<span ng-show='object.tuitionCalculationType == "UNITS"'>
						<spring:message code="label.TuitionInstallmentTariff.ectsCalculationType"
							arguments="<%= TuitionCalculationType.UNITS.getDescriptionI18N().getContent() %>" />
					</span>
				</div>

				<div class="col-sm-6">
					<ui-select id="tuitionInstallmentTariff_ectsCalculationType"
						name="ectscalculationtype"
						ng-model="$parent.object.ectsCalculationType" theme="bootstrap"
						ng-required='object.tuitionCalculationType == "ECTS" || object.tuitionCalculationType == "UNITS"'> 
						<ui-select-match>{{$select.selected.text}}</ui-select-match>
						<ui-select-choices
							repeat="ectsCalculationType.id as ectsCalculationType in object.ectsCalculationTypeDataSource | filter: $select.search">
						<span ng-bind-html="ectsCalculationType.text | highlight: $select.search"></span>
						</ui-select-choices>
					</ui-select>
				</div>
			</div>
			<div class="form-group row" ng-show="object.tuitionCalculationType == 'FIXED_AMOUNT' || object.ectsCalculationType == 'FIXED_AMOUNT'">
				<div class="col-sm-2 control-label">
					<spring:message code="label.TuitionInstallmentTariff.fixedAmount" />
				</div>

				<div class="col-sm-6">
                <div class="input-group">
                        <div class="input-group-addon">
                            <c:out value="${finantialEntity.finantialInstitution.currency.symbol}" />
                        </div>
					<input id="tuitionInstallmentTariff_fixedAmount"
						class="form-control" type="number" ng-model="object.fixedAmount"
						name="fixedamount" pattern="[0-9]+(\.[0-9][0-9]?[0-9]?)?" min="0" step="0.001"
						value="<c:out value='${bean.fixedAmount}'/>" ng-required="object.tuitionCalculationType == 'FIXED_AMOUNT' || object.ectsCalculationType == 'FIXED_AMOUNT'" />
				</div>
				</div>
			</div>
			<div class="form-group row" ng-show="(object.tuitionCalculationType == 'ECTS' || object.tuitionCalculationType == 'UNITS') && (object.ectsCalculationType == 'DEFAULT_PAYMENT_PLAN_INDEXED' || object.ectsCalculationType == 'DEFAULT_PAYMENT_PLAN_COURSE_FUNCTION_COST_INDEXED')">
				<div class="col-sm-2 control-label">
					<spring:message code="label.TuitionInstallmentTariff.factor" />
				</div>

				<div class="col-sm-6">
					<input id="tuitionInstallmentTariff_factor" class="form-control"
						type="number" pattern="\d+(\.\d{4})?" min="0" step="0.001" 
                        ng-model="object.factor" name="factor"
						value='<c:out value='${bean.factor}'/>' ng-required="(object.tuitionCalculationType == 'ECTS' || object.tuitionCalculationType == 'UNITS') && (object.ectsCalculationType == 'DEFAULT_PAYMENT_PLAN_INDEXED' || object.ectsCalculationType == 'DEFAULT_PAYMENT_PLAN_COURSE_FUNCTION_COST_INDEXED')" />
				</div>
			</div>
			<div class="form-group row" ng-show="(object.tuitionCalculationType == 'ECTS' || object.tuitionCalculationType == 'UNITS') && (object.ectsCalculationType == 'DEFAULT_PAYMENT_PLAN_INDEXED' || object.ectsCalculationType == 'DEFAULT_PAYMENT_PLAN_COURSE_FUNCTION_COST_INDEXED')">
				<div class="col-sm-2 control-label">
					<span ng-show='object.tuitionCalculationType == "ECTS"'>
						<spring:message code="label.TuitionInstallmentTariff.totalEctsOrUnits"
							arguments="<%= TuitionCalculationType.ECTS.getDescriptionI18N().getContent() %>" />
					</span>
					<span ng-show='object.tuitionCalculationType == "UNITS"'>
						<spring:message code="label.TuitionInstallmentTariff.totalEctsOrUnits"
							arguments="<%= TuitionCalculationType.UNITS.getDescriptionI18N().getContent() %>" />
					</span>
				</div>

				<div class="col-sm-6" >
					<input id="tuitionInstallmentTariff_totalEctsOrUnits"
						class="form-control" type="number" pattern="[0-9]?" min="0" step="1"
						ng-model="object.totalEctsOrUnits" name="totalectsorunits"
						value='<c:out value='${bean.totalEctsOrUnits}'/>' ng-required="(object.tuitionCalculationType == 'ECTS' || object.tuitionCalculationType == 'UNITS') && (object.ectsCalculationType == 'DEFAULT_PAYMENT_PLAN_INDEXED' object.ectsCalculationType == 'DEFAULT_PAYMENT_PLAN_COURSE_FUNCTION_COST_INDEXED')" />
				</div>
			</div>
			
			<div class="form-group row">
				<div class="col-sm-2 control-label">
					<spring:message code="label.TuitionInstallmentTariff.applyMaximumAmount" />
				</div>
				<div class="col-sm-6">
					<select id="tuitionInstallmentTariff_applyMaximumAmount"
						name="applyMaximumAmount" class="form-control"
						ng-model="object.applyMaximumAmount"
						ng-options="bvalue.value as bvalue.name for bvalue in booleanvalues">
					</select>
				</div>
			</div>
			
			<div class="form-group row" ng-show="object.applyMaximumAmount">
				<div class="col-sm-2 control-label">
					<spring:message code="label.TuitionInstallmentTariff.maximumAmount" />
				</div>

				<div class="col-sm-6">
                <div class="input-group">
                    <div class="input-group-addon">
                        <c:out value="${finantialEntity.finantialInstitution.currency.symbol}" />
                    </div>
					<input id="tuitionInstallmentTariff_maximumAmount"
						class="form-control" type="number" ng-model="object.maximumAmount"
						name="maximumamount" pattern="[0-9]+(\.[0-9][0-9]?)?" min="0" step="0.01"
						value="<c:out value='${bean.maximumAmount}'/>" ng-required="object.applyMaximumAmount" />
				</div>
				</div>
			</div>
			
			<div class="form-group row">
				<div class="col-sm-2 control-label">
					<spring:message code="label.TuitionInstallmentTariff.beginDate" />
				</div>

				<div class="col-sm-6">
					<input id="tuitionInstallmentTariff_beginDate" class="form-control" type="text" name="begindate"  
						bennu-date="object.beginDate" ng-required="true" />
					
					<%-- 
					<input id="tuitionInstallmentTariff_beginDate" class="form-control" type="text" name="begindate"  
						ng-model="object.beginDate" />
					--%>
				</div>
			</div>
			<div class="form-group row">
				<div class="col-sm-2 control-label">
					<spring:message code="label.TuitionInstallmentTariff.dueDateCalculationType" />
				</div>

				<div class="col-sm-6">
					<ui-select id="tuitionInstallmentTariff_dueDateCalculationType"
						name="duedatecalculationtype"
						ng-model="$parent.object.dueDateCalculationType" theme="bootstrap"
						required>
						<ui-select-match>{{$select.selected.text}}</ui-select-match>
						<ui-select-choices
							repeat="dueDateCalculationType.id as dueDateCalculationType in object.dueDateCalculationTypeDataSource | filter: $select.search">
							<span
								ng-bind-html="dueDateCalculationType.text | highlight: $select.search"></span>
						</ui-select-choices>
					</ui-select>
				</div>
			</div>
			<div class="form-group row" ng-show="object.dueDateCalculationType == 'FIXED_DATE' || object.dueDateCalculationType == 'BEST_OF_FIXED_DATE_AND_DAYS_AFTER_CREATION'">
				<div class="col-sm-2 control-label">
					<spring:message code="label.TuitionInstallmentTariff.fixedDueDate" />
				</div>

				<div class="col-sm-6">
					<input id="tuitionInstallmentTariff_fixedduedate" class="form-control" 
						type="text" name="fixedduedate"  bennu-date="object.fixedDueDate" ng-required="object.dueDateCalculationType == 'FIXED_DATE' || object.dueDateCalculationType == 'BEST_OF_FIXED_DATE_AND_DAYS_AFTER_CREATION'" />
				</div>
			</div>
			<div class="form-group row" ng-show="object.dueDateCalculationType == 'DAYS_AFTER_CREATION' || object.dueDateCalculationType == 'BEST_OF_FIXED_DATE_AND_DAYS_AFTER_CREATION'">
				<div class="col-sm-2 control-label">
					<spring:message code="label.TuitionInstallmentTariff.numberOfDaysAfterCreationForDueDate" />
				</div>

				<div class="col-sm-10">
					<input
						id="tuitionInstallmentTariff_numberOfDaysAfterCreationForDueDate"
						type="number" pattern="[0-9]?" min="0" step="1"
						ng-model="object.numberOfDaysAfterCreationForDueDate"
						name="numberofdaysaftercreationforduedate"
						value='<c:out value='${bean.numberOfDaysAfterCreationForDueDate}'/>' ng-required="object.dueDateCalculationType == 'DAYS_AFTER_CREATION' || object.dueDateCalculationType == 'BEST_OF_FIXED_DATE_AND_DAYS_AFTER_CREATION'" pattern="\d+" />
				</div>
			</div>
			<div class="form-group row">
				<div class="col-sm-2 control-label"><spring:message code="label.TuitionPaymentPlan.academicalActBlockingOn"/></div> 
				
				<div class="col-sm-2">
					<select id="tuitionPaymentPlan_academicalActBlockingOn" name="academicalactblockingon" class="form-control" ng-model="object.academicalActBlockingOn"
                    ng-options="bvalue.value as bvalue.name for bvalue in booleanvalues">			
					</select>
				</div>
			</div>		
			<div class="form-group row">
				<div class="col-sm-2 control-label"><spring:message code="label.TuitionPaymentPlan.blockAcademicActsOnDebt"/></div> 
				
				<div class="col-sm-2">
					<select id="tuitionPaymentPlan_blockAcademicActsOnDebt" name="blockacademicactsondebt" class="form-control" ng-model="object.blockAcademicActsOnDebt"
                    ng-options="bvalue.value as bvalue.name for bvalue in booleanvalues">			
					</select>
				</div>
			</div>		
			<div class="form-group row">
				<div class="col-sm-2 control-label">
					<spring:message code="label.TuitionInstallmentTariff.applyInterests" />
				</div>

				<div class="col-sm-2">
					<select id="tuitionInstallmentTariff_applyInterests"
						name="applyinterests" class="form-control"
						ng-model="object.applyInterests" required
                        ng-options="bvalue.value as bvalue.name for bvalue in booleanvalues">
					</select>
				</div>
			</div>
			<div class="form-group row" ng-show="object.applyInterests">
				<div class="col-sm-2 control-label">
					<spring:message code="label.TuitionInstallmentTariff.interestType" />
				</div>

				<div class="col-sm-6">
					<ui-select id="tuitionInstallmentTariff_interestType"
						name="interesttype"
						ng-model="$parent.object.interestType" theme="bootstrap"
						ng-required="object.applyInterests" >
						<ui-select-match>{{$select.selected.text}}</ui-select-match>
						<ui-select-choices
							repeat="interestType.id as interestType in object.interestTypeDataSource | filter: $select.search">
							<span ng-bind-html="interestType.text | highlight: $select.search"></span>
						</ui-select-choices>
					</ui-select>
				</div>
			</div>
			<div class="form-group row" ng-show="object.applyInterests && object.interestType == 'DAILY'">
				<div class="col-sm-2 control-label">
					<spring:message code="label.TuitionInstallmentTariff.numberOfDaysAfterDueDate" />
				</div>

				<div class="col-sm-4">
					<input id="tuitionInstallmentTariff_numberOfDaysAfterDueDate"
						class="form-control" type="number" pattern="[0-9]?" min="0" step="1"
						ng-model="object.numberOfDaysAfterDueDate"
						name="numberofdaysafterduedate"
						value='<c:out value='${tuitionInstallmentTariff.numberOfDaysAfterDueDate}'/>' ng-required="object.applyInterests && object.interestType == 'DAILY'" pattern="\d+" />
				</div>
			</div>
			<div class="form-group row" ng-show="object.applyInterests && object.interestType == 'DAILY'">
				<div class="col-sm-2 control-label">
					<spring:message code="label.TuitionInstallmentTariff.applyInFirstWorkday" />
				</div>

				<div class="col-sm-2">
					<select id="tuitionInstallmentTariff_applyInFirstWorkday"
						name="applyinfirstworkday" class="form-control"
						ng-model="object.applyinfirstworkday" ng-required="object.applyInterests && object.interestType == 'DAILY'">
						<option value="false"><spring:message code="label.no" /></option>
						<option value="true"><spring:message code="label.yes" /></option>
					</select>
					<script>
						$("#tuitionInstallmentTariff_applyInFirstWorkday").select2().val('<c:out value='${bean.applyInFirstWorkday}'/>');
					</script>
				</div>
			</div>
			<div class="form-group row" ng-show="object.applyInterests && object.interestType == 'DAILY'">
				<div class="col-sm-2 control-label">
					<spring:message code="label.TuitionInstallmentTariff.maximumDaysToApplyPenalty" />
				</div>

				<div class="col-sm-4">
					<input id="tuitionInstallmentTariff_maximumDaysToApplyPenalty"
						class="form-control" type="text"
						ng-model="object.maximumDaysToApplyPenalty"
						name="maximumdaystoapplypenalty"
						value='<c:out value='${bean.maximumDaysToApplyPenalty}'/>' ng-required="object.applyInterests && object.interestType == 'DAILY'" pattern="\d+" />
				</div>
			</div>
			<div class="form-group row" ng-show="object.applyInterests && object.interestType == 'FIXED_AMOUNT'">
				<div class="col-sm-2 control-label">
					<spring:message code="label.TuitionInstallmentTariff.interestFixedAmount" />
				</div>
				<div class="col-sm-4">                
					<input id="tuitionInstallmentTariff_interestFixedAmount"
						class="form-control" type="number" pattern="[0-9]+(\.[0-9][0-9]?[0-9]?)?" min="0" step="0.01"
						ng-model="object.interestFixedAmount" name="interestfixedamount"
						value='<c:out value='${bean.interestFixedAmount}'/>' ng-required="object.applyInterests && object.interestType == 'FIXED_AMOUNT'"  pattern="\d+(\.\d{2})?" />
				</div>
			</div>
			<div class="form-group row" ng-show="object.applyInterests && object.interestType == 'DAILY'">
				<div class="col-sm-2 control-label">
					<spring:message code="label.TuitionInstallmentTariff.rate" />
				</div>
                <div class="col-sm-4">
                    <div class="input-group">
                        <div class="input-group-addon">
                            %
                        </div>
                		<input id="tuitionInstallmentTariff_rate" class="form-control"
                			type="number" ng-model="object.rate" name="rate" pattern="\d+(\.\d{4})?" min="0"
                                    max="100" step="0.01"
                			value='<c:out value='${bean.rate}'/>' ng-required="object.applyInterests && object.interestType == 'DAILY'"   pattern="\d+(\.\d{4})?" min="0" max="100" />
                    </div>
                </div>
            </div>
            <div class="form-group row">
                <div class="col-sm-2 control-label">
                </div>
    			<div style="text-align: right" class="col-sm-6">
                 
    				<button type="submit" class="btn btn-primary" role="button"   >
                    <span class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span>&nbsp;<spring:message code="label.add" />
                    </button>
    			</div>
            </div>
	</div>
</form>


<form id="createPaymentPlanform" name='createPaymentPlanform' method="post" class="form-horizontal"
    ng-app="angularAppTuitionInstallmentTariff"
    ng-controller="TuitionInstallmentTariffController"
    action='${pageContext.request.contextPath}<%= TuitionPaymentPlanController.CREATEPAYMENTPLAN_URL %>/${finantialEntity.externalId}/${executionYear.externalId}'>

    <input id="createPaymentPlanURL" type="hidden" name="createTuitionPaymentPlan"
        value="${pageContext.request.contextPath}<%= TuitionPaymentPlanController.CREATEPAYMENTPLAN_URL %>/${finantialEntity.externalId}/${executionYear.externalId}" />

    <input id="backUrl" type="hidden" name="backUrl" 
        value="${pageContext.request.contextPath}<%= TuitionPaymentPlanController.BACKTODEFINE_STUDENT_CONDITIONS_ACTION_URL %>/${finantialEntity.externalId}/${executionYear.externalId}" />

    <input id="otherBean" name="bean" type="hidden" value="{{ object }}" />
    
    <div class="panel panel-default">
        <div class="panel-footer">
            <button type="button" class="btn btn-default" role="button" ng-click="backToDefineStudentConditions()">
            <span class="glyphicon glyphicon-chevron-left"
                aria-hidden="true"></span> &nbsp;
            <spring:message code="label.back" />
            </button>
            <button type="submit" class="btn btn-primary" role="button" ng-click="createPaymentPlan()">
            <span class="glyphicon glyphicon-ok"
                aria-hidden="true"></span>&nbsp;
                <spring:message code="label.finish" />
            </button>
        </div>
     </div>
</form>


<script>
	$(document).ready(function() {});
</script>
