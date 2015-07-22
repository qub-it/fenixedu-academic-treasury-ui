<%@page
    import="org.fenixedu.academictreasury.ui.manageacademicdebtgenerationrule.AcademicDebtGenerationRuleController"%>
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
            code="label.manageacademicdebtgenerationrule.searchAcademicDebtGenerationRule" />
        <small></small>
    </h1>
</div>
<%-- NAVIGATION --%>
<div class="well well-sm" style="display: inline-block">
    <span class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span>&nbsp;<a
        class=""
        href="${pageContext.request.contextPath}/academictreasury/manageacademicdebtgenerationrule/academicdebtgenerationrule/create"><spring:message
            code="label.event.create" /></a> &nbsp;
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


<script type="text/javascript">
	function processDelete(externalId) {
		url = "${pageContext.request.contextPath}/academictreasury/manageacademicdebtgenerationrule/academicdebtgenerationrule/search/delete/"
				+ externalId;
		$("#deleteForm").attr("action", url);
		$('#deleteModal').modal('toggle')
	}
</script>


<div class="modal fade" id="deleteModal">
    <div class="modal-dialog">
        <div class="modal-content">
            <form id="deleteForm" action="#" method="POST">
                <div class="modal-header">
                    <button type="button" class="close"
                        data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                    <h4 class="modal-title">
                        <spring:message code="label.confirmation" />
                    </h4>
                </div>
                <div class="modal-body">
                    <p>
                        <spring:message
                            code="label.manageacademicdebtgenerationrule.searchAcademicDebtGenerationRule.confirmDelete" />
                    </p>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default"
                        data-dismiss="modal">
                        <spring:message code="label.close" />
                    </button>
                    <button id="deleteButton" class="btn btn-danger"
                        type="submit">
                        <spring:message code="label.delete" />
                    </button>
                </div>
            </form>
        </div>
        <!-- /.modal-content -->
    </div>
    <!-- /.modal-dialog -->
</div>
<!-- /.modal -->



