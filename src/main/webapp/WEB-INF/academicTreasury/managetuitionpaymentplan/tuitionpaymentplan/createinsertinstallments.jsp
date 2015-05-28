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



<%-- TITLE --%>
<div class="page-header">
	<h1><spring:message code="label.manageTuitionPaymentPlan.createTuitionPaymentPlan" /></h1>
	<h3><spring:message code="label.manageTuitionPaymentPlan.createInsertInstallments" /></h3>
</div>

<%-- NAVIGATION --%>
<div class="well well-sm" style="display:inline-block">
	<span class="glyphicon glyphicon-arrow-left" aria-hidden="true"></span>
	&nbsp;
	<a class="" href="${pageContext.request.contextPath}/academictreasury/managetuitionpaymentplan/tuitionpaymentplan/">
		<spring:message code="label.event.back" />
	</a>
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

angular.module('angularAppTuitionInstallmentTariff', ['ngSanitize', 'ui.select']).controller('TuitionInstallmentTariffController', ['$scope', function($scope) {

 	$scope.object=angular.fromJson('${tuitionPaymentPlanBeanJson}');
	$scope.postBack = createAngularPostbackFunction($scope); 

	//Begin here of Custom Screen business JS - code
	
	$scope.submitForm = function() {
		$("form").submit();
	};
	
	$scope.createPaymentPlan = function() {
		$("form").attr("action", $("#createPaymentPlanURL").attr("value"));
		$("form").submit();
	}
 	
}]);
</script>

<h3><spring:message code="label.manageTuitionPaymentPlan.installments" /></h3>

<c:if test="${empty bean.tuitionInstallmentBeans}">
	<p><em><spring:message code="label.TuitionInstallmentTariff.installments.empty" /></em></p>
</c:if>

