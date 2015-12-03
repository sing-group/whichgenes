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

package es.uvigo.ei.sing.whichgenes.provider.intact;

import es.uvigo.ei.sing.whichgenes.provider.GeneSetProvider;
import es.uvigo.ei.sing.whichgenes.provider.aautomator.AautomatorQueryRunner;
import es.uvigo.ei.sing.whichgenes.provider.aautomator.Util;
import es.uvigo.ei.sing.whichgenes.provider.idconverter.IDConverter;
import es.uvigo.ei.sing.whichgenes.query.FreeQuery;
import es.uvigo.ei.sing.whichgenes.query.QueryHandler;

public class IntactQuery extends FreeQuery {

	
	public IntactQuery(GeneSetProvider provider){
		super(provider);
	}
	public String getParameterName() {
		return "gene(s) (one per line) ";
	}

	AautomatorQueryRunner runner = null;
	public void runQuery(final QueryHandler handler) {
		// create a robot
		
		String lines[] = this.getQuery().split("\n");
		for (String inputgene: lines){
				
			String[] genes = Util.runRobot("/es/uvigo/ei/sing/whichgenes/provider/intact/gene2uniprotkb.xml", new String[]{inputgene.trim()});
			
			if (genes.length>0){
				
				genes = IDConverter.convert("hsapiens_gene_ensembl", "uniprot_swissprot", "hgnc_symbol", genes);
				
				for (String gene: genes){
					if (!gene.equalsIgnoreCase(inputgene))
						handler.newResult(gene);
				}
			}
		}
		handler.finished();

	}
	
	@Override
	public void requestStop() {
		this.runner.stop();
	}
	public String getParameterDescription() {
		return "ex: BRCA2";
	}
	@Override
	public boolean isMultiline() {
		return true;
	}
	@Override
	public String sampleQuery() {
		return "BRCA2";
	}

}
