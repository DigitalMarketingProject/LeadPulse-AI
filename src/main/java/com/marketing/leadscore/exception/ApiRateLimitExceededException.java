package com.marketing.leadscore.exception;

public class ApiRateLimitExceededException extends RuntimeException {

    private final long retryAfterSeconds;

    public ApiRateLimitExceededException(long retryAfterSeconds) {
        super("Organization API request limit exceeded. Retry after the rate-limit window.");
        this.retryAfterSeconds = retryAfterSeconds;
    }

    public long getRetryAfterSeconds() {
        return retryAfterSeconds;
    }
}
