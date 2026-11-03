<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="_head.jspf" %>
<h2>Lịch sử điều trị</h2>
<c:if test="${role == 'Dental Assistant'}"><form method="get" class="search"><input name="q" value="<c:out value='${q}'/>" placeholder="Tìm bệnh nhân theo họ tên hoặc số điện thoại"><button class="btn">Tìm kiếm</button></form>
<c:if test="${role == 'Dental Assistant' and empty q}"><p class="muted">Đang hiển thị 100 lượt khám gần nhất. Nhập họ tên hoặc số điện thoại để tìm một bệnh nhân.</p></c:if></c:if>
<table><thead><tr><th>Lượt khám</th><c:if test="${role == 'Dental Assistant'}"><th>Bệnh nhân</th></c:if><th>Ngày</th><th>Chẩn đoán</th><th>Dịch vụ đã thực hiện</th><th>Thuốc</th></tr></thead><tbody>
<c:forEach var="v" items="${visits}"><tr><td>V${v.visitId}</td>
  <c:if test="${role == 'Dental Assistant'}"><td><c:out value="${v.patientName}"/><br><span class="muted"><c:out value="${v.patientPhone}"/></span></td></c:if>
  <td><fmt:formatDate value="${v.createdAt}" pattern="dd/MM/yyyy"/></td>
  <td><c:out value="${v.diagnosis}"/></td><td><c:out value="${v.services}"/></td><td><c:out value="${v.medication}"/></td></tr></c:forEach>
<c:if test="${empty visits}"><tr><td colspan="6" class="muted">Không tìm thấy kết quả.</td></tr></c:if>
</tbody></table>
<%@ include file="_foot.jspf" %>
