<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="_head.jspf" %>
<div class="row"><h2>Phiếu khám — <c:out value="${visit.patientName}"/></h2><a class="btn ghost" href="${ctx}/patients">← Danh sách bệnh nhân</a></div>
<p class="muted">Nhập thông tin, bấm Tạo phiếu để kiểm tra lại. Dữ liệu chỉ được lưu khi bạn bấm Xác nhận trong cửa sổ xem trước.</p>
<form method="post" action="${ctx}/examform" class="card" id="examForm" data-exam-form>
  <input type="hidden" name="visitId" value="${visit.visitId}">
  <input type="hidden" name="action" value="create">
    <label>Chẩn đoán * (tối đa 1000 ký tự)<textarea name="diagnosis" rows="3" maxlength="1000" required><c:out value="${form.diagnosis}"/></textarea></label>
  <label>Ghi chú lâm sàng (tối đa 2000 ký tự)<textarea name="clinicalNotes" rows="4" maxlength="2000"><c:out value="${form.clinicalNotes}"/></textarea></label>
  <fieldset><legend>Dịch vụ nha khoa đã thực hiện * (chọn ít nhất một)</legend>
    <div class="checks"><c:forEach var="s" items="${services}">
      <label class="check"><input type="checkbox" name="serviceIds" value="${s.serviceId}" ${not empty form.serviceIds and form.serviceIds.contains(s.serviceId) ? 'checked' : ''}> <c:out value="${s.name}"/></label>
    </c:forEach></div>
  </fieldset>
  <label>Hướng dẫn dùng thuốc (tối đa 1000 ký tự)
    <textarea name="medication"
              rows="3"
              maxlength="1000"><c:out value="${form.medication}"/></textarea>
</label>
  <div class="actions"><button type="submit" class="btn">Tạo phiếu</button></div>
</form>
<dialog id="examPreview" class="dlg exam-preview" aria-label="Xem trước phiếu khám">
  <div class="dlg-head"><strong>📋 Xem lại thông tin phiếu khám</strong><button type="button" class="btn sm ghost" data-back-exam>✕</button></div>
  <div class="dlg-body"><p class="muted">Hãy kiểm tra toàn bộ thông tin trước khi xác nhận. Sau khi xác nhận, buổi khám sẽ chuyển sang chờ thanh toán.</p>
    <dl id="examPreviewDetails" class="preview-details"></dl>
    <div class="actions"><button type="button" class="btn ghost" data-back-exam>← Quay lại</button><button type="button" class="btn" id="confirmExam">Xác nhận</button></div>
  </div>
</dialog>
<%@ include file="_foot.jspf" %>
