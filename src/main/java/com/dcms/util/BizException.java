package com.dcms.util;

/** A business-rule violation whose message is one of the SRS system messages. */
public class BizException extends RuntimeException {
    public BizException(String message) { super(message); }
}
