<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="_head.jspf" %>
<c:set var="sel" value="${sv.selected}"/>
<div class="row"><h2>Lịch khám</h2><span><a class="btn" href="${ctx}/consult?date=${sel.value}">＋ Đặt lịch khám cho bệnh nhân</a> <a class="btn ghost" href="${ctx}/checkout">Danh sách thanh toán</a></span></div>
<p class="muted">Chọn trạng thái ở phần “Hiển thị” để lọc bệnh nhân. Lịch đã khám xong sẽ chuyển sang mục Đợi thanh toán; đã thanh toán xong sẽ không còn trong danh sách lịch đang xử lý. Chỉ có thể check-in lịch hẹn đúng ngày hôm nay. Để chuyển ngày hoặc ca khám, mở mục Đổi lịch trên thanh điều hướng. Có thể hủy các lịch chưa check-in.</p>
<c:set var="scheduleUrl" value="/requests"/>
<%@ include file="_schedule_daytabs.jspf" %>
<form method="get" action="${ctx}/requests" class="search reception-search">
  <input type="hidden" name="date" value="${sel.value}">
  <input name="q" value="<c:out value='${q}'/>" placeholder="Mã lịch hẹn (LH-…) hoặc số điện thoại" aria-label="Tìm kiếm lịch khám">
  <button class="btn" type="submit">Tìm kiếm</button>
  <label class="reception-filter" for="receptionStatus">Hiển thị
    <select id="receptionStatus" name="status" onchange="this.form.requestSubmit()">
      <option value="ALL" ${statusFilter == 'ALL' ? 'selected' : ''}>Tất cả trạng thái</option>
      <option value="REQUESTED" ${statusFilter == 'REQUESTED' ? 'selected' : ''}>Đợi check-in</option>
      <option value="WAITING" ${statusFilter == 'WAITING' ? 'selected' : ''}>Đang chờ khám</option>
      <option value="IN_PROGRESS" ${statusFilter == 'IN_PROGRESS' ? 'selected' : ''}>Đang khám</option>
      <option value="AWAITING_PAYMENT" ${statusFilter == 'AWAITING_PAYMENT' ? 'selected' : ''}>Đợi thanh toán</option>
    </select>
  </label>
  <c:if test="${sv.searching}"><a class="btn ghost" href="${ctx}/requests?date=${sel.value}&amp;status=${statusFilter}">Xóa tìm kiếm</a></c:if>
</form>

<c:choose>
<c:when test="${sv.searching}">
  <section class="card shift-panel">
    <header><h3>🔎 Kết quả tìm kiếm <span class="muted">· ${fn:length(sv.results)} lịch hẹn (gồm cả đã check-in)</span></h3></header>
    <c:choose><c:when test="${empty sv.results}"><div class="empty">Không tìm thấy lịch hẹn nào.</div></c:when>
    <c:otherwise>
    <div class="table-scroll"><table><thead><tr><th>Mã</th><th>Ngày</th><th>Ca</th><th>Họ và tên</th><th>Số điện thoại</th><th>Dịch vụ</th><th>Trạng thái</th><th>Thao tác</th></tr></thead><tbody>
    <c:forEach var="r" items="${sv.results}"><tr>
      <td><c:out value="${r.requestCode}"/></td><td>${r.apptDateText}</td><td>${sh[r.shift]}</td><td><c:out value="${r.fullName}"/></td><td><c:out value="${r.phone}"/></td><td><c:out value="${r.serviceName}"/></td>
      <td><span class="badge ${r.displayStatus}">${st[r.displayStatus]}</span></td>
      <td><%@ include file="_schedule_actions.jspf" %></td>
    </tr></c:forEach>
    </tbody></table></div>
    </c:otherwise></c:choose>
  </section>
</c:when>
<c:otherwise>
  <c:forEach var="pn" items="${sv.panels}">
  <section class="card shift-panel ${pn.open ? '' : 'over'}">
    <header>
      <h3>${pn.icon} ${pn.label} <span class="muted">· ${pn.range} · ${fn:length(pn.rows)} lịch hẹn</span></h3>
      <c:choose>
        <c:when test="${pn.open}"><a class="btn sm ghost" href="${ctx}/consult?date=${sel.value}&shift=${pn.code}">＋ Đặt lịch ${fn:toLowerCase(pn.label)}</a></c:when>
        <c:otherwise><span class="muted">Ca đã qua</span></c:otherwise>
      </c:choose>
    </header>
    <c:choose>
      <c:when test="${empty pn.rows}"><div class="empty">Chưa có lịch hẹn nào trong ${fn:toLowerCase(pn.label)}.</div></c:when>
      <c:otherwise>
        <div class="table-scroll"><table><thead><tr><th>Mã</th><th>Họ và tên</th><th>Số điện thoại</th><th>Dịch vụ</th><th>Ghi chú</th><th>Trạng thái</th><th>Thao tác</th></tr></thead><tbody>
        <c:forEach var="r" items="${pn.rows}"><tr>
          <td><c:out value="${r.requestCode}"/></td><td><c:out value="${r.fullName}"/></td><td><c:out value="${r.phone}"/></td><td><c:out value="${r.serviceName}"/></td><td><c:out value="${r.note}"/></td>
          <td><span class="badge ${r.displayStatus}">${st[r.displayStatus]}</span></td>
          <td><%@ include file="_schedule_actions.jspf" %></td>
        </tr></c:forEach>
        </tbody></table></div>
      </c:otherwise>
    </c:choose>
  </section>
  </c:forEach>
</c:otherwise>
</c:choose>

<c:if test="${not empty sv.overdue and not sv.searching}">
<section class="card shift-panel overdue">
  <header><h3>⚠️ Lịch quá hạn chưa xử lý <span class="muted">· ${fn:length(sv.overdue)} lịch</span></h3><span class="muted">Lịch cũ không thể check-in; mở mục Đổi lịch để thay đổi ngày/ca</span></header>
  <div class="table-scroll"><table><thead><tr><th>Ngày</th><th>Ca</th><th>Mã</th><th>Họ và tên</th><th>Số điện thoại</th><th>Dịch vụ</th><th>Trạng thái</th><th>Thao tác</th></tr></thead><tbody>
  <c:forEach var="r" items="${sv.overdue}"><tr>
    <td>${r.apptDateText}</td><td>${sh[r.shift]}</td><td><c:out value="${r.requestCode}"/></td><td><c:out value="${r.fullName}"/></td><td><c:out value="${r.phone}"/></td><td><c:out value="${r.serviceName}"/></td>
    <td><span class="badge ${r.displayStatus}">${st[r.displayStatus]}</span></td>
    <td><%@ include file="_schedule_actions.jspf" %></td>
  </tr></c:forEach>
  </tbody></table></div>
</section>
</c:if>
<%@ include file="_foot.jspf" %>
