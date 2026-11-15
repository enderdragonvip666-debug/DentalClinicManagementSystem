<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="_head.jspf" %>
<div class="row admin-list-heading"><h2>Quản lý dịch vụ nha khoa</h2>
  <div class="admin-create-actions" aria-label="Thao tác thêm mới">
    <a class="btn" href="${ctx}/admin/users/new">＋ Thêm người dùng</a>
    <a class="btn" href="${ctx}/admin/services/new">＋ Thêm dịch vụ</a>
  </div>
</div>
<div class="table-scroll admin-services-table"><table>
  <thead><tr><th>Tên dịch vụ</th><th class="num">Phí (VND)</th><th>Trạng thái</th><th>Thao tác</th></tr></thead>
  <tbody><c:forEach var="s" items="${services}"><tr>
    <td><c:out value="${s.name}"/></td>
    <td class="num"><fmt:formatNumber value="${s.fee}" maxFractionDigits="0"/></td>
    <td><span class="badge ${s.status}">${st[s.status]}</span></td>
    <td><button class="btn sm ghost" type="button" data-admin-edit
                data-edit-url="${ctx}/admin/services?do=edit&amp;id=${s.serviceId}&amp;modal=1"
                data-edit-dialog="serviceEditDlg">Sửa</button></td>
  </tr></c:forEach>
  <c:if test="${empty services}"><tr><td colspan="4" class="muted">Không tìm thấy kết quả.</td></tr></c:if></tbody>
</table></div>
<dialog id="serviceEditDlg" class="dlg admin-edit-dialog" aria-labelledby="serviceEditTitle">
  <div class="dlg-head"><strong id="serviceEditTitle">Sửa dịch vụ nha khoa</strong>
    <button class="btn sm ghost" type="button" data-close-admin-edit>✕ Đóng</button></div>
  <div class="dlg-body" data-admin-edit-body></div>
</dialog>
<c:if test="${not empty openEditId}">
  <div data-auto-admin-edit-url="${ctx}/admin/services?do=edit&amp;id=${openEditId}&amp;modal=1" data-auto-admin-dialog="serviceEditDlg" hidden></div>
</c:if>
<%@ include file="_foot.jspf" %>