<c:if test="${not empty bean.tuitionInstallmentBeans}">

	<datatables:table id="installments" row="installment" data="${bean.tuitionInstallmentBeans}" 
		cssClass="table responsive table-bordered table-hover" cdn="false" cellspacing="2">
		
		<datatables:column cssStyle="width:2%">
			<input type="checkbox" name="installment-${installment.installmentOrder}" />
		</datatables:column>

		<datatables:column cssStyle="width:8%">
			<datatables:columnHead ><spring:message code="label.TuitionInstallmentTariff.installmentOrder" /></datatables:columnHead>
			<c:out value="${installment.installmentOrder}" />
		</datatables:column>
		
		<datatables:column className="dt-center" cssStyle="width:40%">
			<datatables:columnHead ><spring:message code="label.TuitionInstallmentTariff.amount" /></datatables:columnHead>
	
			<c:choose>
				<c:when test="${installment.tuitionCalculationType.fixedAmount}" >
					<p><strong><spring:message code="TuitionCalculationType.FIXED_AMOUNT" /></strong></p>
											
					<c:out value="${finantialEntity.finantialInstitution.currency.getValueFor(installment.fixedAmount)}" />
				</c:when>
				<c:when test="${installment.tuitionCalculationType.ects}">
					<p>
						<strong>
							<c:out value="${installment.tuitionCalculationType.descriptionI18N.content}" />
							&nbsp;
							[<c:out value="${installment.ectsCalculationType.descriptionI18N.content}" />]
						</strong>
					</p>
	
					<c:if test="${installment.ectsCalculationType.fixedAmount}">
						<p>&nbsp;</p>
						
						<p><spring:message code="label.TuitionInstallmentTariff.amountPerEcts" 
							arguments="${finantialEntity.finantialInstitution.currency.getValueFor(installment.amountPerEctsOrUnit)}" /></p>
					</c:if>
					<c:if test="${installment.ectsCalculationType.defaultPaymentPlanIndexed}">
						<p><em><spring:message code="label.TuitionInstallmentTariff.defaultPaymentPlanIndexed.ectsParameters"
							arguments="${installment.factor},${installment.totalUnits}" /></em></p>
					</c:if>
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
					<c:if test="${installment.ectsCalculationType.defaultPaymentPlanIndexed}">
						<p><em><spring:message code="label.TuitionInstallmentTariff.defaultPaymentPlanIndexed.unitsParameters"
							arguments="${installment.factor},${installment.totalEctsOrUnits}" /></em></p>
					</c:if>
				</c:when>
			</c:choose>
		</datatables:column>

		<datatables:column cssStyle="width:10%">
			<datatables:columnHead ><spring:message code="label.TuitionInstallmentTariff.beginDate" /></datatables:columnHead>
			<joda:format value="${installment.beginDate}" style="S-" />
		</datatables:column>
		
		<datatables:column cssStyle="width:15%">
			<datatables:columnHead ><spring:message code="label.TuitionInstallmentTariff.dueDate" /></datatables:columnHead>

			<c:choose>
				<c:when test="${installment.dueDateCalculationType.noDueDate}">
					<spring:message code="label.TuitionInstallmentTariff.noDueDate" />
				</c:when>
				<c:when test="${installment.dueDateCalculationType.fixedDate}">
					<joda:format value="${installment.endDate}" style="S-" />
				</c:when>
				<c:when test="${installment.dueDateCalculationType.daysAfterCreation}">
					<spring:message code="label.TuitionInstallmentTariff.daysAfterCreation" arguments="${installment.numberOfDaysAfterCreationForDueDate}" />
				</c:when>
			</c:choose>
		</datatables:column>
	
		<datatables:column cssStyle="width:25%">
			<datatables:columnHead ><spring:message code="label.TuitionInstallmentTariff.interests" /></datatables:columnHead>
			<c:if test="${not installment.applyInterests}">
				<p><strong><spring:message code="label.TuitionInstallmentTariff.interests.not.applied" /></strong></p>
			</c:if>
			<c:if test="${installment.applyInterests}">
				<p><strong>[<c:out value="${installment.interestType.descriptionI18N.content}" />]</strong></p>
				
				<c:choose>
					<c:when test="${installment.interestType.daily}">
						<p>
							<strong><spring:message code="label.TuitionInstallmentTariff.numberOfDaysAfterCreationForDueDate"  />:&nbsp;</strong>
							<c:out value="${installment.numberOfDaysAfterDueDate}" />
						</p>
						<p>
							<strong><spring:message code="label.TuitionInstallmentTariff.applyInFirstWorkday" />:&nbsp;</strong>
							<c:if test="${installment.applyInFirstWorkday}">
								<spring:message code="label.true" />
							</c:if>
							<c:if test="${not installment.applyInFirstWorkday}">
								<spring:message code="label.false" />
							</c:if>
						</p>
						
						<c:if test="${installment.maximumDaysToApplyPenaltyApplied}">
							<p>
								<strong><spring:message code="label.TuitionInstallmentTariff.maximumDaysToApplyPenalty" />:&nbsp;</strong>
								<c:out value="${installment.maximumDaysToApplyPenalty}" />
							</p>
						</c:if>
						
						<p>
							<strong><spring:message code="label.TuitionInstallmentTariff.rate" />:&nbsp;</strong>
							<c:out value="${installment.rate}" />
						</p>
					</c:when>
					<c:when test="${installment.interestType.monthly}">
						<p>
							<strong><spring:message code="label.TuitionInstallmentTariff.applyInFirstWorkday" />:&nbsp;</strong>
							<c:if test="${installment.applyInFirstWorkday}">
								<spring:message code="label.true" />
							</c:if>
							<c:if test="${not installment.applyInFirstWorkday}">
								<spring:message code="label.false" />
							</c:if>
						</p>
	
						<c:if test="${installment.maximumMonthsToApplyPenaltyApplied}">
							<p>
								<strong><spring:message code="label.TuitionInstallmentTariff.maximumMonthsToApplyPenalty" />:&nbsp;</strong>
								<c:out value="${installment.maximumMonthsToApplyPenalty}" />
							</p>
						</c:if>
	
						<p>
							<strong><spring:message code="label.TuitionInstallmentTariff.rate" />:&nbsp;</strong>
							<c:out value="${installment.rate}" />
						</p>
					</c:when>
					
					<c:when test="${installment.interestType.fixedAmount}">
						<p>
							<strong><spring:message code="label.TuitionInstallmentTariff.interestFixedAmount" />:&nbsp;</strong>
							<c:out value="${installment.interestFixedAmount}" />
						</p>
					</c:when>
				</c:choose>
			</c:if>
		</datatables:column>
	</datatables:table>
