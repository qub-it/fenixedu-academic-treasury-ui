<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<spring:url var="datatablesUrl" value="/javaScript/dataTables/media/js/jquery.dataTables.latest.min.js"/>
<spring:url var="datatablesBootstrapJsUrl" value="/javaScript/dataTables/media/js/jquery.dataTables.bootstrap.min.js"></spring:url>
<script type="text/javascript" src="${datatablesUrl}"></script>
<script type="text/javascript" src="${datatablesBootstrapJsUrl}"></script>
<spring:url var="datatablesCssUrl" value="/CSS/dataTables/dataTables.bootstrap.min.css"/>

<link rel="stylesheet" href="${datatablesCssUrl}"/>
<spring:url var="datatablesI18NUrl" value="/javaScript/dataTables/media/i18n/${portal.locale.language}.json"/>

<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CSS/dataTables/dataTables.bootstrap.min.css"/>

<link href="${pageContext.request.contextPath}/static/academicTreasury/css/dataTables.responsive.css" rel="stylesheet"/>
<script src="${pageContext.request.contextPath}/static/academicTreasury/js/dataTables.responsive.js"></script>

<link href="${pageContext.request.contextPath}/webjars/datatables-tools/2.2.4/css/dataTables.tableTools.css" rel="stylesheet"/>
<script src="${pageContext.request.contextPath}/webjars/datatables-tools/2.2.4/js/dataTables.tableTools.js"></script>

<link href="${pageContext.request.contextPath}/webjars/select2/4.0.0-rc.2/dist/css/select2.min.css" rel="stylesheet" />
<script src="${pageContext.request.contextPath}/webjars/select2/4.0.0-rc.2/dist/js/select2.min.js"></script>						

<script type="text/javascript" src="${pageContext.request.contextPath}/webjars/bootbox/4.4.0/bootbox.js" ></script>
<script src="${pageContext.request.contextPath}/static/treasury/js/omnis.js"></script>

<script src="${pageContext.request.contextPath}/webjars/angular-sanitize/1.3.11/angular-sanitize.js"></script>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/webjars/angular-ui-select/0.11.2/select.min.css" />
<script src="${pageContext.request.contextPath}/webjars/angular-ui-select/0.11.2/select.min.js"></script>

<!-- Choose ONLY ONE:  bennuToolkit OR bennuAngularToolkit -->
<%--${portal.angularToolkit()} --%>
${portal.toolkit()}

<%-- TITLE --%>
<div class="page-header">
	<h1><spring:message code="label.manageServiceRequestMapEntry.searchServiceRequestMapEntry" />
		<small></small>
	</h1>
</div>
<%-- NAVIGATION --%>
<div class="well well-sm" style="display:inline-block">
	<span class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span>&nbsp;<a class="" href="${pageContext.request.contextPath}/academictreasury/manageservicerequestmapentry/servicerequestmapentry/create"   ><spring:message code="label.event.create" /></a>
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


<script type="text/javascript">
	  function processDelete(externalId) {
	    url = "${pageContext.request.contextPath}/academictreasury/manageservicerequestmapentry/servicerequestmapentry/search/delete/" + externalId;
	    $("#deleteForm").attr("action", url);
	    $('#deleteModal').modal('toggle')
	  }
</script>


<div class="modal fade" id="deleteModal">
  <div class="modal-dialog">
    <div class="modal-content">
    <form id ="deleteForm" action="#" method="POST">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <h4 class="modal-title"><spring:message code="label.confirmation"/></h4>
      </div>
      <div class="modal-body">
        <p><spring:message code = "label.manageServiceRequestMapEntry.searchServiceRequestMapEntry.confirmDelete"/></p>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal"><spring:message code = "label.close"/></button>
        <button id="deleteButton" class ="btn btn-danger" type="submit"> <spring:message code = "label.delete"/></button>
      </div>
      </form>
    </div><!-- /.modal-content -->
  </div><!-- /.modal-dialog -->
</div><!-- /.modal -->



<c:choose>
	<c:when test="${not empty searchservicerequestmapentryResultsDataSet}">
		<table id="searchservicerequestmapentryTable" class="table responsive table-bordered table-hover">
			<thead>
				<tr>
					<%--!!!  Field names here --%>
					<th><spring:message code="label.ServiceRequestMapEntry.serviceRequestType"/></th>
					<th><spring:message code="label.ServiceRequestMapEntry.serviceRequestTypeOptions"/></th>
					<th><spring:message code="label.ServiceRequestMapEntry.product"/></th>
					<th><spring:message code="label.ServiceRequestMapEntry.createEventOnSituation"/></th>
					<%-- Operations Column --%>
					<th></th>
				</tr>
			</thead>
			<tbody>
			<c:forEach items="${searchservicerequestmapentryResultsDataSet}" var="searchResult">
				<tr>
					<td>${searchResult.serviceRequestType.name.content}</td>
					<td>
						<c:forEach items="${searchResult.serviceRequestTypeOptionBooleanValues}" var="option">
							<p>${option.serviceRequestTypeOption.name.content}</p>
						</c:forEach>
					</td>
					<td>${searchResult.product.name.content}</td>
					<td>${searchResult.createEventOnSituation.localizedName}</td>
					<td>
						<a  class="btn btn-xs btn-danger" href="#" onClick="javascript:processDelete('${searchResult.externalId}')">
							<span class="glyphicon glyphicon-trash" aria-hidden="true"></span>&nbsp;<spring:message code='label.delete'/>
						</a>
					</td>
				</tr>
			</c:forEach>				
			</tbody>
		</table>
	</c:when>
	<c:otherwise>
		<div class="alert alert-warning" role="alert">
			<p> <span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true">&nbsp;</span>	<spring:message code="label.noResultsFound" /></p>
		</div>	
	</c:otherwise>
</c:choose>

<script>
	$(document).ready(function() {

		var table = $('#searchservicerequestmapentryTable').DataTable({language : {
			url : "${datatablesI18NUrl}",			
		},
		"columns": [
			{ data: 'servicerequesttype' },
			{ data: 'servicerequesttypeoptionbooleanvalues' },
			{ data: 'product' },
			{ data: 'actions' }
		],
		//CHANGE_ME adjust the actions column width if needed
		"columnDefs": [
		//74
		               { "width": "74px", "targets": 3 } 
		             ],
		"data" : searchservicerequestmapentryDataSet,
		//Documentation: https://datatables.net/reference/option/dom
"dom": '<"col-sm-6"l><"col-sm-3"f><"col-sm-3"T>rtip', //FilterBox = YES && ExportOptions = YES
//"dom": 'T<"clear">lrtip', //FilterBox = NO && ExportOptions = YES
//"dom": '<"col-sm-6"l><"col-sm-6"f>rtip', //FilterBox = YES && ExportOptions = NO
//"dom": '<"col-sm-6"l>rtip', // FilterBox = NO && ExportOptions = NO
        "tableTools": {
            "sSwfPath": "${pageContext.request.contextPath}/static/academictreasury/swf/copy_csv_xls_pdf.swf"
        }
		});
		table.columns.adjust().draw();
		
		  $('#searchservicerequestmapentryTable tbody').on( 'click', 'tr', function () {
		        $(this).toggleClass('selected');
		    } );
		  
	}); 
</script>

