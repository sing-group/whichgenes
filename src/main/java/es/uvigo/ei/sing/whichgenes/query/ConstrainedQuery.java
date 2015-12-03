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

package es.uvigo.ei.sing.whichgenes.query;

import java.util.LinkedList;
import java.util.List;

import es.uvigo.ei.sing.whichgenes.provider.GeneSetProvider;
import es.uvigo.ei.sing.whichgenes.provider.TreeNode;

public abstract class ConstrainedQuery implements Query {

	
	private boolean multiple=true; 
	
	private GeneSetProvider provider;
	
	public ConstrainedQuery(GeneSetProvider provider) {
		this.provider = provider;
	}
	public GeneSetProvider getProvider() {
		return this.provider;
	}
	public boolean isMultiple() {
		return multiple;
	}
	protected void setMultiple(boolean multiple) {
		this.multiple = multiple;
	}
	
	//public abstract TreeNode getNodeById(String id);
	
	public abstract List<TreeNode> getConstraints();
	public void requestStop() {		
	}	
	private List<TreeNode> constraintValues = new LinkedList<TreeNode>();
	public void setConstrainedValues(List<TreeNode> contraintValues){
		this.constraintValues.clear();
		this.constraintValues.addAll(contraintValues);
	}
	public List<TreeNode> getConstrainedValues(){
		return this.constraintValues;
	}
	@Override
	public String toString() {
		String toret = "";
		boolean first = true;
		for (TreeNode value : this.constraintValues){
			if (!first){
				toret+=", ";
				
			}else{
				first = false;
			}
			toret += value.getID();
		}
		return toret;
	}
	
	
	public TreeNode getSampleConstraint(){
		List<TreeNode> constraints = this.getConstraints(); 
		if (constraints.size()>0)
			return constraints.get(0);
		
		return null;
	}
}
