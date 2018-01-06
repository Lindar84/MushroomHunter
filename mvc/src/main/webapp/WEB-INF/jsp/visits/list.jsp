<%@ page contentType="text/html;charset=UTF-8" pageEncoding="utf-8" trimDirectiveWhitespaces="false" session="true" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="my" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<my:pagetemplate title="Visits">
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

        <table class="table table-striped">
            <caption>Visits</caption>
            <thead>
            <tr>
                <th>Hunter</th>
                <th>Forest</th>
                <th>Mushrooms</th>
                <th>Description</th>
                <th>Date</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach items="${visits}" var="visit">
                <tr>
                    <td>
                        <c:choose>
                            <c:when test="${sessionScope.user.admin || visit.hunter.id == sessionScope.user.id}" >
                                <my:a href="/hunters/read/${visit.hunter.id}"><c:out value="${visit.hunter.userNickname}"/></my:a>
                            </c:when>
                            <c:otherwise>
                                <c:out value="${visit.hunter.userNickname}"/>
                            </c:otherwise>
                        </c:choose>
                    </td>
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
                        <my:a href="/${end}/read/${visit.id}"><c:out value="${visit.note}"/></my:a>
                    </td>
                    <td>
                        <my:a href="/${end}/read/${visit.id}"><c:out value="${visit.date}"/></my:a>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>

        <button class="btn btn-primary"
              onclick="location.href='${pageContext.request.contextPath}'">
            Return
        </button>

        <button class="btn btn-primary" onclick="location.href='${pageContext.request.contextPath}/visits/create'">
            Record a new visit
        </button>

    </jsp:attribute>
</my:pagetemplate>
