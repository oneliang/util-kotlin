package com.oneliang.ktx.util.bsdiff;


import java.io.*;
import java.util.zip.GZIPOutputStream;

/**
 * Java Binary Diff utility. Based on bsdiff (v4.2) by Colin Percival (see http://www.daemonology.net/bsdiff/ ) and distributed under BSD license.
 * Running this on large files will probably require an increase of the default maximum heap size (use java -Xmx300m)
 */
public class BinaryDiff {

    //private static final String VERSION = "jbdiff-0.1.0.1";

    // This is
    private static final byte[] MAGIC_BYTES = new byte[]{0x4D, 0x69, 0x63,
            0x72, 0x6F, 0x4D, 0x73, 0x67};

    private static void split(int[] I, int[] V, int start, int len, int h) {

        int i, j, k, x, tmp, jj, kk;

        if (len < 16) {
            for (k = start; k < start + len; k += j) {
                j = 1;
                x = V[I[k] + h];
                for (i = 1; k + i < start + len; i++) {
                    if (V[I[k + i] + h] < x) {
                        x = V[I[k + i] + h];
                        j = 0;
                    }

                    if (V[I[k + i] + h] == x) {
                        tmp = I[k + j];
                        I[k + j] = I[k + i];
                        I[k + i] = tmp;
                        j++;
                    }

                }

                for (i = 0; i < j; i++) {
                    V[I[k + i]] = k + j - 1;
                }
                if (j == 1) {
                    I[k] = -1;
                }
            }

            return;
        }

        x = V[I[start + len / 2] + h];
        jj = 0;
        kk = 0;
        for (i = start; i < start + len; i++) {
            if (V[I[i] + h] < x) {
                jj++;
            }
            if (V[I[i] + h] == x) {
                kk++;
            }
        }

        jj += start;
        kk += jj;

        i = start;
        j = 0;
        k = 0;
        while (i < jj) {
            if (V[I[i] + h] < x) {
                i++;
            } else if (V[I[i] + h] == x) {
                tmp = I[i];
                I[i] = I[jj + j];
                I[jj + j] = tmp;
                j++;
            } else {
                tmp = I[i];
                I[i] = I[kk + k];
                I[kk + k] = tmp;
                k++;
            }

        }

        while (jj + j < kk) {
            if (V[I[jj + j] + h] == x) {
                j++;
            } else {
                tmp = I[jj + j];
                I[jj + j] = I[kk + k];
                I[kk + k] = tmp;
                k++;
            }

        }

        if (jj > start) {
            split(I, V, start, jj - start, h);
        }

        for (i = 0; i < kk - jj; i++) {
            V[I[jj + i]] = kk - 1;
        }

        if (jj == kk - 1) {
            I[jj] = -1;
        }

        if (start + len > kk) {
            split(I, V, kk, start + len - kk, h);
        }

    }

    /**
     * Fast suffix sporting. Larsson and Sadakane's qsufsort algorithm. See
     * http://www.cs.lth.se/Research/Algorithms/Papers/jesper5.ps
     *
     * @param I       I
     * @param V       V
     * @param oldBuf  oldBuf
     * @param oldSize oldSize
     */
    private static void qsufsort(int[] I, int[] V, byte[] oldBuf, int oldSize) {

        // int oldsize = oldBuf.length;
        int[] buckets = new int[256];

        // No need to do that in Java.
        // for ( int i = 0; i < 256; i++ ) {
        // buckets[i] = 0;
        // }

        for (int i = 0; i < oldSize; i++) {
            buckets[oldBuf[i] & 0xff]++;
        }

        for (int i = 1; i < 256; i++) {
            buckets[i] += buckets[i - 1];
        }

        for (int i = 255; i > 0; i--) {
            buckets[i] = buckets[i - 1];
        }

        buckets[0] = 0;

        for (int i = 0; i < oldSize; i++) {
            I[++buckets[oldBuf[i] & 0xff]] = i;
        }

        I[0] = oldSize;
        for (int i = 0; i < oldSize; i++) {
            V[i] = buckets[oldBuf[i] & 0xff];
        }
        V[oldSize] = 0;

        for (int i = 1; i < 256; i++) {
            if (buckets[i] == buckets[i - 1] + 1) {
                I[buckets[i]] = -1;
            }
        }

        I[0] = -1;

        for (int h = 1; I[0] != -(oldSize + 1); h += h) {
            int len = 0;
            int i;
            for (i = 0; i < oldSize + 1; ) {
                if (I[i] < 0) {
                    len -= I[i];
                    i -= I[i];
                } else {
                    // if(len) I[i-len]=-len;
                    if (len != 0) {
                        I[i - len] = -len;
                    }
                    len = V[I[i]] + 1 - i;
                    split(I, V, i, len, h);
                    i += len;
                    len = 0;
                }

            }

            if (len != 0) {
                I[i - len] = -len;
            }
        }

        for (int i = 0; i < oldSize + 1; i++) {
            I[V[i]] = i;
        }
    }


