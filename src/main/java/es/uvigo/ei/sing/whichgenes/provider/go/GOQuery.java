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

package es.uvigo.ei.sing.whichgenes.provider.go;

import java.util.LinkedList;
import java.util.List;

import es.uvigo.ei.sing.whichgenes.provider.GeneSetProvider;
import es.uvigo.ei.sing.whichgenes.provider.TreeNode;
import es.uvigo.ei.sing.whichgenes.provider.aautomator.AautomatorConstrainedQuery;

public class GOQuery extends AautomatorConstrainedQuery {

	public GOQuery(GeneSetProvider provider) {
		super(provider, "hsa", "/es/uvigo/ei/sing/whichgenes/provider/go/GO2GENE.xml");
		this.resourceNamespace = "uniprot_swissprot";
		this.outputNamespace = "hgnc_symbol";

	}

	private class GOTreeNode implements TreeNode{

		private String id;
		public GOTreeNode(String id){
			this.id = id;
		}
		public List<TreeNode> getChilds() {
			LinkedList<TreeNode> toret = new LinkedList<TreeNode>();
			for (String child: GOManager.childsOf(this.id)){
				toret.add(new GOTreeNode(child));
			}
			return toret;
		}

		public String getName() {
			return GOManager.getName(this.id);
		}
		public String getID() {
			return id;
		}
		
	}
	@Override
	public List<TreeNode> getConstraints() {
		List<TreeNode> toret = new LinkedList<TreeNode>();
		
		for (String element:GOManager.getRoots()){
			toret.add(new GOTreeNode(element));
		}
		return toret;
	}

	public String getParameterDescription() {
		return "GO Term";
	}

	public String getParameterName() {
		return "GO Term";
	}
	@Override
	public TreeNode getSampleConstraint() {
		return new TreeNode(){

			public List<TreeNode> getChilds() {
				// TODO Auto-generated method stub
				return null;
			}

			public String getID() {
				return "GO:0022610";
			}

			public String getName() {
				return "biological adhesion";
			}
			
		};
	}

	

}
