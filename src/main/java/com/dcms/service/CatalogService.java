package com.dcms.service;

import com.dcms.dao.DentalServiceDAO;
import com.dcms.model.DentalService;
import com.dcms.util.*;
import java.math.BigDecimal;
import java.util.List;

/** UC07, UC11-UC14: public service list and the Administrator's catalog maintenance. */
public class CatalogService {
    public List<DentalService> listActive() { return DB.read(c -> new DentalServiceDAO(c).listActive()); }
    public List<DentalService> listAll() { return DB.read(c -> new DentalServiceDAO(c).listAll()); }
    public DentalService find(long id) { return DB.read(c -> new DentalServiceDAO(c).findById(id)); }

    /** id = 0 creates. DBR-26: unique name, fee >= 0. A fee change never touches existing invoice_items (DBR-19). */
    public void save(long id, String name, String feeText, String status) {
        name = Validator.trim(name);
        Validator.required(name, "Tên dịch vụ"); Validator.max(name, 100, "Tên dịch vụ");
        BigDecimal fee;
        try { fee = new BigDecimal(Validator.blank(feeText) ? "" : feeText.trim()); } catch (NumberFormatException e) { throw new BizException("Phí: " + Msg.MSG02); }
        if (fee.signum() < 0) throw new BizException(Msg.MSG36);
        if (fee.scale() > 2 || fee.precision() - fee.scale() > 10) throw new BizException("Phí dịch vụ không phải số tiền hợp lệ.");
        if (!"ACTIVE".equals(status) && !"INACTIVE".equals(status)) throw new BizException("Vui lòng chọn trạng thái.");
        final String n = name; final BigDecimal f = fee;
        DB.tx(c -> {
            DentalServiceDAO d = new DentalServiceDAO(c);
            if (d.nameExists(n, id)) throw new BizException(Msg.MSG35);
            if (id == 0) d.insert(n, f, status); else d.update(id, n, f, status);
            return null;
        });
    }
}
