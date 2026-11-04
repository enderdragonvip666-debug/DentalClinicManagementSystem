<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="_head.jspf" %>
<h2>Quản lý dịch vụ nha khoa</h2>
<div class="table-scroll admin-services-table"><table><thead><tr><th>Tên dịch vụ</th><th class="num">Phí (VND)</th><th>Trạng thái</th><th></th></tr></thead><tbody>
<c:forEach var="s" items="${services}"><tr><td><c:out value="${s.name}"/></td><td class="num"><fmt:formatNumber value="${s.fee}" maxFractionDigits="0"/></td>
  <td><span class="badge ${s.status}">${st[s.status]}</span></td><td><a class="btn sm ghost" href="${ctx}/admin/services?do=edit&amp;id=${s.serviceId}#admin-service-form">Sửa</a></td></tr></c:forEach>
<c:if test="${empty services}"><tr><td colspan="4" class="muted">Không tìm thấy kết quả.</td></tr></c:if>
</tbody></table></div>
<section id="admin-service-form" class="admin-form-shell">
  <form method="post" action="${ctx}/admin/services" class="admin-editor card" autocomplete="off">
    <div class="admin-editor-heading">
      <div class="admin-editor-icon" aria-hidden="true">${empty edit ? '🦷' : '✏️'}</div>
      <div>
        <h3>${empty edit ? 'Thêm dịch vụ nha khoa' : 'Sửa dịch vụ nha khoa'}</h3>
        <p class="muted">${empty edit ? 'Khai báo dịch vụ mới để sử dụng khi đặt lịch và khám.' : 'Điều chỉnh thông tin dịch vụ đang có.'}</p>
      </div>
    </div>
    <input type="hidden" name="serviceId" value="${empty edit ? 0 : edit.serviceId}">
    <div class="admin-form-grid">
      <label class="admin-field-wide">Tên dịch vụ (không trùng, tối đa 100 ký tự)
        <input name="name" maxlength="100" required value="<c:out value='${edit.name}'/>">
      </label>
      <label>Phí (VND, không âm)
        <input name="fee" type="number" min="0" step="1" required value="<fmt:formatNumber value='${edit.fee}' groupingUsed='false' maxFractionDigits='0'/>">
      </label>
      <label>Trạng thái
        <select name="status"><option value="ACTIVE" ${edit.status == 'ACTIVE' || empty edit ? 'selected' : ''}>Hoạt động</option><option value="INACTIVE" ${edit.status == 'INACTIVE' ? 'selected' : ''}>Ngừng hoạt động</option></select>
      </label>
    </div>
    <c:if test="${not empty edit}"><p class="note">Thay đổi phí không ảnh hưởng đến các hóa đơn đã tạo.</p></c:if>
    <div class="admin-editor-actions">
      <button type="submit" class="btn">${empty edit ? 'Thêm dịch vụ' : 'Lưu thay đổi'}</button>
      <c:if test="${not empty edit}"><a class="btn ghost" href="${ctx}/admin/services">Hủy</a></c:if>
    </div>
    <p class="admin-form-tip">Dịch vụ đang hoạt động sẽ hiển thị trong danh sách để bệnh nhân đặt lịch khám.</p>
  </form>
</section>
<%@ include file="_foot.jspf" %>
