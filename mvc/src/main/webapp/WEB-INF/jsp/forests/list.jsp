<%@ page contentType="text/html;charset=UTF-8" pageEncoding="utf-8" trimDirectiveWhitespaces="false" session="true" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="my" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<my:pagetemplate title="Forests">
    <jsp:attribute name="body">
      <c:set var="end" value="forests"/>

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
          <caption>Forests</caption>
          <thead>
          <tr>
            <th>Name</th>
            <th>Description</th>
            <th>Delete</th>
            <th>Update</th>
          </tr>
          </thead>
          <tbody>
          <c:forEach items="${forests}" var="forest">
                <tr>
                  <td>
                    <my:a href="/${end}/read/${forest.id}">
                    <c:out value="${forest.name} "/>
                    </my:a>
                  </td>

                  <td>
                    <c:out value="${forest.description}"/>
                  </td>

                  <td>
                    <button class="glyphicon glyphicon-trash btn" onclick=" openModal(${forest.id}) ">
                    </button>

                    <my:modal_template suffix="${forest.id}" title="Delete forest">
                      <jsp:attribute name="body">
                          <strong>Are you sure you want to delete Forest '<c:out value="${forest.name}"/>'?</strong>
                      </jsp:attribute>
                      <jsp:attribute name="footer">
                          <button type="button" class="btn btn-secondary" data-dismiss="modal"
                                  onclick="closeModal(${forest.id})">Close
                          </button>
                        <form style="float: right; margin-left: 10px" method="post"
                              action="${pageContext.request.contextPath}/${end}/delete/${forest.id}">
                          <input type="submit" class="btn btn-primary" value="Delete"/>
                        </form>
                      </jsp:attribute>
                    </my:modal_template>
                  </td>

                  <td>
                    <button class="glyphicon glyphicon-edit btn" onclick="location.href='${pageContext.request.contextPath}/${end}/edit/${forest.id}'">
                    </button>
                  </td>
                </tr>
            </c:forEach>
          </tbody>
        </table>

      <button class="btn btn-primary"
              onclick="location.href='${pageContext.request.contextPath}'">
        Return
      </button>

      <button class="btn btn-primary"
              onclick="location.href='${pageContext.request.contextPath}/${end}/new'">
        New Forest
      </button>

    </jsp:attribute>
</my:pagetemplate>
