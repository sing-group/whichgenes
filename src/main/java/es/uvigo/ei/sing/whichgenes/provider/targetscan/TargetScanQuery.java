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

package es.uvigo.ei.sing.whichgenes.provider.targetscan;

import es.uvigo.ei.sing.whichgenes.provider.GeneSetProvider;
import es.uvigo.ei.sing.whichgenes.provider.aautomator.AautomatorQueryRunner;
import es.uvigo.ei.sing.whichgenes.query.FreeQuery;
import es.uvigo.ei.sing.whichgenes.query.QueryHandler;

public class TargetScanQuery extends FreeQuery {

	public TargetScanQuery(GeneSetProvider provider) {
		super(provider);
	}
	public String getParameterName() {
		return "miRNAs (one per line) ";
	}

	AautomatorQueryRunner runner = null;
	public void runQuery(final QueryHandler handler) {
		this.runner = new AautomatorQueryRunner();
		String[] query = this.query.split("\n");
		if (this.query.indexOf("\n") ==-1){
			query = new String[]{this.query};
		}
		System.out.println("Target scan: Searching for "+query.length+" miRNAs"+ "q: "+this.query);
		this.runner.runQuery("/es/uvigo/ei/sing/whichgenes/provider/targetscan/gettargets.xml", handler, query);				
		
	}
	@Override
	public void requestStop() {
		this.runner.stop();
	}
	public String getParameterDescription() {
		return "ex: hsa-miR-181a, mmu-miR-182";
	}
	@Override
	public boolean isMultiline() {
		return true;
	}
	@Override
	public String sampleQuery() {
		return "hsa-miR-181a";
	}

}
