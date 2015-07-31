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

<!-- Choose ONLY ONE:  bennuToolkit OR bennuAngularToolkit -->
<%--${portal.angularToolkit()} --%>
${portal.toolkit()}

<link href="${pageContext.request.contextPath}/static/academictreasury/css/dataTables.responsive.css" rel="stylesheet"/>
<script src="${pageContext.request.contextPath}/static/academictreasury/js/dataTables.responsive.js"></script>
<link href="${pageContext.request.contextPath}/webjars/datatables-tools/2.2.4/css/dataTables.tableTools.css" rel="stylesheet"/>
<script src="${pageContext.request.contextPath}/webjars/datatables-tools/2.2.4/js/dataTables.tableTools.js"></script>
<link href="${pageContext.request.contextPath}/webjars/select2/4.0.0-rc.2/dist/css/select2.min.css" rel="stylesheet" />
<script src="${pageContext.request.contextPath}/webjars/select2/4.0.0-rc.2/dist/js/select2.min.js"></script>						
<script type="text/javascript" src="${pageContext.request.contextPath}/webjars/bootbox/4.4.0/bootbox.js" ></script>
<script src="${pageContext.request.contextPath}/static/academictreasury/js/omnis.js"></script>



<%-- TITLE --%>
<div class="page-header">
	<h1><spring:message code="label.manageCourseFunctionCost.searchCourseFunctionCost" />
		<small></small>
	</h1>
</div>
<%-- NAVIGATION --%>
<div class="well well-sm" style="display:inline-block">
	<span class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span>&nbsp;<a class="" href="${pageContext.request.contextPath}/academictreasury/managecoursefunctioncost/coursefunctioncost/create"   ><spring:message code="label.event.create" /></a>
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
	    url = "${pageContext.request.contextPath}/academictreasury/managecoursefunctioncost/coursefunctioncost/search/delete/" + externalId;
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
        <p><spring:message code = "label.manageCourseFunctionCost.searchCourseFunctionCost.confirmDelete"/></p>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal"><spring:message code = "label.close"/></button>
        <button id="deleteButton" class ="btn btn-danger" type="submit"> <spring:message code = "label.delete"/></button>
      </div>
      </form>
    </div><!-- /.modal-content -->
  </div><!-- /.modal-dialog -->
</div><!-- /.modal -->

<div class="panel panel-default">
<form method="get" class="form-horizontal">
<div class="panel-body">
<div class="form-group row">
<div class="col-sm-2 control-label"><spring:message code="label.CourseFunctionCost.executionYear"/></div> 

<div class="col-sm-4">
	<%-- Relation to side 1 drop down rendered in input --%>
		 <select id="courseFunctionCost_executionYear" class="js-example-basic-single" name="executionyear">
		 <option value=""></option>
		 <c:forEach items="${CourseFunctionCost_executionYear_options}" var="ey">
			 <option value="ey.externalId"><c:out value="${ey.qualifiedName}" /></option>
		 </c:forEach>
		 
		</select>
				</div>
</div>		
<div class="form-group row">
<div class="col-sm-2 control-label"><spring:message code="label.CourseFunctionCost.degreeCurricularPlan"/></div> 

<div class="col-sm-4">
	<%-- Relation to side 1 drop down rendered in input --%>
		 <select id="courseFunctionCost_degreeCurricularPlan" class="js-example-basic-single" name="degreecurricularplan">
		 <option value=""></option> <%-- empty option remove it if you don't want to have it or give it a label CHANGE_ME --%> 
		</select>
				</div>
</div>		
<%-- 
<div class="form-group row">
<div class="col-sm-2 control-label"><spring:message code="label.CourseFunctionCost.competenceCourses"/></div> 

<div class="col-sm-4">
		 <select id="courseFunctionCost_competenceCourses" class="js-example-basic-single" name="competencecourses">
		 <option value=""></option> 
		</select>
				</div>
</div>		
--%>

</div>
<div class="panel-footer">
	<input type="submit" class="btn btn-default" role="button" value="<spring:message code="label.search" />"/>
</div>
</form>
</div>


<c:choose>
	<c:when test="${not empty searchcoursefunctioncostResultsDataSet}">
		<table id="searchcoursefunctioncostTable" class="table responsive table-bordered table-hover">
			<thead>
				<tr>
					<%--!!!  Field names here --%>
