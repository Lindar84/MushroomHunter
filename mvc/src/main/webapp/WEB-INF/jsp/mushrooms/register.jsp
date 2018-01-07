
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="utf-8" trimDirectiveWhitespaces="false" session="true" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="my" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<my:pagetemplate title="New mushroom">
<jsp:attribute name="body">

    <c:set var="end" value="mushrooms"/>

    <form:form method="post" action="${pageContext.request.contextPath}/mushrooms/new"
               modelAttribute="mushroomCreate" cssClass="form-horizontal">
         <div class="form-group ${name_error?'has-error':''}">
             <form:label path="name" cssClass="col-sm-2 control-label">Name</form:label>
             <div class="col-sm-10">
                 <form:input path="name" cssClass="form-control"/>
                 <form:errors path="name" cssClass="help-block"/>
             </div>
         </div>

        <div class="form-group ${type_error?'has-error':''}">
            <form:label path="type" cssClass="col-sm-2 control-label">Type</form:label>
            <div class="col-sm-10">
                <form:select path="type" cssClass="form-control">
                    <c:forEach items="${mushroomType}" var="c">
                        <form:option value="${c}">${c}</form:option>
                    </c:forEach>
                </form:select>
                <form:errors path="type" cssClass="help-block"/>
            </div>
        </div>
       <div class="form-group ${intervalOfOccurrence_error?'has-error':''}">
           <form:label path="intervalOfOccurrence" cssClass="col-sm-2 control-label">Interval of occurrence</form:label>
           <div class="col-sm-10">
               <form:input path="intervalOfOccurrence" cssClass="form-control"/>
               <form:errors path="intervalOfOccurrence" cssClass="help-block"/>
           </div>
       </div>

      <button class="btn btn-primary" type="submit">Save mushroom</button>
    </form:form>

     <button class="btn btn-primary"
             onclick="location.href='${pageContext.request.contextPath}/${end}'">
         Return
     </button>
</jsp:attribute>
</my:pagetemplate>