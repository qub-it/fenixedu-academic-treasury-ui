<%@page import="org.fenixedu.academictreasury.ui.exemptions.requests.ExemptionsGenerationRequestFileController"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>
<%@ taglib prefix="datatables" uri="http://github.com/dandelion/datatables"%>
<%@ taglib prefix="joda" uri="http://www.joda.org/joda/time/tags"%>

<spring:url var="datatablesUrl" value="/javaScript/dataTables/media/js/jquery.dataTables.latest.min.js" />
<spring:url var="datatablesBootstrapJsUrl" value="/javaScript/dataTables/media/js/jquery.dataTables.bootstrap.min.js"></spring:url>
<script type="text/javascript" src="${datatablesUrl}"></script>
<script type="text/javascript" src="${datatablesBootstrapJsUrl}"></script>
<spring:url var="datatablesCssUrl" value="/CSS/dataTables/dataTables.bootstrap.min.css" />

<link rel="stylesheet" href="${datatablesCssUrl}" />
<spring:url var="datatablesI18NUrl" value="/javaScript/dataTables/media/i18n/${portal.locale.language}.json" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CSS/dataTables/dataTables.bootstrap.min.css" />

<!-- Choose ONLY ONE:  bennuToolkit OR bennuAngularToolkit -->
<%--${portal.angularToolkit()} --%>
${portal.toolkit()}

<link href="${pageContext.request.contextPath}/static/academicTreasury/css/dataTables.responsive.css" rel="stylesheet" />
<script src="${pageContext.request.contextPath}/static/academicTreasury/js/dataTables.responsive.js"></script>
<link href="${pageContext.request.contextPath}/webjars/datatables-tools/2.2.4/css/dataTables.tableTools.css" rel="stylesheet" />
<script src="${pageContext.request.contextPath}/webjars/datatables-tools/2.2.4/js/dataTables.tableTools.js"></script>
<link href="${pageContext.request.contextPath}/webjars/select2/4.0.0-rc.2/dist/css/select2.min.css" rel="stylesheet" />
<script src="${pageContext.request.contextPath}/webjars/select2/4.0.0-rc.2/dist/js/select2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/webjars/bootbox/4.4.0/bootbox.js"></script>
<script src="${pageContext.request.contextPath}/static/academicTreasury/js/omnis.js"></script>


<div class="page-header">
	<h1>
		<spring:message code="label.ExemptionsGenerationRequestFile.confirm" />
	</h1>
</div>

<%-- NAVIGATION --%>
<div class="well well-sm" style="display: inline-block">
	<span class="glyphicon glyphicon-arrow-left" aria-hidden="true"></span>
	&nbsp;
	<a class="" href="${pageContext.request.contextPath}<%= ExemptionsGenerationRequestFileController.SEARCH_URL %>">
		<spring:message code="label.back" />
	</a>
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

<c:choose>
	<c:when test="${not empty rows}">

		<datatables:table id="simpletable" row="r" data="${rows}" cssClass="table responsive table-bordered table-hover" cdn="false" cellspacing="2">

			<datatables:column cssStyle="width:35%">
				<datatables:columnHead>
					<spring:message code="label.ExemptionsGenerationRequestFile.student" />
				</datatables:columnHead>
	
				<p>
					<strong><spring:message code="label.ExemptionsGenerationRequestFile.studentNumber" />:</strong>&nbsp;
					<c:out value='${r.registration.number}' />
				</p>
				<p>
					<strong><spring:message code="label.ExemptionsGenerationRequestFile.studentName" />:</strong>&nbsp;
					<c:out value='${r.registration.student.name}' />
				</p>
				<p>
					<strong><spring:message code="label.ExemptionsGenerationRequestFile.degree" />:</strong>&nbsp;
					<strong>
						<c:out value="${r.registration.degree.code}" />&nbsp;-&nbsp;
						<c:out value="${r.registration.degree.presentationNameI18N.content}" />
						&nbsp;[<c:out value="${r.executionYear.qualifiedName}" />]
					</strong>
				</p>
			</datatables:column>

			<datatables:column cssStyle="width:65%">
					<datatables:columnHead>
						<spring:message code="label.ExemptionsGenerationRequestFile.exemption.summary" />
					</datatables:columnHead>
				
							<p>
								<strong><spring:message code="label.ExemptionsGenerationRequestFile.treasuryEvent" />:</strong>
								<c:out value="${r.treasuryEvent.description.content}" />
							</p>
		
							<p>
								<strong><spring:message code="label.ExemptionsGenerationRequestFile.discountPercentage" />:</strong>
								<c:out value='${r.discountPercentage}%' />
							</p>
		
							<p>
								<strong><spring:message code="label.ExemptionsGenerationRequestFile.reason" />:</strong>
								<br />
								<em><c:out value='${r.reason}' /></em>
							</p>
				
				<c:choose>
					<c:when test="${not empty r.debitEntry}">
								<p>
									<strong><spring:message code="label.ExemptionsGenerationRequestFile.debitEntry" />:</strong>
									<c:out value="${r.debitEntry.description}" />&nbsp;
			                        (<c:out value='${r.debitEntry.currency.getValueFor(r.debitEntry.totalAmount)}' />)
								</p>
								<p>
									<strong><spring:message code="label.ExemptionsGenerationRequestFile.discountAmount" />:</strong>
									<c:out value="${r.debitEntry.currency.getValueFor(r.getDiscountAmount())}" />
								</p>
					</c:when>
					
					<c:when test="${not empty r.tuitionInstallmentsOrderSet}">
						<c:forEach items="${r.tuitionInstallmentsOrderSet}" var="t">
							<c:if test="${not empty r.getTuitionDebitEntry(t)}">
								<c:set var="debitEntry" value="${r.getTuitionDebitEntry(t)}" />

								<p>
									<strong><spring:message code="label.ExemptionsGenerationRequestFile.debitEntry" />:</strong>
									<c:out value="${debitEntry.description}" />&nbsp;
			                        (<c:out value='${debitEntry.currency.getValueFor(debitEntry.totalAmount)}' />)
								</p>
								<p>
									<strong><spring:message code="label.ExemptionsGenerationRequestFile.discountAmount" />:</strong>
									<c:out value="${debitEntry.currency.getValueFor(r.getDiscountAmount(t))}" />
								</p>
							</c:if>
						</c:forEach>
					</c:when>
				</c:choose>
			</datatables:column>
	
		</datatables:table>
		<script>
			createDataTables(
					'simpletable',
					true,
					false,
					true,
					"${pageContext.request.contextPath}",
					"${datatablesI18NUrl}");
		</script>
	
		<c:if test="${processable}">
			<form action="${pageContext.request.contextPath}<%= ExemptionsGenerationRequestFileController.PROCESSREQUEST_URL %>/${requestFile.externalId}" method="post">
				<button class="btn btn-primary">
					<spring:message code="label.confirm" />
				</button>
			</form>
		</c:if>		
		
	</c:when>
	
</c:choose>

