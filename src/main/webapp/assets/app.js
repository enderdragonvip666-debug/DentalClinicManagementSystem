/* Booking dialogs, receptionist rescheduling, exam preview and dentist history. */
(function () {
  'use strict';

  function initBooking(form) {
    if (form.dataset.bookingReady === '1') return;
    form.dataset.bookingReady = '1';
    var days = Array.from(form.querySelectorAll('input[name="date"]'));
    var shifts = Array.from(form.querySelectorAll('input[name="shift"]'));
    function currentDay() { return days.find(function (d) { return d.checked; }); }
    function sync() {
      var d = currentDay();
      shifts.forEach(function (s) {
        var enabled = !d || d.dataset[s.value === 'MORNING' ? 'm' : 'a'] === 'true';
        s.disabled = !enabled;
        if (!enabled) s.checked = false;
      });
    }
    days.forEach(function (d) { d.addEventListener('change', sync); });
    if (!currentDay()) {
      var first = days.find(function (d) { return !d.disabled; });
      if (first) first.checked = true;
    }
    sync();
    if (form.hasAttribute('data-confirm')) {
      form.addEventListener('submit', function (e) {
        // The booking dialog is itself the flow; full-page fallback still confirms.
        if (form.closest('#bookingDlg')) return;
        var d = currentDay(), s = shifts.find(function (x) { return x.checked; });
        if (d && s && !confirm('Xác nhận đặt lịch khám?\n\n• ' + d.dataset.label + '\n• ' + s.dataset.label + ' (' + s.dataset.range + ')')) {
          e.preventDefault();
        }
      });
    }
  }
  document.querySelectorAll('form[data-booking]').forEach(initBooking);

  // GET/POST the existing Servlet, but keep the booking form in a dialog instead of navigating away.
  var bookingDialog = document.getElementById('bookingDlg');
  var bookingBody = document.getElementById('bookingBody');
  var refreshAfterBooking = false;
  function bookingError(message) {
    bookingBody.textContent = '';
    var p = document.createElement('p');
    p.className = 'alert err'; p.textContent = message;
    bookingBody.appendChild(p);
  }
  function bookingHtml(html) {
    bookingBody.innerHTML = html;
    var form = bookingBody.querySelector('form[data-booking]');
    if (form) initBooking(form);
    if (bookingBody.querySelector('.done')) refreshAfterBooking = true;
    bookingBody.scrollTop = 0;
  }
  function loadBooking(url) {
    refreshAfterBooking = false;
    bookingBody.innerHTML = '<p class="muted">Đang mở biểu mẫu đặt lịch khám…</p>';
    if (!bookingDialog.open) bookingDialog.showModal();
    var endpoint = new URL(url, window.location.href);
    endpoint.searchParams.set('modal', '1');
    fetch(endpoint, { credentials: 'same-origin', headers: { 'X-Requested-With': 'XMLHttpRequest' } })
      .then(function (r) { if (!r.ok) throw Error('HTTP ' + r.status); return r.text(); })
      .then(bookingHtml)
      .catch(function () { bookingError('Không thể mở form đặt lịch. Vui lòng thử lại.'); });
  }
  if (bookingDialog) {
    document.addEventListener('click', function (e) {
      var close = e.target.closest('[data-close-booking]');
      if (close && bookingDialog.contains(close)) { bookingDialog.close(); return; }
      var link = e.target.closest('a[href]');
      if (!link || e.defaultPrevented || e.ctrlKey || e.metaKey || e.shiftKey || e.altKey || link.target === '_blank') return;
      var u = new URL(link.href, window.location.href);
      if (u.origin !== window.location.origin || u.pathname !== CTX + '/consult') return;
      e.preventDefault();
      loadBooking(u.href);
    });
    bookingDialog.addEventListener('close', function () {
      if (refreshAfterBooking) window.location.reload();
    });
    bookingBody.addEventListener('submit', function (e) {
      var form = e.target.closest('form[data-booking]');
      if (!form) return;
      e.preventDefault();
      if (!form.reportValidity()) return;
      var submit = form.querySelector('button[type="submit"],button:not([type])');
      if (submit) submit.disabled = true;
      var values = new FormData(form);
      values.set('modal', '1');
      fetch(form.action, { method: 'POST', credentials: 'same-origin', body: new URLSearchParams(values), headers: { 'X-Requested-With': 'XMLHttpRequest' } })
        .then(function (r) { if (!r.ok) throw Error('HTTP ' + r.status); return r.text(); })
        .then(bookingHtml)
        .catch(function () { bookingError('Không thể gửi yêu cầu đặt lịch. Vui lòng kiểm tra kết nối và thử lại.'); });
    });
    document.addEventListener('submit', function (e) {
      var quick = e.target.matches('form.quick[data-booking]') ? e.target : null;
      if (!quick) return;
      e.preventDefault();
      if (!quick.reportValidity()) return;
      var url = new URL(quick.action, window.location.href);
      var values = new FormData(quick);
      url.searchParams.set('date', values.get('date'));
      url.searchParams.set('shift', values.get('shift'));
      loadBooking(url.href);
    });
  }

  // Receptionist can choose a different day as well as the shift; disable finished shifts.
  document.querySelectorAll('form[data-reschedule]').forEach(function (form) {
    var date = form.querySelector('select[name="targetDate"]');
    var shift = form.querySelector('select[name="targetShift"]');
    function sync() {
      var selected = date.options[date.selectedIndex];
      if (!selected || selected.disabled) {
        var first = Array.from(date.options).find(function (o) { return !o.disabled; });
        if (first) { first.selected = true; selected = first; }
      }
      if (!selected) return;
      Array.from(shift.options).forEach(function (o) {
        o.disabled = selected.dataset[o.value === 'MORNING' ? 'm' : 'a'] !== 'true';
      });
      if (shift.selectedOptions[0] && shift.selectedOptions[0].disabled) {
        var available = Array.from(shift.options).find(function (o) { return !o.disabled; });
        if (available) available.selected = true;
      }
    }
    date.addEventListener('change', sync);
    sync();
    form.addEventListener('submit', function (e) {
      var target = date.selectedOptions[0];
      if (!target || target.disabled || shift.selectedOptions[0].disabled) { e.preventDefault(); return; }
      if (!confirm('Đổi lịch khám sang ' + target.textContent.trim() + ' – ' + shift.selectedOptions[0].textContent.trim() + '?')) e.preventDefault();
    });
  });

  // Dentist: first preview locally, then save and close the visit with a single server transaction.
  var examForm = document.querySelector('form[data-exam-form]');
  var examDlg = document.getElementById('examPreview');
  if (examForm && examDlg) {
    var examDetails = document.getElementById('examPreviewDetails');
    function field(label, value) {
      var dt = document.createElement('dt'); dt.textContent = label;
      var dd = document.createElement('dd'); dd.textContent = value || '(Không có)';
      examDetails.appendChild(dt); examDetails.appendChild(dd);
    }
    examForm.addEventListener('submit', function (e) {
      e.preventDefault();
      if (!examForm.reportValidity()) return;
      var services = Array.from(examForm.querySelectorAll('input[name="serviceIds"]:checked'));
      if (!services.length) {
        alert('Vui lòng chọn ít nhất một dịch vụ đã thực hiện.');
        return;
      }
      examDetails.replaceChildren();
      field('Chẩn đoán', examForm.elements.diagnosis.value.trim());
      field('Ghi chú lâm sàng', examForm.elements.clinicalNotes.value.trim());
      field('Dịch vụ đã thực hiện', services.map(function (c) { return c.closest('label').textContent.trim(); }).join(', '));
      field('Hướng dẫn dùng thuốc', examForm.elements.medication.value.trim());
      examDlg.showModal();
    });
    examDlg.querySelectorAll('[data-back-exam]').forEach(function (b) {
      b.addEventListener('click', function () { examDlg.close(); });
    });
    document.getElementById('confirmExam').addEventListener('click', function () {
      // Avoid a second preview and double submission.
      this.disabled = true;
      HTMLFormElement.prototype.submit.call(examForm);
    });
  }

  // Change password directly in a dialog, without navigating to /password.
  var passwordDlg = document.getElementById('passwordDlg');
  var passwordForm = document.getElementById('passwordModalForm');
  if (passwordDlg && passwordForm) {
    var passwordFeedback = document.getElementById('passwordFeedback');
    var passwordSubmit = document.getElementById('saveNewPassword');
    function passwordMessage(message, successful) {
      passwordFeedback.textContent = message;
      passwordFeedback.className = 'alert ' + (successful ? 'ok' : 'err');
      passwordFeedback.hidden = false;
    }
    function resetPasswordDialog() {
      passwordForm.reset();
      passwordForm.hidden = false;
      passwordFeedback.hidden = true;
      passwordFeedback.textContent = '';
      passwordSubmit.disabled = false;
    }
    document.addEventListener('click', function (e) {
      var opener = e.target.closest('[data-open-password]');
      if (!opener || e.defaultPrevented || e.ctrlKey || e.metaKey || e.shiftKey || e.altKey) return;
      e.preventDefault();
      resetPasswordDialog();
      if (!passwordDlg.open) passwordDlg.showModal();
      passwordForm.elements.current.focus();
    });
    passwordDlg.querySelectorAll('[data-close-password]').forEach(function (btn) {
      btn.addEventListener('click', function () { passwordDlg.close(); });
    });
    passwordDlg.addEventListener('close', resetPasswordDialog);
    passwordForm.addEventListener('submit', function (e) {
      e.preventDefault();
      if (!passwordForm.reportValidity()) return;
      if (passwordForm.elements.password.value !== passwordForm.elements.confirm.value) {
        passwordMessage('Mật khẩu nhập lại không khớp.', false);
        passwordForm.elements.confirm.focus();
        return;
      }
      passwordSubmit.disabled = true;
      passwordFeedback.hidden = true;
      var fields = new URLSearchParams(new FormData(passwordForm));
      fields.set('modal', '1');
      fetch(passwordForm.action, {
        method: 'POST', credentials: 'same-origin',
        headers: {'X-Requested-With': 'XMLHttpRequest', 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8'},
        body: fields.toString()
      }).then(function (response) {
        if (response.status === 401 || response.redirected) {
          window.location.assign(CTX + '/login');
          return null;
        }
        return response.text().then(function (message) {
          if (!response.ok) throw new Error(message || 'Không thể đổi mật khẩu.');
          return message;
        });
      }).then(function (message) {
        if (message == null) return;
        passwordMessage(message || 'Đổi mật khẩu thành công.', true);
        passwordForm.reset();
        passwordForm.hidden = true;
      }).catch(function (error) {
        passwordMessage(error.message || 'Không thể kết nối máy chủ. Vui lòng thử lại.', false);
      }).finally(function () { passwordSubmit.disabled = false; });
    });
  }

  window.openHistory = function (btn) {
    var dlg = document.getElementById('histDlg'), body = document.getElementById('histBody');
    document.getElementById('histTitle').textContent = 'Lịch sử khám & tiền sử — ' + btn.dataset.name;
    body.innerHTML = '<p class="muted">Đang tải…</p>';
    dlg.showModal();
    fetch(CTX + '/patient-history?visitId=' + encodeURIComponent(btn.dataset.visit), { credentials: 'same-origin' })
      .then(function (r) { return r.text(); })
      .then(function (t) { body.innerHTML = t; })
      .catch(function () { body.innerHTML = '<p class="muted">Không tải được dữ liệu. Vui lòng thử lại.</p>'; });
  };
  window.saveHistory = function (form) {
    var panel = form.closest('.hist-panel');
    fetch(form.action, { method: 'POST', credentials: 'same-origin', body: new URLSearchParams(new FormData(form)) })
      .then(function (r) { return r.text(); })
      .then(function (t) { panel.outerHTML = t; });
    return false;
  };
})();
