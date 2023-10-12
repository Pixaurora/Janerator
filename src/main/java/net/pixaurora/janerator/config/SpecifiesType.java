package net.pixaurora.janerator.config;

public interface SpecifiesType<A> {
    public SerialType<? extends A> type();
}
