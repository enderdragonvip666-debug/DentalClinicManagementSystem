<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="_head.jspf" %>
<div class="row"><h2>Danh sách bệnh nhân</h2></div>
<div class="daytabs doctor-daytabs" aria-label="Chọn ngày xem danh sách bệnh nhân">
  <c:forEach var="d" items="${doctorDays}">
    <a class="daytab ${d.value == selectedDate ? 'on' : ''}" href="${ctx}/patients?date=${d.value}" ${d.value == selectedDate ? 'aria-current="date"' : ''}>
      <small>${d.dayName}</small>
      <b>${d.shortDate}</b>
      <span class="cnt">${d.morningCount + d.afternoonCount} ca khám</span>
      <c:if test="${d.today}"><span class="tdy">Hôm nay</span></c:if>
    </a>
  </c:forEach>
</div>
<form method="get" action="${ctx}/patients" class="search">
  <input type="hidden" name="date" value="${selectedDate}">
  <input name="q" value="<c:out value='${q}'/>" placeholder="Họ tên hoặc số điện thoại" aria-label="Tìm bệnh nhân theo họ tên hoặc số điện thoại">
  <button class="btn">Tìm kiếm</button>
  <c:if test="${not empty q}"><a class="btn ghost" href="${ctx}/patients?date=${selectedDate}">Xóa lọc</a></c:if>
</form>
<div class="table-scroll doctor-patient-table"><table>
  <thead><tr><th>Bệnh nhân</th><th>Số điện thoại</th><th>Check-in lúc</th><th>Trạng thái</th><th></th></tr></thead>
  <tbody>
  <c:forEach var="v" items="${visits}"><tr>
    <td><c:out value="${v.patientName}"/></td>
    <td><c:out value="${v.patientPhone}"/></td>
    <td><c:choose><c:when test="${not empty v.createdAt}"><fmt:formatDate value="${v.createdAt}" pattern="HH:mm dd/MM"/></c:when><c:otherwise><span class="muted">Chưa check-in</span></c:otherwise></c:choose></td>
    <td><span class="badge ${v.status}">${st[v.status]}</span></td>
    <td><div class="acts">
      <c:choose>
        <c:when test="${v.canOpen}">
          <button type="button" class="btn sm ghost" data-visit="${v.visitId}" data-name="<c:out value='${v.patientName}'/>" onclick="openHistory(this)">📋 Lịch sử &amp; tiền sử</button>
          <a class="btn sm" href="${ctx}/patient?visitId=${v.visitId}">${v.status == 'WAITING' ? 'Bắt đầu khám' : 'Tiếp tục khám'}</a>
        </c:when>
        <c:when test="${v.status == 'REQUESTED'}"><span class="muted">Chờ lễ tân check-in</span></c:when>
        <c:otherwise><span class="muted">Chưa đến lượt khám</span></c:otherwise>
      </c:choose>
    </div></td>
  </tr></c:forEach>
  <c:if test="${empty visits}"><tr><td colspan="5" class="muted">Không có ca khám phù hợp trong ngày này.</td></tr></c:if>
  </tbody>
</table></div>
<p class="muted">Bệnh nhân đã khám xong sẽ không còn xuất hiện trong danh sách này (lễ tân vẫn xem được để thanh toán). Bấm “Bắt đầu khám” khi bệnh nhân đã check-in: trạng thái chuyển từ Đang chờ sang Đang khám. Bấm “Lịch sử &amp; tiền sử” chỉ để xem thông tin.</p>
<dialog id="histDlg" class="dlg">
  <div class="dlg-head"><strong id="histTitle"></strong><button type="button" class="btn sm ghost" onclick="document.getElementById('histDlg').close()">✕ Đóng</button></div>
  <div class="dlg-body" id="histBody"></div>
</dialog>
<%@ include file="_foot.jspf" %>
