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

package es.uvigo.ei.sing.whichgenes.provider.ensemblcytobands;

import es.uvigo.ei.sing.whichgenes.provider.GeneSetProvider;
import es.uvigo.ei.sing.whichgenes.provider.aautomator.AautomatorFreeQuery;

public class EnsemblBandsQuery extends AautomatorFreeQuery {
	public EnsemblBandsQuery(GeneSetProvider provider, String specie){
		super(provider, specie, "/es/uvigo/ei/sing/whichgenes/provider/ensemblcytobands/ensemblbands.xml");		
		this.setMultiline(true);
	}
	
	public String getParameterName() {
		return "Range(s) (one per line)";
	}

	@Override
	protected String[] preprocessInput() {
		String[] bands = this.getQuery().split("\n");
		
		//checking
		for (String band: bands){
			
			if (band.indexOf("-")==-1){
				throw new RuntimeException("Some band is not of the form Chr<chromosome>:<start>-<end>");
			}
			if (band.indexOf(":")==-1){
				throw new RuntimeException("Some band is not of the form Chr<chromosome>:<start>-<end>");
			}
		}
		
		String[] input = new String[bands.length+2];
		if (this.specie.equals("homo sapiens")){
			input[0] = "hsapiens_gene_ensembl";
			input[1] = "hgnc_symbol";
			
		}else if (this.specie.equals("mus musculus")){
			input[0] = "mmusculus_gene_ensembl";
			input[1] = "mgi_symbol";
		}
		
		System.arraycopy(bands, 0, input, 2, bands.length);
		return input;
	}
	
	public String getParameterDescription() {		
		if (this.specie.equals("homo sapiens")) return "ex: Chr1:p36.33-p36.33";
		else if (this.specie.equals("mus musculus")) return "ex: Chr1:A1-A2";
		else return "error";
	}

	@Override
	public String sampleQuery() {
		if (this.specie.equals("homo sapiens")) return "Chr1:p36.33-p36.33";
		else if (this.specie.equals("mus musculus")) return "Chr1:A1-A2";
		return "Chr1:p36.33-p36.33"; 
	}

}
