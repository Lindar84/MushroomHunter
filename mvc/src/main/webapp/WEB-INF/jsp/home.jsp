<%@ page contentType="text/html;charset=UTF-8" pageEncoding="utf-8" trimDirectiveWhitespaces="true" session="true" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="my" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<my:pagetemplate title="Home">
    <jsp:attribute name="body">
      <h1>
        Welcome to Mushroom hunters.
      </h1>
        <div class="container">
            <h3>Care to look at the statistics?</h3>
            <p><a href="${pageContext.request.contextPath}/forests/find">Find the best forest for a mushroom</a></p>
        </div>
    </jsp:attribute>
</my:pagetemplate>