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

<!-- Choose ONLY ONE:  bennuToolkit OR bennuAngularToolkit -->
<%--${portal.angularToolkit()} --%>
${portal.toolkit()}

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



<%-- TITLE --%>
<div class="page-header">
    <h1>
        <spring:message
            code="label.manageTuitionPaymentPlanGroup.createTuitionPaymentPlanGroup" />
        <small></small>
    </h1>
</div>

<%-- NAVIGATION --%>
<div class="well well-sm" style="display: inline-block">
    <span class="glyphicon glyphicon-arrow-left" aria-hidden="true"></span>&nbsp;<a
        class=""
        href="${pageContext.request.contextPath}/academictreasury/managetuitionpaymentplangroup/tuitionpaymentplangroup/"><spring:message
            code="label.event.back" /></a> &nbsp;
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

<form method="post" class="form-horizontal">
    <div class="panel panel-default">
        <div class="panel-body">
            <div class="form-group row">
                <div class="col-sm-2 control-label">
                    <spring:message
                        code="label.TuitionPaymentPlanGroup.code" />
                </div>

                <div class="col-sm-10">
                    <input id="tuitionPaymentPlanGroup_code"
                        class="form-control" type="text" name="code"
                        value='<c:out value='${not empty param.code ? param.code : tuitionPaymentPlanGroup.code }'/>' />
                </div>
            </div>
            <div class="form-group row">
                <div class="col-sm-2 control-label">
                    <spring:message
                        code="label.TuitionPaymentPlanGroup.name" />
                </div>

                <div class="col-sm-10">
                    <input id="tuitionPaymentPlanGroup_name"
                        class="form-control" type="text" name="name"
                        bennu-localized-string
                        value='${not empty param.name ? param.name : "{}" } ' />
                </div>
            </div>
            <div class="form-group row">
                <div class="col-sm-2 control-label">
                    <spring:message
                        code="label.TuitionPaymentPlanGroup.forRegistration" />
                </div>

                <div class="col-sm-2">
                    <select id="tuitionPaymentPlanGroup_forRegistration"
                        name="forRegistration" class="form-control">
                        <option value="false"><spring:message
                                code="label.no" /></option>
                        <option value="true"><spring:message
                                code="label.yes" /></option>
                    </select>
                    <script>
																					$(
																							"#tuitionPaymentPlanGroup_forRegistration")
																							.select2()
																							.select2(
																									'val',
																									'<c:out value='${param.forRegistration}'/>');
																				</script>
                </div>
            </div>

            <div class="form-group row">
                <div class="col-sm-2 control-label">
                    <spring:message
                        code="label.TuitionPaymentPlanGroup.forStandalone" />
                </div>

                <div class="col-sm-2">
                    <select id="tuitionPaymentPlanGroup_forStandalone"
                        name="forStandalone" class="form-control">
                        <option value="false"><spring:message
                                code="label.no" /></option>
                        <option value="true"><spring:message
                                code="label.yes" /></option>
                    </select>
                    <script>
																					$(
																							"#tuitionPaymentPlanGroup_forStandalone")
																							.select2()
																							.select2(
																									'val',
																									'<c:out value='${param.forStandalone}'/>');
																				</script>
                </div>
            </div>

            <div class="form-group row">
                <div class="col-sm-2 control-label">
                    <spring:message
                        code="label.TuitionPaymentPlanGroup.forExtracurricular" />
                </div>

                <div class="col-sm-2">
                    <select
                        id="tuitionPaymentPlanGroup_forExtracurricular"
                        name="forExtracurricular" class="form-control">
                        <option value="false"><spring:message
                                code="label.no" /></option>
                        <option value="true"><spring:message
                                code="label.yes" /></option>
                    </select>
                    <script>
																					
						$("#tuitionPaymentPlanGroup_forExtracurricular").select2().select2('val', '<c:out value='${param.forExtracurricular}'/>');
					
																				</script>
                </div>
            </div>
            <div class="form-group row">
                <div class="col-sm-2 control-label">
                    <spring:message
                        code="label.TuitionPaymentPlanGroup.currentProduct" />
                </div>
                <div class="col-sm-4">
                    <%-- Relation to side 1 drop down rendered in input --%>
                    <select id="tuitionPaymentPlanGroup_currentProduct"
                        class="js-example-basic-single"
                        name="currentProduct">
                        <option value=""></option>
                        <c:forEach var="p" items="${products}">
                            <option value="${p.externalId}">${p.name.content}</option>
                        </c:forEach>
                    </select>
                </div>
            </div>
            <script>
													$(
															"#tuitionPaymentPlanGroup_currentProduct")
															.select2()
															.select2('val',
																	'_$tag___________________________________');
												</script>
        </div>
        <div class="panel-footer">
            <input type="submit" class="btn btn-default" role="button"
                value="<spring:message code="label.submit" />" />
        </div>
    </div>
</form>

<script>
	$(document).ready(function() {
	});
</script>
