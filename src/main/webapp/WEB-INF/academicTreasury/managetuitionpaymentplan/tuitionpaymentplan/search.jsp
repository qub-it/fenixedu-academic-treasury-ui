<%@page import="org.fenixedu.academictreasury.ui.managetuitionpaymentplan.DegreeCurricularPlanController"%>
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

<!-- Choose ONLY ONE:  bennuToolkit OR bennuAngularToolkit -->
<%--${portal.angularToolkit()} --%>
${portal.toolkit()}

<link href="${pageContext.request.contextPath}/static/academicTreasury/css/dataTables.responsive.css" rel="stylesheet"/>
<script src="${pageContext.request.contextPath}/static/academicTreasury/js/dataTables.responsive.js"></script>
<link href="${pageContext.request.contextPath}/webjars/datatables-tools/2.2.4/css/dataTables.tableTools.css" rel="stylesheet"/>
<script src="${pageContext.request.contextPath}/webjars/datatables-tools/2.2.4/js/dataTables.tableTools.js"></script>
<link href="${pageContext.request.contextPath}/webjars/select2/4.0.0-rc.2/dist/css/select2.min.css" rel="stylesheet" />
<script src="${pageContext.request.contextPath}/webjars/select2/4.0.0-rc.2/dist/js/select2.min.js"></script>						
<script type="text/javascript" src="${pageContext.request.contextPath}/webjars/bootbox/4.4.0/bootbox.js" ></script>
<script src="${pageContext.request.contextPath}/static/academicTreasury/js/omnis.js"></script>


<div class="page-header">
	<h1><spring:message code="label.manageTuitionPaymentPlan.searchTuitionPaymentPlan" />
		<small></small>
	</h1>
</div>

<div class="well well-sm" style="display:inline-block">
	<span class="glyphicon glyphicon-arrow-left" aria-hidden="true"></span>
	&nbsp;
	<a class="" href="${pageContext.request.contextPath}<%= DegreeCurricularPlanController.CHOOSEDEGREECURRICULARPLAN_URL %>/${finantialEntity.externalId}/${executionYear.externalId}">
		<spring:message code="label.event.back"/>
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