<th><spring:message code="label.CourseFunctionCost.degreeCurricularPlan"/></th>
<th><spring:message code="label.CourseFunctionCost.competenceCourses"/></th>
<th><spring:message code="label.CourseFunctionCost.executionYear"/></th>
<th><spring:message code="label.CourseFunctionCost.functionCost"/></th>
<%-- Operations Column --%>
					<th></th>
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
	var searchcoursefunctioncostDataSet = [
			<c:forEach items="${searchcoursefunctioncostResultsDataSet}" var="searchResult">
				<%-- Field access / formatting  here CHANGE_ME --%>
				{
				"DT_RowId" : '<c:out value='${searchResult.externalId}'/>',
"degreecurricularplan" : "<c:out value='${searchResult.degreeCurricularPlan.presentationName}'/>",
"competencecourses" : "<c:out value='${searchResult.competenceCourses.nameI18N.content}'/>",
"executionyear" : "<c:out value='${searchResult.executionYear.qualifiedName}'/>",
"functioncost" : "<c:out value='${searchResult.functionCost}'/>",
"actions" :
" <a  class=\"btn btn-xs btn-danger\" href=\"#\" onClick=\"javascript:processDelete('${searchResult.externalId}')\"><span class=\"glyphicon glyphicon-trash\" aria-hidden=\"true\"></span>&nbsp;<spring:message code='label.delete'/></a>" +
                "" 
			},
            </c:forEach>
    ];
	
	$(document).ready(function() {

	<%-- Block for providing executionYear options --%>
	<%-- CHANGE_ME --%> <%-- INSERT YOUR FORMAT FOR element --%>
	executionYear_options = [
		<c:forEach items="${CourseFunctionCost_executionYear_options}" var="element"> 
			{
				text :"<c:out value='${element.qualifiedName}'/>", 
				id : "<c:out value='${element.externalId}'/>"
			},
		</c:forEach>
	];
	
	$("#courseFunctionCost_executionYear").select2();
		    
		    <%-- If it's not from parameter change param.executionYear to whatever you need (it's the externalId already) --%>
		    $("#courseFunctionCost_executionYear").select2().select2('val', '<c:out value='${param.executionYear}'/>');
	<%-- End block for providing executionYear options --%>
	<%-- Block for providing degreeCurricularPlan options --%>
	<%-- CHANGE_ME --%> <%-- INSERT YOUR FORMAT FOR element --%>
	degreeCurricularPlan_options = [
		<c:forEach items="${CourseFunctionCost_degreeCurricularPlan_options}" var="element"> 
			{
				text :"<c:out value='${element.presentationName}'/>", 
				id : "<c:out value='${element.externalId}'/>"
			},
		</c:forEach>
	];
	
	$("#courseFunctionCost_degreeCurricularPlan").select2(
		{
			data : degreeCurricularPlan_options,
		}	  
		    );
		    
		    <%-- If it's not from parameter change param.degreeCurricularPlan to whatever you need (it's the externalId already) --%>
		    $("#courseFunctionCost_degreeCurricularPlan").select2().select2('val', '<c:out value='${param.degreeCurricularPlan}'/>');
	<%-- End block for providing degreeCurricularPlan options --%>
	<%-- Block for providing competenceCourses options --%>
	<%-- CHANGE_ME --%> <%-- INSERT YOUR FORMAT FOR element --%>
	competenceCourses_options = [
		<c:forEach items="${CourseFunctionCost_competenceCourses_options}" var="element"> 
			{
				text :"<c:out value='${element.nameI18N.content}'/>", 
				id : "<c:out value='${element.externalId}'/>"
			},
		</c:forEach>
	];
	
	<%--
	$("#courseFunctionCost_competenceCourses").select2(
		{
			data : competenceCourses_options,
		}	  
		    );
		    
		    $("#courseFunctionCost_competenceCourses").select2().select2('val', '<c:out value='${param.competenceCourses}'/>');
	--%>	


		var table = $('#searchcoursefunctioncostTable').DataTable({language : {
			url : "${datatablesI18NUrl}",			
		},
		"columns": [
			{ data: 'degreecurricularplan' },
			{ data: 'competencecourses' },
			{ data: 'executionyear' },
			{ data: 'functioncost' },
			{ data: 'actions' }
			
		],
		//CHANGE_ME adjust the actions column width if needed
		"columnDefs": [
		//74
		               { "width": "74px", "targets": 4 } 
		             ],
		"data" : searchcoursefunctioncostDataSet,
		//Documentation: https://datatables.net/reference/option/dom
//"dom": '<"col-sm-6"l><"col-sm-3"f><"col-sm-3"T>rtip', //FilterBox = YES && ExportOptions = YES
"dom": 'T<"clear">lrtip', //FilterBox = NO && ExportOptions = YES
//"dom": '<"col-sm-6"l><"col-sm-6"f>rtip', //FilterBox = YES && ExportOptions = NO
//"dom": '<"col-sm-6"l>rtip', // FilterBox = NO && ExportOptions = NO
        "tableTools": {
            "sSwfPath": "${pageContext.request.contextPath}/webjars/datatables-tools/2.2.4/swf/copy_csv_xls_pdf.swf"        	
        }
		});
		table.columns.adjust().draw();
		
		  $('#searchcoursefunctioncostTable tbody').on( 'click', 'tr', function () {
		        $(this).toggleClass('selected');
		    } );
		  
	}); 
</script>

