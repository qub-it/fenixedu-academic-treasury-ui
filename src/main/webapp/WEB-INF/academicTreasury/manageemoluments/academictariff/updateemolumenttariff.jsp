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

${portal.angularToolkit()}

<link
    href="//cdn.datatables.net/responsive/1.0.4/css/dataTables.responsive.css"
    rel="stylesheet" />
<script
    src="//cdn.datatables.net/responsive/1.0.4/js/dataTables.responsive.js"></script>
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
    src="${pageContext.request.contextPath}/static/treasury/js/omnis.js"></script>


<script
    src="${pageContext.request.contextPath}/webjars/angular-sanitize/1.3.11/angular-sanitize.js"></script>
<link rel="stylesheet" type="text/css"
    href="${pageContext.request.contextPath}/webjars/angular-ui-select/0.11.2/select.min.css" />
<script
    src="${pageContext.request.contextPath}/webjars/angular-ui-select/0.11.2/select.min.js"></script>

<%-- TITLE --%>
<div class="page-header">
    <h4>
        <c:out value="${finantialEntity.name.content}" />
    </h4>
    <h1>
        <spring:message
            code="label.manageEmoluments.updateEmolumentTariff" />
    </h1>
</div>

<%-- NAVIGATION --%>
<div class="well well-sm" style="display: inline-block">
    <span class="glyphicon glyphicon-arrow-left" aria-hidden="true"></span>&nbsp;
    <a class=""
        href="${pageContext.request.contextPath}/academictreasury/manageemoluments/academictariff/viewemolumenttariffs/${finantialEntity.externalId}/${product.externalId}">
        <spring:message code="label.event.back" />
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
angular.module('changeExample', [ 'ngSanitize', 'ui.select', 'bennuToolkit' ]).controller('ExampleController', ['$scope', function($scope) {
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

<form name="form" method="post" class="form-horizontal"
    ng-app="changeExample" ng-controller="ExampleController"
    action="${pageContext.request.contextPath}/academictreasury/manageemoluments/academictariff/updateemolumenttariff/${finantialEntity.externalId}/${product.externalId}/${academicTariff.externalId}">

    <input name="postback" type="hidden"
        value="${pageContext.request.contextPath}/academictreasury/manageemoluments/academictariff/updateemolumenttariffpostback/${finantialEntity.externalId}/${product.externalId}/${academicTariff.externalId}" />

    <input name="academicTariffBean" type="hidden" value="{{ object }}" />

    <div class="panel panel-default">
        <div class="panel-body">

            <div class="form-group row">
                <div class="col-sm-2 control-label">
                    <spring:message
                        code="label.AcademicTariff.beginDate" />
                </div>

                <div class="col-sm-4">
                    <input id="academicTariff_beginDate"
                        class="form-control" type="text"
                        name="begindate"
                        value='<c:out value='${not empty academicTariffBean.beginDate ? academicTariffBean.beginDate : "" }'/>'
                        bennu-date="object.beginDate" />
                </div>
            </div>

            <div class="form-group row">
                <div class="col-sm-2 control-label">
                    <spring:message code="label.AcademicTariff.endDate" />
                </div>

                <div class="col-sm-4">
                    <input id="academicTariff_endDate"
                        class="form-control" name="enddate" type="text"
                        value='<c:out value='${not empty academicTariffBean.endDate ? academicTariffBean.endDate : "" }'/>'
                        bennu-date="object.endDate">
                </div>
            </div>

            <div class="form-group row">
                <div class="col-sm-2 control-label">
                    <spring:message
                        code="label.AcademicTariff.applyInterests" />
                </div>

                <div class="col-sm-4">
                    <select id="academicTariff_applyInterests"
                        class="form-control" name="applyinterests"
                        ng-model="object.applyInterests"
                        ng-change="change(object.applyInterests, '{{ object.applyInterests }}')">
                        <option value="true"><spring:message
                                code="label.yes" /></option>
                        <option value="false"><spring:message
                                code="label.no" /></option>
                    </select>
                </div>
            </div>

            <c:if test="${academicTariffBean.applyInterests}">
                <div class="form-group row">
                    <div class="col-sm-2 control-label">
                        <spring:message
                            code="label.AcademicTariff.interestType" />
                    </div>

                    <div class="col-sm-4">
                        <select id="academicTariff_interestType"
                            class="form-control" name="interestype"
                            ng-model="object.interestType"
                            ng-change="change(object.interestType, '{{ object.interestType }}')">
                            <option value=""></option>
                            <%-- empty option remove it if you don't want to have it or give it a label CHANGE_ME --%>
                        </select>
                    </div>
                </div>

                <c:if
                    test="${academicTariffBean.interestType.fixedAmount}">
                    <div class="form-group row">
                        <div class="col-sm-2 control-label">
                            <spring:message
                                code="label.AcademicTariff.interestFixedAmount" />
                        </div>

                        <div class="col-sm-10">
                            <input
                                id="academicTariff_interestFixedAmount"
                                class="form-control" type="text"
                                name="interestFixedAmount"
                                value='<c:out value='${not empty academicTariffBean.interestFixedAmount ? academicTariffBean.interestFixedAmount : "" }'/>'
                                ng-model="object.interestFixedAmount" />
                        </div>
                    </div>
                </c:if>

            </c:if>

        </div>
        <div class="panel-footer">
            <input type="submit" class="btn btn-default" role="button"
                value="<spring:message code="label.submit" />" />
        </div>
    </div>
</form>

<script>

registerJqueryReadyClosure(function() {
	
	$("#academicTariff_applyInterests").select2().select2('val', '${academicTariffBean.applyInterests}');

	interestType_options = [
    		<c:forEach items="${AcademicTariff_interestType_options}" var="element"> 
    				{
    					text : "${element.descriptionI18N.content}",
    					id : "${element}"
    				},
    			</c:forEach>
    		];
	
	if($("#academicTariff_interestType").length > 0) {
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
