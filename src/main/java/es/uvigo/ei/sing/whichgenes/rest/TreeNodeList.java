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

import java.util.List;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAccessType;

@XmlRootElement(name = "Catalog")
@XmlAccessorType(XmlAccessType.PROPERTY)

public class TreeNodeList {
	
	@XmlElement(name = "term")
	public List<JAXBTreeNode> getNodes() {	
		
		return nodes;
	}
	public TreeNodeList() {
		// TODO Auto-generated constructor stub
	}
	private List<JAXBTreeNode> nodes;
	public TreeNodeList(List<JAXBTreeNode> nodes) {
		this.nodes = nodes;
	}
}
