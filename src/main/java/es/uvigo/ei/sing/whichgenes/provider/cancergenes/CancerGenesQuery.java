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

package es.uvigo.ei.sing.whichgenes.provider.cancergenes;

import java.util.LinkedList;
import java.util.List;

import es.uvigo.ei.sing.whichgenes.provider.DefaultTreeNode;
import es.uvigo.ei.sing.whichgenes.provider.GeneSetProvider;
import es.uvigo.ei.sing.whichgenes.provider.TreeNode;
import es.uvigo.ei.sing.whichgenes.provider.aautomator.AautomatorConstrainedQuery;
import es.uvigo.ei.sing.whichgenes.provider.aautomator.Util;

public class CancerGenesQuery extends AautomatorConstrainedQuery {

	public CancerGenesQuery(GeneSetProvider provider) {
		super(provider, "/es/uvigo/ei/sing/whichgenes/provider/cancergenes/getgenes.xml");
	}
	@Override
	public List<TreeNode> getConstraints() {
		String[] catalog = Util.runRobot("/es/uvigo/ei/sing/whichgenes/provider/cancergenes/catalog.xml", new String[]{})[0].split("\n");
		List<TreeNode> nodes = new LinkedList<TreeNode>();
		
		boolean first = true; //skip first set "All genes" it doesnt returns anything
		for (String source:catalog){
			String[] idName = source.split("\t");
			
			
			if (first)
				first=false;
			else
				nodes.add(new DefaultTreeNode(idName[0],idName[1]));
		}
		return nodes;
	}

	public String getParameterDescription() {
		return "Source";
	}

	public String getParameterName() {
		return "Sources";
	}


}
