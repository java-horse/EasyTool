package easy.restful.api;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Objects;

public enum HttpMethod {

    REQUEST,
    GET,
    OPTIONS,
    POST,
    PUT,
    DELETE,
    PATCH,
    HEAD,
    TRACE;

    @NotNull
    public static HttpMethod[] getValues() {
        return Arrays.stream(HttpMethod.values()).filter(method -> !method.equals(HttpMethod.REQUEST)).toArray(HttpMethod[]::new);
    }

    @NotNull
    public static HttpMethod parse(@Nullable Object method) {
        try {
            if (Objects.isNull(method)) {
                return REQUEST;
            }
            if (method instanceof HttpMethod) {
                return (HttpMethod) method;
            }
            return HttpMethod.valueOf(method.toString());
        } catch (Exception ignore) {
            return REQUEST;
        }
    }

}
