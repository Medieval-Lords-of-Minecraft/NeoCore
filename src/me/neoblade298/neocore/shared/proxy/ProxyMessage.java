package me.neoblade298.neocore.shared.proxy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public record ProxyMessage(String type, List<String> fields) {
	public static final String CHANNEL = "neocore:proxy";
	private static final int MAGIC = 0x4E434F52;
	private static final int VERSION = 1;
	private static final int MAX_PACKET_SIZE = 32_767;
	private static final int MAX_FIELDS = 64;
	private static final int MAX_FIELD_SIZE = 8_192;

	public ProxyMessage {
		Objects.requireNonNull(type, "type");
		fields = List.copyOf(fields);
		validateField(type);
		if (fields.size() > MAX_FIELDS) {
			throw new IllegalArgumentException("Too many proxy message fields: " + fields.size());
		}
		fields.forEach(ProxyMessage::validateField);
	}

	public static ProxyMessage of(String type, String... fields) {
		return new ProxyMessage(type, List.of(fields));
	}

	public byte[] encode() {
		try {
			ByteArrayOutputStream bytes = new ByteArrayOutputStream();
			try (DataOutputStream output = new DataOutputStream(bytes)) {
				output.writeInt(MAGIC);
				output.writeByte(VERSION);
				output.writeUTF(type);
				output.writeByte(fields.size());
				for (String field : fields) {
					output.writeUTF(field);
				}
			}
			byte[] encoded = bytes.toByteArray();
			if (encoded.length > MAX_PACKET_SIZE) {
				throw new IllegalArgumentException("Proxy message exceeds " + MAX_PACKET_SIZE + " bytes");
			}
			return encoded;
		}
		catch (IOException ex) {
			throw new IllegalStateException("Failed to encode proxy message", ex);
		}
	}

	public static ProxyMessage decode(byte[] data) {
		Objects.requireNonNull(data, "data");
		if (data.length == 0 || data.length > MAX_PACKET_SIZE) {
			throw new IllegalArgumentException("Invalid proxy message size: " + data.length);
		}
		try (DataInputStream input = new DataInputStream(new ByteArrayInputStream(data))) {
			if (input.readInt() != MAGIC) {
				throw new IllegalArgumentException("Invalid proxy message header");
			}
			int version = input.readUnsignedByte();
			if (version != VERSION) {
				throw new IllegalArgumentException("Unsupported proxy message version: " + version);
			}
			String type = input.readUTF();
			int fieldCount = input.readUnsignedByte();
			if (fieldCount > MAX_FIELDS) {
				throw new IllegalArgumentException("Too many proxy message fields: " + fieldCount);
			}
			List<String> fields = new ArrayList<>(fieldCount);
			for (int i = 0; i < fieldCount; i++) {
				fields.add(input.readUTF());
			}
			if (input.available() != 0) {
				throw new IllegalArgumentException("Proxy message contains trailing data");
			}
			return new ProxyMessage(type, fields);
		}
		catch (IOException ex) {
			throw new IllegalArgumentException("Malformed proxy message", ex);
		}
	}

	private static void validateField(String value) {
		Objects.requireNonNull(value, "Proxy message fields cannot be null");
		if (value.getBytes(StandardCharsets.UTF_8).length > MAX_FIELD_SIZE) {
			throw new IllegalArgumentException("Proxy message field exceeds " + MAX_FIELD_SIZE + " bytes");
		}
	}
}
