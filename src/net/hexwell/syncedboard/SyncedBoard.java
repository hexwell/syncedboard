package net.hexwell.syncedboard;

import net.hexwell.syncedboard.api.Backend;
import net.hexwell.syncedboard.api.Ui;

final class SyncedBoard {
    public static void main(final String[] args) {
        final Ui ui = new SwingUi();
        final Backend backend = new JGroupsBackend();

        ui.setBackend(backend);
        backend.setUi(ui);

        ui.setup();
        backend.connect();
    }
}
