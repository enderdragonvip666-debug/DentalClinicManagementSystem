<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="vst" value="${{'AWAITING_PAYMENT':'Chờ thanh toán','COMPLETED':'Hoàn thành'}}"/>
<div class="hist-panel">
<c:if test="${not empty panelOk}"><div class="alert ok"><c:out value="${panelOk}"/></div></c:if>
<c:if test="${not empty panelErr}"><div class="alert err"><c:out value="${panelErr}"/></div></c:if>
<c:if test="${not empty visit}">
  <h3>🩺 Tiền sử bệnh</h3>
  <form method="post" action="${ctx}/patient-history" onsubmit="return saveHistory(this)">
    <input type="hidden" name="visitId" value="${visit.visitId}">
    <textarea name="medicalHistory" rows="3" maxlength="1000" placeholder="Dị ứng thuốc, bệnh nền, thuốc đang dùng, điều trị răng trước đây… (tối đa 1000 ký tự)"><c:out value="${medHistory}"/></textarea>
    <div class="actions"><button class="btn sm">Lưu tiền sử</button><c:if test="${empty medHistory}"><span class="muted">Chưa có tiền sử được ghi nhận.</span></c:if></div>
  </form>

  <h3>📋 Lịch sử đã khám</h3>
  <c:choose>
    <c:when test="${empty history}"><div class="empty-box">Bệnh nhân chưa từng khám tại phòng khám — đây là lần khám đầu tiên.</div></c:when>
    <c:otherwise>
      <c:forEach var="h" items="${history}">
        <div class="visit-card">
          <header><strong>Lượt khám V${h.visitId} · <fmt:formatDate value="${h.createdAt}" pattern="dd/MM/yyyy"/></strong><span class="badge ${h.status}">${vst[h.status]}</span></header>
          <p><strong>Chẩn đoán:</strong> <c:out value="${h.diagnosis}"/></p>
          <c:if test="${not empty h.services}"><p><strong>Dịch vụ đã thực hiện:</strong> <c:out value="${h.services}"/></p></c:if>
          <c:if test="${not empty h.clinicalNotes}"><p><strong>Ghi chú lâm sàng:</strong> <c:out value="${h.clinicalNotes}"/></p></c:if>
          <c:if test="${not empty h.medication}"><p><strong>Thuốc:</strong> <c:out value="${h.medication}"/></p></c:if>
        </div>
      </c:forEach>
    </c:otherwise>
  </c:choose>
</c:if>
</div>
