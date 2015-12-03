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

package es.uvigo.ei.sing.whichgenes.geneset;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import es.uvigo.ei.sing.whichgenes.Entity;

public class GeneSet extends Entity{
	private static final long serialVersionUID = 1L;

	public GeneSet(String name) {
		super(name);
		
	}
	
	private HashSet<Gene> genes = new HashSet<Gene>();
	
	public void setGenes(Collection<Gene> genes){
		genes.clear();
		genes.addAll(genes);
		this.setChanged();
		this.notifyObservers();
	}
	public void addGene(Gene g){
		genes.add(g);
		this.setChanged();
		this.notifyObservers();
	}
	
	
	public void clear(){
		genes.clear();
		this.setChanged();
		this.notifyObservers();
	}
	
	public void removeGene(Gene g){
		genes.remove(g);
		this.setChanged();
		this.notifyObservers();
	}
	public List<Gene> getGenes(){
		ArrayList<Gene> toret = new ArrayList<Gene>(genes.size());
		toret.addAll(genes);
		return toret;
	}
	


}
