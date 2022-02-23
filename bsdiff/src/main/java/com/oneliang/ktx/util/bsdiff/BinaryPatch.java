package com.oneliang.ktx.util.bsdiff;


import java.io.*;
import java.util.zip.GZIPInputStream;

/**
 * Java Binary patcher (based on bspatch by Joe Desbonnet, joe@galway.net (JBPatch))
 *
 * @author hakonzhao
 */
public class BinaryPatch {

    /**
     * the patch process is end up successfully
     */
    public static final int RETURN_SUCCESS = 1;

    /**
     * diffFile is null, or the diffFile does not exist
     */
    public static final int RETURN_DIFF_FILE_ERR = 2;

    /**
     * oldFile is null, or the oldFile does not exist
     */
    public static final int RETURN_OLD_FILE_ERR = 3;

    /**
     * newFile is null, or can not create the newFile
     */
    public static final int RETURN_NEW_FILE_ERR = 4;

    /**
     * BSPatch using less memory size.
     * Memory size = diffFile size + max block size
     *
     * @param oldFile  oldFile
     * @param newFile  newFile
     * @param diffFile diffFile
     * @param extLength extLength
     * @return int
     * @throws IOException IOException
     */
    public static int patchLessMemory(RandomAccessFile oldFile, File newFile, File diffFile, int extLength) throws IOException {
        if (oldFile == null || oldFile.length() <= 0)
            return RETURN_OLD_FILE_ERR;
        if (newFile == null)
            return RETURN_NEW_FILE_ERR;
        if (diffFile == null || diffFile.length() <= 0)
            return RETURN_DIFF_FILE_ERR;

        byte[] diffBytes = new byte[(int) diffFile.length()];
        InputStream diffInputStream = new FileInputStream(diffFile);
        BinaryUtil.readFromStream(diffInputStream, diffBytes, 0, diffBytes.length);

        return patchLessMemory(oldFile, (int) oldFile.length(), diffBytes, diffBytes.length, newFile, extLength);
    }

    /**
     * BSPatch using less memory size.
     * Memory size = diffFile size + max block size
     *
     * @param oldFile  oldFile
     * @param oldSize  oldSize
     * @param diffBuf  diffBuf
     * @param diffSize diffSize
     * @param newFile  newFile
     * @param extLength   the length of the apk external info. set 0 if has no external info.
     * @return int
     * @throws IOException IOException
     */
    public static int patchLessMemory(RandomAccessFile oldFile, int oldSize, byte[] diffBuf, int diffSize, File newFile, int extLength) throws IOException {

        if (oldFile == null || oldSize <= 0)
            return RETURN_OLD_FILE_ERR;
        if (newFile == null)
            return RETURN_NEW_FILE_ERR;
        if (diffBuf == null || diffSize <= 0)
            return RETURN_DIFF_FILE_ERR;

        int commentLenPos = oldSize - extLength - 2;
        if (commentLenPos <= 2) {
            return RETURN_OLD_FILE_ERR;
        }

        DataInputStream diffIn = new DataInputStream(new ByteArrayInputStream(diffBuf, 0, diffSize));

        diffIn.skip(8); // skip headerMagic at header offset 0 (length 8 bytes)
        long ctrlBlockLen = diffIn.readLong(); // ctrlBlockLen after bzip2 compression at heater offset 8 (length 8 bytes)
        long diffBlockLen = diffIn.readLong(); // diffBlockLen after bzip2 compression at header offset 16 (length 8 bytes)
        int newsize = (int) diffIn.readLong(); // size of new file at header offset 24 (length 8 bytes)

        diffIn.close();

        InputStream in = new ByteArrayInputStream(diffBuf, 0, diffSize);
        in.skip(BinaryUtil.HEADER_SIZE);
        DataInputStream ctrlBlockIn = new DataInputStream(new GZIPInputStream(in));

        in = new ByteArrayInputStream(diffBuf, 0, diffSize);
        in.skip(ctrlBlockLen + BinaryUtil.HEADER_SIZE);
        InputStream diffBlockIn = new GZIPInputStream(in);

        in = new ByteArrayInputStream(diffBuf, 0, diffSize);
        in.skip(diffBlockLen + ctrlBlockLen + BinaryUtil.HEADER_SIZE);
        InputStream extraBlockIn = new GZIPInputStream(in);

        OutputStream outStream = new FileOutputStream(newFile);

        int oldpos = 0;
        int newpos = 0;
        int[] ctrl = new int[3];

        // int nbytes;
        while (newpos < newsize) {

            for (int i = 0; i <= 2; i++) {
                ctrl[i] = ctrlBlockIn.readInt();
            }

            if (newpos + ctrl[0] > newsize) {
                outStream.close();
                return RETURN_DIFF_FILE_ERR;
            }

            // Read ctrl[0] bytes from diffBlock stream
            byte[] buffer = new byte[ctrl[0]];
            if (!BinaryUtil.readFromStream(diffBlockIn, buffer, 0, ctrl[0])) {
                outStream.close();
                return RETURN_DIFF_FILE_ERR;
            }

            byte[] oldBuffer = new byte[ctrl[0]];
            if (oldFile.read(oldBuffer, 0, ctrl[0]) < ctrl[0]) {
                outStream.close();
                return RETURN_DIFF_FILE_ERR;
            }
            for (int i = 0; i < ctrl[0]; i++) {
                if (oldpos + i == commentLenPos) {
                    oldBuffer[i] = 0;
                    oldBuffer[i + 1] = 0;
                }

                if ((oldpos + i >= 0) && (oldpos + i < oldSize)) {
                    buffer[i] += oldBuffer[i];
                }
            }
            outStream.write(buffer);

//			System.out.println(""+ctrl[0]+ ", " + ctrl[1]+ ", " + ctrl[2]);

            newpos += ctrl[0];
            oldpos += ctrl[0];

            if (newpos + ctrl[1] > newsize) {
                outStream.close();
                return RETURN_DIFF_FILE_ERR;
            }

            buffer = new byte[ctrl[1]];
            if (!BinaryUtil.readFromStream(extraBlockIn, buffer, 0, ctrl[1])) {
                outStream.close();
                return RETURN_DIFF_FILE_ERR;
            }
            outStream.write(buffer);
            outStream.flush();

            newpos += ctrl[1];
            oldpos += ctrl[2];
            oldFile.seek(oldpos);
        }
        ctrlBlockIn.close();
        diffBlockIn.close();
        extraBlockIn.close();

        oldFile.close();
        outStream.close();
        return RETURN_SUCCESS;
    }

