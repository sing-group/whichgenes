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

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;



public class Basket extends Observable implements Serializable{	
	private static final long serialVersionUID = 1L;

	private List<GeneSet> sets = new LinkedList<GeneSet>();
	
	
	private HashMap<String, GeneSet> setsByName = new HashMap<String, GeneSet>();
	public List<GeneSet> getSets(){
		return sets;
	}
	
	public GeneSet getByName(String setName){
		return this.setsByName.get(setName);
	}
	
	private String generateUniqueName(String name){
		if (this.getByName(name) == null){
			return name;
		}
		
		int count = 1;
		while(this.getByName(name)!=null){
			name = name+"_"+count;
			count++;
		}
		return name;
	}
	public void addGeneSet(GeneSet set){
		if (this.setsByName.containsKey(set.getName())){			
			//throw new RuntimeException("there is a gene set with the same name");
			System.out.println("Generating unique name for "+set);
			set.setName(generateUniqueName(set.getName()));
		}
		
		
		
		this.sets.add(set);
		this.setsByName.put(set.getName(), set);
		this.setChanged();
		this.notifyObservers();
		
	}
	
	public void removeGeneSet(GeneSet set){
		if (this.setsByName.containsKey(set.getName())){
			sets.remove(set);
			this.setsByName.remove(set.getName());
			this.setChanged();
			this.notifyObservers();
		}
	}
	
	public void clear(){
		this.setsByName.clear();
		this.sets.clear();
		this.setChanged();
		this.notifyObservers();
	}
	
	public void copy(Basket b){
		this.sets = b.sets;
		this.setsByName = b.setsByName;
		this.setChanged();
		this.notifyObservers();
	}
	
	transient private Vector<Observer> obs = new Vector<Observer>(); 
	@Override
	public synchronized void addObserver(Observer o) {
		// TODO Auto-generated method stub
		if (!this.obs.contains(o)){
			this.obs.add(o);
		}
	}
	@Override
	public synchronized void deleteObserver(Observer o) {
		this.obs.remove(o);
	}
	@Override
	public synchronized int countObservers() {
		return this.obs.size();
	}
	
	@Override
	public void notifyObservers() {
		notifyObservers(null);
	}
	@Override
	public void notifyObservers(Object arg) {
		for (Observer o : this.obs){
			try{				
				o.update(this,arg);
			}catch(Exception e){
				System.out.println("Exception during notification of observer");
				e.printStackTrace();
			}
		}
	}
	
	public void renameSet(GeneSet s, String newName){
		if (!newName.equals(s.getName()) && this.setsByName.containsKey(newName)){
			throw new RuntimeException("there is a gene set with the same name");
		}
		int index = this.sets.indexOf(s);
		
		if (index == -1) throw new IllegalArgumentException("set doesn't exists");
	
		GeneSet copy = new GeneSet(newName);
		for (Gene g: s.getGenes()){
			copy.addGene(g);
		}
		this.sets.remove(index);
		this.setsByName.remove(s.getName());
		this.sets.add(index, copy);
		this.setsByName.put(copy.getName(),copy);
		this.setChanged();
		this.notifyObservers();
	}
	

}
