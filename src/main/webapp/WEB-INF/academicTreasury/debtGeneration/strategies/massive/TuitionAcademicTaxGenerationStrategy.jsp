<%@page import="org.fenixedu.academictreasury.ui.createdebts.massive.tuitions.MassiveDebtGenerationRequestFileController"%>
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
		<spring:message code="label.MassiveDebtGenerationRequestFile.confirmdebtcreation" />
		<small><c:out value="${requestFile.executionYear.qualifiedName}" /></small>
	</h1>
</div>

<%-- NAVIGATION --%>
<div class="well well-sm" style="display: inline-block">
	<span class="glyphicon glyphicon-arrow-left" aria-hidden="true"></span>
	&nbsp;
	<a class="" href="${pageContext.request.contextPath}/academictreasury/massivedebtgenerationrequestfile/search">
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

<div class="alert alert-warning" role="alert">
	<strong>
		<span class="glyphicon glyphicon-warning-sign" aria-hidden="true"></span>&nbsp;
		<spring:message code="label.MassiveDebtGenerationRequestFile.test.before.do" />
	</strong>
</div>

<c:choose>
	<c:when test="${not empty rows}">

		<datatables:table id="simpletable" row="r" data="${rows}" cssClass="table responsive table-bordered table-hover" cdn="false" cellspacing="2">

			<datatables:column cssStyle="width:7%">
				<datatables:columnHead>
					<spring:message code="label.MassiveDebtGenerationRequestFile.studentNumber" />
				</datatables:columnHead>
				<c:out value='${r.studentCurricularPlan.registration.student.number}' />
			</datatables:column>

			<datatables:column cssStyle="width:23%">
				<datatables:columnHead>
					<spring:message code="label.MassiveDebtGenerationRequestFile.studentName" />
				</datatables:columnHead>
				<c:out value="${r.studentCurricularPlan.registration.student.name}" />
			</datatables:column>

			<datatables:column cssStyle="width:30%">
				<datatables:columnHead>
					<spring:message code="label.MassiveDebtGenerationRequestFile.degree" />
				</datatables:columnHead>
				<strong>
					<c:out value="${r.studentCurricularPlan.registration.degree.code}" />&nbsp;-&nbsp;
					<c:out value="${r.studentCurricularPlan.registration.degree.presentationNameI18N.content}" />
				</strong>
				<br/>
				<br/>
				<em><c:out value="${r.studentCurricularPlan.name}" /></em>
			</datatables:column>

			<datatables:column cssStyle="width:10%">
				<datatables:columnHead>
					<spring:message code="label.MassiveDebtGenerationRequestFile.registration.startExecutionYear" />
				</datatables:columnHead>
				<c:out value="${r.studentCurricularPlan.registration.startExecutionYear.qualifiedName}" />
			</datatables:column>

			<datatables:column cssStyle="width:10%">
				<datatables:columnHead>
					<spring:message code="label.MassiveDebtGenerationRequestFile.debtDate" />
				</datatables:columnHead>
				<c:out value='${r.debtDate.toString("yyyy-MM-dd")}' />
			</datatables:column>

			<datatables:column cssStyle="width:20%">
				<datatables:columnHead>
					<spring:message code="label.MassiveDebtGenerationRequestFile.tuitionPaymentPlan" />
				</datatables:columnHead>
				<c:out value='${r.tuitionPaymentPlan.conditionsDescription}' />
			</datatables:column>

		</datatables:table>
		<script>
			createDataTables(
					'simpletable',
					false,
					false,
					true,
					"${pageContext.request.contextPath}",
					"${datatablesI18NUrl}");
		</script>
	
		<c:if test="${processable}">
			<form action="${pageContext.request.contextPath}/academictreasury/massivedebtgenerationrequestfile/processrequest/${requestFile.externalId}" method="post">
				<button class="btn btn-primary">
					<spring:message code="label.confirm" />
				</button>
			</form>
		</c:if>		
		
	</c:when>
	
</c:choose>

