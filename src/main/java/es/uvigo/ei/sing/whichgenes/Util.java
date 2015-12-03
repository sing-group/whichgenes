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

package es.uvigo.ei.sing.whichgenes;

import javax.naming.InitialContext;
import javax.naming.NamingException;

public class Util {

	public static String getConfigParam(String param){
		if (System.getProperty(param)!=null){
			System.out.println("Parameter "+param+" is available as a system property, getting it from there");
			return System.getProperty(param);
		}else{
			try {
				
				// si estamos en un servlet, mirar si se ha definido la ruta al fichero de diccionario
				InitialContext ic = new InitialContext();
				return (String) ic.lookup("java:comp/env/"+param);
				
			} catch (NamingException e) {
				return System.getProperty(param);
			}
			
		}
	}
	public static String getHost(){
		return getConfigParam("whichgenes.host");
	}
	
}
