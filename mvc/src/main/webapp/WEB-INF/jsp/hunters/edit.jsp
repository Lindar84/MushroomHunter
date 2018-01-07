<%@ page contentType="text/html;charset=UTF-8" pageEncoding="utf-8" trimDirectiveWhitespaces="false" session="true" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="my" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<my:pagetemplate title="Edit hunter">
<jsp:attribute name="body">
  <c:set var="end" value="hunters"/>

    <form:form method="post" action="${pageContext.request.contextPath}/${end}/edit/${hunterEdit.id}"
               modelAttribute="hunterEdit" cssClass="form-horizontal">

        <div class="form-group ${firstName_error?'has-error':''}">
            <form:label path="firstName" cssClass="col-sm-2 control-label">First Name</form:label>
            <div class="col-sm-10">
                <form:input path="firstName" cssClass="form-control"/>
                <form:errors path="firstName" cssClass="help-block"/>
            </div>
        </div>

        <div class="form-group ${surname_error?'has-error':''}">
            <form:label path="surname" cssClass="col-sm-2 control-label">Surname</form:label>
            <div class="col-sm-10">
                <form:input path="surname" cssClass="form-control"/>
                <form:errors path="surname" cssClass="help-block"/>
            </div>
        </div>
       <div class="form-group ${userNickname_error?'has-error':''}">
           <form:label path="userNickname" cssClass="col-sm-2 control-label">Nickname</form:label>
           <div class="col-sm-10">
               <form:input path="userNickname" cssClass="form-control"/>
               <form:errors path="userNickname" cssClass="help-block"/>
           </div>
       </div>

      <c:if test="${sessionScope.user.admin}"> <%-- TODO: reset session after admin change and self delete--%>
          <div class="form-group ${admin_error?'has-error':''}">
              <form:label path="admin" cssClass="col-sm-2 control-label">Admin</form:label>
              <div class="col-sm-10">
                  <form:checkbox path="admin" cssClass="form-control"/>
                  <form:errors path="admin" cssClass="help-block"/>
              </div>
          </div>
      </c:if>

     <div class="form-group ${personalInfo_error?'has-error':''}">
         <form:label path="personalInfo" cssClass="col-sm-2 control-label">Personal info</form:label>
         <div class="col-sm-10">
             <form:textarea path="personalInfo" cssClass="form-control"/>
             <form:errors path="personalInfo" cssClass="help-block"/>
         </div>
     </div>

      <button class="btn btn-primary" type="submit">Update User</button>
    </form:form>

  <button class="btn btn-primary"
          onclick="location.href='${pageContext.request.contextPath}/${end}'">
      Return
  </button>

</jsp:attribute>
</my:pagetemplate>
