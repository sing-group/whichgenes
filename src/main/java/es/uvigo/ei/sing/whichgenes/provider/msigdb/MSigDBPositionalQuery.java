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

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import es.uvigo.ei.sing.whichgenes.provider.DefaultTreeNode;
import es.uvigo.ei.sing.whichgenes.provider.GeneSetProvider;
import es.uvigo.ei.sing.whichgenes.provider.TreeNode;
import es.uvigo.ei.sing.whichgenes.provider.aautomator.AautomatorConstrainedQuery;
import es.uvigo.ei.sing.whichgenes.provider.aautomator.Util;

public class MSigDBPositionalQuery extends AautomatorConstrainedQuery {

	
	public MSigDBPositionalQuery(GeneSetProvider provider) {
		super(provider, "hsa", "/es/uvigo/ei/sing/whichgenes/provider/msigdb/getgenes.xml");
	}

	@Override
	public List<TreeNode> getConstraints() {
		List<TreeNode> nodes = new LinkedList<TreeNode>();
		String[] sets = Util.runRobot("/es/uvigo/ei/sing/whichgenes/provider/msigdb/getcollection.xml", new String[]{"C1"});
		for (String set:sets){
			String name = set;
			String id = set;			
			nodes.add(new DefaultTreeNode(id,name));
		}
		return nodes;
	}

	public String getParameterDescription() {
		return "Gene Sets";
	}

	public String getParameterName() {
		return "Gene Sets";
	}

	
	public static void main(String[] args){
		MSigDBPositionalQuery q = new MSigDBPositionalQuery(null);
		for (TreeNode node: q.getConstraints()){
			System.out.println(node.getName());
		}
	}

}
