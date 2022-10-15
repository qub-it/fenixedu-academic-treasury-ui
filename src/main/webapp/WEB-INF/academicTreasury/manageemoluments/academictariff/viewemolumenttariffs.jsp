<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>
<%@ taglib prefix="joda" uri="http://www.joda.org/joda/time/tags"%>

<script type="text/javascript" src="https://cdn.datatables.net/1.10.12/js/jquery.dataTables.min.js"></script>
<script type="text/javascript" src="https://cdn.datatables.net/select/1.2.0/js/dataTables.select.min.js"></script>
<script type="text/javascript" src="https://cdn.datatables.net/buttons/1.2.2/js/dataTables.buttons.min.js"></script>
<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/jszip/2.5.0/jszip.min.js"></script>
<script type="text/javascript" src="https://cdn.rawgit.com/bpampuch/pdfmake/0.1.18/build/pdfmake.min.js"></script>
<script type="text/javascript" src="https://cdn.rawgit.com/bpampuch/pdfmake/0.1.18/build/vfs_fonts.js"></script>
<script type="text/javascript" src="https://cdn.datatables.net/buttons/1.2.2/js/buttons.html5.min.js"></script>
<link rel="stylesheet" type="text/css" href="https://cdn.datatables.net/buttons/1.2.2/css/buttons.dataTables.min.css" />


<spring:url var="datatablesBootstrapJsUrl" value="/javaScript/dataTables/media/js/jquery.dataTables.bootstrap.min.js"></spring:url>
<script type="text/javascript" src="${datatablesBootstrapJsUrl}"></script>
<spring:url var="datatablesCssUrl" value="/CSS/dataTables/dataTables.bootstrap.min.css" />

<link rel="stylesheet" href="${datatablesCssUrl}" />
<spring:url var="datatablesI18NUrl" value="/javaScript/dataTables/media/i18n/${portal.locale.language}.json" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CSS/dataTables/dataTables.bootstrap.min.css" />

<!-- Choose ONLY ONE:  bennuToolkit OR bennuAngularToolkit -->
<%-- ${portal.angularToolkit()} --%>
${portal.toolkit()}

<link href="${pageContext.request.contextPath}/static/fenixedu-ulisboa-specifications/css/dataTables.responsive.css" rel="stylesheet" />
<script src="${pageContext.request.contextPath}/static/fenixedu-ulisboa-specifications/js/dataTables.responsive.js"></script>
<link href="${pageContext.request.contextPath}/webjars/datatables-tools/2.2.4/css/dataTables.tableTools.css" rel="stylesheet" />
<script src="${pageContext.request.contextPath}/webjars/datatables-tools/2.2.4/js/dataTables.tableTools.js"></script>
<link href="${pageContext.request.contextPath}/webjars/select2/4.0.0-rc.2/dist/css/select2.min.css" rel="stylesheet" />
<script src="${pageContext.request.contextPath}/webjars/select2/4.0.0-rc.2/dist/js/select2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/webjars/bootbox/4.4.0/bootbox.js"></script>
<script src="${pageContext.request.contextPath}/static/fenixedu-ulisboa-specifications/js/omnis.js"></script>

<script src="${pageContext.request.contextPath}/webjars/angular-sanitize/1.3.11/angular-sanitize.js"></script>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/webjars/angular-ui-select/0.11.2/select.min.css" />
<script src="${pageContext.request.contextPath}/webjars/angular-ui-select/0.11.2/select.min.js"></script>


<%-- TITLE --%>
<div class="page-header">
    <h3>${finantialEntity.name.content}</h3>
    <h1>
        <spring:message
            code="label.manageEmoluments.viewEmolumentTariffs" />
        <small></small>
    </h1>
    <p>
        <strong>${product.name.content}</strong>
    </p>
