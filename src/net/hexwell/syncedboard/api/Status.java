package net.hexwell.syncedboard.api;

public enum Status {
    CONNECTING("Connecting..."),
    CONNECTED("Connected"),
    ERROR("Error!");

    private final String label;

    Status(final String status) {
        this.label = status;
    }

    @Override
    public final String toString() {
        return this.label;
    }
}
