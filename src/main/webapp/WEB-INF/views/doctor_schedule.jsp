<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="_head.jspf" %>
<c:set var="sel" value="${sv.selected}"/>
<div class="row">
  <h2>Lịch khám theo ngày</h2>
  <a class="btn ghost" href="${ctx}/patients">Danh sách bệnh nhân đang chờ / đang khám</a>
</div>
<p class="muted">Xem lịch đặt khám chung của phòng khám theo ngày và ca. Đây là màn hình xem lịch, không có thao tác check-in, đổi lịch hoặc hủy lịch. Bệnh nhân được phân công cho bác sĩ khi lễ tân check-in.</p>
<c:set var="scheduleUrl" value="/doctor-schedule"/>
<%@ include file="_schedule_daytabs.jspf" %>
<form method="get" action="${ctx}/doctor-schedule" class="search">
  <input type="hidden" name="date" value="${sel.value}">
  <input name="q" value="<c:out value='${q}'/>" placeholder="Tìm theo mã lịch hẹn (LH-…) hoặc số điện thoại">
  <button class="btn">Tìm kiếm</button>
  <c:if test="${sv.searching}"><a class="btn ghost" href="${ctx}/doctor-schedule?date=${sel.value}">Xóa tìm kiếm</a></c:if>
</form>
<c:choose>
<c:when test="${sv.searching}">
  <section class="card shift-panel">
    <header><h3>🔎 Kết quả tìm kiếm <span class="muted">· ${fn:length(sv.results)} lịch hẹn</span></h3></header>
    <c:choose>
    <c:when test="${empty sv.results}"><div class="empty">Không tìm thấy lịch hẹn nào.</div></c:when>
    <c:otherwise>
      <div class="table-scroll"><table class="doctor-schedule-table">
        <thead><tr><th>Mã lịch hẹn</th><th>Ngày khám</th><th>Ca</th><th>Bệnh nhân</th><th>Số điện thoại</th><th>Dịch vụ</th><th>Trạng thái</th></tr></thead>
        <tbody><c:forEach var="r" items="${sv.results}"><tr>
          <td><c:out value="${r.requestCode}"/></td><td>${r.apptDateText}</td><td>${sh[r.shift]}</td>
          <td><c:out value="${r.fullName}"/></td><td><c:out value="${r.phone}"/></td>
          <td><c:out value="${r.serviceName}"/></td><td><span class="badge ${r.displayStatus}">${st[r.displayStatus]}</span></td>
        </tr></c:forEach></tbody>
      </table></div>
    </c:otherwise>
    </c:choose>
  </section>
</c:when>
<c:otherwise>
  <c:forEach var="pn" items="${sv.panels}">
    <section class="card shift-panel">
      <header><h3>${pn.icon} ${pn.label} <span class="muted">· ${pn.range} · ${fn:length(pn.rows)} lịch hẹn</span></h3></header>
      <c:choose>
        <c:when test="${empty pn.rows}"><div class="empty">Chưa có lịch hẹn nào trong ${fn:toLowerCase(pn.label)}.</div></c:when>
        <c:otherwise>
          <div class="table-scroll"><table class="doctor-schedule-table">
            <thead><tr><th>Mã lịch hẹn</th><th>Bệnh nhân</th><th>Số điện thoại</th><th>Dịch vụ</th><th>Ghi chú</th><th>Trạng thái</th></tr></thead>
            <tbody><c:forEach var="r" items="${pn.rows}"><tr>
              <td><c:out value="${r.requestCode}"/></td>
              <td><c:out value="${r.fullName}"/></td><td><c:out value="${r.phone}"/></td>
              <td><c:out value="${r.serviceName}"/></td><td><c:out value="${r.note}"/></td>
              <td><span class="badge ${r.displayStatus}">${st[r.displayStatus]}</span></td>
            </tr></c:forEach></tbody>
          </table></div>
        </c:otherwise>
      </c:choose>
    </section>
  </c:forEach>
</c:otherwise>
</c:choose>
<%@ include file="_foot.jspf" %>
