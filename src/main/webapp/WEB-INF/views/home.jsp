<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="_head.jspf" %>
<section class="banner">
  <img src="${ctx}/assets/img/banner.jpg" alt="20 năm kiến tạo – Siêu sinh nhật, siêu ưu đãi: giảm tới 50% phí dịch vụ">
  <a class="banner-link" href="${ctx}/consult" aria-label="Đặt lịch khám"></a>
</section>

<c:if test="${empty role or role == 'Patient' or role == 'Dental Assistant'}">
<form method="get" action="${ctx}/consult" class="card quick" data-booking>
  <h3>📅 Đặt lịch khám nhanh</h3>
  <div class="days">
    <c:forEach var="d" items="${days}">
      <label class="day"><input type="radio" name="date" value="${d.value}" required data-m="${d.morningOpen}" data-a="${d.afternoonOpen}" data-label="${d.label}" ${d.open ? '' : 'disabled'}>
        <span><small>${d.dayName}</small><b>${d.shortDate}</b><em><c:if test="${d.today}">Hôm nay</c:if></em></span></label>
    </c:forEach>
  </div>
  <div class="shifts" style="margin-top:12px">
    <c:forEach var="s" items="${shifts}">
      <label class="shift"><input type="radio" name="shift" value="${s.code}" required data-label="${s.label}" data-range="${s.range}">
        <span><i class="ico">${s.icon}</i><span><b>${s.label}</b><small>${s.range}</small></span></span></label>
    </c:forEach>
  </div>
  <div class="actions"><button class="btn">Tiếp tục đặt lịch →</button><span class="muted">Đặt trước tối đa 7 ngày</span></div>
</form>
<div class="steps3">
  <div class="step-card"><div class="n">1</div><div><b>Chọn ngày &amp; ca</b><span>Ca sáng hoặc ca chiều, trong 7 ngày tới.</span></div></div>
  <div class="step-card"><div class="n">2</div><div><b>Điền thông tin</b><span>Họ tên, số điện thoại và dịch vụ cần khám.</span></div></div>
  <div class="step-card"><div class="n">3</div><div><b>Đến khám đúng ca</b><span>Lễ tân check-in, nha sĩ sẽ gọi bạn vào khám.</span></div></div>
</div>
</c:if>

<h2 class="sec-title"><span>✦</span> Dịch vụ tại nha khoa <b class="hola">HOLA</b> <span>✦</span></h2>
<div class="svc-grid">
  <article class="svc-card">
    <img class="svc-art" src="${ctx}/assets/img/svc-implant.jpg" alt="Công nghệ trồng răng Hansafe">
    <div class="svc-label"><div><small>Trồng răng</small><strong>Implant</strong></div>
      <a class="svc-go" href="${ctx}/consult" title="Đặt lịch khám"><img src="${ctx}/assets/img/ico-implant.png" alt="Đặt lịch khám"></a></div>
  </article>
  <article class="svc-card">
    <img class="svc-art" src="${ctx}/assets/img/svc-nieng-rang.jpg" alt="Niềng răng Biosmart">
    <div class="svc-label"><div><small>Dịch vụ</small><strong>Niềng răng</strong></div>
      <a class="svc-go" href="${ctx}/consult" title="Đặt lịch khám"><img src="${ctx}/assets/img/ico-nieng-rang.png" alt="Đặt lịch khám"></a></div>
  </article>
  <article class="svc-card">
    <img class="svc-art" src="${ctx}/assets/img/svc-nho-rang-8.jpg" alt="Công nghệ Piezotome">
    <div class="svc-label"><div><small>Dịch vụ</small><strong>Nhổ răng 8</strong></div>
      <a class="svc-go" href="${ctx}/consult" title="Đặt lịch khám"><img src="${ctx}/assets/img/ico-nho-rang-8.png" alt="Đặt lịch khám"></a></div>
  </article>
  <article class="svc-card">
    <img class="svc-art" src="${ctx}/assets/img/svc-rang-su.jpg" alt="Răng sứ">
    <div class="svc-label"><div><small>Dịch vụ</small><strong>Răng sứ</strong></div>
      <a class="svc-go" href="${ctx}/consult" title="Đặt lịch khám"><img src="${ctx}/assets/img/ico-rang-su.png" alt="Đặt lịch khám"></a></div>
  </article>
  <article class="svc-card">
    <img class="svc-art" src="${ctx}/assets/img/svc-noi-nha.jpg" alt="Điều trị nội nha">
    <div class="svc-label"><div><small>Điều trị</small><strong>Nội nha</strong></div>
      <a class="svc-go" href="${ctx}/consult" title="Đặt lịch khám"><img src="${ctx}/assets/img/ico-noi-nha.png" alt="Đặt lịch khám"></a></div>
  </article>
  <article class="svc-card">
    <img class="svc-art" src="${ctx}/assets/img/svc-tong-quat.jpg" alt="Nha khoa tổng quát">
    <div class="svc-label"><div><small>Nha khoa</small><strong>Tổng quát</strong></div>
      <a class="svc-go" href="${ctx}/consult" title="Đặt lịch khám"><img src="${ctx}/assets/img/ico-tong-quat.png" alt="Đặt lịch khám"></a></div>
  </article>
</div>
<%@ include file="_foot.jspf" %>
