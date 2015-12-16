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

package es.uvigo.ei.sing.whichgenes.provider.idconverter;

import java.util.Arrays;
import java.util.HashSet;

import es.uvigo.ei.sing.whichgenes.provider.aautomator.Util;

public class IDConverter {
	
	private static final HashSet<String> mouseIDs = new HashSet<String>();
	private static final HashSet<String> humanIDs = new HashSet<String>();
	
	public static HashSet<String> getMouseIDs(){
		return mouseIDs;
	}
	public static HashSet<String> getHumanIDs(){
		return humanIDs;
	}
	static{
		
		mouseIDs.add("external_gene_id");
		mouseIDs.add("ensembl_gene_id");
		mouseIDs.add("entrezgene");	
		mouseIDs.add("affy_mg_u74a");
		mouseIDs.add("affy_mg_u74av2");
		mouseIDs.add("affy_mg_u74b");
		mouseIDs.add("affy_mg_u74bv2");
		mouseIDs.add("affy_mg_u74c");
		mouseIDs.add("affy_mg_u74cv2");
		mouseIDs.add("affy_moe430a");
		mouseIDs.add("affy_moe430b");
		mouseIDs.add("affy_mouse430_2");
		mouseIDs.add("affy_mouse430a_2");
		mouseIDs.add("affy_mu11ksuba");
		mouseIDs.add("affy_mu11ksubb");
		mouseIDs.add("agilent_wholegenome");
		mouseIDs.add("affy_mogene_1_0_st_v1");
		mouseIDs.add("affy_moex_1_0_st_v1");
		mouseIDs.add("mgi_id");
		mouseIDs.add("mgi_symbol");
		
		
		
		humanIDs.add("ensembl_gene_id");
		humanIDs.add("embl");
		humanIDs.add("entrezgene");			
		humanIDs.add("refseq_dna");
		humanIDs.add("agilent_wholegenome");			
		humanIDs.add("agilent_cgh_44b");
		humanIDs.add("affy_hugenefl");
		humanIDs.add("affy_hg_u133a");
		humanIDs.add("affy_hg_u133a_2");
		humanIDs.add("affy_hg_u133b");
		humanIDs.add("affy_hg_u95av2");
		humanIDs.add("affy_hg_u95a");
		humanIDs.add("affy_hc_g110");
		humanIDs.add("affy_hg_focus");
		humanIDs.add("affy_hg_u133_plus_2");
		humanIDs.add("affy_hg_u95b");
		humanIDs.add("affy_hg_u95c");
		humanIDs.add("affy_hg_u95d");
		humanIDs.add("affy_hg_u95e");
		humanIDs.add("affy_huex_1_0_st_v2");
		humanIDs.add("affy_hugene_1_0_st_v1");
		humanIDs.add("affy_u133_x3p");
		
	}
	
	public static String getSpecieDBName(String speciecode){
		if (speciecode.equals("hsa"))
			return "hsapiens_gene_ensembl";
		if (speciecode.equals("mmu")){
			return "mmusculus_gene_ensembl";
		}
		return null;
		
	}
	public static String[] convert(String specie, String[] from, String to, String[] ids) {
		HashSet<String> results = new HashSet<String>();
		
		for (String fromNamespace: from) {
			results.addAll(Arrays.asList(convert(specie, fromNamespace, to, ids)));
		}
		
		return results.toArray(new String[0]);
	}
	public static String[] convert(String from, String to, String[] ids){
		return convert("hsapiens_gene_ensembl", from, to, ids);
	}
	public static String[] convert(String specie, String from, String to, String[] ids){
		if (ids.length==0){
			return new String[0];
		}
		String[] input = new String[4];
		input[0] = from;
		input[1] = to;
		input[2] = specie;
		if (getSpecieDBName(specie)!=null){
			input[2] = getSpecieDBName(specie);
		}
		input[3] = "";
		for (int i = 0; i<ids.length; i++){
			input[3] += (i>0?",":"")+ids[i];
		}
		String[] output = Util.runRobot("/es/uvigo/ei/sing/whichgenes/provider/idconverter/idconverter.xml", input);
		
		//System.out.println("idconverter: converted "+Arrays.toString(ids)+" to: "+Arrays.toString(output));
		return output;
	}
	
	public static void main(String[] args){
		String[] names = IDConverter.convert("entrezgene", "hgnc_symbol", new String[]{"675"});
		
		for (String name:names){
			System.out.println(name);
		}
	}
}
