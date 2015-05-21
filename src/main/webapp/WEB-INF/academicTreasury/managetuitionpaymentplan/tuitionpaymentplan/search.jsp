<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="datatables" uri="http://github.com/dandelion/datatables"%>
<%@ taglib prefix="cur" uri="http://example.com/currency"%>

<spring:url var="datatablesUrl" value="/javaScript/dataTables/media/js/jquery.dataTables.latest.min.js"/>
<spring:url var="datatablesBootstrapJsUrl" value="/javaScript/dataTables/media/js/jquery.dataTables.bootstrap.min.js"></spring:url>
<script type="text/javascript" src="${datatablesUrl}"></script>
<script type="text/javascript" src="${datatablesBootstrapJsUrl}"></script>
<spring:url var="datatablesCssUrl" value="/CSS/dataTables/dataTables.bootstrap.min.css"/>

<link rel="stylesheet" href="${datatablesCssUrl}"/>
<spring:url var="datatablesI18NUrl" value="/javaScript/dataTables/media/i18n/${portal.locale.language}.json"/>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CSS/dataTables/dataTables.bootstrap.min.css"/>

<!-- Choose ONLY ONE:  bennuToolkit OR bennuAngularToolkit -->
<%--${portal.angularToolkit()} --%>
${portal.toolkit()}

<link href="${pageContext.request.contextPath}/static/academicTreasury/css/dataTables.responsive.css" rel="stylesheet"/>
<script src="${pageContext.request.contextPath}/static/academicTreasury/js/dataTables.responsive.js"></script>
<link href="${pageContext.request.contextPath}/webjars/datatables-tools/2.2.4/css/dataTables.tableTools.css" rel="stylesheet"/>
<script src="${pageContext.request.contextPath}/webjars/datatables-tools/2.2.4/js/dataTables.tableTools.js"></script>
<link href="${pageContext.request.contextPath}/webjars/select2/4.0.0-rc.2/dist/css/select2.min.css" rel="stylesheet" />
<script src="${pageContext.request.contextPath}/webjars/select2/4.0.0-rc.2/dist/js/select2.min.js"></script>						
<script type="text/javascript" src="${pageContext.request.contextPath}/webjars/bootbox/4.4.0/bootbox.js" ></script>
<script src="${pageContext.request.contextPath}/static/academicTreasury/js/omnis.js"></script>


<div class="page-header">
	<h1><spring:message code="label.manageTuitionPaymentPlan.searchTuitionPaymentPlan" />
		<small></small>
	</h1>
</div>

<div class="well well-sm" style="display:inline-block">
	<span class="glyphicon glyphicon-arrow-left" aria-hidden="true"></span>&nbsp;<a class="" href="${pageContext.request.contextPath}/academictreasury/managetuitionpaymentplan/degreecurricularplan/choosedegreecurricularplan"   ><spring:message code="label.event.back"/></a>
|&nbsp;&nbsp;	<span class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span>&nbsp;<a class="" href="${pageContext.request.contextPath}/academictreasury/managetuitionpaymentplan/tuitionpaymentplan/createchoosedegreecurricularplans"   ><spring:message code="label.event.create" /></a>
|&nbsp;&nbsp;</div>
	<c:if test="${not empty infoMessages}">
		<div class="alert alert-info" role="alert">
			
			<c:forEach items="${infoMessages}" var="message"> 
				<p> <span class="glyphicon glyphicon glyphicon-ok-sign" aria-hidden="true">&nbsp;</span>
							${message}
						</p>
			</c:forEach>
			
		</div>	
	</c:if>
	<c:if test="${not empty warningMessages}">
		<div class="alert alert-warning" role="alert">
			
			<c:forEach items="${warningMessages}" var="message"> 
				<p> <span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true">&nbsp;</span>
							${message}
						</p>
			</c:forEach>
			
		</div>	
	</c:if>
	<c:if test="${not empty errorMessages}">
		<div class="alert alert-danger" role="alert">
			
			<c:forEach items="${errorMessages}" var="message"> 
				<p> <span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true">&nbsp;</span>
							${message}
						</p>
			</c:forEach>
			
		</div>	
	</c:if>