    /**
     * 分别将 oldBufd[start..oldSize] 和 oldBufd[end..oldSize] 与  newBuf[newBufOffset...newSize] 进行匹配，
     * 返回他们中的最长匹配长度，并且将最长匹配的开始位置记录到pos.value中。
     */
    private static int search(int[] I, byte[] oldBuf, int oldSize, byte[] newBuf, int newSize, int newBufOffset, int start, int end, IntByRef pos) {

        if (end - start < 2) {
            int x = matchLength(oldBuf, oldSize, I[start], newBuf, newSize, newBufOffset);
            int y = matchLength(oldBuf, oldSize, I[end], newBuf, newSize, newBufOffset);

            if (x > y) {
                pos.value = I[start];
                return x;
            } else {
                pos.value = I[end];
                return y;
            }
        }

        // binary search
        int x = start + (end - start) / 2;
        if (compare(oldBuf, oldSize, I[x], newBuf, newSize, newBufOffset) < 0) {
            return search(I, oldBuf, oldSize, newBuf, newSize, newBufOffset, x, end, pos);  // Calls itself recursively
        } else {
            return search(I, oldBuf, oldSize, newBuf, newSize, newBufOffset, start, x, pos);
        }
    }


    /**
     * Count the number of bytes that match in oldBuf[oldOffset...oldSize] and newBuf[newOffset...newSize]
     */
    private static int matchLength(byte[] oldBuf, int oldSize, int oldOffset, byte[] newBuf, int newSize, int newOffset) {

        int end = Math.min(oldSize - oldOffset, newSize - newOffset);
        for (int i = 0; i < end; i++) {
            if (oldBuf[oldOffset + i] != newBuf[newOffset + i]) {
                return i;
            }
        }
        return end;
    }

    /**
     * Compare two byte array segments to see if they are equal
     *
     * @return return 1 if s1[s1offset...s1Size] is bigger than s2[s2offset...s2Size] otherwise return -1
     */
    private static int compare(byte[] s1, int s1Size, int s1offset, byte[] s2, int s2Size, int s2offset) {

        int n = s1Size - s1offset;

        if (n > (s2Size - s2offset)) {
            n = s2Size - s2offset;
        }

        for (int i = 0; i < n; i++) {

            if (s1[i + s1offset] != s2[i + s2offset]) {
                return s1[i + s1offset] < s2[i + s2offset] ? -1 : 1;
            }
        }
        return 0;
    }

    /**
     * @param oldFile oldFile
     * @param newFile newFile
     * @param diffFile diffFile
     * @throws IOException IOException
     */
    public static void binaryDiff(File oldFile, File newFile, File diffFile) throws IOException {
        InputStream oldInputStream = new BufferedInputStream(new FileInputStream(oldFile));
        InputStream newInputStream = new BufferedInputStream(new FileInputStream(newFile));
        OutputStream diffOutputStream = new FileOutputStream(diffFile);

        byte[] diffBytes = binaryDiff(oldInputStream, (int) oldFile.length(), newInputStream, (int) newFile.length());

        diffOutputStream.write(diffBytes);
        diffOutputStream.close();

        System.out.println("make patch file finish");
    }

    /**
     * @param oldInputStream oldInputStream
     * @param oldSize oldSize
     * @param newInputStream newInputStream
     * @param newSize newSize
     * @return byte[]
     * @throws IOException IOException
     */
    public static byte[] binaryDiff(InputStream oldInputStream, int oldSize, InputStream newInputStream, int newSize) throws IOException {

        byte[] oldBuf = new byte[oldSize];

        BinaryUtil.readFromStream(oldInputStream, oldBuf, 0, oldSize);
        oldInputStream.close();

        byte[] newBuf = new byte[newSize];
        BinaryUtil.readFromStream(newInputStream, newBuf, 0, newSize);
        newInputStream.close();

        return binaryDiff(oldBuf, oldSize, newBuf, newSize);
    }

