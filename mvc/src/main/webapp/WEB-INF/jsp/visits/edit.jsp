<%@ page contentType="text/html;charset=UTF-8" pageEncoding="utf-8" trimDirectiveWhitespaces="false" session="true" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="my" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<my:pagetemplate title="Edit visit">
<jsp:attribute name="body">
  <c:set var="end" value="visits"/>

    <form:form method="post" action="${pageContext.request.contextPath}/${end}/edit/${visitEdit.id}"
               modelAttribute="visitEdit" cssClass="form-horizontal">

        <form:input type="hidden" path="hunter" value="${hunter}" />

        <div class="form-group ${note_error?'has-error':''}">
            <form:label path="note" cssClass="col-sm-2 control-label">Note</form:label>
            <div class="col-sm-10">
                <form:input path="note" cssClass="form-control"/>
                <form:errors path="note" cssClass="help-block"/>
            </div>
        </div>

        <div class="form-group ${forest_error?'has-error':''}">
            <form:label path="forest" cssClass="col-sm-2 control-label" >Forest</form:label>
            <div class="col-sm-10">
                <form:select path="forest" name="forest">
                    <c:forEach items="${forests}" var="forest">
                        <form:option value="${forest.id}">${forest.name}</form:option>
                    </c:forEach>
                </form:select>
                <form:errors path="forest" cssClass="help-block"/>
            </div>
        </div>

         <div class="form-group ${mushrooms_error?'has-error':''}">
             <form:label path="mushrooms" cssClass="col-sm-2 control-label">Mushrooms found</form:label>
             <div class="col-sm-10">
                 <form:checkboxes path="mushrooms" items="${mushrooms}" itemLabel="name" itemValue="id" delimiter="<br/>"/>
                 <form:errors path="mushrooms" cssClass="help-block"/>
             </div>
         </div>

        <div class="form-group ${date_error?'has-error':''}">
            <form:label path="date" cssClass="col-sm-2 control-label">Date (YYYY-MM-DD)</form:label>
            <div class="form-group col-sm-10">
                <div class='input-group date' id='datetimepicker1' required="true">
                    <form:input  path="date" type='text' cssClass="form-control" />
                    <form:errors path="date" cssClass="help-block"/>
                    <span class="input-group-addon">
                        <span class="glyphicon glyphicon-calendar"></span>
                    </span>
                </div>
            </div>
            <script type="text/javascript">
                $(function () {
                    $('#datetimepicker1').datetimepicker({
                        format:'YYYY-MM-DD',
                        maxDate: moment()
                    })
                });
            </script>
        </div>

      <button class="btn btn-primary" type="submit">Update Visit</button>
    </form:form>

  <button class="btn btn-primary"
          onclick="location.href='${pageContext.request.contextPath}/${end}'">
      Return
  </button>

</jsp:attribute>
</my:pagetemplate>
