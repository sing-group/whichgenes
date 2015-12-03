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

package es.uvigo.ei.sing.whichgenes.provider;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import es.uvigo.ei.sing.whichgenes.Entity;

public class DefaultTreeNode extends Entity implements TreeNode {	
	private static final long serialVersionUID = 1L;

	private String id;
	public DefaultTreeNode(String id, String name){
		super(name);
		this.id = id;
	}
	
	private LinkedList<TreeNode> childs = new LinkedList<TreeNode>();
	
	public List<TreeNode> getChilds(){
		return Collections.unmodifiableList(childs);		
	}
	
	public void addChild(TreeNode child){
		this.childs.add(child);
	}
	
	public boolean hasChilds(){
		return this.getChilds().size() != 0;
	}

	public String getID() {
		return this.id;
	}
}
