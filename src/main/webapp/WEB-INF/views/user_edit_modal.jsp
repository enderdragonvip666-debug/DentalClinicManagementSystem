<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="st" value="${{'ACTIVE':'Hoạt động','INACTIVE':'Ngừng hoạt động','SUSPENDED':'Bị khóa'}}"/>
<c:set var="rl" value="${{'Patient':'Bệnh nhân','Dental Assistant':'Trợ lý nha sĩ','Dentist':'Nha sĩ','Administrator':'Quản trị viên'}}"/>
<%@ include file="_account_editor.jspf" %>
