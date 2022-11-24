package com.minelittlepony.hdskins.util.net;

import java.io.IOException;

public class HttpException extends IOException {
    private static final long serialVersionUID = -6168434367054139332L;

    private final String reason;

    private final int statusCode;

    public HttpException(String reason, int statusCode, Throwable cause) {
        super("(" + statusCode + ") " + reason, cause);

        this.reason = reason;
        this.statusCode = statusCode;
    }

    public String getReasonPhrase() {
        return reason;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
