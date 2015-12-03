/*
	This file is part of WhichGenes.

	WhichGenes is free software: you can redistribute it and/or modify
	it under the terms of the GNU Lesser General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.

	WhichGenes is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU Lesser General Public License for more details.

	You should have received a copy of the GNU Lesser General Public License
	along with Lesser.  If not, see <http://www.gnu.org/licenses/
*/

package es.uvigo.ei.sing.whichgenes.tomcat;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ExporterServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest arg0, HttpServletResponse arg1) throws ServletException, IOException {
		System.out.println("requesting");
		
		String content = (String) arg0.getSession().getAttribute("exported");
		String format = arg0.getParameter("f");
		if (content!=null){
			arg1.setContentType(format);
			arg1.setHeader("Content-Disposition", "filename=genesets."+format.substring(format.lastIndexOf('/')+1));
			arg1.getOutputStream().write(content.getBytes());
			arg1.getOutputStream().close();
			
		}
		else{
			arg1.getOutputStream().write("No content to export!\n".getBytes());
			arg1.getOutputStream().close();
		}

	}
	
}
