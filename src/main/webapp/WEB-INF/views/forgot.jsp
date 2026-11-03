<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="_head.jspf" %>
<c:choose>
<c:when test="${not empty token}">
<form method="post" action="${ctx}/reset" class="card narrow">
  <h2>Đặt mật khẩu mới</h2>
  <input type="hidden" name="token" value="<c:out value='${token}'/>">
  <label>Mật khẩu mới<input type="password" name="password" minlength="8" required></label>
  <label>Nhập lại mật khẩu mới<input type="password" name="confirm" minlength="8" required></label>
  <button class="btn">Lưu mật khẩu mới</button>
</form>
</c:when>
<c:otherwise>
<form method="post" action="${ctx}/forgot" class="card narrow">
  <h2>Quên mật khẩu</h2>
  <label>Số điện thoại<input name="phone" pattern="0[0-9]{9,10}" required></label>
  <button class="btn">Gửi email đặt lại mật khẩu</button>
  <p class="muted"><a href="${ctx}/login">Quay lại đăng nhập</a></p>
</form>
</c:otherwise></c:choose>
<%@ include file="_foot.jspf" %>
