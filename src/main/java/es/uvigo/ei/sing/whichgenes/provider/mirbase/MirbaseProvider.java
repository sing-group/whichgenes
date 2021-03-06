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

package es.uvigo.ei.sing.whichgenes.provider.mirbase;

import es.uvigo.ei.sing.whichgenes.provider.AbstractGeneSetProvider;
import es.uvigo.ei.sing.whichgenes.query.Query;

public class MirbaseProvider extends AbstractGeneSetProvider {

	private String specie;
	public MirbaseProvider(String specie) {
		super("miRBase Targets");
		super.setDescription("Uses miRBase Targets version 5 to retrieve genes as target of microRNAs");
		this.specie=specie;
	}

	public Query newQuery() {
		return new MirbaseQuery(this, specie);
	}

	public String getOrganism() {
		return this.specie;
	}

}
