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

package es.uvigo.ei.sing.whichgenes.provider.dummy;

import java.util.LinkedList;
import java.util.List;

import es.uvigo.ei.sing.whichgenes.provider.DefaultTreeNode;
import es.uvigo.ei.sing.whichgenes.provider.TreeNode;
import es.uvigo.ei.sing.whichgenes.provider.aautomator.AautomatorQueryRunner;
import es.uvigo.ei.sing.whichgenes.query.ConstrainedQuery;
import es.uvigo.ei.sing.whichgenes.query.FreeQuery;
import es.uvigo.ei.sing.whichgenes.query.QueryHandler;

public class DummyQuery extends ConstrainedQuery {
	public DummyQuery() {
		super(null);
		// TODO Auto-generated constructor stub
	}
	public String getParameterName() {
		return "not important";
	}

	
	private String generateString(){
		String chars ="ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
		
		int stringlength = (int) ((5*Math.random()) + 2);
		String geneName = "";
		for (int j= 0; j<stringlength; j++){
			geneName+=chars.charAt((int) (chars.length()*Math.random()));
		}
		return geneName;
		
	}
	public void runQuery(final QueryHandler handler) {
		
		
		
		for (TreeNode s : this.getConstrainedValues()){
			
			handler.newResult(s.getID());
		}
		handler.finished();
		
	}
	@Override
	public void requestStop() {
		//this.runner.stop();
	}
	public String getParameterDescription() {
		return "ex: whatever you want";
	}


	@Override
	public List<TreeNode> getConstraints() {
		List<TreeNode> toret = new LinkedList<TreeNode>();
		
		
		TreeNode node = new DefaultTreeNode(generateString(),generateString());
		toret.add(node);
		
		DefaultTreeNode nodeWithChilds = new DefaultTreeNode(generateString(),generateString());
		List<TreeNode> childs = generateChilds(3);
		for (TreeNode child: childs){
			nodeWithChilds.addChild(child);
		}
		toret.add(nodeWithChilds);
		
		for (int i = 1; i<200; i++){
			node = new DefaultTreeNode(generateString(), generateString());
			toret.add(node);
		}
		
		return toret;
		
		
	}
	
	private List<TreeNode> generateChilds(int number){
		List<TreeNode> toret = new LinkedList<TreeNode>();
		for (int i = 0; i<number; i++){
			TreeNode node = new DefaultTreeNode(generateString(),generateString());
			toret.add(node);
		}
		return toret;
	}

}
