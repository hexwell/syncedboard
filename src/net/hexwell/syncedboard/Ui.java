package net.hexwell.syncedboard;

import com.bulenkov.darcula.DarculaLaf;
import net.hexwell.syncedboard.api.Delta;
import net.hexwell.syncedboard.api.Status;
import net.hexwell.syncedboard.api.Ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.util.function.Consumer;

import static net.hexwell.syncedboard.api.Delta.Type.INSERT;
import static net.hexwell.syncedboard.api.Delta.Type.REMOVE;

final class SwingUi implements Ui {
    private Consumer<Delta> backend;

    private JTextArea board;
    private JLabel statusLabel;

    private DeltaListener changeListener;

    @Override
    public void setup() {
        try {
            UIManager.setLookAndFeel(new DarculaLaf());

        } catch (final UnsupportedLookAndFeelException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        final JFrame root = new JFrame();
        root.setTitle("Synced Board v1.0.0");
        root.setSize(500, 300);
        root.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        final JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(0, 0, 1, 5));
        this.statusLabel = new JLabel("...");
        panel.add(this.statusLabel, BorderLayout.EAST);
        this.board = new JTextArea();
        final Container contentPane = root.getContentPane();
        contentPane.add(new JScrollPane(this.board), BorderLayout.CENTER);
        contentPane.add(panel, BorderLayout.SOUTH);

        root.setVisible(true);

        this.changeListener = new DeltaListener(this.backend);
        this.board.getDocument().addDocumentListener(this.changeListener);
    }

    private void insert(final Delta delta) {
        try {
            this.board.getDocument().insertString(
                    delta.getOffset(),
                    delta.getInsertion(),
                    null
            );

        } catch (final BadLocationException e) {
            e.printStackTrace();
        }
    }

    private void remove(final Delta delta) {
        try {
            this.board.getDocument().remove(delta.getOffset(), delta.getLength());

        } catch (final BadLocationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void accept(final Delta delta) {
        this.board.getDocument().removeDocumentListener(this.changeListener);

        switch (delta.getType()) {
            //noinspection UnqualifiedStaticUsage
            case INSERT:
                this.insert(delta);
                break;

            //noinspection UnqualifiedStaticUsage
            case REMOVE:
                this.remove(delta);
        }

        this.board.getDocument().addDocumentListener(this.changeListener);
    }

    @Override
    public void setBackend(final Consumer<Delta> backend) {
        this.backend = backend;
    }

    @Override
    public void setStatus(final Status status) {
        this.statusLabel.setText(status.toString());
    }

    @Override
    public void setStatus(final Status status, final int members) {
        this.setStatus(status);
        this.statusLabel.setText(this.statusLabel.getText() + " (" + members + ")");
    }

    @Override
    public String getBoardContent() {
        return this.board.getText();
    }
}

final class DeltaListener implements DocumentListener {
    private final Consumer<Delta> deltaConsumer;

    DeltaListener(final Consumer<Delta> deltaConsumer) {
        this.deltaConsumer = deltaConsumer;
    }

    @Override
    public void insertUpdate(final DocumentEvent e) {
        try {
            this.deltaConsumer.accept(new Delta(
                    INSERT,
                    e.getOffset(),
                    e.getDocument().getText(
                            e.getOffset(),
                            e.getLength()
                    )
            ));

        } catch (final BadLocationException ignored) {
        }
    }

    @Override
    public void removeUpdate(final DocumentEvent e) {
        this.deltaConsumer.accept(
                new Delta(REMOVE, e.getOffset(), e.getLength())
        );
    }

    @Override
    public void changedUpdate(final DocumentEvent e) {
    }
}