    /**
     * @param oldBuffer oldBuf
     * @param oldSize oldSize
     * @param newBuffer newBuf
     * @param newSize newSize
     * @return byte[]
     * @throws IOException IOException
     */
    public static byte[] binaryDiff(byte[] oldBuffer, int oldSize, byte[] newBuffer, int newSize) throws IOException {

        int[] I = new int[oldSize + 1];
        qsufsort(I, new int[oldSize + 1], oldBuffer, oldSize);

        // diff block
        int diffBLockLen = 0;
        byte[] diffBlock = new byte[newSize];

        // extra block
        int extraBlockLen = 0;
        byte[] extraBlock = new byte[newSize];

        /*
         * Diff file is composed as follows:
         *
         * Header (32 bytes) Data (from offset 32 to end of file)
         *
         * Header:
         * Offset 0, length 8 bytes: file magic "MicroMsg"
         * Offset 8, length 8 bytes: length of compressed ctrl block
         * Offset 16, length 8 bytes: length of compressed diff block
         * Offset 24, length 8 bytes: length of new file
         *
         * Data:
         * 32 (length ctrlBlockLen): ctrlBlock (bzip2)
         * 32 + ctrlBlockLen (length diffBlockLen): diffBlock (bzip2)
         * 32 + ctrlBlockLen + diffBlockLen (to end of file): extraBlock (bzip2)
         *
         * ctrlBlock comprises a set of records, each record 12 bytes.
         * A record comprises 3 x 32 bit integers. The ctrlBlock is not compressed.
         */

        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        DataOutputStream diffOut = new DataOutputStream(byteOut);

        // Write as much of header as we have now. Size of ctrlBlock and diffBlock must be filled in later.
        diffOut.write(MAGIC_BYTES);
        diffOut.writeLong(-1); // place holder for ctrlBlockLen
        diffOut.writeLong(-1); // place holder for diffBlockLen
        diffOut.writeLong(newSize);
        diffOut.flush();

        GZIPOutputStream bzip2Out = new GZIPOutputStream(diffOut);
        DataOutputStream dataOut = new DataOutputStream(bzip2Out);

        int oldscore, scsc;

        int overlap, Ss, lens;
        int i;
        int scan = 0;
        int matchLen = 0;
        int lastscan = 0;
        int lastpos = 0;
        int lastoffset = 0;

        IntByRef pos = new IntByRef();
        // int ctrlBlockLen = 0;

        while (scan < newSize) {
            oldscore = 0;

            for (scsc = scan += matchLen; scan < newSize; scan++) {
                // 寻找 oldBuf[0...oldsize] 与  newBuf[scan...newSize] 的最长匹配段的长度. pos.value记录最长匹配段在oldBuf的起始位置，scan记录最长匹配段在newBuf的起始位置
                matchLen = search(I, oldBuffer, oldSize, newBuffer, newSize, scan, 0, oldSize, pos);

                for (; scsc < scan + matchLen; scsc++) {
                    if ((scsc + lastoffset < oldSize) && (oldBuffer[scsc + lastoffset] == newBuffer[scsc])) {
                        oldscore++;
                    }
                }

                if (((matchLen == oldscore) && (matchLen != 0)) || (matchLen > oldscore + 8)) { // 匹配段前有8个以上的字节不同
                    break;
                }

                if ((scan + lastoffset < oldSize) && (oldBuffer[scan + lastoffset] == newBuffer[scan])) {
                    oldscore--;
                }
            }

            if ((matchLen != oldscore) || (scan == newSize)) {  // (matchLen != oldscore)表示并非完全匹配，(scan == newsize)表示已经处理到new file的结尾了

                // 根据上面得到的匹配段，分别向前和后两个方向扩展，获取匹配集合，集合中的元素两两不相重叠。
                // new file中不在这个匹配集合中的段，将记录到diff中。
                int equalNum = 0;
                int Sf = 0;
                int lenFromOld = 0;
                for (i = 0; (lastscan + i < scan) && (lastpos + i < oldSize); ) {
                    if (oldBuffer[lastpos + i] == newBuffer[lastscan + i])
                        equalNum++;  // oldBuf和newBuf对应位置匹配的个数
                    i++;
                    if (equalNum * 2 - i > Sf * 2 - lenFromOld) {
                        Sf = equalNum;
                        lenFromOld = i; // 如果对应位置的匹配个数超过了一半，则记录到lenFromOld
                    }
                }

                int lenb = 0;
                if (scan < newSize) {
                    equalNum = 0;
                    int Sb = 0;
                    for (i = 1; (scan >= lastscan + i) && (pos.value >= i); i++) {
                        if (oldBuffer[pos.value - i] == newBuffer[scan - i])
                            equalNum++;  // 在最长匹配段之前对应位置的匹配个数
                        if (equalNum * 2 - i > Sb * 2 - lenb) {
                            Sb = equalNum;
                            lenb = i; // 如果在最长匹配段之前的i个字节对应位置的匹配个数超过了一半，则记录i值到lenb
                        }
                    }
                }

                if (lastscan + lenFromOld > scan - lenb) {
                    overlap = (lastscan + lenFromOld) - (scan - lenb);
                    equalNum = 0;
                    Ss = 0;
                    lens = 0;
                    for (i = 0; i < overlap; i++) {
                        if (newBuffer[lastscan + lenFromOld - overlap + i] == oldBuffer[lastpos + lenFromOld - overlap + i]) {
                            equalNum++;
                        }
                        if (newBuffer[scan - lenb + i] == oldBuffer[pos.value - lenb + i]) {
                            equalNum--;
                        }
                        if (equalNum > Ss) {
                            Ss = equalNum;
                            lens = i + 1;
                        }
                    }

                    lenFromOld += lens - overlap;
                    lenb -= lens;
                }

                // ? byte casting introduced here -- might affect things
                for (i = 0; i < lenFromOld; i++) {
                    diffBlock[diffBLockLen + i] = (byte) (newBuffer[lastscan + i] - oldBuffer[lastpos + i]);
                }

                for (i = 0; i < (scan - lenb) - (lastscan + lenFromOld); i++) {
                    extraBlock[extraBlockLen + i] = newBuffer[lastscan + lenFromOld + i];
                }

                diffBLockLen += lenFromOld;
                extraBlockLen += (scan - lenb) - (lastscan + lenFromOld);

                // Write control block entry (3 x int)
                dataOut.writeInt(lenFromOld);  // oldBuf中的一段匹配数据的长度
                dataOut.writeInt((scan - lenb) - (lastscan + lenFromOld));  // diffBuf中extraBlock的一段数据的长度
                dataOut.writeInt((pos.value - lenb) - (lastpos + lenFromOld));  // oldBuf应该跳过的一段无效匹配的长度

                lastscan = scan - lenb;
                lastpos = pos.value - lenb;
                lastoffset = pos.value - scan;
            } // end if
        } // end while loop

        dataOut.flush();
        bzip2Out.finish();

        // now compressed ctrlBlockLen
        int ctrlBlockLen = diffOut.size() - BinaryUtil.HEADER_SIZE;

        // GZIPOutputStream gzOut;

        /*
         * Write diff block
         */
        bzip2Out = new GZIPOutputStream(diffOut);
        bzip2Out.write(diffBlock, 0, diffBLockLen);
        bzip2Out.finish();
        bzip2Out.flush();
        int diffBlockLen = diffOut.size() - ctrlBlockLen - BinaryUtil.HEADER_SIZE;
        // System.err.println( "Diff: diffBlockLen=" + diffBlockLen );

        /*
         * Write extra block
         */
        bzip2Out = new GZIPOutputStream(diffOut);
        bzip2Out.write(extraBlock, 0, extraBlockLen);
        bzip2Out.finish();
        bzip2Out.flush();

        diffOut.close();

        /*
         * Write missing header info.
         */
        ByteArrayOutputStream byteHeaderOut = new ByteArrayOutputStream(BinaryUtil.HEADER_SIZE);
        DataOutputStream headerOut = new DataOutputStream(byteHeaderOut);
        headerOut.write(MAGIC_BYTES);
        headerOut.writeLong(ctrlBlockLen); // place holder for ctrlBlockLen
        headerOut.writeLong(diffBlockLen); // place holder for diffBlockLen
        headerOut.writeLong(newSize);
        headerOut.close();

        // Copy header information into the diff
        byte[] diffBytes = byteOut.toByteArray();
        byte[] headerBytes = byteHeaderOut.toByteArray();

        System.arraycopy(headerBytes, 0, diffBytes, 0, headerBytes.length);

        return diffBytes;
    }

    /**
     * Run JBDiff from the command line. Params: oldfile newfile difffile. diff
     * file will be created.
     *
     * @param args args
     * @throws IOException IOException
     */
    public static void main(String[] args) throws IOException {

        if (args.length != 3) {
            System.err.println("usage example: java -Xmx250m JBDiff oldfile newfile patchfile\n");
            return;
        }
        File oldFile = new File(args[0]);
        File newFile = new File(args[1]);
        File diffFile = new File(args[2]);

        binaryDiff(oldFile, newFile, diffFile);

    }

    private static class IntByRef {
        private int value;
    }
}
