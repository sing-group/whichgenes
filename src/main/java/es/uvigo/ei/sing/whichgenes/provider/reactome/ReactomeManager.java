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

package es.uvigo.ei.sing.whichgenes.provider.reactome;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import es.uvigo.ei.sing.whichgenes.provider.aautomator.Util;


public class ReactomeManager {

	
	private static HashMap<String,List<String>> pathways =new HashMap<String, List<String>>();
	
	public static void releaseResources(){
		pathways = null;
		System.gc();
	}

	private static List<String> retrievePathways(String specie) {
		String[] pathways = Util.runRobot("/es/uvigo/ei/sing/whichgenes/provider/reactome/getpathways.xml", new String[]{specie});
		List<String> list = new LinkedList<String>();
		for (String path: pathways){
			list.add(path);
		}
		return list;
	}
	
	
	public static List<String> getPathways(String specie){
		if (pathways.get(specie) == null){
			pathways.put(specie,retrievePathways(specie));
			Collections.sort(pathways.get(specie));
		}
		return pathways.get(specie);
	}
		
	public static Collection<String> retrieveGenesFromPathay(String pathid, String specie) throws RemoteException {
		String[] genes = Util.runRobot("/es/uvigo/ei/sing/whichgenes/provider/reactome/getensbypath.xml", new String[]{pathid});
		
		
		
		
		ArrayList<String> toret = new ArrayList<String>(genes.length);
		
		for (String gene : genes){
			toret.add(gene);
		}
		return toret;
		
		
	}
	
}
