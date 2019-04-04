package net.hexwell.syncedboard.api;

import java.io.Serializable;

@SuppressWarnings("PublicMethodNotExposedInInterface")
public final class Delta implements Serializable {
    private final Delta.Type type;
    private final int offset;
    private int length;
    private String insertion;

    private Delta(final Delta.Type type, final int offset) {
        this.type = type;
        this.offset = offset;
    }

    public Delta(final Delta.Type type, final int offset, final int length) {
        this(type, offset);

        this.length = length;
    }

    public Delta(final Delta.Type type, final int offset, final String insertion) {
        this(type, offset);

        this.insertion = insertion;
    }

    public Delta.Type getType() {
        return this.type;
    }

    public int getOffset() {
        return this.offset;
    }

    public int getLength() {
        return this.length;
    }

    public String getInsertion() {
        return this.insertion;
    }

    @Override
    public String toString() {
        switch (this.type) {
            //noinspection UnqualifiedStaticUsage
            case INSERT:
                return "Delta{" +
                               "type=" + this.type +
                               ", offset=" + this.offset +
                               ", insertion='" + this.insertion + '\'' +
                               '}';

            //noinspection UnqualifiedStaticUsage
            case REMOVE:
                return "Delta{" +
                               "type=" + this.type +
                               ", offset=" + this.offset +
                               ", length=" + this.length +
                               '}';

            default:
                throw new RuntimeException("Unknown type");
        }
    }

    public enum Type {
        INSERT,
        REMOVE
    }
}
