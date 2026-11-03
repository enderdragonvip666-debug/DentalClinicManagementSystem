<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="_head.jspf" %>
<section class="auth-layout" aria-label="Đăng nhập Dental Clinic">
  <div class="auth-card card">
    <div class="auth-heading"><span class="auth-icon" aria-hidden="true">🦷</span><div><h1>Đăng nhập</h1><p class="muted">Chào mừng bạn quay lại Nha khoa HOLA</p></div></div>
    <form method="post" action="${ctx}/login" class="auth-form">
      <label>Số điện thoại
        <input type="tel" name="phone" value="<c:out value='${param.phone}'/>" autocomplete="username" inputmode="tel" pattern="0[0-9]{9,10}" maxlength="11" required autofocus>
      </label>
      <label>Mật khẩu
        <input type="password" name="password" autocomplete="current-password" required>
      </label>
      <div class="auth-tools"><a href="${ctx}/forgot">Quên mật khẩu?</a></div>
      <button type="submit" class="btn auth-submit">Đăng nhập</button>
      <p class="auth-switch muted">Chưa có tài khoản? <a href="${ctx}/signup">Đăng ký ngay</a></p>
    </form>
  </div>
  <aside class="auth-visual" aria-label="Giới thiệu Nha khoa HOLA">
    <div class="auth-intro-title"><span class="auth-title-line" aria-hidden="true"></span><span class="auth-sparkle" aria-hidden="true">✦</span><h2>NHA KHOA HOLA</h2><span class="auth-sparkle" aria-hidden="true">✦</span><span class="auth-title-line" aria-hidden="true"></span></div>
    <img class="auth-clinic-photos" src="${ctx}/assets/img/hola-4-photos.jpg" alt="Bốn hình ảnh về quá trình điều trị nha khoa, mô phỏng răng và thiết bị phòng khám" loading="eager">
    <div class="auth-description">
      <p>Chào mừng bạn đến với <strong>Nha khoa HOLA</strong> — nơi quan tâm đến sức khỏe răng miệng và nụ cười của bạn.</p>
      <p>Đặt lịch khám thuận tiện, theo dõi thông tin điều trị và quản lý lịch hẹn ngay trên hệ thống. HOLA mong muốn mang đến trải nghiệm chăm sóc nha khoa rõ ràng, chu đáo và dễ dàng hơn cho mỗi bệnh nhân.</p>
    </div>
  </aside>
</section>
<%@ include file="_foot.jspf" %>
