package dev.marfien.rewibw.util;

import java.util.function.Supplier;

public class LazyValue<T> {

    private Supplier<T> supplier;
    private T value;

    public LazyValue(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    public T get() {
        if (this.supplier != null) {
            this.value = this.supplier.get();
            this.supplier = null;
        }

        return this.value;
    }

}
