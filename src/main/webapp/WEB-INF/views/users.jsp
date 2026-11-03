<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="_head.jspf" %>
<h2>Quản lý người dùng</h2>
<form method="get" class="search"><input name="q" value="<c:out value='${q}'/>" placeholder="Họ tên hoặc số điện thoại"><button class="btn">Tìm kiếm</button></form>
<div class="table-scroll admin-users-table"><table><thead><tr><th>Họ và tên</th><th>Số điện thoại</th><th>Vai trò</th><th>Trạng thái</th><th></th></tr></thead><tbody>
<c:forEach var="u" items="${users}"><tr><td><c:out value="${u.fullName}"/></td><td><c:out value="${u.phone}"/></td><td>${rl[u.roleName]}</td>
  <td><span class="badge ${u.status}">${st[u.status]}</span></td><td><a class="btn sm ghost" href="${ctx}/admin/users?do=edit&amp;id=${u.accountId}#admin-account-form">Sửa</a></td></tr></c:forEach>
<c:if test="${empty users}"><tr><td colspan="5" class="muted">Không tìm thấy kết quả.</td></tr></c:if>
</tbody></table></div>

<c:set var="lastAdmin" value="${not empty edit and edit.roleName == 'Administrator' and edit.status == 'ACTIVE' and activeAdminCount le 1}"/>
<section id="admin-account-form" class="admin-form-shell">
  <form method="post" action="${ctx}/admin/users" class="admin-editor card" autocomplete="off">
    <div class="admin-editor-heading">
      <div class="admin-editor-icon" aria-hidden="true">${empty edit ? '➕' : '✏️'}</div>
      <div>
        <h3>${empty edit ? 'Tạo tài khoản mới' : 'Sửa thông tin tài khoản'}</h3>
        <p class="muted">${empty edit ? 'Thêm người dùng và phân quyền truy cập hệ thống.' : 'Cập nhật thông tin, vai trò và trạng thái người dùng.'}</p>
      </div>
    </div>
    <c:if test="${lastAdmin}">
      <div class="admin-role-warning" role="note">
        <strong>Quản trị viên duy nhất đang hoạt động</strong>
        <p>Bạn vẫn có thể sửa thông tin, nhưng không được đổi vai trò hoặc khóa tài khoản này. Hãy tạo thêm một quản trị viên hoạt động trước.</p>
      </div>
    </c:if>
    <input type="hidden" name="accountId" value="${empty edit ? 0 : edit.accountId}">
    <div class="admin-form-grid">
      <label>Họ và tên<input name="fullName" autocomplete="off" maxlength="150" required value="<c:out value='${edit.fullName}'/>"></label>
      <label>Số điện thoại<input name="phone" type="tel" inputmode="tel" autocomplete="off" pattern="0[0-9]{9,10}" required ${empty edit ? '' : 'readonly'} value="<c:out value='${edit.phone}'/>"></label>
      <label>Email (không bắt buộc)<input name="email" type="email" autocomplete="off" maxlength="150" value="<c:out value='${edit.email}'/>"></label>
      <label>${empty edit ? 'Mật khẩu (ít nhất 8 ký tự)' : 'Mật khẩu mới (để trống nếu giữ nguyên)'}
        <input name="password" type="password" autocomplete="new-password" minlength="8" ${empty edit ? 'required' : ''}>
      </label>
      <label>Vai trò
        <c:if test="${lastAdmin}"><input type="hidden" name="role" value="Administrator"></c:if>
        <select name="role" ${lastAdmin ? 'disabled' : ''}>
          <c:forEach var="r" items="${roles}"><option value="${r.roleName}" ${edit.roleName == r.roleName ? 'selected' : ''}>${rl[r.roleName]}</option></c:forEach>
        </select>
      </label>
      <label>Trạng thái
        <c:if test="${lastAdmin}"><input type="hidden" name="status" value="ACTIVE"></c:if>
        <select name="status" ${lastAdmin ? 'disabled' : ''}>
          <c:forEach var="s" items="${statuses}"><option value="${s}" ${(empty edit ? 'ACTIVE' : edit.status) == s ? 'selected' : ''}>${st[s]}</option></c:forEach>
        </select>
      </label>
    </div>
    <div class="admin-editor-actions">
      <button type="submit" class="btn">${empty edit ? 'Tạo tài khoản' : 'Lưu thay đổi'}</button>
      <c:if test="${not empty edit}"><a class="btn ghost" href="${ctx}/admin/users">Hủy</a></c:if>
    </div>
    <p class="admin-form-tip">Hệ thống phải luôn có ít nhất một tài khoản <strong>Quản trị viên</strong> đang hoạt động.</p>
  </form>
</section>
<%@ include file="_foot.jspf" %>
