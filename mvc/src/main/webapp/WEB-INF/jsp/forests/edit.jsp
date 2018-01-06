<%@ page contentType="text/html;charset=UTF-8" pageEncoding="utf-8" trimDirectiveWhitespaces="false" session="true" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="my" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<my:pagetemplate title="Edit forest">
<jsp:attribute name="body">
  <c:set var="end" value="forests"/>

    <form:form method="post" action="${pageContext.request.contextPath}/${end}/edit/${forestEdit.id}"
               modelAttribute="forestEdit" cssClass="form-horizontal">

        <div class="form-group ${name_error?'has-error':''}">
          <form:label path="name" cssClass="col-sm-2 control-label">Name</form:label>
          <div class="col-sm-10">
            <form:input path="name" cssClass="form-control"/>
            <form:errors path="name" cssClass="help-block"/>
          </div>
        </div>

        <div class="form-group ${description_error?'has-error':''}">
          <form:label path="description" cssClass="col-sm-2 control-label">Description</form:label>
          <div class="col-sm-10">
            <form:input path="description" cssClass="form-control"/>
            <form:errors path="description" cssClass="help-block"/>
          </div>

      <button class="btn btn-primary" type="submit">Update Forest</button>
    </form:form>
  <button class="btn btn-primary"
          onclick="location.href='${pageContext.request.contextPath}/${end}'">
    Return
  </button>
</jsp:attribute>
</my:pagetemplate>
