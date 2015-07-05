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

<link rel="stylesheet" type="text/css"
	href="${pageContext.request.contextPath}/CSS/dataTables/dataTables.bootstrap.min.css"/>

${portal.angularToolkit()}

<link href="//cdn.datatables.net/responsive/1.0.4/css/dataTables.responsive.css" rel="stylesheet"/>
<script src="//cdn.datatables.net/responsive/1.0.4/js/dataTables.responsive.js"></script>
<link href="${pageContext.request.contextPath}/webjars/datatables-tools/2.2.4/css/dataTables.tableTools.css" rel="stylesheet"/>
<script src="${pageContext.request.contextPath}/webjars/datatables-tools/2.2.4/js/dataTables.tableTools.js"></script>
<link href="${pageContext.request.contextPath}/webjars/select2/4.0.0-rc.2/dist/css/select2.min.css" rel="stylesheet" />
<script src="${pageContext.request.contextPath}/webjars/select2/4.0.0-rc.2/dist/js/select2.min.js"></script>						
<script type="text/javascript" src="${pageContext.request.contextPath}/webjars/bootbox/4.4.0/bootbox.js" ></script>
<script src="${pageContext.request.contextPath}/static/treasury/js/omnis.js"></script>


<script src="${pageContext.request.contextPath}/webjars/angular-sanitize/1.3.11/angular-sanitize.js"></script>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/webjars/angular-ui-select/0.11.2/select.min.css" />
<script src="${pageContext.request.contextPath}/webjars/angular-ui-select/0.11.2/select.min.js"></script>

<%-- TITLE --%>
<div class="page-header">
	<h4>
		<c:out value="${finantialEntity.name.content}" />
	</h4>
	<h1><spring:message code="label.manageEmoluments.createEmolumentTariff" /></h1>
</div>

<%-- NAVIGATION --%>
<div class="well well-sm" style="display:inline-block">
	<span class="glyphicon glyphicon-arrow-left" aria-hidden="true"></span>&nbsp;
	<a class="" href="${pageContext.request.contextPath}/academictreasury/manageemoluments/academictariff/viewemolumenttariffs/${finantialEntity.externalId}/${product.externalId}">
		<spring:message code="label.event.back" />
	</a>
|&nbsp;&nbsp;</div>
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
angular.module('changeExample', ['bennuToolkit']).controller('ExampleController', ['$scope', function($scope) {
	$scope.object=${academicTariffBeanJson};
	$scope.degreeTypeDropdownInitialized=false;
	
	$scope.change = function(newValue, oldValue) {

		var form = $('form[name="' + $scope.form.$name + '"]');
		if(newValue !== oldValue) {
			console.log(newValue);
			console.log($scope.object.degreeType);
			
			form.find('input[name="academicTariffBean"]').attr('value', angular.toJson($scope.object));
			form.attr("action", form.find('input[name="postback"]').attr('value'));
			form.submit();
		}
	};
}]);

window.jclosures = [];

function registerJqueryReadyClosure(func) {
	window.jclosures.push(func);
}

</script>