</c:if>

<div style="margin-bottom: 10px;">
	<input type="button" class="btn" role="button" value="<spring:message code="label.delete" />" />
</div>

<h3><spring:message code="label.manageTuitionPaymentPlan.installments.new" /></h3>

<form name='form' method="post" class="form-horizontal"
	ng-app="angularAppTuitionInstallmentTariff"
	ng-controller="TuitionInstallmentTariffController"
	action='${pageContext.request.contextPath}<%= TuitionPaymentPlanController.ADDINSTALLMENTSPOSTBACK_URL %>/${finantialEntity.externalId}/${executionYear.externalId}'>

	<input id="createPaymentPlanURL" type="hidden" name="createTuitionPaymentPlan"
		value="${pageContext.request.contextPath}<%= TuitionPaymentPlanController.CREATEPAYMENTPLAN_URL %>/${finantialEntity.externalId}/${executionYear.externalId}" />
		
	<input type="hidden" name="postback"
		value='${pageContext.request.contextPath}<%= TuitionPaymentPlanController.CREATEINSERTINSTALLMENTSPOSTBACK_URL %>/${finantialEntity.externalId}/${executionYear.externalId}' />

	<input name="bean" type="hidden" value="{{ object }}" />

	<div class="panel panel-default">
		<div class="panel-body">
			<div class="form-group row">
				<div class="col-sm-2 control-label">
					<spring:message code="label.TuitionInstallmentTariff.tuitionCalculationType" />
				</div>

				<div class="col-sm-4">
					<ui-select id="tuitionInstallmentTariff_tuitionCalculationType"
						class="form-control" name="tuitioncalculationtype"
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

				<div class="col-sm-4">
					<ui-select id="tuitionInstallmentTariff_ectsCalculationType"
						class="form-control" name="ectscalculationtype"
						ng-model="$parent.object.ectsCalculationType" theme="bootstrap"
						required> 
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

				<div class="col-sm-10">
					<input id="tuitionInstallmentTariff_fixedAmount"
						class="form-control" type="text" ng-model="object.fixedAmount"
						name="fixedamount"
						value='<c:out value='${bean.fixedAmount}'/>' />
				</div>
			</div>
			<div class="form-group row" ng-show="(object.tuitionCalculationType == 'ECTS' || object.tuitionCalculationType == 'UNITS') && object.ectsCalculationType == 'DEFAULT_PAYMENT_PLAN_INDEXED'">
				<div class="col-sm-2 control-label">
					<spring:message code="label.TuitionInstallmentTariff.factor" />
				</div>

				<div class="col-sm-10">
					<input id="tuitionInstallmentTariff_factor" class="form-control"
						type="text" ng-model="object.factor" name="factor"
						value='<c:out value='${bean.factor}'/>' required pattern="\d+(\.\d{2})?" />
				</div>
			</div>
			<div class="form-group row" ng-show="(object.tuitionCalculationType == 'ECTS' || object.tuitionCalculationType == 'UNITS') && object.ectsCalculationType == 'DEFAULT_PAYMENT_PLAN_INDEXED'">
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

				<div class="col-sm-10" >
					<input id="tuitionInstallmentTariff_totalEctsOrUnits"
						class="form-control" type="text"
						ng-model="object.totalEctsOrUnits" name="totalectsorunits"
						value='<c:out value='${tuitionInstallmentTariff.totalEctsOrUnits}'/>' required pattern="\d+(\.\d{2})?" />
				</div>
			</div>
			<div class="form-group row">
				<div class="col-sm-2 control-label">
					<spring:message code="label.TuitionInstallmentTariff.beginDate" />
				</div>

				<div class="col-sm-4">
					<input id="tuitionInstallmentTariff_beginDate" class="form-control" type="date" name="begindate" value='${bean.beginDate}' 
						ng-model="object.beginDate" />
				</div>
			</div>
			<div class="form-group row">
				<div class="col-sm-2 control-label">
					<spring:message code="label.TuitionInstallmentTariff.dueDateCalculationType" />
				</div>

				<div class="col-sm-4">
					<ui-select id="tuitionInstallmentTariff_dueDateCalculationType"
						class="form-control" name="duedatecalculationtype"
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
			<div class="form-group row" ng-show="object.dueDateCalculationType == 'FIXED_DATE'">
				<div class="col-sm-2 control-label">
					<spring:message code="label.TuitionInstallmentTariff.endDate" />
				</div>

				<div class="col-sm-4">
					<input id="tuitionInstallmentTariff_endDate" class="form-control" 
						type="date" name="enddate"  value='<c:out value='${bean.endDate}'/>' ng-model="object.endDate" required />
				</div>
			</div>
			<div class="form-group row" ng-show="object.dueDateCalculationType == 'DAYS_AFTER_CREATION'">
				<div class="col-sm-2 control-label">
					<spring:message code="label.TuitionInstallmentTariff.numberOfDaysAfterCreationForDueDate" />
				</div>

				<div class="col-sm-10">
					<input
						id="tuitionInstallmentTariff_numberOfDaysAfterCreationForDueDate"
						class="form-control" type="text"
						ng-model="object.numberOfDaysAfterCreationForDueDate"
						name="numberofdaysaftercreationforduedate"
						value='<c:out value='${bean.numberOfDaysAfterCreationForDueDate}'/>' required pattern="\d+" />
				</div>
			</div>
			<div class="form-group row">
				<div class="col-sm-2 control-label">
					<spring:message code="label.TuitionInstallmentTariff.applyInterests" />
				</div>

				<div class="col-sm-2">
					<select id="tuitionInstallmentTariff_applyInterests"
						name="applyinterests" class="form-control"
						ng-model="object.applyInterests" required>
						<option value="false"><spring:message code="label.no" /></option>
						<option value="true"><spring:message code="label.yes" /></option>
					</select>
					<script>
						$("#tuitionInstallmentTariff_applyInterests").select2().val('<c:out value='${bean.applyInterests}'/>');
					</script>
				</div>
			</div>
			<div class="form-group row" ng-show="object.applyInterests == 'true'">
				<div class="col-sm-2 control-label">
					<spring:message code="label.TuitionInstallmentTariff.interestType" />
				</div>

				<div class="col-sm-4">
					<ui-select id="tuitionInstallmentTariff_interestType"
						class="form-control" name="interesttype"
						ng-model="$parent.object.interestType" theme="bootstrap"
						required >
						<ui-select-match>{{$select.selected.text}}</ui-select-match>
						<ui-select-choices
							repeat="interestType.id as interestType in object.interestTypeDataSource | filter: $select.search">
							<span ng-bind-html="interestType.text | highlight: $select.search"></span>
						</ui-select-choices>
					</ui-select>
				</div>
			</div>
			<div class="form-group row" ng-show="object.applyInterests=='true' && object.interestType == 'DAILY'">
				<div class="col-sm-2 control-label">
					<spring:message code="label.TuitionInstallmentTariff.numberOfDaysAfterDueDate" />
				</div>

				<div class="col-sm-10">
					<input id="tuitionInstallmentTariff_numberOfDaysAfterDueDate"
						class="form-control" type="text"
						ng-model="object.numberOfDaysAfterDueDate"
						name="numberofdaysafterduedate"
						value='<c:out value='${tuitionInstallmentTariff.numberOfDaysAfterDueDate}'/>' required pattern="\d+" />
				</div>
			</div>
			<div class="form-group row" ng-show="object.applyInterests=='true' && object.interestType == 'DAILY'">
				<div class="col-sm-2 control-label">
					<spring:message code="label.TuitionInstallmentTariff.applyInFirstWorkday" />
				</div>

				<div class="col-sm-2">
					<select id="tuitionInstallmentTariff_applyInFirstWorkday"
						name="applyinfirstworkday" class="form-control"
						ng-model="object.applyinfirstworkday" required>
						<option value="false"><spring:message code="label.no" /></option>
						<option value="true"><spring:message code="label.yes" /></option>
					</select>
					<script>
						$("#tuitionInstallmentTariff_applyInFirstWorkday").select2().val('<c:out value='${bean.applyInFirstWorkday}'/>');
					</script>
				</div>
			</div>
			<div class="form-group row" ng-show="object.applyInterests=='true' && object.interestType == 'DAILY'">
				<div class="col-sm-2 control-label">
					<spring:message code="label.TuitionInstallmentTariff.maximumDaysToApplyPenalty" />
				</div>

				<div class="col-sm-10">
					<input id="tuitionInstallmentTariff_maximumDaysToApplyPenalty"
						class="form-control" type="text"
						ng-model="object.maximumDaysToApplyPenalty"
						name="maximumdaystoapplypenalty"
						value='<c:out value='${bean.maximumDaysToApplyPenalty}'/>' required pattern="\d+" />
				</div>
			</div>
			<div class="form-group row" ng-show="object.applyInterests=='true' && object.interestType == 'MONTHLY'">
				<div class="col-sm-2 control-label">
					<spring:message code="label.TuitionInstallmentTariff.maximumMonthsToApplyPenalty" />
				</div>

				<div class="col-sm-10">
					<input id="tuitionInstallmentTariff_maximumMonthsToApplyPenalty"
						class="form-control" type="text"
						ng-model="object.maximumMonthsToApplyPenalty"
						name="maximummonthstoapplypenalty"
						value='<c:out value='${bean.maximumMonthsToApplyPenalty}'/>' />
				</div>
			</div>
			<div class="form-group row" ng-show="object.applyInterests=='true' && object.interestType == 'FIXED_AMOUNT'">
				<div class="col-sm-2 control-label">
					<spring:message code="label.TuitionInstallmentTariff.interestFixedAmount" />
				</div>

				<div class="col-sm-10">
					<input id="tuitionInstallmentTariff_interestFixedAmount"
						class="form-control" type="text"
						ng-model="object.interestFixedAmount" name="interestfixedamount"
						value='<c:out value='${bean.interestFixedAmount}'/>' required  pattern="\d+(\.\d{2})?" />
				</div>
			</div>
			<div class="form-group row" ng-show="object.applyInterests=='true' && (object.interestType == 'DAILY' || object.interestType == 'MONTHLY')">
				<div class="col-sm-2 control-label">
					<spring:message code="label.TuitionInstallmentTariff.rate" />
				</div>

				<div class="col-sm-10">
					<input id="tuitionInstallmentTariff_rate" class="form-control"
						type="text" ng-model="object.rate" name="rate"
						value='<c:out value='${bean.rate}'/>' required   pattern="\d+(\.\d{4})?" min="0" max="100" />
				</div>
			</div>
		</div>
		<div style="text-align: right">
			<input type="submit" class="btn btn-default" role="button" value="<spring:message code="label.add" />" ng-click="submitForm()" />
		</div>
		<div class="panel-footer">
			<input type="submit" class="btn btn-default" role="button" value="<spring:message code="label.create" />" ng-click="createPaymentPlan()" />
		</div>
	</div>
	
</form>

<script>
	$(document).ready(function() {});
</script>
