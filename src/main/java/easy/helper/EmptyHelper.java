package easy.helper;

import cn.hutool.core.util.ObjectUtil;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 空值快捷处理
 *
 * @author mabin
 * @project EasyTool
 * @package easy.helper
 * @date 2024/06/19 09:26
 */
public final class EmptyHelper<T> {

    private final T value;

    private EmptyHelper(T value) {
        this.value = ObjectUtil.isEmpty(value) ? null : value;
    }

    public static <T> EmptyHelper<T> of(T value) {
        return new EmptyHelper<>(value);
    }

    public <U> EmptyHelper<U> map(Function<? super T, ? extends U> mapper) {
        Objects.requireNonNull(mapper);
        if (!ifPresent()) {
            return new EmptyHelper<>(null);
        } else {
            return EmptyHelper.of(mapper.apply(value));
        }
    }

    public boolean ifPresent() {
        return Objects.nonNull(value);
    }

    public void ifPresent(Consumer<? super T> action) {
        Optional.ofNullable(value).ifPresent(action);
    }

    public void ifPresent(Runnable runnable) {
        Optional.ofNullable(value).ifPresent(y -> runnable.run());
    }

    public <X extends Throwable> T ifEmptyThrow(Supplier<? extends X> exceptionSupplier) throws X {
        return Optional.ofNullable(value).orElseThrow(exceptionSupplier);
    }

    public T orElse(T other) {
        return Objects.nonNull(value) ? value : other;
    }

    public T get() {
        return value;
    }

}
