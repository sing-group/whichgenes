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

import es.uvigo.ei.sing.whichgenes.provider.AbstractGeneSetProvider;
import es.uvigo.ei.sing.whichgenes.query.Query;

public class IntactProvider extends AbstractGeneSetProvider {

	
	public IntactProvider() {
		super("IntAct interacting genes");
		super.setDescription("Uses IntAct to get genes whose proteins interact with the protein of a given gene(s)");
		
	}

	public Query newQuery() {
		return new IntactQuery(this);
	}

	public String getOrganism() {
		return "homo sapiens";
	}

}