<c:forEach items="${searchtuitionpaymentplanResultsDataSet}" var="paymentPlan"  varStatus="loopStatus">
	<p><c:out value="${paymentPlan.name}" /></p>	

	<datatables:table id="paymentPlans-${loopStatus.index}" row="installment" data="${paymentPlan.tuitionInstallmentTariffs}" 
		cssClass="table responsive table-bordered table-hover" cdn="false" cellspacing="2">
		
		<datatables:column cssStyle="width:10%">
			<datatables:columnHead ><spring:message code="label.TuitionInstallmentTariff.order" /></datatables:columnHead>
			<c:out value="${installment.installmentOrder}" />
		</datatables:column>
		
		<datatables:column cssStyle="width:20%">
			<datatables:columnHead ><spring:message code="label.TuitionInstallmentTariff.amount" /></datatables:columnHead>
	
			<c:choose>
				<c:when test="${installment.tuitionCalculationType.fixedAmount}" >
					<c:out value="${installment.finantialEntity.finantialInstitution.getValue(installment.fixedAmount)}" />
				</c:when>
				<c:when test="${installment.tuitionCalculationType.ects}" >
					<p>
						<strong>
							<c:out value="${installment.tuitionCalculationType.descriptionI18N}" />
							[<c:out value="${installment.ectsCalculationType.descriptionI18N}" />]
						</strong>
					</p>

					<c:if test="${installment.ectsCalculationType.fixedAmount}">
						<p>&nbsp;</p>
						<p><spring:message code="label.TuitionInstallmentTariff.amountPerEcts" 
							arguments="${installment.finantialEntity.finantialInstitution.getValue(installment.amountPerEcts)}" /></p>
					</c:if>
					<c:if test="${installment.ectsCalculationType.defaultPaymentPlanIndexed}">
						<p><em><spring:message code="label.TuitionInstallmentTariff.defaultPaymentPlanIndexed.ectsParameters"
							arguments="${installment.factor},${installment.totalUnits}" /></em></p>
						<p>&nbsp;</p>

						<p><spring:message code="label.TuitionInstallmentTariff.amountByEcts" 
							arguments="${installment.finantialEntity.finantialInstitution.getValue(installment.amountPerEcts)}" /></p>
						<p><spring:message code="label.</p>
					</c:if>
				</c:when>
			</c:choose>
		</datatables:column>
		
		
	</datatables:table>
		
</c:forEach>

<c:choose>
	<c:when test="${not empty searchtuitionpaymentplanResultsDataSet}">
		<table id="searchtuitionpaymentplanTable" class="table responsive table-bordered table-hover">
			<thead>
				<tr>
					<th>
						<spring:message code="label.TuitionPaymentPlan.order"/>
					</th>
					<th><spring:message code="label.TuitionPaymentPlan.fixedAmount"/></th>
					<th><spring:message code="label.TuitionPaymentPlan.beginDate"/></th>
					<th><spring:message code="label.TuitionPaymentPlan.dueDate"/></th>
					<th><spring:message code="label.TuitionPaymentPlan.interests"/></th>
				</tr>
			</thead>
			<tbody>
				
			</tbody>
		</table>
	</c:when>
	<c:otherwise>
		<div class="alert alert-warning" role="alert">
			<p> <span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true">&nbsp;</span>			<spring:message code="label.noResultsFound" /></p>
		</div>	
	</c:otherwise>
</c:choose>

<script>
	var searchtuitionpaymentplanDataSet = [
			<c:forEach items="${searchtuitionpaymentplanResultsDataSet}" var="searchResult">
				<%-- Field access / formatting  here CHANGE_ME --%>
				{
				"DT_RowId" : '<c:out value='${searchResult.externalId}'/>',
"order" : "<c:out value='${searchResult.order}'/>",
"fixedamount" : "<c:out value='${searchResult.fixedAmount}'/>",
"begindate" : "<c:out value='${searchResult.beginDate}'/>",
"duedate" : "<c:out value='${searchResult.dueDate}'/>",
"interests" : "<c:out value='${searchResult.interests}'/>",
			},
            </c:forEach>
    ];
	
	$(document).ready(function() {

	


		var table = $('#searchtuitionpaymentplanTable').DataTable({language : {
			url : "${datatablesI18NUrl}",			
		},
		"columns": [
			{ data: 'order' },
			{ data: 'fixedamount' },
			{ data: 'begindate' },
			{ data: 'duedate' },
			{ data: 'interests' },
			
		],
		"data" : searchtuitionpaymentplanDataSet,
		//Documentation: https://datatables.net/reference/option/dom
"dom": '<"col-sm-6"l><"col-sm-3"f><"col-sm-3"T>rtip', //FilterBox = YES && ExportOptions = YES
//"dom": 'T<"clear">lrtip', //FilterBox = NO && ExportOptions = YES
//"dom": '<"col-sm-6"l><"col-sm-6"f>rtip', //FilterBox = YES && ExportOptions = NO
//"dom": '<"col-sm-6"l>rtip', // FilterBox = NO && ExportOptions = NO
        "tableTools": {
            "sSwfPath": "${pageContext.request.contextPath}/webjars/datatables-tools/2.2.4/swf/copy_csv_xls_pdf.swf"        	
        }
		});
		table.columns.adjust().draw();
		
		  $('#searchtuitionpaymentplanTable tbody').on( 'click', 'tr', function () {
		        $(this).toggleClass('selected');
		    } );
		  
	}); 
</script>

