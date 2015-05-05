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

<link href="${pageContext.request.contextPath}/static/academicTreasury/css/dataTables.tableTools.css" rel="stylesheet"/>
<script src="${pageContext.request.contextPath}/static/academicTreasury/js/dataTables.tableTools.min.js"></script>

<link href="${pageContext.request.contextPath}/static/academicTreasury/css/select2.min.css" rel="stylesheet" />
<script src="${pageContext.request.contextPath}/static/academicTreasury/js/select2.min.js"></script>

<script src="${pageContext.request.contextPath}/static/academicTreasury/js/bootbox.min.js"></script>
<script src="${pageContext.request.contextPath}/static/academicTreasury/js/omnis.js"></script>

<!-- Choose ONLY ONE:  bennuToolkit OR bennuAngularToolkit -->
<%--${portal.angularToolkit()} --%>
${portal.toolkit()}

<%-- TITLE --%>
<div class="page-header">
	<h1><spring:message code="label.manageServiceRequestMapEntry.createServiceRequestMapEntry" />
		<small></small>
	</h1>
</div>

<%-- NAVIGATION --%>
<div class="well well-sm" style="display:inline-block">
	<span class="glyphicon glyphicon-arrow-left" aria-hidden="true"></span>&nbsp;<a class="" href="${pageContext.request.contextPath}/academictreasury/manageservicerequestmapentry/servicerequestmapentry/"  ><spring:message code="label.event.back" /></a>
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

<form method="post" class="form-horizontal">
	<div class="panel panel-default">
		<div class="panel-body">
			<div class="form-group row">
				<div class="col-sm-2 control-label"><spring:message code="label.ServiceRequestMapEntry.serviceRequestType"/></div> 
				
				<div class="col-sm-4">
					<%-- Relation to side 1 drop down rendered in input --%>
					<select id="serviceRequestMapEntry_serviceRequestType" class="js-example-basic-single" name="servicerequesttype">
						<option value=""></option> <%-- empty option remove it if you don't want to have it or give it a label CHANGE_ME --%> 
					</select>
				</div>
			</div>		
			<div class="form-group row">
				<div class="col-sm-2 control-label"><spring:message code="label.ServiceRequestMapEntry.product"/></div> 
				
				<div class="col-sm-4">
					<%-- Relation to side 1 drop down rendered in input --%>
					<select id="serviceRequestMapEntry_product" class="js-example-basic-single" name="product">
						<option value=""></option> <%-- empty option remove it if you don't want to have it or give it a label CHANGE_ME --%> 
					</select>
				</div>
			</div>		
			<div class="form-group row">
				<div class="col-sm-2 control-label"><spring:message code="label.ServiceRequestMapEntry.serviceRequestTypeOptions"/></div> 
				
				<div class="col-sm-4">
					<select id="ServiceRequestMapEntry_serviceRequestTypeOption_options" class="js-example-basic-single form-control" name="servicerequesttypeoptions" multiple="multiple">
						<option value=""></option>
					</select>
				</div>
			</div>		
			<div class="form-group row">
				<div class="col-sm-2 control-label"><spring:message code="label.ServiceRequestMapEntry.createEventOnSituation"/></div> 
				
				<div class="col-sm-4">
					<%-- Relation to side 1 drop down rendered in input --%>
					<select id="serviceRequestMapEntry_createEventOnSituation" class="js-example-basic-single" name="createEventOnSituation">
						<option value=""></option>
					</select>
				</div>
			</div>		
		</div>
		<div class="panel-footer">
		<input type="submit" class="btn btn-default" role="button" value="<spring:message code="label.submit" />"/>
		</div>
	</div>
</form>

<script>
$(document).ready(function() {

		<%-- Block for providing serviceRequestType options --%>
		<%-- CHANGE_ME --%> <%-- INSERT YOUR FORMAT FOR element --%>
		serviceRequestType_options = [
			<c:forEach items="${ServiceRequestMapEntry_serviceRequestType_options}" var="element"> 
				{
					text : "<c:out value='${element.name.content}'/>",  
					id : "<c:out value='${element.externalId}'/>"
				},
			</c:forEach>
		];
		
		$("#serviceRequestMapEntry_serviceRequestType").select2(
			{
				data : serviceRequestType_options,
			}	  
	    );
	    
	    
	    
	    $("#serviceRequestMapEntry_serviceRequestType").select2().select2('val', '<c:out value='${param.servicerequesttype}'/>');
	
		<%-- End block for providing serviceRequestType options --%>
		<%-- Block for providing serviceRequestTypeOptionBooleanValues options --%>
		<%-- CHANGE_ME --%> <%-- INSERT YOUR FORMAT FOR element --%>
		serviceRequestTypeOptions_options = [
			<c:forEach items="${ServiceRequestMapEntry_serviceRequestTypeOption_options}" var="element"> 
				{
					text : "<c:out value='${element.name.content}'/>",  
					id : "<c:out value='${element.externalId}'/>"
				},
			</c:forEach>
		];
		
		$("#ServiceRequestMapEntry_serviceRequestTypeOption_options").select2(
			{
				data : serviceRequestTypeOptions_options,
			}	  
	    );
	    
	    <% java.util.List hackingListServiceRequestTypeOptionBooleanValues = request.getParameterValues("servicerequesttypeoptionbooleanvalues") != null ? 
	            java.util.Arrays.asList(request.getParameterValues("servicerequesttypeoptionbooleanvalues")) : new java.util.ArrayList();
	            
			request.setAttribute("hackingListServiceRequestTypeOptions",hackingListServiceRequestTypeOptionBooleanValues);
		%>
	    
	    <c:if test="${not empty hackingListServiceRequestTypeOptions}">
				$("#ServiceRequestMapEntry_serviceRequestTypeOption_options").select2().select2('val', [
                	<c:forEach items="${ServiceRequestMapEntry_serviceRequestTypeOption_options}" var="element" varStatus="status">
                    	"<c:out value='${element}'/>"
                        <c:if test="${not status.last}">
                        ,
                       </c:if>
                      </c:forEach>
                     ]);
            </c:if>
	
		<%-- End block for providing serviceRequestTypeOptionBooleanValues options --%>
		<%-- Block for providing product options --%>
		<%-- CHANGE_ME --%> <%-- INSERT YOUR FORMAT FOR element --%>
		product_options = [
			<c:forEach items="${ServiceRequestMapEntry_product_options}" var="element"> 
				{
					text : "<c:out value='${element.name.content}'/>",  
					id : "<c:out value='${element.externalId}'/>"
				},
			</c:forEach>
		];
		
		$("#serviceRequestMapEntry_product").select2(
			{
				data : product_options,
			}	  
	    );
	    
	    $("#serviceRequestMapEntry_createEventOnSituation").select2().select2('val', '<c:out value='${param.createEventOnSituationType}'/>');

	    
		product_options = [
			<c:forEach items="${ServiceRequestMapEntry_situationType_options}" var="element"> 
				{
					text : "${element.localizedName}",
					id : "${element}"
				},
			</c:forEach>
		];
		
		$("#serviceRequestMapEntry_createEventOnSituation").select2({ data : product_options });
	    
	    $("#serviceRequestMapEntry_createEventOnSituation").select2().select2('val', '<c:out value='${param.product}'/>');
	    
	    
		<%-- End block for providing product options --%>
	
	
	});
</script>
