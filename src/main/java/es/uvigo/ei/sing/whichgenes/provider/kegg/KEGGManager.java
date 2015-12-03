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

package es.uvigo.ei.sing.whichgenes.provider.kegg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class KEGGManager {

	
	private static HashMap<String,List<String>> pathways =new HashMap<String, List<String>>();
	
	public static void releaseResources(){
		pathways = null;
		System.gc();
	}


	private static List<String> retrievePathways(String specie) throws IOException {
		List<String> toret = new LinkedList<String>();
		URLConnection conn = new URL("http://rest.kegg.jp/list/pathway/"+specie).openConnection();
		System.out.println("connected to kegg: "+conn.getURL().toString());
		BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String line = null;
			
		while ((line = reader.readLine())!=null){
			String[] pathwayTokens = line.split("\t");
			System.err.println(Arrays.toString(pathwayTokens));
			toret.add(pathwayTokens[1]+" ["+pathwayTokens[0]+"]");
		}
	
		return toret;
	}
	
	
	public static List<String> getPathways(String specie) throws IOException{
		if (pathways.get(specie) == null){
			pathways.put(specie,retrievePathways(specie));
			Collections.sort(pathways.get(specie));
		}
		return pathways.get(specie);
	}
		
	public static Collection<String> retrieveGenesFromPathay(String pathid) throws IOException{
		HashSet<String> toret = new HashSet<String>();

		URLConnection conn = new URL("http://rest.kegg.jp/link/"+pathid.substring(5, 8)+"/"+pathid.substring(5)).openConnection();
		System.out.println("connected to kegg: "+conn.getURL().toString());
		BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String line = null;
			
		while ((line = reader.readLine())!=null){
			String[] genesTokens = line.split("\t");
			if (genesTokens.length == 2) {
				toret.add(genesTokens[1]);
			}
		}
		return toret;
		
	}
}
