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

package es.uvigo.ei.sing.whichgenes.provider.gmxprovider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class GMXManager {
	
	
	private List<String> setNames;
	private HashMap<String, List<String>> setContents = new HashMap<String, List<String>>(); 
	public GMXManager(String filepath) throws IOException{
		parseGMX(filepath);
		
	}
	private void parseGMX(String filepath) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(GMXManager.class.getResourceAsStream(filepath)));
		
		//set names
		
		String[] names = reader.readLine().split("\\t");		
		setNames = Arrays.asList(names);
		for (int i = 0; i<names.length; i++){
			setContents.put(names[i], new LinkedList<String>());
		}
		
		reader.readLine(); //skip na's line
		
		String line = null;
		
		while ((line = reader.readLine())!=null){
			String[] genesinline = line.split("\\t");
			
			for (int i = 0; i< genesinline.length; i++){
				if (genesinline[i].length()>0){
					setContents.get(names[i]).add(genesinline[i]);
				}
			}
		}
	}
	
	public List<String> getSetNames(){
		return Collections.unmodifiableList(setNames);
	}
	public List<String> getSetGenes(String setName){		
		return Collections.unmodifiableList(setContents.get(setName));
	}
}
