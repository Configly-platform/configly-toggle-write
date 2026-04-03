package com.configly.toggle.write;

import java.util.Objects;

public final class StubSupport<T> {

    private final String methodName;
    private boolean stubbed;
    private T value;
    private RuntimeException exception;

    private StubSupport(String methodName) {
        this.methodName = Objects.requireNonNull(methodName);
    }

    public static <T> StubSupport<T> forMethod(String methodName) {
        return new StubSupport<>(methodName);
    }

    public void willReturn(T value) {
        this.value = value;
        this.exception = null;
        this.stubbed = true;
    }

    public void willThrow(RuntimeException exception) {
        this.value = null;
        this.exception = Objects.requireNonNull(exception);
        this.stubbed = true;
    }

    public T get() {
        if (!stubbed) {
            throw new AssertionError(methodName + " was not stubbed");
        }
        if (exception != null) {
            throw exception;
        }
        return value;
    }

    public void reset() {
        stubbed = false;
        value = null;
        exception = null;
    }
}
