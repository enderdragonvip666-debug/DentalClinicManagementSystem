<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="_head.jspf" %>
<div class="row admin-list-heading">
  <h2>Quản lý người dùng</h2>
  <div class="admin-create-actions" aria-label="Thao tác thêm mới">
    <a class="btn" href="${ctx}/admin/users/new">＋ Thêm người dùng</a>
    <a class="btn" href="${ctx}/admin/services/new">＋ Thêm dịch vụ</a>
  </div>
</div>
<form method="get" action="${ctx}/admin/users" class="search">
  <input name="q" value="<c:out value='${q}'/>" placeholder="Họ tên hoặc số điện thoại">
  <button class="btn">Tìm kiếm</button>
</form>
<div class="table-scroll admin-users-table"><table>
  <thead><tr><th>Họ và tên</th><th>Số điện thoại</th><th>Vai trò</th><th>Trạng thái</th><th>Thao tác</th></tr></thead>
  <tbody><c:forEach var="u" items="${users}"><tr>
    <td><c:out value="${u.fullName}"/></td><td><c:out value="${u.phone}"/></td>
    <td>${rl[u.roleName]}</td><td><span class="badge ${u.status}">${st[u.status]}</span></td>
    <td><button class="btn sm ghost" type="button" data-admin-edit
                data-edit-url="${ctx}/admin/users?do=edit&amp;id=${u.accountId}&amp;modal=1"
                data-edit-dialog="userEditDlg">Sửa</button></td>
  </tr></c:forEach>
  <c:if test="${empty users}"><tr><td colspan="5" class="muted">Không tìm thấy kết quả.</td></tr></c:if></tbody>
</table></div>
<dialog id="userEditDlg" class="dlg admin-edit-dialog" aria-labelledby="userEditTitle">
  <div class="dlg-head"><strong id="userEditTitle">Sửa thông tin người dùng</strong>
    <button class="btn sm ghost" type="button" data-close-admin-edit>✕ Đóng</button></div>
  <div class="dlg-body" data-admin-edit-body></div>
</dialog>
<c:if test="${not empty openEditId}">
  <div data-auto-admin-edit-url="${ctx}/admin/users?do=edit&amp;id=${openEditId}&amp;modal=1" data-auto-admin-dialog="userEditDlg" hidden></div>
</c:if>
<%@ include file="_foot.jspf" %>
