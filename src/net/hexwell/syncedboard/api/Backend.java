package net.hexwell.syncedboard.api;

import java.util.function.Consumer;

public interface Backend extends Consumer<Delta> {
    void setUi(Ui ui);

    void connect();
}
