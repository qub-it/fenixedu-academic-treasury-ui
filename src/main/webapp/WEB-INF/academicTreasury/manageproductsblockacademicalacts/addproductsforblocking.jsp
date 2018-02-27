<%@page import="org.fenixedu.academictreasury.ui.manageproductsblockacademicalacts.ManageProductsForBlockAcademicalActsController"%>
<%@page import="org.fenixedu.academictreasury.ui.manageacademictreasurysettings.AcademicTreasurySettingsController"%>
<%@page import="org.fenixedu.academictreasury.domain.settings.AcademicTreasurySettings"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>
<%@ taglib prefix="datatables" uri="http://github.com/dandelion/datatables"%>

<jsp:include page="../commons/angularInclude.jsp" />

<%-- TITLE --%>
<div class="page-header">
    <h1>
        <spring:message code="label.title.academictreasury.ManageProductsForBlockAcademicalActs.addProductsToBlock" />
        <small></small>
    </h1>
</div>

<%-- NAVIGATION --%>
<div class="well well-sm" style="display: inline-block">
	<span class="glyphicon glyphicon-arrow-left" aria-hidden="true"></span>
	&nbsp;
	<a class="" href="${pageContext.request.contextPath}<%= ManageProductsForBlockAcademicalActsController.SEARCH_URL %>">
		<spring:message code="label.event.back" />
	</a>
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
		var array = window.datatable.column(0).checkboxes.selected();
		
		$("#" + formID).empty();
		
		var numberOfProducts = array.length;
		if (numberOfProducts > 0) {
			
			var messageToShow = numberOfProducts > 1 ? '<spring:message code="label.ManageProductsForBlockAcademicalActs.addProducts.confirm.message" />' :
				'<spring:message code="label.ManageProductsForBlockAcademicalActs.addProducts.confirm.message.singular" />'
			
			bootbox.dialog({
				title : '<spring:message code="label.ManageProductsForBlockAcademicalActs.addProducts.confirm.title" />',
				message: messageToShow.replace('{numberOfProducts}', numberOfProducts),
				buttons: {
					cancel: {
						label: '<spring:message code="label.close" />',
						callback: function() {
							bootbox.hideAll();
						}
					},
					success: {
						label: '<spring:message code="label.true" />',
						callback: function() {
							$.each(array, function(index, value) {
								$("#" + formID).append("<input type='hidden' name='" + attributeName + "' value='" + value + "'/>");
							});
							
							$("#" + formID).submit();
						}
					}
				}
			});
			
			
		} else {
			messageAlert('<spring:message code = "label.warning"/>','<spring:message code = "label.select.mustselect"/>', 0);
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
	            action="${pageContext.request.contextPath}<%= ManageProductsForBlockAcademicalActsController.ADD_PRODUCTS_BLOCKING_URL %>"
	            style="display: none;" method="post">
	        </form>
	
			<button id="addEntryButton" type="button" onclick="javascript:submitOptions('nonBlockingProductsTable', 'addproducts', 'products')">
				<span class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span>&nbsp;
				    <spring:message code='label.event.add' />
			</button>
			
			</form>
		</c:otherwise>
	</c:choose>
</div>

