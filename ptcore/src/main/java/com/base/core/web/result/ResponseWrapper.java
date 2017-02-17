package com.base.core.web.result;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.*;

public class ResponseWrapper extends HttpServletResponseWrapper {

    ByteArrayOutputStream os = new ByteArrayOutputStream();
    BufferServletOutputStream outputStream = null;
    PrintWriter printWriter = null;

    public ResponseWrapper(HttpServletResponse response) {
        super(response);

    }

    public ServletOutputStream getOutputStream() throws IOException {
        if (outputStream == null) {
            outputStream = new BufferServletOutputStream(os);
        }
        return outputStream;
    }

    public PrintWriter getWriter() throws IOException {
        if (printWriter == null) {
            printWriter = new PrintWriter(new OutputStreamWriter(getOutputStream(), this.getCharacterEncoding()));
        }
        return printWriter;
    }

    public void flushBuffer() throws IOException {
        super.flushBuffer();
        if (outputStream != null) {
            outputStream.flush();
        }
        if (printWriter != null) {
            printWriter.flush();
        }
    }

    public void reset() {
        super.reset();
        os.reset();
    }

    public byte[] toByteArray() throws IOException {
        this.flushBuffer();
        return os.toByteArray();
    }

    public void writeTo(OutputStream outputStream) throws IOException {
        this.flushBuffer();
        os.writeTo(outputStream);
    }

    protected void finalize() {
        try {
            super.finalize();
            if (printWriter != null) {
                printWriter.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }

            if (os != null) {
                os.close();
            }


        } catch (Throwable e) {
            e.printStackTrace();
        }

    }

    public String getContent() {
        String content = "";
        try {
            this.flushBuffer();
            content = os.toString(this.getCharacterEncoding());            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content;
    }

}
