package first.robot.vision;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Small, bounded MessagePack decoder for Limelight's NetworkTables result payload. */
final class MessagePackReader {
  private static final int MAX_CONTAINER_SIZE = 16_384;
  private static final int MAX_VALUE_BYTES = 4 * 1024 * 1024;

  private final ByteBuffer input;

  private MessagePackReader(byte[] bytes) {
    input = ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN);
  }

  static Object decode(byte[] bytes) {
    Object value = new MessagePackReader(bytes).readValue();
    return value;
  }

  private Object readValue() {
    int prefix = unsignedByte();
    if (prefix <= 0x7f) {
      return (long) prefix;
    }
    if (prefix >= 0xe0) {
      return (long) (byte) prefix;
    }
    if ((prefix & 0xe0) == 0xa0) {
      return readString(prefix & 0x1f);
    }
    if ((prefix & 0xf0) == 0x90) {
      return readArray(prefix & 0x0f);
    }
    if ((prefix & 0xf0) == 0x80) {
      return readMap(prefix & 0x0f);
    }

    return switch (prefix) {
      case 0xc0 -> null;
      case 0xc2 -> false;
      case 0xc3 -> true;
      case 0xc4 -> readBytes(unsignedByte());
      case 0xc5 -> readBytes(unsignedShort());
      case 0xc6 -> readBytes(checkedLength(unsignedInt()));
      case 0xca -> (double) input.getFloat();
      case 0xcb -> input.getDouble();
      case 0xcc -> (long) unsignedByte();
      case 0xcd -> (long) unsignedShort();
      case 0xce -> unsignedInt();
      case 0xcf -> input.getLong();
      case 0xd0 -> (long) input.get();
      case 0xd1 -> (long) input.getShort();
      case 0xd2 -> (long) input.getInt();
      case 0xd3 -> input.getLong();
      case 0xd9 -> readString(unsignedByte());
      case 0xda -> readString(unsignedShort());
      case 0xdb -> readString(checkedLength(unsignedInt()));
      case 0xdc -> readArray(unsignedShort());
      case 0xdd -> readArray(checkedLength(unsignedInt()));
      case 0xde -> readMap(unsignedShort());
      case 0xdf -> readMap(checkedLength(unsignedInt()));
      default -> throw new IllegalArgumentException(
          String.format("unsupported MessagePack prefix 0x%02x", prefix));
    };
  }

  private List<Object> readArray(int size) {
    checkContainerSize(size);
    List<Object> values = new ArrayList<>(size);
    for (int i = 0; i < size; i++) {
      values.add(readValue());
    }
    return values;
  }

  private Map<String, Object> readMap(int size) {
    checkContainerSize(size);
    Map<String, Object> values = new LinkedHashMap<>(size);
    for (int i = 0; i < size; i++) {
      Object key = readValue();
      if (!(key instanceof String stringKey)) {
        throw new IllegalArgumentException("MessagePack map key is not a string");
      }
      values.put(stringKey, readValue());
    }
    return values;
  }

  private String readString(int size) {
    return new String(readBytes(size), StandardCharsets.UTF_8);
  }

  private byte[] readBytes(int size) {
    if (size < 0 || size > MAX_VALUE_BYTES || size > input.remaining()) {
      throw new IllegalArgumentException("invalid MessagePack byte length " + size);
    }
    byte[] value = new byte[size];
    input.get(value);
    return value;
  }

  private int unsignedByte() {
    return Byte.toUnsignedInt(input.get());
  }

  private int unsignedShort() {
    return Short.toUnsignedInt(input.getShort());
  }

  private long unsignedInt() {
    return Integer.toUnsignedLong(input.getInt());
  }

  private static int checkedLength(long size) {
    if (size > Integer.MAX_VALUE) {
      throw new IllegalArgumentException("MessagePack length exceeds Java array limit");
    }
    return (int) size;
  }

  private static void checkContainerSize(int size) {
    if (size < 0 || size > MAX_CONTAINER_SIZE) {
      throw new IllegalArgumentException("invalid MessagePack container size " + size);
    }
  }
}
