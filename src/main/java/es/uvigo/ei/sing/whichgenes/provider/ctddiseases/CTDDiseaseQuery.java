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

package es.uvigo.ei.sing.whichgenes.provider.ctddiseases;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import es.uvigo.ei.sing.whichgenes.provider.GeneSetProvider;
import es.uvigo.ei.sing.whichgenes.provider.ctd.CTDQuery;
import es.uvigo.ei.sing.whichgenes.query.QueryHandler;

public class CTDDiseaseQuery extends CTDQuery {

	
	public CTDDiseaseQuery(GeneSetProvider provider, String specie){
		super(provider, specie, "/es/uvigo/ei/sing/whichgenes/provider/ctddiseases/disease2genes_"+specie+".xml");
	}
	public String getParameterName() {
		return "disease name";
	}
	public String getParameterDescription() {
		return "ex: Acute, Myeloid, Leukemia";
	}
	@Override
	public String sampleQuery() {
		// TODO Auto-generated method stub
		return "Acute, Myeloid, Leukemia";
	}
	
	@Override
	public void runQuery(final QueryHandler handler) {
		try {
			this.query = URLEncoder.encode(this.query,"UTF-8");
			super.runQuery(handler);
		} catch (UnsupportedEncodingException e) {	
			e.printStackTrace();
		}						
		
	}
}
