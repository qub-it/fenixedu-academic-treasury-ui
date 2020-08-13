<%@page import="org.fenixedu.academic.domain.contacts.PhysicalAddress"%>
<%@page import="org.fenixedu.academictreasury.domain.customer.PersonCustomer"%>
<%@page import="org.fenixedu.academictreasury.ui.document.forwardpayments.implementations.onlinepaymentsgateway.sibs.SibsOnlinePaymentsGatewayForwardPaymentController"%>
<%@page import="org.fenixedu.treasury.ui.document.forwardpayments.ForwardPaymentController"%>
<%@page import="org.fenixedu.treasury.ui.accounting.managecustomer.DebtAccountController"%>
<%@page import="org.fenixedu.treasury.ui.document.managepayments.SettlementNoteController"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>
<%@ taglib prefix="pf" uri="http://example.com/placeFunctions"%>
<spring:url var="datatablesUrl" value="/javaScript/dataTables/media/js/jquery.dataTables.latest.min.js" />
<spring:url var="datatablesBootstrapJsUrl" value="/javaScript/dataTables/media/js/jquery.dataTables.bootstrap.min.js"></spring:url>
<script type="text/javascript" src="${datatablesUrl}"></script>
<script type="text/javascript" src="${datatablesBootstrapJsUrl}"></script>
<spring:url var="datatablesCssUrl" value="/CSS/dataTables/dataTables.bootstrap.min.css" />

<link rel="stylesheet" href="${datatablesCssUrl}" />
<spring:url var="datatablesI18NUrl" value="/javaScript/dataTables/media/i18n/${portal.locale.language}.json" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CSS/dataTables/dataTables.bootstrap.min.css" />

${portal.angularToolkit()}


<link href="${pageContext.request.contextPath}/static/academicTreasury/css/dataTables.responsive.css" rel="stylesheet" />
<script src="${pageContext.request.contextPath}/static/academicTreasury/js/dataTables.responsive.js"></script>
<link href="${pageContext.request.contextPath}/webjars/datatables-tools/2.2.4/css/dataTables.tableTools.css" rel="stylesheet" />
<script src="${pageContext.request.contextPath}/webjars/datatables-tools/2.2.4/js/dataTables.tableTools.js"></script>
<link href="${pageContext.request.contextPath}/webjars/select2/4.0.0-rc.2/dist/css/select2.min.css" rel="stylesheet" />
<script src="${pageContext.request.contextPath}/webjars/select2/4.0.0-rc.2/dist/js/select2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/webjars/bootbox/4.4.0/bootbox.js"></script>
<script src="${pageContext.request.contextPath}/static/academicTreasury/js/omnis.js"></script>

<script src="${pageContext.request.contextPath}/webjars/angular-sanitize/1.3.11/angular-sanitize.js"></script>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/webjars/angular-ui-select/0.11.2/select.min.css" />
<script src="${pageContext.request.contextPath}/webjars/angular-ui-select/0.11.2/select.min.js"></script>


<%-- TITLE --%>
<div class="page-header">
	<h1><spring:message code="label.ForwardPaymentController.onlinePayment" /></h1>
    <h1><small><spring:message code="label.ForwardPaymentController.chooseInvoiceEntries" /></small></h1>
	
	<div>
		<div class="well well-sm">
			<p>
				<strong><spring:message code="label.DebtAccount.finantialInstitution" />:</strong>
				<c:out value="${debtAccount.finantialInstitution.name}" />
			</p>
			<p>
				<strong><spring:message code="label.FinantialInstitution.fiscalNumber" />:</strong>
				<c:out value="${debtAccount.finantialInstitution.fiscalNumber}" />
			</p>
			<p>
				<strong><spring:message code="label.DebtAccount.finantialInstitution.address" />:</strong>
				<c:out value="${debtAccount.finantialInstitution.address}" />,&nbsp;
				<c:out value="${debtAccount.finantialInstitution.zipCode}" />&nbsp;-&nbsp;
				<c:out value="${debtAccount.finantialInstitution.locality}" />,&nbsp;
				<pf:placeName place="${debtAccount.finantialInstitution.country}" />
			</p>
			<p>
				<strong><spring:message code="label.FinantialInstitution.telephoneContact" />:</strong>
				<c:out value="${debtAccount.finantialInstitution.telephoneContact}" />
			</p>
			<p>
				<strong><spring:message code="label.FinantialInstitution.email" />:</strong>
				<c:out value="${debtAccount.finantialInstitution.email}" />
			</p>
			
            <p>&nbsp;</p>
			<p>
				<strong><spring:message code="label.DebtAccount.customer" />:</strong>
				<c:out value="${debtAccount.customer.businessIdentification} - ${debtAccount.customer.name}" />
			</p>
			<p>
				<strong><spring:message code="label.Customer.fiscalNumber" />:</strong>
				<c:out value="${debtAccount.customer.uiFiscalNumber}" />
			</p>
		</div>
	</div>
</div>

<%-- NAVIGATION --%>
<div class="well well-sm" style="display: inline-block">
	<span class="glyphicon glyphicon-arrow-left" aria-hidden="true"></span>
	&nbsp;
	<a class="" href="${pageContext.request.contextPath}${debtAccountUrl}${debtAccount.externalId}">
		<spring:message code="label.event.back" />
	</a>
	&nbsp;
</div>
<c:if test="${not empty infoMessages}">
	<div class="alert alert-info" role="alert">
		<c:forEach items="${infoMessages}" var="message">
			<p>
				<span class="glyphicon glyphicon glyphicon-ok-sign" aria-hidden="true">&nbsp;</span>
				${message}
			</p>
		</c:forEach>
	</div>
