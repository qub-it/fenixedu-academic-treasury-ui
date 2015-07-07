<%@page import="org.fenixedu.academictreasury.ui.academicservicerequestdebtcreation.AcademicServiceRequestDebtCreationBeanController"%>
<%@page import="org.fenixedu.academictreasury.ui.academictaxdebtcreation.AcademicTaxDebtCreationBeanController"%>
<%@page import="org.fenixedu.academictreasury.ui.tuitiondebtcreation.standalone.OtherTuitionDebtCreationBeanController"%>
<%@page import="org.fenixedu.academictreasury.ui.tuitiondebtcreation.TuitionDebtCreationBeanController"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>
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
    <h1>
        <spring:message code="label.TuitionDebtCreation.createTuitionDebtCreationBean" />
        <small></small>
    </h1>
</div>

<%-- NAVIGATION --%>
<div class="well well-sm" style="display: inline-block">
    <span class="glyphicon glyphicon-arrow-left" aria-hidden="true"></span> &nbsp; <a class=""
        href="${pageContext.request.contextPath}/treasury/accounting/managecustomer/debtaccount/read/${debtAccount.externalId}"> <spring:message code="label.event.back" />
    </a> |&nbsp;&nbsp;
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

<div>
    <ul class="list-group">
        <li class="list-group-item"><strong>
        <span class="glyphicon glyphicon-bookmark" aria-hidden="true"></span>&nbsp;
         <a href="${pageContext.request.contextPath}<%= TuitionDebtCreationBeanController.CREATE_URL %>/${debtAccount.externalId}">
                     <spring:message code="label.TuitionDebtCreationBean.create.tuition.debts" />
            </a>
        </strong></li>
        <li class="list-group-item"><strong>
         <span class="glyphicon glyphicon-bookmark" aria-hidden="true"></span>&nbsp; <a
                href="${pageContext.request.contextPath}<%= OtherTuitionDebtCreationBeanController.CREATESTANDALONE_URL %>/${debtAccount.externalId}"> <spring:message
                        code="label.TuitionDebtCreationBean.create.standalonetuition.debts" />
            </a>
        </strong></li>
        <li class="list-group-item"><strong>  <span class="glyphicon glyphicon-bookmark" aria-hidden="true"></span>&nbsp;<a
                href="${pageContext.request.contextPath}<%= OtherTuitionDebtCreationBeanController.CREATEEXTRACURRICULAR_URL %>/${debtAccount.externalId}"> <spring:message
                        code="label.TuitionDebtCreationBean.create.extracurriculartuition.debts" />
            </a>
        </strong></li>
        <li class="list-group-item"><strong> 
         <span class="glyphicon glyphicon-book" aria-hidden="true"></span>&nbsp;<a
                href="${pageContext.request.contextPath}<%= AcademicTaxDebtCreationBeanController.CREATE_URL %>/${debtAccount.externalId}"> <spring:message
                        code="label.AcademicTaxDebtCreationBean.create.academictax.debts" />
            </a>
        </strong></li>
        <li class="list-group-item"><strong> <span class="glyphicon glyphicon-book" aria-hidden="true"></span>&nbsp; <a
                href="${pageContext.request.contextPath}<%= AcademicServiceRequestDebtCreationBeanController.CREATE_URL %>/${debtAccount.externalId}"> <spring:message
                        code="label.AcademicServiceRequestDebtCreationBean.create.academicservicerequest.debts" />
            </a>
        </strong></li>
    </ul>
</div>
