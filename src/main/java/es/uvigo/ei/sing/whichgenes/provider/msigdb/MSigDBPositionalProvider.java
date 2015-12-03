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

package es.uvigo.ei.sing.whichgenes.provider.msigdb;

import es.uvigo.ei.sing.whichgenes.provider.AbstractGeneSetProvider;
import es.uvigo.ei.sing.whichgenes.query.Query;

public class MSigDBPositionalProvider extends AbstractGeneSetProvider {

	public MSigDBPositionalProvider() {
		super("MSigDB Positional GeneSets");
		super.setDescription("GeneSets of the MSigDB's C1 Collection (GSEA)");
	}

	public Query newQuery() {
		return new MSigDBPositionalQuery(this);
	}

	public String getOrganism() {
		return "homo sapiens";
	}

}