</div>
<%-- NAVIGATION --%>
<div class="well well-sm" style="display: inline-block">
<span class="glyphicon glyphicon-arrow-left" aria-hidden="true"></span>&nbsp;
    <a class="" href="${pageContext.request.contextPath}/academictreasury/manageemoluments/product/searchemoluments/${finantialEntity.externalId}">
        <spring:message code="label.event.back"/>
    </a> |&nbsp;&nbsp;
    <span class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span>&nbsp;
    <a class=""
        href="${pageContext.request.contextPath}/academictreasury/manageemoluments/academictariff/createemolumenttariff/${finantialEntity.externalId}/${product.externalId}">
        <spring:message code="label.event.create" />
    </a> &nbsp;
</div>
<c:if test="${not empty infoMessages}">
    <div class="alert alert-info" role="alert">

        <c:forEach items="${infoMessages}" var="message">
            <p>${message}</p>
        </c:forEach>

    </div>
</c:if>
<c:if test="${not empty warningMessages}">
    <div class="alert alert-warning" role="alert">

        <c:forEach items="${warningMessages}" var="message">
            <p>${message}</p>
        </c:forEach>

    </div>
</c:if>
<c:if test="${not empty errorMessages}">
    <div class="alert alert-danger" role="alert">

        <c:forEach items="${errorMessages}" var="message">
            <p>${message}</p>
        </c:forEach>

    </div>
</c:if>


