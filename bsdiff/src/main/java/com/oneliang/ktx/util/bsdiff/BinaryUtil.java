package com.oneliang.ktx.util.bsdiff;


import java.io.IOException;
import java.io.InputStream;

class BinaryUtil {

	// JBDiff extensions by Stefan.Liebig@compeople.de:
	//
	// - introduced a HEADER_SIZE constant here

	/**
	 * Length of the diff file header.
	 */
	public static final int HEADER_SIZE = 32;


	/**
	 * Read from input stream and fill the given buffer from the given offset up
	 * to length len.
	 * 
	 * @param inputStream inputStream
	 * @param byteArray byteArray
	 * @param offset offset
	 * @param len len
	 * @return boolean
	 * @throws IOException IOException
	 */
	static boolean readFromStream(InputStream inputStream, byte[] byteArray, int offset, int len) throws IOException {

		int totalBytesRead = 0;
		while (totalBytesRead < len) {
			int bytesRead = inputStream.read(byteArray, offset + totalBytesRead, len - totalBytesRead);
			if (bytesRead < 0) {
				return false;
			}
			totalBytesRead += bytesRead;
		}
		return true;
	}
}