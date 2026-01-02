package com.app.server.utils.file;

import org.springframework.stereotype.Component;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;


public class  LimitedInputStream extends FilterInputStream {
    private long remaining;

    public LimitedInputStream(InputStream in, long limit) {
        super(in);
        this.remaining = limit;
    }

    @Override
    public int read() throws IOException {
        if (remaining <= 0) {
            return -1; // End of stream
        }
        int result = super.read();
        if (result != -1) {
            remaining--;
        }
        return result;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        if (remaining <= 0) {
            return -1; // End of stream
        }
        // Only read up to remaining bytes
        int toRead = (int) Math.min(len, remaining);
        int result = super.read(b, off, toRead);
        if (result > 0) {
            remaining -= result;
        }
        return result;
    }

    @Override
    public long skip(long n) throws IOException {
        long toSkip = Math.min(n, remaining);
        long skipped = super.skip(toSkip);
        remaining -= skipped;
        return skipped;
    }

    @Override
    public int available() throws IOException {
        return (int) Math.min(super.available(), remaining);
    }
}