</c:if>
<c:if test="${not empty warningMessages}">
	<div class="alert alert-warning" role="alert">
		<c:forEach items="${warningMessages}" var="message">
			<p>
				<span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true">&nbsp;</span>
				${message}
			</p>
		</c:forEach>
	</div>
</c:if>
<c:if test="${not empty errorMessages}">
	<div class="alert alert-danger" role="alert">
		<c:forEach items="${errorMessages}" var="message">
			<p>
				<span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true">&nbsp;</span>
				${message}
			</p>
		</c:forEach>
	</div>
</c:if>

<div>
	<p>
			1.
			<spring:message code="label.ForwardPaymentController.chooseInvoiceEntries" />
			<span class="glyphicon glyphicon-arrow-right" aria-hidden="true"></span>
			2.
			<spring:message code="label.ForwardPaymentController.confirmPayment" />
			<span class="glyphicon glyphicon-arrow-right" aria-hidden="true"></span>
		<strong>
			3.
			<spring:message code="label.ForwardPaymentController.enterPaymentDetails" />
			<span class="glyphicon glyphicon-arrow-right" aria-hidden="true"></span>
		</strong>
			4.
			<spring:message code="label.ForwardPaymentController.paymentConfirmation" />
	</p>
</div>

<script>
	angular.module('angularApp', [ 'ngSanitize', 'ui.select', 'bennuToolkit' ]).controller(
			'controller', [ '$scope', function($scope) {

				//Begin here of Custom Screen business JS - code
				
			}]);
</script>

<form name='form' method="post" class="form-horizontal" ng-app="angularApp" ng-controller="controller"
    action='${pageContext.request.contextPath}<%= SibsOnlinePaymentsGatewayForwardPaymentController.SELECT_PHYSICAL_ADDRESS_URL %>/${forwardPayment.externalId}'>

    <div class="panel panel-primary">
		<div class="panel-heading">
			<h3 class="panel-title">
				<spring:message code="label.SibsOnlinePaymentsGatewayForwardPaymentController.selectPhysicalAddress.title" />
			</h3>
		</div>
    
        <div class="panel-body">
			
			<div class="alert alert-info" role="alert">
				<p><span class="glyphicon glyphicon glyphicon-info-sign" aria-hidden="true"></span></p>
				<p><spring:message code="label.SibsOnlinePaymentsGatewayForwardPaymentController.selectPhysicalAddress.text" /></p>
				<p><spring:message code="label.SibsOnlinePaymentsGatewayForwardPaymentController.selectPhysicalAddress.text2" /></p>
			</div>
			
            <div class="form-group row">
                <div class="col-sm-2 control-label">
                    <spring:message code="label.Customer.fiscalAddress" />
                </div>

                <div class="col-sm-10">
                	<select name="physicalAddressId" class="form-control">
                		<option value="" onchange="changeAddressPresentation(e)">
                			<spring:message code="label.SibsOnlinePaymentsGatewayForwardPaymentController.select.address" />
                		</option>
                		<c:forEach items="${physicalAddresses}" var="address">
                			<c:set var="billingCountryCode">
                				<%= PersonCustomer.addressCountryCode((PhysicalAddress) pageContext.getAttribute("address")) %>
                			</c:set>
                			<c:set var="billingAddress">
                				<%= PersonCustomer.address((PhysicalAddress) pageContext.getAttribute("address")) %>
                			</c:set>
                			<c:set var="billingCity">
                				<%= PersonCustomer.districtSubdivision((PhysicalAddress) pageContext.getAttribute("address")) %>
                			</c:set>
                			<c:set var="billingZipCode">
                				<%= PersonCustomer.zipCode((PhysicalAddress) pageContext.getAttribute("address")) %>
                			</c:set>
                			
                			<c:if test="${empty billingCity}">
	                			<c:set var="billingCity">
	                				<%= PersonCustomer.region((PhysicalAddress) pageContext.getAttribute("address")) %>
	                			</c:set>
                			</c:if>
                			
                			<c:choose>
                				<c:when test="${params.fiscalAddressId == address.externalId}">
			                		<option value="${address.externalId}"
			                			data-address-country-code="${billingCountryCode}"
			                			data-address="${billingAddress}"
			                			data-city="${billingCity}"
			                			data-zip-code="${billingZipCode}" selected="selected" onselect="changeAddressPresentation(e)">
			                			<c:out value="${address.uiFiscalPresentationValue}" />
			                		</option>
                				</c:when>
                				<c:otherwise>
			                		<option value="${address.externalId}"
			                			data-address-country-code="${billingCountryCode}"
			                			data-address="${billingAddress}"
			                			data-city="${billingCity}"
			                			data-zip-code="${billingZipCode}" onselect="changeAddressPresentation(e)">
			                			<c:out value="${address.uiFiscalPresentationValue}" />
			                		</option>
                				</c:otherwise>
                			</c:choose>
                		</c:forEach>
                	</select>
                </div>
            </div>
        </div>
        <div class="panel-footer">
			<button type="submit" class="btn btn-primary">
				<spring:message code="label.continue" />
				&nbsp;<span class="glyphicon glyphicon-chevron-right" aria-hidden="true"></span>
			</button>
        </div>
    </div>
</form>

<c:if test="${forwardPaymentConfiguration.isLogosPageDefined()}">
	<jsp:include page="${logosPage}" />
</c:if>

<script>
	$(document).ready(function() {});
</script>
	
