package com.base.core.web.result;

import javax.servlet.ServletOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;


public class BufferServletOutputStream extends ServletOutputStream {

    ByteArrayOutputStream output;

    public BufferServletOutputStream(ByteArrayOutputStream out) {
        this.output = out;
    }


    public void write(int arg0) throws IOException {
        this.output.write(arg0);
    }


    public void write(byte[] b) throws IOException {
        this.output.write(b);
    }


    public void write(byte[] b, int off, int len) throws IOException {
        this.output.write(b, off, len);
    }


    public void print(String s) throws IOException {
        super.print(s);

    }


    public void println(String s) throws IOException {
        super.println(s);
    }

}
