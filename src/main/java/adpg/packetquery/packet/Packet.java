package adpg.packetquery.packet;

import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.ArrayList;

public class Packet implements Serializable {

    private int INDEX = 0;
    private final ArrayList<String> CONTENT;

    /// @see PacketBuilder
    public Packet(ArrayList<String> content) {
        this.CONTENT = content;
    }

    /// @return if the next field contains a value (not null)
    public boolean hasNext() {
        return hasNext(INDEX);
    }

    /// @return if the specified field contains a value (not null)
    public boolean hasNext(int index) {
        return index < CONTENT.size() && CONTENT.get(index) != null;
    }

    /// @return the next field in the packet, may be {@code null} if it is empty
    @Nullable
    public String read() {
        String message = null;

        if (INDEX < CONTENT.size()) {
            message = CONTENT.get(INDEX);
            INDEX++;
        }

        return message;
    }

    /// @return a specific field in the packet, may be {@code null} if it is empty
    @Nullable
    public String readAt(int index) {
        String message = null;

        if (index < CONTENT.size()) {
            message = CONTENT.get(index);
        }

        return message;
    }

    /**
     * Resets the reader so the next time a field is read it will start from the beginning
     */
    public void resetReader() {
        INDEX = 0;
    }

}
