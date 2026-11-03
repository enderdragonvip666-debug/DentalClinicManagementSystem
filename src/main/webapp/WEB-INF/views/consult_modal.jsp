<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="role" value="${sessionScope.user.roleName}"/>
<c:if test="${not empty err}"><div class="alert err"><c:out value="${err}"/></div></c:if>
<%@ include file="_booking_form.jspf" %>
