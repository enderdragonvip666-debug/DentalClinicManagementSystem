-- DCMS: đặt lịch khám theo ngày + ca (sáng/chiều) và tiền sử bệnh.
-- Chạy trên database dcmsv2. An toàn khi chạy nhiều lần. (Ứng dụng cũng tự chạy các lệnh này khi khởi động.)
IF COL_LENGTH('consultation_requests', 'appt_date')  IS NULL ALTER TABLE consultation_requests ADD appt_date DATE NULL;
IF COL_LENGTH('consultation_requests', 'appt_shift') IS NULL ALTER TABLE consultation_requests ADD appt_shift VARCHAR(10) NULL;  -- MORNING | AFTERNOON
IF COL_LENGTH('patients', 'medical_history')         IS NULL ALTER TABLE patients ADD medical_history NVARCHAR(1000) NULL;
GO
-- Lịch cũ chưa có ngày/ca: lấy ngày tạo và ca sáng
UPDATE consultation_requests SET appt_date  = CAST(created_at AS DATE) WHERE appt_date  IS NULL;
UPDATE consultation_requests SET appt_shift = 'MORNING'                WHERE appt_shift IS NULL;
GO
