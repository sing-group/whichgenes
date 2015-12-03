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

package es.uvigo.ei.sing.whichgenes.zkgui;

import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Vbox;

import es.uvigo.ei.sing.whichgenes.geneset.Gene;
import es.uvigo.ei.sing.whichgenes.geneset.GeneSet;

public class GeneSetEditor extends Vbox {

	private static final long serialVersionUID = 1L;

	
	
	private EnhancedGrid grid;
	private GeneSet geneSet;
	private Label titleLabel;
	
	public GeneSetEditor(){
		initialize();
	}
	public void setListHeight(String height){
		this.grid.getListBox().setHeight(height);
	}

	private void initialize() {
		
		//this.appendChild(getTitleLabel());
		this.appendChild(getGrid());
		
		
	}
	
	private Label getTitleLabel() {
		if (this.titleLabel == null){
			this.titleLabel = new Label();
			this.titleLabel.setValue("Gene Set: ");
			
		}
		return this.titleLabel;
	}

	private EnhancedGrid getGrid() {
		if (this.grid == null){
			this.grid = new EnhancedGrid();
			this.grid.setRowTitles(new String[]{"Gene"});
			this.grid.setWidth("100%");
			this.grid.setShowClearAll(false);
			this.grid.setMultiple(true);
			this.grid.setShowSelectionButtons(false);
			
			this.grid.getListBox().setMold("paging");
			this.grid.getListBox().setPageSize(100);
			
			this.grid.addEventListener(EnhancedGrid.ONREMOVE_EVENT, new EventListener(){
				
				public void onEvent(Event arg0) throws Exception {

					geneSet.clear();
					
				}
				
			});
			this.grid.addEventListener(EnhancedGrid.ONREMOVED_EVENT, new EventListener(){

				public void onEvent(Event arg0) throws Exception {
					List<Listitem> items = (List<Listitem>) arg0.getData();
					for (Listitem item : items){
						for (Object cellobj:item.getChildren()){
							Listcell cell = (Listcell) cellobj;
							String geneName = cell.getLabel();
							LinkedList<Gene> copy = new LinkedList<Gene>();						
							copy.addAll(geneSet.getGenes());
							for (Gene gene: copy){
								if (gene.getName().equals(geneName)){
									geneSet.removeGene(gene);
									
								}
							}
						}
					}
					
				}
				
			});
		}
		return this.grid;

	}

	private GeneSetObserver observer = new GeneSetObserver();
	private class GeneSetObserver implements Observer{

		public void update(Observable arg0, Object arg1) {
			grid.clear();
			GeneSet set = (GeneSet) arg0;
			
			grid.setRowTitles(new String[]{set.getName()});
			for (Gene gene : set.getGenes()){
				grid.addRow(new String[]{gene.getName()});
			}
			
		}
		
	}
	public void setGeneSet(GeneSet set){
		if (this.geneSet != null){
			this.geneSet.deleteObserver(this.observer);
		}
		
		this.geneSet = set;
		
		if (set!=null){
			this.geneSet.addObserver(this.observer);
		
			this.observer.update(this.geneSet, null);
		}else{
			noSet();
		}
		
	}
	public GeneSet getGeneSet(){
		return this.geneSet;
	}
	private void noSet(){
		this.grid.clear();
		//this.getTitleLabel().setValue("Gene Set: ");
	}
}
