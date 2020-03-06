<%@page import="org.fenixedu.treasury.ui.accounting.managecustomer.DebtAccountController"%>
<%@page import="org.fenixedu.academictreasury.ui.academicdebtgenerationregistration.AcademicDebtGenerationRegistrationController"%>

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

${portal.angularToolkit()}

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
            code="label.AcademicDebtGenerationRegistration.choose.registration" />
        <small></small>
    </h1>
</div>

<%-- NAVIGATION --%>
<div class="well well-sm" style="display: inline-block">
    <span class="glyphicon glyphicon-arrow-left" aria-hidden="true"></span>
    &nbsp; <a class=""
        href="${pageContext.request.contextPath}<%= DebtAccountController.READ_URL %>${debtAccount.externalId}">
        <spring:message code="label.event.back" />
    </a> &nbsp;
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

<form name='form' method="post" class="form-horizontal"
    action='${pageContext.request.contextPath}<%= AcademicDebtGenerationRegistrationController.CREATE_URL %>/${debtAccount.externalId}'>

	<div class="panel panel-primary">
	    <div class="panel-heading">
	        <h3 class="panel-title">
	            <spring:message code="label.Customer.customerDetails" />
	        </h3>
	    </div>
	    <div class="panel-body">
	          <table class="table">
	              <tbody>
	                  <tr>
	                      <th scope="row" class="col-xs-3"><spring:message code="label.Customer.fiscalNumber" /></th>
	                      <td><c:out value='${debtAccount.customer.uiFiscalNumber}' /></td>
	                  </tr>
	
	                  <tr>
	                      <th scope="row" class="col-xs-3"><spring:message code="label.DebtAccount.customer" /></th>
	                      <td><c:out value='${debtAccount.customer.businessIdentification}' /> - <c:out value='${debtAccount.customer.name}' /></td>
	                  </tr>
	                  <tr>
	                      <th scope="row" class="col-xs-3"><spring:message code="label.DebtAccount.finantialInstitution" /></th>
	                      <td><c:out value='${debtAccount.finantialInstitution.name}' /></td>
	                  </tr>
	              </tbody>
	          </table>
	    </div>
	</div>

    <div class="panel panel-default">
        <div class="panel-body">
            <div class="form-group row">
                <div class="col-sm-2 control-label">
                    <spring:message code="label.AcademicDebtGenerationRegistration.registration" />
                </div>

                <div class="col-sm-10">
                    <%-- Relation to side 1 drop down rendered in input --%>
                    <select id="academicDebtGenerationRegistration_registration" name="registrationId">
                    <c:forEach var="registration" items="${AcademicDebtGenerationRegistration_registration_options}">
                        <option value="${registration.externalId}">
                        	<c:out value="${registration.degree.presentationNameI18N.content}" />
                        	(<c:out value='${registration.startDate.toString("yyyy-MM-dd")}' /> <c:out value='${registration.registrationProtocol.description.content}' />)
                        </option>
                    </c:forEach>
                    </select>
                </div>
            </div>
            <div class="form-group row">
                <div class="col-sm-2 control-label">
                    <spring:message code="label.AcademicDebtGenerationRegistration.executionYear" />
                </div>

                <div class="col-sm-10">
                    <%-- Relation to side 1 drop down rendered in input --%>
                    <select id="academicDebtGenerationRegistration_executionYear" name="executionYearId">
                    <c:forEach var="executionYear" items="${AcademicDebtGenerationRegistration_executionYear_options}">
                        <option value="${executionYear.externalId}"><c:out value="${executionYear.qualifiedName}" /></option>
                    </c:forEach>
                    </select>
                </div>
            </div>
        </div>
        <div class="panel-footer">
            <input type="submit" class="btn btn-default" role="button" value="<spring:message code="label.submit" />" />
        </div>
    </div>
</form>

<script>
	$(document).ready(function() {
		$("#academicDebtGenerationRegistration_registration").select2().select2('val', '${param.registrationId != null ? param.registrationId : null}');
		$("#academicDebtGenerationRegistration_executionYear").select2().select2('val', '${param.executionYearId != null ? param.executionYearId : null}');
	});
</script>