<c:forEach items="${searchtuitionpaymentplanResultsDataSet}" var="paymentPlan"  varStatus="loopStatus">
	<p>
		<strong><c:out value="${paymentPlan.name.content}" /></strong>
		&nbsp;
		<em><c:if test="${paymentPlan.defaultPaymentPlan}"><spring:message code="label.TuitionPaymentPlan.defaultPaymentPlan" /></c:if></em>
	</p>
	
	<p style="color: blue">
		<strong><em><c:out value="${paymentPlan.conditionsDescription.content}" /></em></strong>
	</p>

	<datatables:table id="paymentPlans-${loopStatus.index}" row="installment" data="${paymentPlan.orderedTuitionInstallmentTariffs}" 
		cssClass="table responsive table-bordered table-hover" cdn="false" cellspacing="2">
		
		<datatables:column cssStyle="width:10%">
			<datatables:columnHead ><spring:message code="label.TuitionInstallmentTariff.installmentOrder" /></datatables:columnHead>
			<c:out value="${installment.installmentOrder}" />
		</datatables:column>
		
		<datatables:column cssStyle="width:20%">
			<datatables:columnHead ><spring:message code="label.TuitionInstallmentTariff.amount" /></datatables:columnHead>

			<p><c:out value="${installment.product.name.content}" /></p>
			
			<p>&nbsp;</p>

			<c:choose>
				<c:when test="${installment.tuitionCalculationType.fixedAmount}" >
					<p><strong><spring:message code="TuitionCalculationType.FIXED_AMOUNT" /></strong></p>
				
					<c:out value="${installment.finantialEntity.finantialInstitution.currency.getValueFor(installment.fixedAmount)}" />
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
							arguments="${installment.finantialEntity.finantialInstitution.currency.getValueFor(installment.amountPerEctsOrUnit)}" /></p>
					</c:if>
					<c:if test="${installment.ectsCalculationType.defaultPaymentPlanIndexed}">
						<p><em><spring:message code="label.TuitionInstallmentTariff.defaultPaymentPlanIndexed.ectsParameters"
							arguments="${installment.factor},${installment.totalEctsOrUnits}" /></em></p>
						<p>&nbsp;</p>
						
						<p><spring:message code="label.TuitionInstallmentTariff.amountPerEcts"
							arguments="${installment.finantialEntity.finantialInstitution.currency.getValueFor(installment.amountPerEctsOrUnit, 3)}" /></p>
						<p><em><spring:message code="label.TuitionInstallmentTariff.calculatedAutomaticaly" /></em></p>
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
							arguments="${installment.finantialEntity.finantialInstitution.currency.getValueFor(installment.amountPerEctsOrUnit)}" /></p>
					</c:if>
					<c:if test="${installment.ectsCalculationType.defaultPaymentPlanIndexed}">
						<p><em><spring:message code="label.TuitionInstallmentTariff.defaultPaymentPlanIndexed.unitsParameters"
							arguments="${installment.factor},${installment.totalEctsOrUnits}" /></em></p>
						<p>&nbsp;</p>

						<p><spring:message code="label.TuitionInstallmentTariff.amountPerUnits" 
							arguments="${installment.finantialEntity.finantialInstitution.currency.getValueFor(installment.amountPerEctsOrUnit)}" /></p>
						<p><em><spring:message code="label.TuitionInstallmentTariff.calculatedAutomaticaly" /></em></p>
					</c:if>
				</c:when>
			</c:choose>
			
			<c:if test="${installment.academicalActBlockingOff}">
				<p><span class="label label-warning">
						<spring:message code="label.TuitionPaymentPlan.academicalActBlockingOff" />
				</span></p>
			</c:if>
			
		</datatables:column>
		<datatables:column>
			<datatables:columnHead ><spring:message code="label.TuitionInstallmentTariff.beginDate" /></datatables:columnHead>
			<joda:format value="${installment.beginDate}" style="S-" />
		</datatables:column>
		<datatables:column>
			<datatables:columnHead ><spring:message code="label.TuitionInstallmentTariff.dueDate" /></datatables:columnHead>
			<c:choose>
				<c:when test="${installment.dueDateCalculationType.noDueDate}">
					<p><spring:message code="label.TuitionInstallmentTariff.noDueDate" /></p>
				</c:when>
				<c:when test="${installment.dueDateCalculationType.fixedDate}">
					<p><joda:format value="${installment.fixedDueDate}" style="S-" /></p>
				</c:when>
				<c:when test="${installment.dueDateCalculationType.daysAfterCreation}">
					<p><spring:message code="label.TuitionInstallmentTariff.daysAfterCreation" arguments="${installment.numberOfDaysAfterCreationForDueDate}" /></p>
				</c:when>
				<c:when test="${installment.dueDateCalculationType.bestOfFixedDateAndDaysAfterCreation}">
					<p>
						<joda:format value="${installment.fixedDueDate}" style="S-" />
						<spring:message code="label.TuitionInstallmentTariff.bestOfFixedDateAndDaysAfterCreation" arguments="${installment.numberOfDaysAfterCreationForDueDate}" />
					</p>
				</c:when>
			</c:choose>
		</datatables:column>

		<datatables:column cssStyle="width:25%">
			<datatables:columnHead ><spring:message code="label.TuitionInstallmentTariff.interests" /></datatables:columnHead>
			<c:if test="${not installment.applyInterests}">
				<p><strong><spring:message code="label.TuitionInstallmentTariff.interests.not.applied" /></strong></p>
			</c:if>
			<c:if test="${installment.applyInterests}">
				<p><strong>[<c:out value="${installment.interestRate.interestType.descriptionI18N.content}" />]</strong></p>
				
				<c:choose>
					<c:when test="${installment.interestRate.interestType.daily}">
						<p>
							<strong><spring:message code="label.TuitionInstallmentTariff.numberOfDaysAfterCreationForDueDate"  />:&nbsp;</strong>
							<c:out value="${installment.interestRate.numberOfDaysAfterDueDate}" />
						</p>
						<p>
							<strong><spring:message code="label.TuitionInstallmentTariff.applyInFirstWorkday" />:&nbsp;</strong>
							<c:if test="${installment.interestRate.applyInFirstWorkday}">
								<spring:message code="label.true" />
							</c:if>
							<c:if test="${not installment.interestRate.applyInFirstWorkday}">
								<spring:message code="label.false" />
							</c:if>
						</p>
						
						<c:if test="${installment.interestRate.maximumDaysToApplyPenaltyApplied}">
							<p>
								<strong><spring:message code="label.TuitionInstallmentTariff.maximumDaysToApplyPenalty" />:&nbsp;</strong>
								<c:out value="${installment.interestRate.maximumDaysToApplyPenalty}" />
							</p>
						</c:if>
						
						<p>
							<strong><spring:message code="label.TuitionInstallmentTariff.rate" />:&nbsp;</strong>
							<c:out value="${installment.interestRate.rate}" />&nbsp;&#37;
						</p>
					</c:when>
					<c:when test="${installment.interestRate.interestType.monthly}">
						<p>
							<strong><spring:message code="label.TuitionInstallmentTariff.applyInFirstWorkday" />:&nbsp;</strong>
							<c:if test="${installment.interestRate.applyInFirstWorkday}">
								<spring:message code="label.true" />
							</c:if>
							<c:if test="${not installment.interestRate.applyInFirstWorkday}">
								<spring:message code="label.false" />
							</c:if>
						</p>
	
						<c:if test="${installment.interestRate.maximumMonthsToApplyPenaltyApplied}">
							<p>
								<strong><spring:message code="label.TuitionInstallmentTariff.maximumMonthsToApplyPenalty" />:&nbsp;</strong>
								<c:out value="${installment.interestRate.maximumMonthsToApplyPenalty}" />
							</p>
						</c:if>
	
						<p>
							<strong><spring:message code="label.TuitionInstallmentTariff.rate" />:&nbsp;</strong>
							<c:out value="${installment.interestRate.rate}" />&nbsp;&#37;
						</p>
					</c:when>
					
					<c:when test="${installment.interestRate.interestType.fixedAmount}">
						<p>
							<strong><spring:message code="label.TuitionInstallmentTariff.interestFixedAmount" />:&nbsp;</strong>
							<c:out value="${finantialEntity.finantialInstitution.currency.getValueFor(installment.interestRate.interestFixedAmount)}" />
						</p>
					</c:when>
				</c:choose>
			</c:if>
		</datatables:column>
		
	</datatables:table>
	<script>
		createDataTables("paymentPlans-${loopStatus.index}", false, false, false, "${pageContext.request.contextPath}", "${datatablesI18NUrl}");
	</script>
	
	<p>
		<a href="${pageContext.request.contextPath}<%= TuitionPaymentPlanController.SEARCH_TO_DELETE_ACTION_URL %>/${finantialEntity.externalId}/${executionYear.externalId}/${degreeCurricularPlan.externalId}/${paymentPlan.externalId}">
			<spring:message code="label.TuitionPaymentPlan.delete.plan" />
		</a>
		&nbsp;
		<a href="${pageContext.request.contextPath}<%= TuitionPaymentPlanController.ORDER_UP_ACTION_URL %>/${finantialEntity.externalId}/${executionYear.externalId}/${degreeCurricularPlan.externalId}/${paymentPlan.externalId}">
			<spring:message code="label.TuitionPaymentPlan.order.up" />
		</a>
		&nbsp;
		<a href="${pageContext.request.contextPath}<%= TuitionPaymentPlanController.ORDER_DOWN_ACTION_URL %>/${finantialEntity.externalId}/${executionYear.externalId}/${degreeCurricularPlan.externalId}/${paymentPlan.externalId}">
			<spring:message code="label.TuitionPaymentPlan.order.down" />
		</a>
	</p>
	
	<div style="margin-bottom: 60px">
	</div>
	
</c:forEach>


<script>
	$(document).ready(function() {}); 
</script>

