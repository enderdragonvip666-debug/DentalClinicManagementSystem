<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="_head.jspf" %>
<div class="row"><h2>Lịch khám của tôi</h2><span><a class="btn" href="${ctx}/consult">Đặt lịch khám mới</a> <a class="btn ghost" href="${ctx}/history">Lịch sử điều trị</a></span></div>
<table><thead><tr><th>Mã</th><th>Ngày khám</th><th>Ca</th><th>Dịch vụ</th><th>Ghi chú</th><th>Trạng thái</th><th></th></tr></thead><tbody>
<c:forEach var="r" items="${requests}"><tr>
  <td><c:out value="${r.requestCode}"/></td><td>${r.apptDateText}</td><td>${sh[r.shift]}</td><td><c:out value="${r.serviceName}"/></td><td><c:out value="${r.note}"/></td>
  <td><span class="badge ${r.status}">${st[r.status]}</span></td>
  <td><c:if test="${r.status == 'REQUESTED'}"><form method="post" action="${ctx}/my-requests" class="inline" onsubmit="return confirm('Bạn có chắc muốn hủy lịch hẹn này không?');">
      <input type="hidden" name="requestId" value="${r.requestId}"><button class="btn danger sm">Hủy lịch</button></form></c:if></td></tr></c:forEach>
<c:if test="${empty requests}"><tr><td colspan="7" class="muted">Bạn chưa có lịch khám nào. <a href="${ctx}/consult">Đặt lịch ngay</a></td></tr></c:if>
</tbody></table>
<%@ include file="_foot.jspf" %>
