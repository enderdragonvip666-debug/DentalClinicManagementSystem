package com.dcms.util;

public final class Validator {
    private Validator() {}
    public static boolean phone(String p) { return p != null && p.matches("0\\d{9,10}"); }
    public static boolean blank(String s) { return s == null || s.trim().isEmpty(); }
    public static String trim(String s) { return s == null ? null : s.trim(); }
    public static void required(String v, String label) {
        if (blank(v)) throw new BizException(label + ": " + Msg.MSG02);
    }
    public static void max(String v, int n, String label) {
        if (v != null && v.length() > n) throw new BizException(label + ": " + String.format(Msg.MSG03, n));
    }
    public static void phoneOk(String p) { if (!phone(p)) throw new BizException(Msg.MSG04); }
    public static void passwordOk(String p, String confirm) {
        if (p == null || p.length() < 8) throw new BizException(Msg.MSG05);
        if (confirm != null && !p.equals(confirm)) throw new BizException(Msg.MSG06);
    }
}
