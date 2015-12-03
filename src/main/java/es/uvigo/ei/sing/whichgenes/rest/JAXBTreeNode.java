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

package es.uvigo.ei.sing.whichgenes.rest;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import javax.xml.bind.annotation.XmlRootElement;

import es.uvigo.ei.sing.whichgenes.provider.TreeNode;

@XmlRootElement(name = "term")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class JAXBTreeNode {

	
	private TreeNode node;
	
	@XmlElement(name = "term")
	public List<JAXBTreeNode> getChilds() {
		
		List<JAXBTreeNode> nodes = new LinkedList<JAXBTreeNode>();
		for (TreeNode child: this.node.getChilds()){
			nodes.add(new JAXBTreeNode(child));
		}
		return nodes;
	}
	
	
	@XmlAttribute(name = "id")
	public String getId() {
		
		return node.getID();
	}
	@XmlAttribute(name = "name")
	public String getName() {
		return node.getName();
	}
	public JAXBTreeNode() {
		// TODO Auto-generated constructor stub
	}
	public JAXBTreeNode(TreeNode node) {
		this.node = node;
		
		
	}
	
	

}
