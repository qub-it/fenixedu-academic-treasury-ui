<%@page
    import="org.fenixedu.academictreasury.ui.manageacademictreasurysettings.AcademicTreasurySettingsController"%>
<%@page
    import="org.fenixedu.academictreasury.domain.settings.AcademicTreasurySettings"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>
<%@ taglib prefix="datatables"
    uri="http://github.com/dandelion/datatables"%>

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

<%-- jQuery DataTables Checkboxes --%>
<link type="text/css" href="//gyrocode.github.io/jquery-datatables-checkboxes/1.2.9/css/dataTables.checkboxes.css" rel="stylesheet" />
<script type="text/javascript" src="//gyrocode.github.io/jquery-datatables-checkboxes/1.2.9/js/dataTables.checkboxes.min.js"></script>

<%-- TITLE --%>
<div class="page-header">
    <h1>
        <spring:message code="label.title.academictreasury.ManageProductsForBlockAcademicalActs.addProductsToBlock" />
        <small></small>
    </h1>
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
	function submitOptions(tableID, formID, attributeName) {
		array = $("#" + tableID).DataTable().rows(".selected")[0];
		
		$("#" + formID).empty();
		if (array.length>0) {
			$.each(array,function(index, value) {
				externalId = $("#" + tableID).DataTable().row(value).data()["DT_RowId"];
				$("#" + formID).append("<input type='hidden' name='" + attributeName+ "' value='" + externalId + "'/>");
			});
			$("#" + formID).submit();
		} else {
			messageAlert('<spring:message code = "label.warning"/>','<spring:message code = "label.select.mustselect"/>');
		}
		
	}
</script>


<div class="col-sm-12">
	<c:choose>
		<c:when test="${empty nonblockingproducts}">
	        <div class="alert alert-warning" role="alert">
	            <p>
	                <span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true">&nbsp;</span>
	                <spring:message code="label.noResultsFound" />
	            </p>
	        </div>
		</c:when>
		
		<c:otherwise>
		
		    <datatables:table id="nonBlockingProductsTable" row="p" data="${nonblockingproducts}"
		        cssClass="table responsive table-bordered table-hover" cdn="false" cellspacing="2">
		
		        <datatables:column>
		            <c:out value='${p.externalId}' />
		        </datatables:column>

		        <datatables:column>
		            <datatables:columnHead>
		                <spring:message code="label.Product.code" />
		            </datatables:columnHead>
		
		            <c:out value='${p.code}' />
		        </datatables:column>
		
		        <datatables:column>
		            <datatables:columnHead>
		                <spring:message code="label.Product.name" />
		            </datatables:columnHead>
		            
					<c:out value='${p.name.content}' />
		        </datatables:column>

		        <datatables:column>
		            <datatables:columnHead>
		                <spring:message code="label.Product.active" />
		            </datatables:columnHead>
		            
		            <spring:message code="label.${p.active}" />
		        </datatables:column>
		        
		    </datatables:table>
		    <script>
		    	$(document).ready(function() {
					window.datatable = createDataTablesWithSelectionByCheckbox(
							'nonBlockingProductsTable', 
							true /*filterable*/, 
							false /*show tools*/, 
							true /*paging*/, 
							"${pageContext.request.contextPath}","${datatablesI18NUrl}");
		    	});

			</script>

	        <form id="addproducts"
	            action="${pageContext.request.contextPath}<%= AcademicTreasurySettingsController.ADDACADEMICALACTBLOCKINGPRODUCT_URL %>"
	            style="display: none;" method="post">
	        </form>
	
			<button id="addEntryButton" type="button" onclick="javascript:submitOptions('searchpendingentriesTable', 'addproducts', 'products')">
				<span class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span>&nbsp;
				    <spring:message code='label.event.add' />
			</button>
			
			</form>
		</c:otherwise>
	</c:choose>
</div>

