package software.coley.instrument.util;

import software.coley.instrument.command.CommandConstants;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Utility to facilitate generating byte array content.
 *
 * @author Matt Coley
 */
public class ByteGen {
	private final ByteArrayOutputStream out = new ByteArrayOutputStream();
	private final DataOutputStream stream = new DataOutputStream(out);

	/**
	 * New generator.
	 */
	public ByteGen() {
		// Placeholders for ID and content length, updated on final build later.
		appendByte(0);
		appendInt(0);
	}

	/**
	 * @param data
	 * 		String to append.
	 *
	 * @return Self.
	 */
	public ByteGen appendString(String data) {
		try {
			// Not using writeUTF to match ByteBuffer read operations elsewhere
			if (data == null) {
				stream.writeInt(0);
			} else {
				stream.writeInt(data.length());
				stream.write(data.getBytes());
			}
			return this;
		} catch (IOException ex) {
			throw new IllegalStateException(ex);
		}
	}

	/**
	 * @param data
	 * 		byte to append.
	 *
	 * @return Self.
	 */
	public ByteGen appendByte(int data) {
		try {
			stream.writeByte(data);
			return this;
		} catch (IOException ex) {
			throw new IllegalStateException(ex);
		}
	}

	/**
	 * @param data
	 * 		int to append.
	 *
	 * @return Self.
	 */
	public ByteGen appendInt(int data) {
		try {
			stream.writeInt(data);
			return this;
		} catch (IOException ex) {
			throw new IllegalStateException(ex);
		}
	}

	/**
	 * @param data
	 * 		array to append.
	 *
	 * @return Self.
	 */
	public ByteGen appendByteArray(byte[] data) {
		try {
			if (data == null) {
				stream.writeInt(0);
			} else {
				stream.writeInt(data.length);
				stream.write(data);
			}
			return this;
		} catch (IOException ex) {
			throw new IllegalStateException(ex);
		}
	}

	/**
	 * @param key
	 * 		Command key.
	 *
	 * @return Generated byte array.
	 */
	public byte[] build(byte key) {
		byte[] output = out.toByteArray();
		byte[] length = createLengthArray(output.length - CommandConstants.HEADER_SIZE);
		// Update key in output
		output[0] = key;
		// Update content length in output
		System.arraycopy(length, 0, output, 1, 4);
		return output;
	}

	/**
	 * @param length
	 * 		Length int.
	 *
	 * @return {@code byte[]} representation of integer.
	 */
	private static byte[] createLengthArray(int length) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		try {
			dos.writeInt(length);
		} catch (IOException ignored) {
			// not actually thrown
		}
		return baos.toByteArray();
	}
}