<form name="form" method="post" class="form-horizontal" ng-app="changeExample" ng-controller="ExampleController" 
	action="${pageContext.request.contextPath}/academictreasury/manageemoluments/academictariff/createemolumenttariff/${finantialEntity.externalId}/${product.externalId}"#<%= System.currentTimeMillis() %>>
	
	<input name="postback" type="hidden"
		value="${pageContext.request.contextPath}/academictreasury/manageemoluments/academictariff/createemolumenttariffpostback/${finantialEntity.externalId}/${product.externalId}" />
	
	<input name="academicTariffBean" type="hidden" value="{{ object }}" />

	<div class="panel panel-default">
		  <div class="panel-body">
				<div class="form-group row">
					<div class="col-sm-2 control-label"><spring:message code="label.AcademicTariff.administrativeOffice"/></div> 
						<div class="col-sm-4">
							<%-- Relation to side 1 drop down rendered in input --%>
							<select id="academicTariff_administrativeOffice" class="js-example-basic-single" name="administrativeoffice" 
								ng-model="object.administrativeOffice">
							 <option value=""></option> <%-- empty option remove it if you don't want to have it or give it a label CHANGE_ME --%> 
							</select>
						</div>
					</div>
				
				<div class="form-group row">
					<div class="col-sm-2 control-label"><spring:message code="label.AcademicTariff.degreeType"/></div> 
					<div class="col-sm-4">
						<%-- Relation to side 1 drop down rendered in input --%>
							 <select id="academicTariff_degreeType" class="js-example-basic-single" name="degreetype" 
							 	ng-model="object.degreeType" ng-change="change(object.degreeType, '{{ object.degreeType }}')">
								 <option value=""></option> <%-- empty option remove it if you don't want to have it or give it a label CHANGE_ME --%> 
							</select>
					</div>
				</div>		
				<div class="form-group row">
					<div class="col-sm-2 control-label"><spring:message code="label.AcademicTariff.degree"/></div> 
					
					<div class="col-sm-4">
						<%-- Relation to side 1 drop down rendered in input --%>
							 <select id="academicTariff_degree" class="js-example-basic-single" name="degree" 
							 	ng-model="object.degree" ng-change="change(object.degree, '{{ object.degree }}')">
							 <option value=""></option><%-- empty option remove it if you don't want to have it or give it a label CHANGE_ME --%> 
							</select>
					</div>
				</div>
				
				<div class="form-group row">
					<div class="col-sm-2 control-label"><spring:message code="label.AcademicTariff.cycleType"/></div> 
					
					<div class="col-sm-4">
						<%-- Relation to side 1 drop down rendered in input --%>
							 <select id="academicTariff_cycleType" class="js-example-basic-single" name="cycleType" 
							 	ng-model="object.cycleType">
							 <option value=""></option> 
							</select>
					</div>
				</div>
	
				<div class="form-group row">
					<div class="col-sm-2 control-label"><spring:message code="label.AcademicTariff.baseAmount"/></div> 
					
					<div class="col-sm-10">
                                        <div class="input-group">
                        <div class="input-group-addon">
                            <c:out value="${finantialEntity.finantialInstitution.currency.symbol}" />
                        </div>
                    
						<input id="academicTariff_baseAmount" class="form-control" type="text" name="baseAmount"  
							value='<c:out value='${not empty academicTariffBean.baseAmount ? academicTariffBean.baseAmount : "" }'/>' ng-model="object.baseAmount" />
					</div>
                    </div>	
				</div>		
				<div class="form-group row">
					<div class="col-sm-2 control-label"><spring:message code="label.AcademicTariff.unitsForBase"/></div> 
					
					<div class="col-sm-10">
						<input id="academicTariff_unitsForBase" class="form-control" type="text" name="unitsForBase"  
							value='<c:out value='${not empty academicTariffBean.unitsForBase ? academicTariffBean.unitsForBase : "" }'/>' ng-model="object.unitsForBase" />
					</div>	
				</div>		
				<div class="form-group row">
					<div class="col-sm-2 control-label"><spring:message code="label.AcademicTariff.applyUnitsAmount"/></div> 
					
					<div class="col-sm-10">
						<select id="academicTariff_applyUnitsAmount" name="applyUnitsAmount" ng-model="object.applyUnitsAmount" 
							ng-change="change(object.applyUnitsAmount, '{{ object.applyUnitsAmount }}')" >
							<option value="true" type="radio" selected><spring:message code="label.yes" /></option>
							<option value="false" type="radio"><spring:message code="label.no"/></option>
						</select>
					</div>	
				</div>
			
			<c:if test="${academicTariffBean.applyUnitsAmount}">
				<div class="form-group row">
					<div class="col-sm-2 control-label"><spring:message code="label.AcademicTariff.unitAmount"/></div> 
					
					<div class="col-sm-10">
						<input id="academicTariff_unitAmount" class="form-control" type="text" name="unitAmount"  
							value='<c:out value='${not empty academicTariffBean.unitAmount ? academicTariffBean.unitAmount : "" }'/>' ng-model="object.unitAmount" />
					</div>	
				</div>
			</c:if>
	
				<div class="form-group row">
					<div class="col-sm-2 control-label"><spring:message code="label.AcademicTariff.applyPagesAmount"/></div> 
					
					<div class="col-sm-10">
						<select id="academicTariff_applyPagesAmount" name="applyPagesAmount" ng-model="object.applyPagesAmount"
							ng-change="change(object.applyPagesAmount, '{{ object.applyPagesAmount }}')">
							<option value="true" type="radio"><spring:message code="label.yes"/></option>
							<option value="false" type="radio"><spring:message code="label.no"/></option>
						</select>
					
					</div>
				</div>
				
			<c:if test="${academicTariffBean.applyPagesAmount}">
				<div class="form-group row">
					<div class="col-sm-2 control-label"><spring:message code="label.AcademicTariff.pageAmount"/></div> 
					
					<div class="col-sm-10">
						<input id="academicTariff_pageAmount" class="form-control" type="text" name="pageamount" value='${academicTariffBean.pageAmount}' 
							ng-model="object.pageAmount" />
					</div>
				</div>
			</c:if>
			
				<div class="form-group row">
					<div class="col-sm-2 control-label"><spring:message code="label.AcademicTariff.applyMaximumAmount"/></div> 
					
					<div class="col-sm-10">
						<select id="academicTariff_applyMaximumAmount" name="applyMaximumAmount" ng-model="object.applyMaximumAmount"
							ng-change="change(object.applyMaximumAmount, '{{ object.applyMaximumAmount }}')" >
							<option value="true"><spring:message code="label.yes"/></option>
							<option value="false"><spring:message code="label.no"/></option>
						</select>
					
					</div>	
				</div>
				
			<c:if test="${academicTariffBean.applyMaximumAmount}">
				<div class="form-group row">
					<div class="col-sm-2 control-label"><spring:message code="label.AcademicTariff.maximumAmount"/></div> 
					
					<div class="col-sm-10">
						<input id="academicTariff_maximumAmount" class="form-control" type="text" name="maximumamount"  
							value='<c:out value='${not empty academicTariffBean.maximumAmount ? academicTariffBean.maximumAmount : "" }'/>' ng-model="object.maximumAmount" />
					</div>	
				</div>		
			</c:if>
			
				<div class="form-group row">
					<div class="col-sm-2 control-label"><spring:message code="label.AcademicTariff.urgencyRate"/></div> 
					
					<div class="col-sm-10">
						<input id="academicTariff_urgencyRate" class="form-control" type="text" name="urgencyrate" 
							value='<c:out value='${not empty academicTariffBean.urgencyRate ? academicTariffBean.urgencyRate : "" }'/>' ng-model="object.urgencyRate" />
					</div>	
				</div>
	
				<div class="form-group row">
					<div class="col-sm-2 control-label"><spring:message code="label.AcademicTariff.languageTranslationRate"/></div> 
					
					<div class="col-sm-10">
						<input id="academicTariff_languageTranslationRate" class="form-control" type="text" name="languagetranslationrate" 
							value='<c:out value='${not empty academicTariffBean.languageTranslationRate ? academicTariffBean.languageTranslationRate : "" }'/>' ng-model="object.languageTranslationRate" />
					</div>	
				</div>
				
				<div class="form-group row">
					<div class="col-sm-2 control-label"><spring:message code="label.AcademicTariff.beginDate"/></div> 
					
					<div class="col-sm-4">
						<input id="academicTariff_beginDate" class="form-control" type="text" name="begindate" bennu-date="object.beginDate" />
					
						 <%-- <input id="academicTariff_beginDate" class="form-control" type="date" name="begindate" ng-model="object.beginDate"/>--%>
					</div>
				</div>
				
				<div class="form-group row">
					<div class="col-sm-2 control-label"><spring:message code="label.AcademicTariff.endDate"/></div> 
					
					<div class="col-sm-4">
						<input id="academicTariff_endDate" class="form-control" type="text" name="date" 
						 bennu-date="object.endDate" >
					</div>
				</div>
				
				<div class="form-group row">
					<div class="col-sm-2 control-label"><spring:message code="label.AcademicTariff.dueDateCalculationType"/></div> 
					
					<div class="col-sm-4">
						<select id="academicTariff_dueDateCalculationType" class="form-control" name="duedatecalculationtype" 
							ng-model="object.dueDateCalculationType" ng-change="change(object.dueDateCalculationType, '{{ object.dueDateCalculationType }}')" >
							<option value=""></option> <%-- empty option remove it if you don't want to have it or give it a label CHANGE_ME --%>
						</select>
					</div>
				</div>
				
		<c:choose>
			<c:when test="${academicTariffBean.dueDateCalculationType.fixedDate}">
				<div class="form-group row">
					<div class="col-sm-2 control-label"><spring:message code="label.AcademicTariff.fixedDueDate"/></div> 
					
					<div class="col-sm-4">
						<input id="academicTariff_fixedDueDate" class="form-control" type="text" name="fixedDueDate"  bennu-date
						value = '<c:out value='${not empty academicTariffBean.fixedDueDate ? academicTariffBean.fixedDueDate : "" }'/>' 
						ng-model="object.fixedDueDate" />
					</div>
				</div>
			</c:when>
			<c:when test="${academicTariffBean.dueDateCalculationType.daysAfterCreation}">
				<div class="form-group row">
					<div class="col-sm-2 control-label"><spring:message code="label.AcademicTariff.numberOfDaysAfterCreationForDueDate"/></div> 
					
					<div class="col-sm-4">
						<input id="academicTariff_numberOfDaysAfterCreationForDueDate" class="form-control" type="text" name="numberOfDaysAfterCreationForDueDate"  
							value = '${academicTariffBean.numberOfDaysAfterCreationForDueDate}' ng-model="object.numberOfDaysAfterCreationForDueDate" />
					</div>
				</div>
			</c:when>
		</c:choose>
		
				<div class="form-group row">
					<div class="col-sm-2 control-label"><spring:message code="label.AcademicTariff.applyInterests"/></div> 
					
					<div class="col-sm-4">
						<select id="academicTariff_applyInterests" class="form-control" name="applyinterests" 
							ng-model="object.applyInterests" ng-change="change(object.applyInterests, '{{ object.applyInterests }}')" >
							<option value="true"><spring:message code="label.yes"/></option>
							<option value="false"><spring:message code="label.no"/></option>
						</select>
					</div>
				</div>

		<c:if test="${academicTariffBean.applyInterests}">
				<div class="form-group row">
					<div class="col-sm-2 control-label"><spring:message code="label.AcademicTariff.interestType"/></div> 
					
					<div class="col-sm-4">
						<select id="academicTariff_interestType" class="form-control" name="interestype" 
							ng-model="object.interestType" ng-change="change(object.interestType, '{{ object.interestType }}')" >
							<option value=""></option> <%-- empty option remove it if you don't want to have it or give it a label CHANGE_ME --%>
						</select>
					</div>
				</div>

			<c:if test="${academicTariffBean.interestType.daily}">
				<div class="form-group row">
					<div class="col-sm-2 control-label"><spring:message code="label.AcademicTariff.numberOfDaysAfterDueDate"/></div> 
					
					<div class="col-sm-10">
						<input id="academicTariff_numberOfDaysAfterDueDate" class="form-control" type="text" name="numberOfDaysAfterDueDate" 
							value='<c:out value='${not empty academicTariffBean.numberOfDaysAfterDueDate ? academicTariffBean.numberOfDaysAfterDueDate : "" }'/>' 
							ng-model="object.numberOfDaysAfterDueDate" />
					</div>	
				</div>

				<div class="form-group row">
					<div class="col-sm-2 control-label"><spring:message code="label.AcademicTariff.applyInFirstWorkday"/></div> 
					
					<div class="col-sm-4">
						<select id="academicTariff_applyInFirstWorkday" class="form-control" name="applyInFirstWorkday" 
							ng-model="object.applyInFirstWorkday" >
							<option value="true"><spring:message code="label.yes"/></option>
							<option value="false"><spring:message code="label.no"/></option>
						</select>
					</div>
				</div>
				<script type="text/javascript">
					registerJqueryReadyClosure(function() {
						$("#academicTariff_applyInFirstWorkday").select2().select2('val', '${academicTariffBean.applyInFirstWorkday}');
					});
				</script>
				

				<div class="form-group row">
					<div class="col-sm-2 control-label"><spring:message code="label.AcademicTariff.maximumDaysToApplyPenalty"/></div> 
					
					<div class="col-sm-10">
						<input id="academicTariff_maximumDaysToApplyPenalty" class="form-control" type="text" name="maximumDaysToApplyPenalty" 
							value='<c:out value='${not empty academicTariffBean.maximumDaysToApplyPenalty ? academicTariffBean.maximumDaysToApplyPenalty : "" }'/>' 
							ng-model="object.maximumDaysToApplyPenalty" />
					</div>
				</div>
			</c:if>
			<c:if test="${academicTariffBean.interestType.monthly}">
				<div class="form-group row">
					<div class="col-sm-2 control-label"><spring:message code="label.AcademicTariff.maximumMonthsToApplyPenalty"/></div> 
					
					<div class="col-sm-10">
						<input id="academicTariff_maximumMonthsToApplyPenalty" class="form-control" type="text" name="maximumMonthsToApplyPenalty" 
							value='<c:out value='${not empty academicTariffBean.maximumMonthsToApplyPenalty ? academicTariffBean.maximumMonthsToApplyPenalty : "" }'/>' 
							ng-model="object.maximumMonthsToApplyPenalty" />
					</div>
				</div>
			</c:if>
			<c:if test="${academicTariffBean.interestType.fixedAmount}">
				<div class="form-group row">
					<div class="col-sm-2 control-label"><spring:message code="label.AcademicTariff.interestFixedAmount"/></div> 
					
					<div class="col-sm-10">
						<input id="academicTariff_interestFixedAmount" class="form-control" type="text" name="interestFixedAmount" 
							value='<c:out value='${not empty academicTariffBean.interestFixedAmount ? academicTariffBean.interestFixedAmount : "" }'/>' 
							ng-model="object.interestFixedAmount" />
					</div>
				</div>
			</c:if>
	
		</c:if>

		</div>
		<div class="panel-footer">
			<input type="submit" class="btn btn-default" role="button" value="<spring:message code="label.submit" />"/>
		</div>
	</div>
