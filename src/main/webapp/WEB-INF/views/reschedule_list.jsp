<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="_head.jspf" %>
<c:set var="sel" value="${sv.selected}"/>
<div class="row">
  <h2>Đổi lịch khám</h2>
  <a class="btn ghost" href="${ctx}/requests">← Quay lại lịch khám</a>
</div>
<p class="muted">Chọn lịch đang <strong>Chờ check-in</strong> để đổi ngày hoặc ca khám. Lịch đã check-in hoặc đã hủy không được đổi. Không thể chọn ca đã kết thúc.</p>
<c:set var="scheduleUrl" value="/reschedule"/>
<%@ include file="_schedule_daytabs.jspf" %>
<form method="get" action="${ctx}/reschedule" class="search reception-search">
  <input type="hidden" name="date" value="${sel.value}">
  <input name="q" value="<c:out value='${q}'/>" placeholder="Tìm mã lịch hẹn hoặc số điện thoại" aria-label="Tìm lịch cần đổi">
  <button class="btn" type="submit">Tìm kiếm</button>
  <c:if test="${sv.searching}"><a class="btn ghost" href="${ctx}/reschedule?date=${sel.value}">Xóa tìm kiếm</a></c:if>
</form>
<c:choose>
  <c:when test="${sv.searching}">
    <section class="card shift-panel">
      <header><h3>Kết quả tìm kiếm <span class="muted">· ${fn:length(sv.results)} lịch</span></h3></header>
      <c:choose>
        <c:when test="${empty sv.results}"><div class="empty">Không tìm thấy lịch đủ điều kiện đổi.</div></c:when>
        <c:otherwise><div class="table-scroll"><table>
          <thead><tr><th>Mã</th><th>Ngày</th><th>Ca</th><th>Họ và tên</th><th>Số điện thoại</th><th>Dịch vụ</th><th>Thao tác</th></tr></thead>
          <tbody><c:forEach var="r" items="${sv.results}"><tr>
            <td><c:out value="${r.requestCode}"/></td><td>${r.apptDateText}</td><td>${sh[r.shift]}</td>
            <td><c:out value="${r.fullName}"/></td><td><c:out value="${r.phone}"/></td>
            <td><c:out value="${r.serviceName}"/></td><td><%@ include file="_reschedule_actions.jspf" %></td>
          </tr></c:forEach></tbody>
        </table></div></c:otherwise>
      </c:choose>
    </section>
  </c:when>
  <c:otherwise>
    <c:forEach var="pn" items="${sv.panels}">
      <section class="card shift-panel ${pn.open ? '' : 'over'}">
        <header><h3>${pn.icon} ${pn.label} <span class="muted">· ${pn.range} · ${fn:length(pn.rows)} lịch chờ check-in</span></h3></header>
        <c:choose>
          <c:when test="${empty pn.rows}"><div class="empty">Không có lịch chờ check-in trong ca này.</div></c:when>
          <c:otherwise><div class="table-scroll"><table>
            <thead><tr><th>Mã</th><th>Họ và tên</th><th>Số điện thoại</th><th>Dịch vụ</th><th>Ngày hiện tại</th><th>Ca hiện tại</th><th>Đổi lịch</th></tr></thead>
            <tbody><c:forEach var="r" items="${pn.rows}"><tr>
              <td><c:out value="${r.requestCode}"/></td><td><c:out value="${r.fullName}"/></td>
              <td><c:out value="${r.phone}"/></td><td><c:out value="${r.serviceName}"/></td>
              <td>${r.apptDateText}</td><td>${sh[r.shift]}</td><td><%@ include file="_reschedule_actions.jspf" %></td>
            </tr></c:forEach></tbody>
          </table></div></c:otherwise>
        </c:choose>
      </section>
    </c:forEach>
  </c:otherwise>
</c:choose>
<c:if test="${not empty sv.overdue and not sv.searching}">
  <section class="card shift-panel overdue">
    <header><h3>Lịch quá hạn chưa check-in <span class="muted">· ${fn:length(sv.overdue)} lịch</span></h3></header>
    <div class="table-scroll"><table>
      <thead><tr><th>Ngày cũ</th><th>Ca cũ</th><th>Mã</th><th>Họ tên</th><th>Số điện thoại</th><th>Dịch vụ</th><th>Đổi lịch</th></tr></thead>
      <tbody><c:forEach var="r" items="${sv.overdue}"><tr>
        <td>${r.apptDateText}</td><td>${sh[r.shift]}</td><td><c:out value="${r.requestCode}"/></td>
        <td><c:out value="${r.fullName}"/></td><td><c:out value="${r.phone}"/></td>
        <td><c:out value="${r.serviceName}"/></td><td><%@ include file="_reschedule_actions.jspf" %></td>
      </tr></c:forEach></tbody>
    </table></div>
  </section>
</c:if>
<%@ include file="_foot.jspf" %>
