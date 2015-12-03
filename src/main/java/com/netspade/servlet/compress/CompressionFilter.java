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

import java.util.Enumeration;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Servlet filter for compressing output to the Web browser if supported.
 *
 * @author Jason Davies
 * @version $Revision: 1.1 $
 */
public class CompressionFilter implements Filter
{
    private FilterConfig config;

    /** @see Filter **/
    public void init(FilterConfig config) throws ServletException
    {
        this.config = config;
    }

    private static final String HAS_RUN_KEY = CompressionFilter.class.getName() + ".HAS_RUN";

    /** @see Filter **/
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException
    {
        boolean compress = false;
        if (request.getAttribute(HAS_RUN_KEY) == null
                && request instanceof HttpServletRequest)
        {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            Enumeration headers = httpRequest.getHeaders("Accept-Encoding");
            while (headers.hasMoreElements())
            {
                String value = (String) headers.nextElement();
                if (value.indexOf("gzip") != -1)
                {
                    compress = true;
                }
            }
        }
        request.setAttribute(HAS_RUN_KEY, "true");
        if (compress)
        {
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.addHeader("Content-Encoding", "gzip");
            CompressionResponse compressionResponse
                = new CompressionResponse(httpResponse);
            chain.doFilter(request, compressionResponse);
            compressionResponse.finish();
        }
        else
        {
            chain.doFilter(request, response);
        }
    }

    /** @see Filter **/
    public void destroy()
    {
        config = null;
    }
}
