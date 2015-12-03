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

package es.uvigo.ei.sing.whichgenes.provider.decipher;

import java.util.LinkedList;
import java.util.List;

import es.uvigo.ei.sing.whichgenes.provider.DefaultTreeNode;
import es.uvigo.ei.sing.whichgenes.provider.GeneSetProvider;
import es.uvigo.ei.sing.whichgenes.provider.TreeNode;
import es.uvigo.ei.sing.whichgenes.provider.aautomator.AautomatorConstrainedQuery;
import es.uvigo.ei.sing.whichgenes.provider.aautomator.Util;

public class DecipherQuery extends AautomatorConstrainedQuery {

	public DecipherQuery(GeneSetProvider provider){
		super(provider, "hsa", "/es/uvigo/ei/sing/whichgenes/provider/decipher/id2ensembl.xml");
		this.resourceNamespace = "hgnc_symbol";
		this.outputNamespace = "hgnc_symbol";
	}
	
	@Override
	public List<TreeNode> getConstraints() {
		String[] catalog = Util.runRobot("/es/uvigo/ei/sing/whichgenes/provider/decipher/catalog.xml", new String[]{})[0].split("\n");
		List<TreeNode> nodes = new LinkedList<TreeNode>();
		for (String source:catalog){
			System.out.println(source);
			if (source.length()>0) {
				String[] idName = source.split("\t");			
				nodes.add(new DefaultTreeNode(idName[0],idName[1]));
			}
		}
		return nodes;
	}

	public String getParameterDescription() {
		return "Syndrome";
	}

	public String getParameterName() {
		return "Syndrome";
	}

	
	
}
