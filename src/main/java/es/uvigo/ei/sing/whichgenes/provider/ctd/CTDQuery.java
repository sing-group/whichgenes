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

package es.uvigo.ei.sing.whichgenes.provider.ctd;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import es.uvigo.ei.sing.whichgenes.provider.GeneSetProvider;
import es.uvigo.ei.sing.whichgenes.provider.aautomator.AautomatorFreeQuery;
import es.uvigo.ei.sing.whichgenes.query.QueryHandler;

public class CTDQuery extends AautomatorFreeQuery {
	public CTDQuery(GeneSetProvider provider, String specie){		
		this(provider, specie, "/es/uvigo/ei/sing/whichgenes/provider/ctd/chem2genes_"+specie+".xml");
	}
	public CTDQuery(GeneSetProvider provider, String specie, String resourcePath) {
		super(provider, specie,resourcePath);
		if (specie.equalsIgnoreCase("hsa")){
		/*	this.resourceNamespace= "hgnc_symbol";
			this.outputNamespace= "hgnc_symbol";*/
		}
		else if (specie.equalsIgnoreCase("mmu")){
		/*	this.resourceNamespace = "mgi_symbol";
			this.outputNamespace = "mgi_symbol";*/
			
		}
	}
	public String getParameterName() {
		return "chemical name";
	}
	
	
	public void runQuery(final QueryHandler handler) {
		//try {
			//this.query = URLEncoder.encode(this.query,"UTF-8");
			super.runQuery(handler);
	/*	} catch (UnsupportedEncodingException e) {	
			e.printStackTrace();
		}*/						
		
	}
	
	
	public String getParameterDescription() {
		return "ex: arsenic";
	}
	@Override
	public String sampleQuery() {		
		return "arsenic";
	}
	
	

}
