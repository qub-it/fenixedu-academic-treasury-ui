<%@page import="org.fenixedu.academictreasury.ui.managetuitionpaymentplan.ImportTuitionPaymentPlansController"%>
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
		<spring:message code="label.ImportTuitionPaymentPlans.create" />
		<small></small>
	</h1>
</div>

<%-- NAVIGATION --%>
<div class="well well-sm" style="display: inline-block">
	<span class="glyphicon glyphicon-arrow-left" aria-hidden="true"></span>
	&nbsp;
	<a class="" href="${pageContext.request.contextPath}<%= ImportTuitionPaymentPlansController.SEARCH_URL %>">
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


<form method="post" class="form-horizontal" enctype="multipart/form-data">
	<div class="panel panel-default">
		<div class="panel-body">
			<div class="form-group row">
				<div class="col-sm-2 control-label">
					<spring:message code="label.TuitionPaymentPlanImportFile.finantialEntity" />
				</div>

				<div class="col-sm-4">
					<%-- Relation to side 1 drop down rendered in input --%>
					<select id="finantialEntity" class="js-example-basic-single" name="finantialEntityId">
						<option value="">&nbsp;</option>
						<c:forEach var="f" items="${finantialEntityList}">
							<option value="${f.externalId}"><c:out value="${f.name.content}" /></option>
						</c:forEach>
					</select>
					<script>
						$(document).ready(function() {
							$("#finantialEntity").select2().select2("val", '<c:out value="${param.finantialEntityId}" />');
						});
					</script>
				</div>
			</div>

			<div class="form-group row">
				<div class="col-sm-2 control-label">
					<spring:message code="label.TuitionPaymentPlanImportFile.product" />
				</div>

				<div class="col-sm-4">
					<%-- Relation to side 1 drop down rendered in input --%>
					<select id="product" class="js-example-basic-single" name="productId">
						<option value="">&nbsp;</option>
						<c:forEach var="p" items="${productList}">
							<option value="${p.externalId}"><c:out value="${p.name.content}" /></option>
						</c:forEach>
					</select>
					<script>
						$(document).ready(function() {
							$("#product").select2().select2("val", '<c:out value="${param.productId}" />');
						});
					</script>
				</div>
			</div>

			<div class="form-group row">
				<div class="col-sm-2 control-label">
					<spring:message code="label.TuitionPaymentPlanImportFile.tuitionPaymentPlanGroup" />
				</div>

				<div class="col-sm-4">
					<%-- Relation to side 1 drop down rendered in input --%>
					<select id="tuitionPaymentPlanGroup" class="js-example-basic-single" name="tuitionPaymentPlanGroupId">
						<option value="">&nbsp;</option>
						<c:forEach var="t" items="${tuitionPaymentPlanGroupList}">
							<option value="${t.externalId}"><c:out value="${t.name.content}" /></option>
						</c:forEach>
					</select>
					<script>
						$(document).ready(function() {
							$("#tuitionPaymentPlanGroup").select2().select2("val", '<c:out value="${param.tuitionPaymentPlanGroupId}" />');
						});
					</script>
				</div>
			</div>

			<div class="form-group row">
				<div class="col-sm-2 control-label">
					<spring:message code="label.TuitionPaymentPlanImportFile.executionYear" />
				</div>

				<div class="col-sm-4">
					<%-- Relation to side 1 drop down rendered in input --%>
					<select id="executionYear" class="js-example-basic-single" name="executionYearId">
						<option value="">&nbsp;</option>
						<c:forEach var="e" items="${executionYearList}">
							<option value="${e.externalId}"><c:out value="${e.qualifiedName}" /></option>
						</c:forEach>
					</select>
					<script>
						$(document).ready(function() {
							$("#executionYear").select2().select2("val", '<c:out value="${param.executionYearId}" />');
						});
					</script>
				</div>
			</div>

			<div class="form-group row">
				<div class="col-sm-2 control-label">
					<spring:message code="label.TuitionPaymentPlanImportFile.requestFile" />
				</div>

				<div class="col-sm-6">
					<input type="file" name="requestFile" accept="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" />
				</div>
			</div>

		</div>
		<div class="panel-footer">
			<input type="submit" class="btn btn-default" role="button"
				value="<spring:message code="label.submit" />" />
		</div>
	</div>
</form>