    /**
     * This patch method is fast ,but using mory memoty.
     * Memory size = oldBuf + diffBuf + newBuf
     *
     * @param oldFile   oldFile
     * @param newFile   newFile
     * @param diffFile  diffFile
     * @param extLength extLength
     * @return int
     * @throws IOException IOException
     */
    public static int patchFast(File oldFile, File newFile, File diffFile, int extLength) throws IOException {
        if (oldFile == null || oldFile.length() <= 0)
            return RETURN_OLD_FILE_ERR;
        if (newFile == null)
            return RETURN_NEW_FILE_ERR;
        if (diffFile == null || diffFile.length() <= 0)
            return RETURN_DIFF_FILE_ERR;

        InputStream oldInputStream = new BufferedInputStream(new FileInputStream(oldFile));
        byte[] diffBytes = new byte[(int) diffFile.length()];
        InputStream diffInputStream = new FileInputStream(diffFile);
        BinaryUtil.readFromStream(diffInputStream, diffBytes, 0, diffBytes.length);

        byte[] newBytes = patchFast(oldInputStream, (int) oldFile.length(), diffBytes, extLength);

        OutputStream newOutputStream = new FileOutputStream(newFile);
        newOutputStream.write(newBytes);
        newOutputStream.close();
        return RETURN_SUCCESS;
    }

    /**
     * This patch method is fast ,but using mory memoty.
     * Memory size = oldBuf + diffBuf + newBuf
     *
     * @param oldInputStream oldInputStream
     * @param oldSize        oldSize
     * @param diffByteArray  diffByteArray
     * @param extLength      extLength
     * @return byte[]
     */
    public static byte[] patchFast(InputStream oldInputStream, int oldSize, byte[] diffByteArray, int extLength) throws IOException {
        // Read in old file (file to be patched) to oldBuf
        byte[] oldBuf = new byte[oldSize];
        BinaryUtil.readFromStream(oldInputStream, oldBuf, 0, oldSize);
        oldInputStream.close();

        return patchFast(oldBuf, oldSize, diffByteArray, diffByteArray.length, extLength);
    }