</form>

<script>

registerJqueryReadyClosure(function() {
	<%-- Block for providing administrativeOffice options --%>
	<%-- CHANGE_ME --%> <%-- INSERT YOUR FORMAT FOR element --%>
	administrativeOffice_options = [
		<c:forEach items="${AcademicTariff_administrativeOffice_options}" var="element"> 
			{
				text : "<c:out value='${element.name.content}'/>",  
				id : "<c:out value='${element.externalId}'/>"
			},
		</c:forEach>
	];
	
	$("#academicTariff_administrativeOffice").select2(
			{
			data : administrativeOffice_options,
		}	  
    );
    
    $("#academicTariff_administrativeOffice").select2().select2('val', '<c:out value='${not empty academicTariffBean.administrativeOffice ? academicTariffBean.administrativeOffice.externalId : ""}'/>');

	<%-- End block for providing administrativeOffice options --%>
	<%-- Block for providing degreeType options --%>
	<%-- CHANGE_ME --%> <%-- INSERT YOUR FORMAT FOR element --%>
	degreeType_options = [
		<c:forEach items="${AcademicTariff_degreeType_options}" var="element"> 
			{
				text : "<c:out value='${element.name.content}'/>",  
				id : "<c:out value='${element.externalId}'/>"
			},
		</c:forEach>
	];
	
	$("#academicTariff_degreeType").select2(
			{
			data : degreeType_options,
		}
	    );
	
    $("#academicTariff_degreeType").select2().select2('val', '<c:out value='${not empty academicTariffBean.degreeType ? academicTariffBean.degreeType.externalId : ""}'/>');

	<%-- End block for providing degreeType options --%>
	<%-- Block for providing degree options --%>
	<%-- CHANGE_ME --%> <%-- INSERT YOUR FORMAT FOR element --%>
	degree_options = [
		<c:forEach items="${AcademicTariff_degree_options}" var="element"> 
			{
				text : "<c:out value='${element.nameI18N.content}'/>",  
				id : "<c:out value='${element.externalId}'/>"
			},
		</c:forEach>
	];
	
	$("#academicTariff_degree").select2(
		{
			data : degree_options,
		}
    );
    
    $("#academicTariff_degree").select2().select2('val', '<c:out value='${not empty academicTariffBean.degree ? academicTariffBean.degree.externalId : ""}'/>');

	<%-- End block for providing degree options --%>

	cycleType_options = [
		<c:forEach items="${AcademicTariff_cycleType_options}" var="element"> 
				{
					text : "${element.descriptionI18N.content}",
					id : "${element}"
				},
			</c:forEach>
		];
	
	console.log(cycleType_options);
	
	$("#academicTariff_cycleType").select2({ data : cycleType_options } );
	$("#academicTariff_cycleType").select2().select2('val', '<c:out value='${not empty academicTariffBean.cycleType ? academicTariffBean.cycleType.name() : ""}'/>');
	
	$("#academicTariff_applyUnitsAmount").select2().select2('val', '${academicTariffBean.applyUnitsAmount}');
	$("#academicTariff_applyPagesAmount").select2().select2('val', '${academicTariffBean.applyPagesAmount}');
	$("#academicTariff_applyMaximumAmount").select2().select2('val', '${academicTariffBean.applyMaximumAmount}');
	
	dueDateCalculationType_options = [
    		<c:forEach items="${AcademicTariff_dueDateCalculationType_options}" var="element"> 
    				{
    					text : "${element.descriptionI18N.content}",
    					id : "${element}"
    				},
    			</c:forEach>
    		];

	$("#academicTariff_dueDateCalculationType").select2({ data : dueDateCalculationType_options } );
	$("#academicTariff_dueDateCalculationType").select2().select2('val', '${not empty academicTariffBean.dueDateCalculationType ? academicTariffBean.dueDateCalculationType : ""}');
	
	$("#academicTariff_applyInterests").select2().select2('val', '${academicTariffBean.applyInterests}');

	if($("#academicTariff_interestType").length) {
		interestType_options = [
	    		<c:forEach items="${AcademicTariff_interestType_options}" var="element"> 
	    				{
	    					text : "${element.descriptionI18N.content}",
	    					id : "${element}"
	    				},
	    			</c:forEach>
	    		];
	
		$("#academicTariff_interestType").select2({ data : interestType_options } );
		$("#academicTariff_interestType").select2().select2('val', '${not empty academicTariffBean.interestType ? academicTariffBean.interestType : ""}');
	}

});

$(document).ready(function() {
	
	for(var i = 0; i < window.jclosures.length; i++) {
		window.jclosures[i].apply();
	}
	
});
</script>
