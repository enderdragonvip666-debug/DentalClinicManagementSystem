<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="_head.jspf" %>
<form method="post" action="${ctx}/password" class="card narrow">
  <h2>Đổi mật khẩu</h2>
  <label>Mật khẩu hiện tại<input type="password" name="current" required></label>
  <label>Mật khẩu mới<input type="password" name="password" minlength="8" required></label>
  <label>Nhập lại mật khẩu mới<input type="password" name="confirm" minlength="8" required></label>
  <button class="btn">Lưu</button>
</form>
<%@ include file="_foot.jspf" %>
