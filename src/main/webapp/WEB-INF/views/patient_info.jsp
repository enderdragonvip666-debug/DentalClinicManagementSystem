<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="_head.jspf" %>
<div class="row"><h2>Thông tin bệnh nhân</h2><a class="btn ghost" href="${ctx}/patients">← Danh sách bệnh nhân</a></div>
<div class="card"><p><strong>Họ và tên:</strong> <c:out value="${visit.patientName}"/></p><p><strong>Số điện thoại:</strong> <c:out value="${visit.patientPhone}"/></p>
<p><strong>Lượt khám hiện tại:</strong> V${visit.visitId} <span class="badge ${visit.status}">${st[visit.status]}</span></p>
<a class="btn" href="${ctx}/examform?visitId=${visit.visitId}">Phiếu khám</a></div>
<div class="card"><jsp:include page="_history_panel.jsp"/></div>
<%@ include file="_foot.jspf" %>