<c:choose>
    <c:when
        test="${not empty searchacademicdebtgenerationruleResultsDataSet}">
        <table id="searchacademicdebtgenerationruleTable"
            class="table responsive table-bordered table-hover">
            <thead>
                <tr>
                    <%--!!!  Field names here --%>
                    <th><spring:message
                            code="label.AcademicDebtGenerationRule.academicDebtGenerationRuleEntries" /></th>
                    <th><spring:message
                            code="label.AcademicDebtGenerationRule.executionYear" /></th>
                    <%-- Operations Column --%>
                    <th></th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="rule"
                    items="${searchacademicdebtgenerationruleResultsDataSet}">
                    <tr>
                        <td><c:forEach var="entry"
                                items="${rule.academicDebtGenerationRuleEntriesSet}">
                                <p>
                                    ${entry.product.name.content} &nbsp;
                                    <c:if test="${entry.createDebt}">
										(<em><spring:message
                                                code="label.AcademicDebtGenerationRuleEntry.createDebt" /></em>)
									</c:if>
                                </p>
                            </c:forEach></td>
                        <td>
                            <p>
                                <span><strong><spring:message
                                            code="label.AcademicDebtGenerationRule.executionYear" />:</strong></span>
                                <span>${rule.executionYear.qualifiedName}</span>
                            </p>

                            <p>
                                <span><strong><spring:message
                                            code="label.AcademicDebtGenerationRule.aggregateOnDebitNote" />:</strong></span>
                                <span> <c:if
                                        test="${rule.aggregateOnDebitNote}">
                                        <spring:message
                                            code="label.true" />
                                    </c:if> <c:if
                                        test="${not rule.aggregateOnDebitNote}">
                                        <spring:message
                                            code="label.false" />
                                    </c:if>
                                </span>
                            </p> <c:if test="${rule.aggregateOnDebitNote}">
                                <p>
                                    <span><strong><spring:message
                                                code="label.AcademicDebtGenerationRule.aggregateAllOrNothing" />:</strong></span>
                                    <span> <c:if
                                            test="${rule.aggregateAllOrNothing}">
                                            <spring:message
                                                code="label.true" />
                                        </c:if> <c:if
                                            test="${not rule.aggregateAllOrNothing}">
                                            <spring:message
                                                code="label.false" />
                                        </c:if>
                                    </span>
                                </p>
                            </c:if> <c:if test="${rule.aggregateOnDebitNote}">
                                <p>
                                    <span><strong><spring:message
                                                code="label.AcademicDebtGenerationRule.closeDebitNote" />:</strong></span>
                                    <span> <c:if
                                            test="${rule.closeDebitNote}">
                                            <spring:message
                                                code="label.true" />
                                        </c:if> <c:if
                                            test="${not rule.closeDebitNote}">
                                            <spring:message
                                                code="label.false" />
                                        </c:if>
                                    </span>
                                </p>
                            </c:if> <c:if test="${rule.closeDebitNote}">
                                <p>
                                    <span><strong><spring:message
                                                code="label.AcademicDebtGenerationRule.createPaymentReferenceCode" />:</strong></span>
                                    <span> <c:if
                                            test="${rule.createPaymentReferenceCode}">
                                            <spring:message
                                                code="label.true" />
                                        </c:if> <c:if
                                            test="${not rule.createPaymentReferenceCode}">
                                            <spring:message
                                                code="label.false" />
                                        </c:if>
                                    </span>
                                </p>
                            </c:if> <c:if test="${rule.active}">
                                <p style="color: green;">
                                    <strong><spring:message
                                            code="label.AcademicDebtGenerationRule.active.message" /></strong>
                                </p>
                            </c:if> <c:if test="${not rule.active}">
                                <p style="color: red;">
                                    <strong><spring:message
                                            code="label.AcademicDebtGenerationRule.inactive.message" /></strong>
                                </p>
                            </c:if>

                        </td>
                        <td><a class="btn-xs btn-warning"
                            href="#"
                            onClick="javascript:processDelete('${rule.externalId}')">
                                <span class="glyphicon glyphicon-trash"
                                aria-hidden="true"></span>&nbsp;<spring:message
                                    code='label.delete' />
                        </a> <c:if test="${rule.active}">

                                <a class="btn-default btn-xs"
                                    href="${pageContext.request.contextPath}<%= AcademicDebtGenerationRuleController.PROCESS_ACTION_URL %>${rule.externalId}">
                                    <span class="glyphicon glyphicon-cog"
                                aria-hidden="true"></span>&nbsp;<spring:message
                                        code='label.manageacademicdebtgenerationrule.process' />
                                </a>

                                <a class="btn-default btn-xs"
                                    href="${pageContext.request.contextPath}<%= AcademicDebtGenerationRuleController.SEARCH_TO_INACTIVATE_ACTION_URL %>${rule.externalId}">
                                    <spring:message
                                        code='label.manageacademicdebtgenerationrule.inactivate' />
                                </a>
                            </c:if> <c:if test="${not rule.active}">
                                <a class="btn-xs btn-default"
                                    href="${pageContext.request.contextPath}<%= AcademicDebtGenerationRuleController.SEARCH_TO_ACTIVATE_ACTION_URL %>${rule.externalId}">
                                    <spring:message
                                        code='label.manageacademicdebtgenerationrule.activate' />
                                </a>
                            </c:if>
                            <a class="btn-default btn-xs"
                                href="${pageContext.request.contextPath}<%= AcademicDebtGenerationRuleController.SEARCH_TO_READ_LOG_ACTION_URL %>${rule.externalId}">
                                <spring:message
                                    code='label.manageacademicdebtgenerationrule.readlog' />
                            </a>
                            </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </c:when>
    <c:otherwise>
        <div class="alert alert-warning" role="alert">

            <p>
                <span class="glyphicon glyphicon-exclamation-sign"
                    aria-hidden="true">&nbsp;</span>
                <spring:message code="label.noResultsFound" />
            </p>

        </div>

    </c:otherwise>
</c:choose>

<script>
	$(document)
			.ready(
					function() {

						var table = $('#searchacademicdebtgenerationruleTable')
								.DataTable(
										{
											language : {
												url : "${datatablesI18NUrl}",
											},
											//CHANGE_ME adjust the actions column width if needed
											"columnDefs" : [
											//74
											//222
											{
												"width" : "222px",
												"targets" : 2
											} ],
											//Documentation: https://datatables.net/reference/option/dom
											//"dom": '<"col-sm-6"l><"col-sm-3"f><"col-sm-3"T>rtip', //FilterBox = YES && ExportOptions = YES
											//"dom": 'T<"clear">lrtip', //FilterBox = NO && ExportOptions = YES
											//"dom": '<"col-sm-6"l><"col-sm-6"f>rtip', //FilterBox = YES && ExportOptions = NO
											"dom" : '<"col-sm-6"l>rtip', // FilterBox = NO && ExportOptions = NO
											"tableTools" : {
												"sSwfPath" : "${pageContext.request.contextPath}/webjars/datatables-tools/2.2.4/swf/copy_csv_xls_pdf.swf"
											}
										});
						table.columns.adjust().draw();

						$('#searchacademicdebtgenerationruleTable tbody').on(
								'click', 'tr', function() {
									$(this).toggleClass('selected');
								});

					});
</script>