<script type="text/javascript">
	  function processDelete(externalId) {
	    url = "${pageContext.request.contextPath}/academictreasury/manageemoluments/academictariff/viewEmolumentTariffs/delete/${finantialEntity.externalId}/${product.externalId}/" + externalId;
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
                            code="label.manageEmoluments.viewEmolumentTariffs.confirmDelete" />
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
    <c:when test="${not empty viewemolumenttariffsResultsDataSet}">
        <table id="viewemolumenttariffsTable"
            class="table responsive table-bordered table-hover">
            <thead>
                <tr>
                    <th><spring:message
                            code="label.AcademicTariff.period" /></th>
                    <th><spring:message
                            code="label.AcademicTariff.amount" /></th>
                    <th><spring:message
                            code="label.AcademicTariff.interests" /></th>
                    <th></th>
                </tr>
            </thead>
            <tbody>
                <c:forEach items="${viewemolumenttariffsResultsDataSet}"
                    var="tariff">
                    <tr>
                        <td>
                            <p>
                                <c:if test="${tariff.endDateDefined}">
                                    <span>[${tariff.beginDate.toLocalDate()}
                                        -
                                        ${tariff.endDate.toLocalDate()}]</span>
                                </c:if>
                                <c:if test="${!tariff.endDateDefined}">
										${tariff.beginDate.toLocalDate()}
									</c:if>
                            </p>

                            <p>&nbsp;</p> <c:if
                                test="${not empty tariff.administrativeOffice}">
                                <p>
                                    <strong> <c:out
                                            value="${tariff.administrativeOffice.name.content}" />
                                    </strong>
                                </p>
                            </c:if> <c:if test="${not empty tariff.degreeType}">
                                <p>
                                    <strong> <c:out
                                            value="${tariff.degreeType.name.content}" />
                                    </strong>
                                </p>
                            </c:if> <c:if test="${not empty tariff.degree}">
                                <p>
                                    <strong> <c:out
                                            value="${tariff.degree.nameI18N.content}" />
                                    </strong>
                                </p>
                            </c:if> <c:if
                                test="${(not empty tariff.degree) && (not empty tariff.cycleType)}">
                                <p>
                                    <strong> <c:out
                                            value="${tariff.cycleType.descriptionI18N.content}" />
                                    </strong>
                                </p>
                            </c:if>
                        </td>
                        <td><c:if test="${tariff.applyBaseAmount}">
                                <p>
                                    <strong><spring:message
                                            code="label.AcademicTariff.baseAmount" />:</strong>
                                    <span>${finantialEntity.finantialInstitution.currency.getValueFor(tariff.baseAmount)}</span> 
                                    <span><em> 
                                    <c:if
                                                test="${tariff.unitsForBase == 1}">
											(<spring:message
                                                    code="label.AcademicTariff.unitsForBase.view.singular"
                                                    arguments="${tariff.unitsForBase}" />)
										</c:if> <c:if test="${tariff.unitsForBase} > 1">
											(<spring:message
                                                    code="label.AcademicTariff.unitsForBase.view"
                                                    arguments="${tariff.unitsForBase}" />)
										</c:if>
                                    </em></span>
                                </p>
                            </c:if> <c:if test="${tariff.applyUnitsAmount}">
                                <p>
                                    <strong><spring:message
                                            code="label.AcademicTariff.unitAmount" />:</strong>
                                    <span>${finantialEntity.finantialInstitution.currency.getValueFor(tariff.unitAmount)}</span>
                                </p>
                            </c:if> <c:if test="${tariff.applyPagesAmount}">
                                <p>
                                    <strong><spring:message
                                            code="label.AcademicTariff.pageAmount" />:</strong>
                                    <span>${finantialEntity.finantialInstitution.currency.getValueFor(tariff.pageAmount)}</span>
                                </p>
                            </c:if> <c:if test="${tariff.applyMaximumAmount}">
                                <p>
                                    <strong><spring:message
                                            code="label.AcademicTariff.maximumAmount" />:</strong>
                                    <span>${finantialEntity.finantialInstitution.currency.getValueFor(tariff.maximumAmount)}</span>
                                </p>
                            </c:if> <c:if test="${tariff.applyUrgencyRate}">
                                <p>
                                    <strong><spring:message
                                            code="label.AcademicTariff.urgencyRate" />:</strong>
                                    <span>${tariff.urgencyRate} %</span>
                                </p>
                            </c:if> <c:if
                                test="${tariff.applyLanguageTranslationRate}">
                                <p>
                                    <strong><spring:message
                                            code="label.AcademicTariff.languageTranslationRate" /></strong>
                                    <span>${tariff.languageTranslationRate}%</span>
                                </p>
                            </c:if>

                            <p>
                            </p>
                        
	                        <c:choose>
	                            <c:when test="${tariff.dueDateCalculationType.noDueDate}">
	                                <p>
		                                <strong><spring:message code="label.AcademicTariff.dueDateCalculationType" />:</strong>
	                                    <spring:message code="label.TuitionInstallmentTariff.noDueDate" />
	                                </p>
	                            </c:when>
	                            <c:when test="${tariff.dueDateCalculationType.fixedDate}">
	                                <p>
		                                <strong><spring:message code="label.AcademicTariff.dueDateCalculationType" />:</strong>
	                                    <joda:format value="${tariff.fixedDueDate}" style="S-" />
	                                </p>
	                            </c:when>
	                            <c:when test="${tariff.dueDateCalculationType.daysAfterCreation}">
	                                <p>
		                                <strong><spring:message code="label.AcademicTariff.dueDateCalculationType" />:</strong>
	                                    <spring:message code="label.TuitionInstallmentTariff.daysAfterCreation" arguments="${tariff.numberOfDaysAfterCreationForDueDate}" />
	                                </p>
	                            </c:when>
	                            <c:when test="${tariff.dueDateCalculationType.bestOfFixedDateAndDaysAfterCreation}">
	                                <p>
		                                <strong><spring:message code="label.AcademicTariff.dueDateCalculationType" />:</strong>
	                                    <joda:format var="fixedDueDate" value="${tariff.fixedDueDate}" style="S-" />
	                                    <spring:message code="label.TuitionInstallmentTariff.bestOfFixedDateAndDaysAfterCreation"
	                                        arguments="${fixedDueDate},${tariff.numberOfDaysAfterCreationForDueDate}" />
	                                </p>
	                            </c:when>
	                        </c:choose>
                        
                            
                        </td>
                        <td><c:if test="${!tariff.applyInterests}">
                                <p>
                                    <strong><spring:message
                                            code="label.AcademicTariff.interests.not.applied.message" /></strong>
                                </p>
                            </c:if> <c:if test="${tariff.applyInterests}">
                                <p align="center">
                                    <strong>[${tariff.interestRate.interestType.descriptionI18N.content}]</strong>

                                    <c:if
                                        test="${tariff.interestRate.interestType.fixedAmount}">
                                        <p>
                                            <strong><spring:message
                                                    code="label.AcademicTariff.interestFixedAmount" />:</strong>
                                            <span>${finantialEntity.finantialInstitution.currency.getValueFor(tariff.interestRate.interestFixedAmount)}</span>
                                        </p>
                                    </c:if>
                            </c:if></td>
                        <td><a class="btn btn-xs btn-default"
                            href="${pageContext.request.contextPath}/academictreasury/manageemoluments/academictariff/updateemolumenttariff/${finantialEntity.externalId}/${product.externalId}/${tariff.externalId}">
                                <span class="glyphicon"
                                aria-hidden="true"></span> &nbsp; <spring:message
                                    code='label.edit' />
                        </a> &nbsp; <a class="btn btn-xs btn-warning"
                            href="#"
                            onClick="javascript:processDelete('${tariff.externalId}')">
                                <span class="glyphicon glyphicon-trash"
                                aria-hidden="true"></span> &nbsp; <spring:message
                                    code='label.delete' />
                        </a></td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </c:when>
    <c:otherwise>
        <div class="alert alert-warning" role="alert">
            <spring:message code="label.noResultsFound" />
        </div>
    </c:otherwise>
</c:choose>

<script>
	var viewemolumenttariffsDataSet = [
			<c:forEach items="${viewemolumenttariffsResultsDataSet}" var="searchResult">
				<%-- Field access / formatting  here CHANGE_ME --%>
				{
				"DT_RowId" : '<c:out value='${searchResult.externalId}'/>',
"begindate" : "<c:out value='${searchResult.beginDate}'/>",
"enddate" : "<c:out value='${searchResult.endDate}'/>",
"baseamount" : "<c:out value='${searchResult.baseAmount}'/>",
"administrativeoffice" : "<c:out value='${searchResult.administrativeOffice}'/>",
"actions" :
" <a  class=\"btn btn-default btn-xs\" href=\"${pageContext.request.contextPath}/academictreasury/manageemoluments/academictariff/viewemolumenttariffsviewEmolumentTariffs/view/${searchResult.externalId}\"><spring:message code='label.view'/></a>" +
" <a  class=\"btn btn-xs btn-danger\" href=\"#\" onClick=\"javascript:processDelete('${searchResult.externalId}')\"><span class=\"glyphicon glyphicon-trash\" aria-hidden=\"true\"></span>&nbsp;<spring:message code='label.delete'/></a>" +
                "" },
            </c:forEach>
    ];
	
	$(document).ready(function() {

		var table = $('#viewemolumenttariffsTable').DataTable({language : {
			url : "${datatablesI18NUrl}",			
		},
 		"columns": [
			{ data: 'begindate' },
			{ data: 'enddate' },
			{ data: 'baseamount' },
			/*{ data: 'administrativeoffice' },*/
			{ data: 'actions',className:"all" }
			
		],
 		//CHANGE_ME adjust the actions column width if needed
		"columnDefs": [
		//54
		//128
		               { "width": "135px", "targets": 3 } 
		             ],
		//"data" : viewemolumenttariffsDataSet,
		//Documentation: https://datatables.net/reference/option/dom
        //"dom": 'T<"clear">lrtip', //FilterBox = NO && ExportOptions = YES
        //"dom": '<"col-sm-6"l><"col-sm-6"f>rtip', //FilterBox = YES && ExportOptions = NO
        //"dom": '<"col-sm-6"l>rtip', // FilterBox = NO && ExportOptions = NO
        dom: '<"col-sm-5"l><"col-sm-3"f><"col-sm-3"B>rtip', //FilterBox = YES && ExportOptions = YES
        buttons: [
            'copyHtml5',
            'excelHtml5',
            'csvHtml5',
            'pdfHtml5'
        ],
        "tableTools": {
            "sSwfPath": "//cdn.datatables.net/tabletools/2.2.3/swf/copy_csv_xls_pdf.swf"
        }
		});
		table.columns.adjust().draw();
		
		  $('#viewemolumenttariffsTable tbody').on( 'click', 'tr', function () {
		        $(this).toggleClass('selected');
		    } );
		  
	}); 
</script>

