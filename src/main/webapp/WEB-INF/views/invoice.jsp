<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="_head.jspf" %>
<fmt:formatNumber var="totalText" value="${bill.total}" maxFractionDigits="0"/>
<div class="row noprint"><h2>Hóa đơn và thanh toán</h2><a class="btn ghost" href="${ctx}/checkout">← Danh sách thanh toán</a></div>
<section id="printable" class="card">
  <h3>Phòng khám nha khoa — Hóa đơn <c:if test="${bill.invoiced}">#${bill.invoice.invoiceId}</c:if></h3>
  <p><strong>Bệnh nhân:</strong> <c:out value="${bill.visit.patientName}"/> · <c:out value="${bill.visit.patientPhone}"/> · Lượt khám V${bill.visit.visitId}</p>
  <p><strong>Trạng thái:</strong> <span class="badge ${bill.status}">${st[bill.status]}</span></p>
  <table><thead><tr><th>Dịch vụ nha khoa</th><th class="num">Phí (VND)</th></tr></thead><tbody>
    <c:forEach var="l" items="${bill.lines}"><tr><td><c:out value="${l.serviceName}"/></td><td class="num"><fmt:formatNumber value="${l.feeAtBilling}" maxFractionDigits="0"/></td></tr></c:forEach>
    <tr class="total"><td>Tổng cộng</td><td class="num">${totalText}</td></tr></tbody></table>
  <c:if test="${not bill.invoiced}"><p class="muted noprint">Xem trước theo mức phí hiện tại. Hóa đơn sẽ được tạo khi bạn bấm In.</p></c:if>
  <h4>Phiếu khám</h4>
  <p><strong>Chẩn đoán:</strong> <c:out value="${bill.examForm.diagnosis}"/></p>
  <p><strong>Ghi chú lâm sàng:</strong> <c:out value="${bill.examForm.clinicalNotes}"/></p>
  <p><strong>Thuốc:</strong> <c:out value="${bill.examForm.medication}"/></p>
</section>
<div class="actions noprint">
  <form method="post" action="${ctx}/invoice" class="inline"><input type="hidden" name="visitId" value="${bill.visit.visitId}"><button class="btn">In hóa đơn và phiếu khám</button></form>
  <form method="post" action="${ctx}/payment" class="inline" onsubmit="return confirm('Xác nhận đã nhận đủ số tiền ${totalText} VND?');">
    <input type="hidden" name="visitId" value="${bill.visit.visitId}">
    <button class="btn warn" ${bill.invoiced and not bill.paid ? '' : 'disabled'}>Ghi nhận thanh toán</button></form>
</div>
<c:if test="${autoPrint}"><script>window.addEventListener('load', function () { window.print(); });</script></c:if>
<%@ include file="_foot.jspf" %>
