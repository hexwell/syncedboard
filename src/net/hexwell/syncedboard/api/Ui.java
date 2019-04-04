package net.hexwell.syncedboard.api;

import java.util.function.Consumer;

public interface Ui extends Consumer<Delta> {
    void setBackend(Consumer<Delta> backend);

    void setStatus(Status status);

    void setStatus(Status status, int members);

    String getBoardContent();

    void setup();
}