    /**
     * This patch method is fast ,but using mory memoty.
     * Memory size = oldBuf + diffBuf + newBuf
     *
     * @param oldBuffer  oldBuffer
     * @param oldSize    oldSize
     * @param diffBuffer diffBuffer
     * @param diffSize   diffSize
     * @param extLength extLength
     * @return byte[]
     * @throws IOException IOException
     */
    public static byte[] patchFast(byte[] oldBuffer, int oldSize, byte[] diffBuffer, int diffSize, int extLength) throws IOException {

        int commentLenPos = oldSize - extLength - 2;
        if (commentLenPos <= 2) {
            throw new IOException("Corrupt by wrong old file.");
        }
        oldBuffer[commentLenPos] = 0;
        oldBuffer[commentLenPos + 1] = 0;

        DataInputStream diffIn = new DataInputStream(new ByteArrayInputStream(diffBuffer, 0, diffSize));

        diffIn.skip(8); // skip headerMagic at header offset 0 (length 8 bytes)
        long ctrlBlockLen = diffIn.readLong(); // ctrlBlockLen after bzip2 compression at heater offset 8 (length 8 bytes)
        long diffBlockLen = diffIn.readLong(); // diffBlockLen after bzip2 compression at header offset 16 (length 8 bytes)
        int newSize = (int) diffIn.readLong(); // size of new file at header offset 24 (length 8 bytes)

        diffIn.close();

        InputStream in = new ByteArrayInputStream(diffBuffer, 0, diffSize);
        in.skip(BinaryUtil.HEADER_SIZE);
        DataInputStream ctrlBlockIn = new DataInputStream(new GZIPInputStream(in));

        in = new ByteArrayInputStream(diffBuffer, 0, diffSize);
        in.skip(ctrlBlockLen + BinaryUtil.HEADER_SIZE);
        InputStream diffBlockIn = new GZIPInputStream(in);

        in = new ByteArrayInputStream(diffBuffer, 0, diffSize);
        in.skip(diffBlockLen + ctrlBlockLen + BinaryUtil.HEADER_SIZE);
        InputStream extraBlockIn = new GZIPInputStream(in);

        // byte[] newBuf = new byte[newsize + 1];
        byte[] newBuf = new byte[newSize];

        int oldpos = 0;
        int newpos = 0;
        int[] ctrl = new int[3];

        // int nbytes;
        while (newpos < newSize) {

            for (int i = 0; i <= 2; i++) {
                ctrl[i] = ctrlBlockIn.readInt();
            }

            if (newpos + ctrl[0] > newSize) {
                throw new IOException("Corrupt by wrong patch file.");
            }

            // Read ctrl[0] bytes from diffBlock stream
            if (!BinaryUtil.readFromStream(diffBlockIn, newBuf, newpos, ctrl[0])) {
                throw new IOException("Corrupt by wrong patch file.");
            }

            for (int i = 0; i < ctrl[0]; i++) {
                if ((oldpos + i >= 0) && (oldpos + i < oldSize)) {
                    newBuf[newpos + i] += oldBuffer[oldpos + i];
                }
            }

            newpos += ctrl[0];
            oldpos += ctrl[0];

            if (newpos + ctrl[1] > newSize) {
                throw new IOException("Corrupt by wrong patch file.");
            }

            if (!BinaryUtil.readFromStream(extraBlockIn, newBuf, newpos, ctrl[1])) {
                throw new IOException("Corrupt by wrong patch file.");
            }

            newpos += ctrl[1];
            oldpos += ctrl[2];
        }
        ctrlBlockIn.close();
        diffBlockIn.close();
        extraBlockIn.close();

        return newBuf;
    }

//	/**
//	 * Run JBPatch from the command line. Params: oldfile newfile patchfile.
//	 * newfile will be created.
//	 * 
//	 * @param arg
//	 * @throws IOException
//	 */
//	public static void main(String[] arg) throws IOException {
//
//		if (arg.length != 3) {
//			System.err.println("usage example: java -Xmx200m ie.wombat.jbdiff.JBPatch oldfile newfile patchfile");
//		}
//		File oldFile = new File(arg[0]);
//		File newFile = new File(arg[1]);
//		File diffFile = new File(arg[2]);
//		
////		patchFast(oldFile, newFile, diffFile);
//
//		patchLessMemory(new RandomAccessFile(oldFile, "r"), newFile, diffFile);
//		
//	}
}