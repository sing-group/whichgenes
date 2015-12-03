/**
 * The NetSpade Compression Filter
 *
 * Copyright (C) 2004 Jason Davies.
 * 
 * This file is part of the NetSpade Compression Filter.
 *
 * The NetSpade Compression Filter is free software;
 * you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * Foobar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Foobar; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * More information about this project can be found at:
 * http://www.netspade.com/software/java/compression-filter/
 */
package com.netspade.servlet.compress;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * Compresses a {@link HttpServletResponse} using GZIP compression.
 *
 * @author Jason Davies
 * @version $Revision: 1.1 $
 */
public class CompressionResponse extends HttpServletResponseWrapper
{
    private HttpServletResponse response;
    private ServletOutputStream out;
    private CompressedStream compressedOut;
    private PrintWriter writer;
    private int contentLength;

    /**
     * Creates a new compressed response wrapping the specified HTTP response.
     *
     * @param response the HTTP response to wrap.
     * @throws IOException if an I/O error occurs.
     */
    public CompressionResponse(HttpServletResponse response)
        throws IOException
    {
        super(response);
        this.response = response;
        compressedOut
            = new CompressedStream(response.getOutputStream());
    }

    /**
     * Ignore attempts to set the content length since the actual content
     * length will be determined by the GZIP compression.
     *
     * @param len the content length
     */
    public void setContentLength(int len)
    {
        contentLength = len;
    }

    /** @see HttpServletResponse **/
    public ServletOutputStream getOutputStream() throws IOException
    {
        if (null == out)
        {
            if (null != writer)
            {
                throw new IllegalStateException("getWriter() has already been "
                        + "called on this response.");
            }
            out = compressedOut;
        }
        return out;
    }

    /** @see HttpServletResponse **/
    public PrintWriter getWriter() throws IOException
    {
        if (null == writer)
        {
            if (null != out)
            {
                throw new IllegalStateException("getOutputStream() has "
                        + "already been called on this response.");
            }
            writer = new PrintWriter(compressedOut);
        }
        return writer;
    }

    /** @see HttpServletResponse **/
    public void flushBuffer()
    {
        try
        {
            if (writer != null)
            {
                writer.flush();
            }
            else if (out != null)
            {
                out.flush();
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /** @see HttpServletResponse **/
    public void reset()
    {
        super.reset();
        try
        {
            compressedOut.reset();
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    /** @see HttpServletResponse **/
    public void resetBuffer()
    {
        super.resetBuffer();
        try
        {
            compressedOut.reset();
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * Finishes writing the compressed data to the output stream.
     * Note: this closes the underlying output stream.
     *
     * @throws IOException if an I/O error occurs.
     */
    public void finish() throws IOException
    {
        if (writer != null)
        {
            writer.close();
        }
        else if (out != null)
        {
            out.close();
        }
    }
}
