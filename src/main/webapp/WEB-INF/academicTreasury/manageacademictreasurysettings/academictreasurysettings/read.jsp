<%@page import="org.fenixedu.academictreasury.ui.manageacademictreasurysettings.AcademicTreasurySettingsController"%>
<%@page import="org.fenixedu.academictreasury.domain.settings.AcademicTreasurySettings"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>
<%@ taglib prefix="datatables" uri="http://github.com/dandelion/datatables"%>

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

<link href="${pageContext.request.contextPath}/static/academictreasury/css/dataTables.responsive.css" rel="stylesheet" />
<script src="${pageContext.request.contextPath}/static/academictreasury/js/dataTables.responsive.js"></script>
<link href="${pageContext.request.contextPath}/webjars/datatables-tools/2.2.4/css/dataTables.tableTools.css" rel="stylesheet" />
<script src="${pageContext.request.contextPath}/webjars/datatables-tools/2.2.4/js/dataTables.tableTools.js"></script>
<link href="${pageContext.request.contextPath}/webjars/select2/4.0.0-rc.2/dist/css/select2.min.css" rel="stylesheet" />
<script src="${pageContext.request.contextPath}/webjars/select2/4.0.0-rc.2/dist/js/select2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/webjars/bootbox/4.4.0/bootbox.js"></script>
<script src="${pageContext.request.contextPath}/static/academicTreasury/js/omnis.js"></script>



<%-- TITLE --%>
<div class="page-header">
    <h1>
        <spring:message code="label.manageAcademicTreasurySettings.readAcademicTreasurySettings" />
        <small></small>
    </h1>
</div>

<!-- /.modal -->
<%-- NAVIGATION --%>
<div class="well well-sm" style="display: inline-block">
    <span class="glyphicon glyphicon-pencil" aria-hidden="true"></span>&nbsp;<a class=""
        href="${pageContext.request.contextPath}<%= AcademicTreasurySettingsController.UPDATE_URL %>"> <spring:message code="label.event.update" /></a> |&nbsp;&nbsp;
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
                <span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true">&nbsp;</span> ${message}
            </p>
        </c:forEach>

    </div>
</c:if>
<c:if test="${not empty errorMessages}">
    <div class="alert alert-danger" role="alert">

        <c:forEach items="${errorMessages}" var="message">
            <p>
                <span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true">&nbsp;</span> ${message}
            </p>
        </c:forEach>

    </div>
</c:if>

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
                        <th scope="row" class="col-xs-3"><spring:message code="label.AcademicTreasurySettings.emolumentsProductGroup" /></th>
                        <td><c:out value='${academicTreasurySettings.emolumentsProductGroup.name.content}' /></td>
                    </tr>
                    <tr>
                        <th scope="row" class="col-xs-3"><spring:message code="label.AcademicTreasurySettings.tuitionProductGroup" /></th>
                        <td><c:out value='${academicTreasurySettings.tuitionProductGroup.name.content}' /></td>
                    </tr>
                    <tr>
                        <th scope="row" class="col-xs-3"><spring:message code="label.AcademicTreasurySettings.improvementAcademicTax" /></th>
                        <td><c:out value="${academicTreasurySettings.improvementAcademicTax.product.name.content}" /></td>
                    </tr>
                    <tr>
                        <th scope="row" class="col-xs-3"><spring:message code="label.AcademicTreasurySettings.closeServiceRequestEmolumentsWithDebitNote" /></th>
                        <td>
                        	<c:if test="${academicTreasurySettings.closeServiceRequestEmolumentsWithDebitNote}">
	                        	<spring:message code="label.true" />
                        	</c:if>
                        	<c:if test="${!academicTreasurySettings.closeServiceRequestEmolumentsWithDebitNote}">
	                        	<spring:message code="label.false" />
                        	</c:if>
                        </td>
                    </tr>
                </tbody>
            </table>
        </form>
    </div>
</div>

<div style="margin-bottom: 50px"></div>

<h2 style="margin-bottom: 50px">
    <spring:message code="label.AcademicTreasurySettings.academicalActBlockingProducts" />
</h2>
<div class="col-sm-12">
<datatables:table id="academicalActBlockingProductsTable" row="p" data="${products}" cssClass="table responsive table-bordered table-hover" cdn="false" cellspacing="2">
    <datatables:column>
        <datatables:columnHead>
            <spring:message code="label.AcademicTreasurySettings.academicalActBlockingProduct" />
        </datatables:columnHead>
        <p>
            <c:out value='${p.name.content}' />
            <c:if test="${academicTreasurySettings.isAcademicalActBlocking(p)}">
                <p class="label label-warning">
                    <spring:message code="label.AcademicTreasurSettings.academicalActBlocking.selected" />
                </p>
            </c:if>
        </p>
    </datatables:column>
    <datatables:column>
        <c:if test="${academicTreasurySettings.isAcademicalActBlocking(p)}">
            <a class="btn btn-danger" href="${pageContext.request.contextPath}<%= AcademicTreasurySettingsController.REMOVEACADEMICALACTBLOCKINGPRODUCT_URL %>${p.externalId}">
                <spring:message code="label.delete" />
            </a>
        </c:if>
        <c:if test="${not academicTreasurySettings.isAcademicalActBlocking(p)}">
            <a class="btn btn-primary" href="${pageContext.request.contextPath}<%= AcademicTreasurySettingsController.ADDACADEMICALACTBLOCKINGPRODUCT_URL %>${p.externalId}"> <spring:message
                    code="label.add" />
            </a>
        </c:if>
    </datatables:column>

</datatables:table>
<script>
    createDataTables("academicalActBlockingProductsTable", true, false, true,
            "${pageContext.request.contextPath}", "${datatablesI18NUrl}");
</script>
</div>


<script>
	$(document).ready(function() {

	});
</script>
