<%@ page contentType="text/html;charset=UTF-8" pageEncoding="utf-8" trimDirectiveWhitespaces="false" session="true" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="my" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<my:pagetemplate title="Visit Profile">
<jsp:attribute name="body">
    <c:set var="end" value="visits"/>
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

    <table class="table">
        <caption><c:out value="${visit.hunter.userNickname}"/>s visit: <c:out value="${visit.note}"/></caption>
        <thead>
        <tr>
            <th>Mushroom Hunter</th>
            <th>Forest</th>
            <th>Mushrooms</th>
            <th>Note</th>
            <th>Date</th>
            <th>Delete</th>
            <th>Update</th>
        </tr>
        </thead>

        <tbody>
        <tr>
            <td>
                <c:choose>
                <c:when test="${sessionScope.user.admin || visit.hunter.id == sessionScope.user.id}" >
                    <my:a href="/hunters/read/${visit.hunter.id}"><c:out value="${visit.hunter.firstName} "/>
                        <c:out value="${visit.hunter.surname}"/></my:a>
                </c:when>
                <c:otherwise>
                    <c:out value="${visit.hunter.firstName} "/>
                        <c:out value="${visit.hunter.surname}"/>
                </c:otherwise>
            </c:choose>
            </td>
            <td>
                <my:a href="/forests/read/${visit.forest.id}"><c:out value="${visit.forest.name}"/></my:a>
            </td>
            <td>
                <c:forEach items="${visit.mushrooms}" var="mushroom">
                    <my:a href="/mushrooms/read/${mushroom.id}"><c:out value="${mushroom.name}"/></my:a>
                </c:forEach>
            </td>
            <td>
                <c:out value="${visit.note}"/>
            </td>
            <td>
                <c:out value="${visit.date}"/>
            </td>
            <td>
            <c:choose>
                <c:when test="${sessionScope.user.admin || visit.hunter.id == sessionScope.user.id}" >
                    <button class="glyphicon glyphicon-trash btn" onclick=" openModal(${visit.id}) ">
                    </button>
                    <my:modal_template suffix="${visit.id}" title="Delete visit">
                      <jsp:attribute name="body">
                          <strong>Are you sure you want to delete this visit?</strong>
                      </jsp:attribute>
                      <jsp:attribute name="footer">
                          <button type="button" class="btn btn-secondary" data-dismiss="modal"
                                  onclick="closeModal(${visit.id})">Close
                          </button>
                        <form style="float: right; margin-left: 10px" method="post"
                              action="${pageContext.request.contextPath}/${end}/delete/${visit.id}">
                            <input type="submit" class="btn btn-primary" value="Delete"/>
                        </form>
                      </jsp:attribute>
                    </my:modal_template>
                </c:when>
                <c:otherwise>
                </c:otherwise>
            </c:choose>
            </td>

            <td>
                <c:choose>
                    <c:when test="${visit.hunter.id == sessionScope.user.id}" >
                <button class="glyphicon glyphicon-edit btn"
                        onclick="location.href='${pageContext.request.contextPath}/${end}/edit/${visit.id}'">
                </button>
                </c:when>
                <c:otherwise>
                </c:otherwise>
                </c:choose>
            </td>
        </tr>
        </tbody>
    </table>

</jsp:attribute>
</my:pagetemplate>
