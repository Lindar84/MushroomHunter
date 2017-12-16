<%--
  Created by IntelliJ IDEA.
  User: bencikpeter
  Date: 16/12/2017
  Time: 11:18
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="utf-8" trimDirectiveWhitespaces="false" session="true" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="my" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<my:pagetemplate title="Mushroom detail">
<jsp:attribute name="body">
    <c:set var="end" value="mushrooms"/>
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
        <caption>Mushroom hunter '<c:out value="${mushroom.name}"/>'</caption>
        <thead>
        <tr>
            <th>Name</th>
            <th>Type</th>
            <th>Interval of occurrence</th>
            <th>Delete</th>
            <th>Update</th>
        </tr>
        </thead>

        <tbody>
        <tr>
            <td>
                <my:a href="/${end}/read/${mushroom.id}"><c:out value="${mushroom.name} "/></my:a>
            </td>
            <td>
                <c:out value="${mushroom.type}"/>
            </td>
            <td>
                <c:out value="${mushroom.intervalOfOccurrence}"/>
            </td>
            <td>
                <button class="glyphicon glyphicon-trash btn" onclick=" openModal(${mushroom.id}) ">
                </button>


                <my:modal_template suffix="${mushroom.id}" title="Delete mushroom">
                  <jsp:attribute name="body">
                      <strong>Are you sure you want to delete mushroom '<c:out
                              value="${mushroom.name}"/>'?</strong>
                  </jsp:attribute>
                  <jsp:attribute name="footer">
                      <button type="button" class="btn btn-secondary" data-dismiss="modal"
                              onclick="closeModal(${mushroom.id})">Close
                      </button>
                    <form style="float: right; margin-left: 10px" method="post"
                          action="${pageContext.request.contextPath}/${end}/delete/${mushroom.id}">
                        <input type="submit" class="btn btn-primary" value="Delete"/>
                    </form>
                  </jsp:attribute>
                </my:modal_template>

            </td>
            <td>
                <button class="glyphicon glyphicon-edit btn"
                        onclick="location.href='${pageContext.request.contextPath}/${end}/edit/${mushroom.id}'">
                </button>
            </td>
        </tr>
        </tbody>
    </table>

</jsp:attribute>
</my:pagetemplate>
