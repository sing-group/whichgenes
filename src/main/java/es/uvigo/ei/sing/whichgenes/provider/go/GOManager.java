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

package es.uvigo.ei.sing.whichgenes.provider.go;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;




import org.geneontology.oboedit.dataadapter.DefaultOBOParser;
import org.geneontology.oboedit.dataadapter.OBOParseEngine;
import org.geneontology.oboedit.dataadapter.OBOParseException;
import org.geneontology.oboedit.datamodel.OBOClass;
import org.geneontology.oboedit.datamodel.OBORestriction;
import org.geneontology.oboedit.datamodel.OBOSession;

import es.uvigo.ei.sing.whichgenes.Util;


public class GOManager {

	private static String FILE_PATH = (GOManager.class.getProtectionDomain().getCodeSource().getLocation()+"/../gene_ontology_edit.obo").substring(5);
	public static OBOSession session;
	public static void closeSession(){
		
		session = null;
		System.gc();
	}
	
	private static OBOSession getSession(){
		if (session == null){
			createSession();
		}
		return session;
	}
	public static List<String> childsOf(String id){
		List<String> toret = new LinkedList<String>();
		OBOClass element= getSession().getTerm(id);
		Set set = element.getChildren();
		
		for (Object o : set.toArray()){
			Object child =  ((OBORestriction)o).getChild();
			if (child instanceof OBOClass){
				toret.add(((OBOClass)child).getID());		
			}
		}
		Collections.sort(toret, new Comparator<String>(){

			public int compare(String arg0, String arg1) {
				return GOManager.getName(arg0).compareTo(GOManager.getName(arg1));
			}
			
		});
		return toret;
	}
	private static void createSession() {
//		parse the OBO file
		
		FILE_PATH = Util.getConfigParam("goprovider.obopath");
	 	
		DefaultOBOParser parser = new DefaultOBOParser();
		OBOParseEngine engine = new OBOParseEngine(parser);
		
		// GOBOParseEngine can parse several files at once
		// and create one munged-together ontology,
		// so we need to provide a Collection to the setPaths() method
		Collection paths = new LinkedList();
		
		System.out.println("Trying to load Gene Ontology from: "+FILE_PATH);
		paths.add(FILE_PATH);
		
		
		engine.setPaths(paths);
		try {
			engine.parse();
			session = parser.getSession();
			System.out.println("Gene Ontology loaded");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OBOParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}
	public static String getName(String id){
		return getSession().getTerm(id).getName();
	}
	public static List<String> getRoots(){
		List<String> toret = new LinkedList<String>();
		Set roots = getSession().getRoots();
		
		Iterator it = roots.iterator();
		while(it.hasNext()){
			OBOClass element = (OBOClass) it.next();
			if (element.getNamespace()!=null){
				toret.add(element.getID());
			}
		}
		Collections.sort(toret, new Comparator<String>(){

			public int compare(String arg0, String arg1) {
				return GOManager.getName(arg0).compareTo(GOManager.getName(arg1));
			}
			
		});
		return toret;
	}
	
	public static void main(String args[]){
		
		Set roots = getSession().getRoots();
		Iterator it = roots.iterator();
		while(it.hasNext()){
			
			OBOClass element = (OBOClass) it.next();
		
			System.out.println(element.getName());
			if (element.getNamespace()!=null){
				System.out.println("Children of: "+element.getName()+" "+element.getID());
				for (String child : childsOf(element.getID())){
					System.out.println("\t"+child+": "+getName(child));
				}
			}
		}
	}
}

