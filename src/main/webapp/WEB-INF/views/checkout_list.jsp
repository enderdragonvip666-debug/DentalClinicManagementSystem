<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="_head.jspf" %>
<h2>Danh sách thanh toán</h2>
<form method="get" class="search"><input name="q" value="<c:out value='${q}'/>" placeholder="Họ tên hoặc số điện thoại"><button class="btn">Tìm kiếm</button></form>
<table><thead><tr><th>Lượt khám</th><th>Bệnh nhân</th><th>Số điện thoại</th><th></th></tr></thead><tbody>
<c:forEach var="v" items="${visits}"><tr><td>V${v.visitId}</td><td><c:out value="${v.patientName}"/></td><td><c:out value="${v.patientPhone}"/></td>
  <td><a class="btn sm" href="${ctx}/invoice?visitId=${v.visitId}">Mở</a></td></tr></c:forEach>
<c:if test="${empty visits}"><tr><td colspan="4" class="muted">Không tìm thấy kết quả.</td></tr></c:if>
</tbody></table>
<%@ include file="_foot.jspf" %>
