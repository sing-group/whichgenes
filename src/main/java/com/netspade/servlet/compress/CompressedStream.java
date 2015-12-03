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

import java.util.zip.GZIPOutputStream;

import javax.servlet.ServletOutputStream;

/**
 * Compressed implementation of {@link ServletOutputStream} using GZIP.
 *
 * @author Jason Davies
 * @version $Revision: 1.1 $
 */
public class CompressedStream extends ServletOutputStream
{
    private ServletOutputStream out;
    private GZIPOutputStream gzip;

    /**
     * Compresses the specified stream using the specified initial buffer.
     *
     * @param out the stream to compress.
     * @throws IOException if an error occurs with the {@link GZIPOutputStream}.
     */
    public CompressedStream(ServletOutputStream out) throws IOException
    {
        this.out = out;
        reset();
    }

    /** @see ServletOutputStream **/
    public void close() throws IOException
    {
        gzip.close();
    }

    /** @see ServletOutputStream **/
    public void flush() throws IOException
    {
        gzip.flush();
    }

    /** @see ServletOutputStream **/
    public void write(byte[] b) throws IOException
    {
        write(b, 0, b.length);
    }

    /** @see ServletOutputStream **/
    public void write(byte[] b, int off, int len) throws IOException
    {
        gzip.write(b, off, len);
    }

    /** @see ServletOutputStream **/
    public void write(int b) throws IOException
    {
        gzip.write(b);
    }

    /**
     * Resets the stream.
     *
     * @throws IOException if an I/O error occurs.
     */
    public void reset() throws IOException
    {
        gzip = new GZIPOutputStream(out);
    }
}
