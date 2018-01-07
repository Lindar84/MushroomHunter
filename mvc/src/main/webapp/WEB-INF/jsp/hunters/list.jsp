<%@ page contentType="text/html;charset=UTF-8" pageEncoding="utf-8" trimDirectiveWhitespaces="false" session="true" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="my" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<my:pagetemplate title="Mushroom Hunters">
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

        <table class="table table-striped">
          <thead>
          <tr>
            <th>Name</th>
            <th>Nick</th>
            <th>Admin?</th>
              <my:protected>
            <th>Delete</th>
            <th>Update</th>
              </my:protected>
          </tr>
          </thead>
          <tbody>
          <c:forEach items="${hunters}" var="hunter">
                <tr>
                    <td>
                        <my:a href="/${end}/read/${hunter.id}"><c:out value="${hunter.firstName} "/><c:out value="${hunter.surname}"/> </my:a>
                    </td>
                    <td>
                        <c:out value="${hunter.userNickname}"/>
                    </td>
                    <td>
                     <c:choose>
                        <c:when test="${hunter.admin}">
                            <span class= "glyphicon glyphicon-ok"> </span>
                        </c:when>
                        <c:otherwise>
                            <span class= "glyphicon glyphicon-remove"> </span>
                        </c:otherwise>
                        </c:choose>
                  </td>
                    <my:protected>
                  <td>
                    <button class="glyphicon glyphicon-trash btn" onclick=" openModal(${hunter.id}) ">
                    </button>

                        <my:modal_template suffix="${hunter.id}" title="Delete user">
                      <jsp:attribute name="body">
                          <strong>Are you sure you want to delete mushroom hunter '<c:out value="${hunter.userNickname}"/>'?</strong>
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
                    <button class="glyphicon glyphicon-edit btn" onclick="location.href='${pageContext.request.contextPath}/${end}/edit/${hunter.id}'">
                    </button>
                  </td>
                    </my:protected>
                </tr>
            </c:forEach>
            </tbody>
        </table>

      <button class="btn btn-primary"
              onclick="location.href='${pageContext.request.contextPath}'">
          Return
      </button>

    </jsp:attribute>
</my:pagetemplate>
