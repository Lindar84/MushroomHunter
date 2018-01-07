<%@ page contentType="text/html;charset=UTF-8" pageEncoding="utf-8" trimDirectiveWhitespaces="false" session="true" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="my" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>


<my:pagetemplate title="Forests">
    <jsp:attribute name="body">
      <c:set var="end" value="forests"/>

          <div class="container">
              <h4>Search for places to find your favourite mushroom!</h4>
              <form:form method="post" action="${pageContext.request.contextPath}/forests/find" cssClass="form-horizontal">
                  <div class="form-group">
                      <div class="col-sm-10">
                          <select name="mushroomId">
                              <c:forEach items="${mushrooms}" var="mushroom">
                                <option value="${mushroom.id}">${mushroom.name}</option>
                              </c:forEach>
                          </select>
                      </div>
                  </div>
                  <div class="form-group">
                      <input type="submit" class="btn btn-primary" value="Search"/>
                  </div>
            </form:form>
          </div>

        <table class="table table-striped">
            <thead>
            <tr>
                <th>Name</th>
                <th>Number of mushrooms</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach items="${forests}" var="forest">
                <tr>
                    <td>
                    <my:a href="/forests/read/${forest.key.id}"><c:out value="${forest.key.name} "/></my:a>
                    </td>

                    <td>
                        <c:out value="${forest.value}"/>
                    </td>
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