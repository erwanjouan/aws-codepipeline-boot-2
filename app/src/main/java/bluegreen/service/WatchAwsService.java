package bluegreen.service;

import java.util.Optional;
import java.util.function.Supplier;

public interface WatchAwsService<T> {

    T watch();

    default Object safe(final Supplier<Object> supplier) {
        return Optional.ofNullable(supplier.get())
                .orElse("");
    }

}
