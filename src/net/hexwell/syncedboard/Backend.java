package net.hexwell.syncedboard;

import net.hexwell.syncedboard.api.Backend;
import net.hexwell.syncedboard.api.Delta;
import net.hexwell.syncedboard.api.Status;
import net.hexwell.syncedboard.api.Ui;
import org.jgroups.*;

@SuppressWarnings("resource")
final class JGroupsBackend implements Backend {
    private Ui ui;
    private JChannel channel;

    @Override
    public void connect() {
        this.ui.setStatus(Status.CONNECTING);

        System.setProperty("java.net.preferIPv4Stack", "true");

        try {
            this.channel = new JChannel();
            this.channel.connect("syncedboard");

        } catch (final Exception e) {
            e.printStackTrace();

            this.error();

            return;
        }

        this.channel.setDiscardOwnMessages(true);
        this.channel.setReceiver(new DeltaReceiver(this.ui, this.channel, this));

        this.ui.setStatus(Status.CONNECTED, this.channel.getView().getMembers().size());
    }

    void send(final Delta delta, final Address address) {
        try {
            this.channel.send(new Message(address, delta));

        } catch (final Exception e) {
            e.printStackTrace();

            this.error();
        }
    }

    @Override
    public void accept(final Delta delta) {
        this.send(delta, null);
    }

    @Override
    public void setUi(final Ui ui) {
        this.ui = ui;
    }

    private void error() {
        this.ui.setStatus(Status.ERROR);
    }
}

class DeltaReceiver extends ReceiverAdapter {
    private final Ui ui;
    private final JChannel channel;
    private final JGroupsBackend backend;

    private int members = 1;

    DeltaReceiver(final Ui ui, final JChannel channel, final JGroupsBackend backend) {
        this.ui = ui;
        this.channel = channel;
        this.backend = backend;
    }

    @Override
    public void receive(final Message msg) {
        final Delta delta = msg.getObject();

        this.ui.accept(delta);
    }

    @Override
    public void viewAccepted(final View view) {
        super.viewAccepted(view);

        final int newMembers = view.getMembers().size();

        this.ui.setStatus(Status.CONNECTED, newMembers);

        // If a new member joined and this instance is the coordinator
        if (this.members < newMembers && this.channel.getAddress().equals(view.getCoord())) {

            this.bootstrapNewMember(view.getMembers().get(view.getMembers().size() - 1));

        }

        this.members = newMembers;
    }

    private void bootstrapNewMember(final Address newMember) {
        final Delta boardContent = new Delta(
                Delta.Type.INSERT,
                0,
                this.ui.getBoardContent()
        );

        final Thread sendTimer = new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (final InterruptedException ignored) {
            }

            this.backend.send(
                    boardContent,
                    newMember
            );
        });

        sendTimer.setDaemon(true);
        sendTimer.start();
    }
}
