<%@ page contentType="text/html;charset=UTF-8" pageEncoding="utf-8" trimDirectiveWhitespaces="false" session="true" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="my" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>


<c:choose>
    <c:when test="${hunter.id == sessionScope.user.id}">
        <c:set var="pageTitle" value="My profile" scope="page"/>
    </c:when>
    <c:otherwise>
        <c:set var="pageTitle" value="Profile of ${hunter.firstName} ${hunter.surname}" scope="page"/>
    </c:otherwise>
</c:choose>


<my:pagetemplate title="${pageTitle}">
<jsp:attribute name="body">
    <c:set var="end" value="hunters"/>
    <script>
        function openModal(suffix) {
            var modal = $("#modal_" + suffix);
            if (modal)
                modal.modal('show');
        }

        function closeModal(suffix) {
            var modal = $("#modal_" + suffix);
            if (modal)
                modal.modal('hide');
        }
    </script>

    <table class="table"> <%-- TODO: change size, add create visit button--%>

        <thead>
        <tr>
            <th>Name</th>
            <th>Nick</th>
            <th>Admin?</th>
            <c:if test="${sessionScope.user.admin or sessionScope.user.id == hunter.id}">
                <th>Delete</th>
                <th>Update</th>
            </c:if>
        </tr>
        </thead>

        <tbody>
            <td>
                <my:a href="/${end}/read/${hunter.id}"><c:out value="${hunter.firstName} "/><c:out value="${hunter.surname}"/> </my:a>
            </td>
            <td>
                <c:out value="${hunter.userNickname}"/>
            </td>
            <td>
                 <c:choose>
                    <c:when test="${hunter.admin}">
                        <span class="glyphicon glyphicon-ok"> </span>
                    </c:when>
                    <c:otherwise>
                        <span class="glyphicon glyphicon-remove"> </span>
                    </c:otherwise>
                    </c:choose>
            </td>
            <c:if test="${sessionScope.user.admin or sessionScope.user.id == hunter.id}">
            <td>
                <button class="glyphicon glyphicon-trash btn" onclick=" openModal(${hunter.id}) ">
                </button>


                <my:modal_template suffix="${hunter.id}" title="Delete hunter">
                  <jsp:attribute name="body">
                      <strong>Are you sure you want to delete mushroom hunter '<c:out
                              value="${hunter.userNickname}"/>'?</strong>
                  </jsp:attribute>
                  <jsp:attribute name="footer">
                      <button type="button" class="btn btn-secondary" data-dismiss="modal"
                              onclick="closeModal(${hunter.id})">Close
                      </button>
                    <form style="float: right; margin-left: 10px" method="post"
                          action="${pageContext.request.contextPath}/${end}/delete/${hunter.id}">
                        <input type="submit" class="btn btn-primary" value="Delete"/>
                    </form>
                  </jsp:attribute>
                </my:modal_template>

            </td>
            <td>
                <button class="glyphicon glyphicon-edit btn"
                        onclick="location.href='${pageContext.request.contextPath}/${end}/edit/${hunter.id}'">
                </button>
            </td>
            </c:if>

        </tr>
        </tbody>
    </table>

    <%-- personal info --%>
    <c:if test="${not empty hunter.personalInfo}">
        <label for="hunterInfo">About me</label>
        <blockquote class="blockquote" id="hunterInfo">
            <p><c:out value="${hunter.personalInfo}"/></p>
        </blockquote>
    </c:if>

    <%-- create visit button --%>
    <c:if test="${sessionScope.user.id == hunter.id}">
        <button class="btn btn-primary" onclick="location.href='${pageContext.request.contextPath}/visits/create'">
            Record a new visit
        </button>

    </c:if>
    <%-- visits --%>
    <div style="margin-top:20px" >
        <my:visit_table_template tableName="Recorded visits"/>
    </div>

</jsp:attribute>
</my:pagetemplate>
