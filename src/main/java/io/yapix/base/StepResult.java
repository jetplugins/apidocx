package io.yapix.base;

/**
 * 某个步骤的执行结果
 */
public class StepResult<T> {

    private StepType type;
    private T data;

    public enum StepType {
        CONTINUE, STOP
    }

    public boolean isContinue() {
        return type == StepType.CONTINUE;
    }

    public StepResult(StepType type, T data) {
        this.type = type;
        this.data = data;
    }

    public static <T> StepResult<T> ok(T data) {
        return new StepResult<>(StepType.CONTINUE, data);
    }

    public static <T> StepResult<T> stop() {
        return new StepResult<>(StepType.STOP, null);
    }

    public T getData() {
        return data;
    }
}
