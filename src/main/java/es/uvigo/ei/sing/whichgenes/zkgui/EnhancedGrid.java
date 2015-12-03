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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zul.Button;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vbox;

public class EnhancedGrid extends Vbox {	
	private static final long serialVersionUID = 1L;
	
	private Listbox listbox;
	private Button clearTableButton;
	private Button removeSelectedButton;
	private Button clearSelectionButton;
	private Button selectAllButton;
	private Button invertSelectionButton;
	
	private Label filterLabel;
	private Textbox filterbox;
	private String filter;
	
	public static final String ONREMOVE_EVENT = "onRemove";
	public static final String ONREMOVED_EVENT = "onRemoved";
	public static final String ONSELECTIONCHANGE_EVENT = "onSelectionChange";
	
	private void initialize(){
		
		this.appendChild(getListBox());
		Hbox controls = new Hbox();
		
		controls.appendChild(getSelectAllButton());
		controls.appendChild(getClearSelectionButton());
		controls.appendChild(getInvertSelectionButton());
		controls.appendChild(getClearTableButton());
		controls.appendChild(getRemoveSelectedButton());
		
		this.listbox.setCheckmark(true);
		this.appendChild(new Hbox(new Component[]{this.getFilterLabel(), this.getFilterBox()}){{this.setWidth("100%");}});
		this.appendChild(controls);
		
		
	}
	private Button getRemoveSelectedButton() {
		if (this.removeSelectedButton == null){
			this.removeSelectedButton = new Button();
			this.removeSelectedButton.setLabel("remove selected");
			this.removeSelectedButton.setStyle("font-size:0.8em");
			this.removeSelectedButton.addEventListener("onClick", new EventListener(){

				public void onEvent(Event arg0) throws Exception {
					removeSelected();
					
				}
				
			});
		}
		return this.removeSelectedButton;
	}
	
	private Button getClearTableButton() {
		if (this.clearTableButton == null){
			this.clearTableButton = new Button();
			this.clearTableButton.setLabel("clear table");
			this.clearTableButton.setStyle("font-size:0.8em");
			this.clearTableButton.addEventListener("onClick", new EventListener(){
				public void onEvent(Event arg0) throws Exception {
					clear();
					Event evt = new Event(ONREMOVE_EVENT, EnhancedGrid.this);
					Events.postEvent(evt);
				}
			});
		}
		return this.clearTableButton;
	}
	private Button getClearSelectionButton() {
		if (this.clearSelectionButton == null){
			this.clearSelectionButton = new Button();
			this.clearSelectionButton.setLabel("clear selection");
			this.clearSelectionButton.setStyle("font-size:0.8em");
			this.clearSelectionButton.addEventListener("onClick", new EventListener(){
				public void onEvent(Event arg0) throws Exception {
					clearSelection();
					
				}
			});
		}
		return this.clearSelectionButton;
	}
	private Button getInvertSelectionButton() {
		if (this.invertSelectionButton == null){
			this.invertSelectionButton = new Button();
			this.invertSelectionButton.setLabel("invert selection");
			this.invertSelectionButton.setStyle("font-size:0.8em");
			this.invertSelectionButton.addEventListener("onClick", new EventListener(){
				public void onEvent(Event arg0) throws Exception {
					invertSelection();
					
				}
			});
		}
		return this.invertSelectionButton;
	}
	private Button getSelectAllButton() {
		if (this.selectAllButton == null){
			this.selectAllButton = new Button();
			this.selectAllButton.setLabel("select all");
			this.selectAllButton.setStyle("font-size:0.8em");
			this.selectAllButton.addEventListener("onClick", new EventListener(){
				public void onEvent(Event arg0) throws Exception {
					selectAll();
					
				}
			});
		}
		return this.selectAllButton;
	}
	public Listbox getListBox() {
		if (this.listbox == null){
			this.listbox = new Listbox(){
				@Override
				public void setMold(String arg0) {
					super.setMold(arg0);
					if (arg0.equals("paging")){
						setShowFilter(false);
					}
				}
			};
			
		}
		return this.listbox;
	}
	
	private Textbox getFilterBox(){
		if (this.filterbox == null){
			this.filterbox = new Textbox();
			//this.filterbox.setWidth("100%"); //falla en IE7
			this.filterbox.addEventListener("onChanging", new EventListener(){

				public void onEvent(Event arg0) throws Exception {
					setFilter(((InputEvent)arg0).getValue());
					
				}

				
				
			});
		}
		return this.filterbox;
	}
	private Label getFilterLabel(){
		if (this.filterLabel == null){
			this.filterLabel = new Label("filter");
		}
		return this.filterLabel;
	}
	
	private void setFilter(String value) {
		this.filter = value;
		this.applyFilter();
		
	}
	private void applyFilter() {
		
		List listitems = this.getListBox().getChildren();
		Iterator it = listitems.iterator();
		if (it.hasNext()){
			it.next(); //skip listhead
		}
		while (it.hasNext()){
			Listitem item = (Listitem) it.next();
			Listcell cell =(Listcell)item.getFirstChild(); 
			if (cell.getLabel().indexOf(this.filter)==-1){
				cell.setVisible(false);
				if (item.getChildren().size()>1){
					for (int i = 1; i<item.getChildren().size(); i++){
						((Listcell)item.getChildren().get(i)).setVisible(false);
					}
				}
				
			}else{
				cell.setVisible(true);
				if (item.getChildren().size()>1){
					for (int i = 1; i<item.getChildren().size(); i++){
						((Listcell)item.getChildren().get(i)).setVisible(true);
					}
				}
			}
		}
		
	}
	public EnhancedGrid(){
		initialize();
	}
	public void setMultiple(boolean multiple){
		this.getListBox().setMultiple(multiple);
	}
	
	private Listhead currentListHead;
	public void setRowTitles(String[] values){
	
		Listbox grid = getListBox();
		if(grid!=null){
			Listhead head = currentListHead;
			if (head==null){
				head = new Listhead();
				head.setSizable(true);
				currentListHead = head;
			}else{
				Util.removeChilds(head);
			}			
			
			for (int i = 0; i<values.length; i++){				
				Listheader header = new Listheader();
				header.setLabel(values[i]);
				
				
				header.setSort("auto");
				head.appendChild(header);
				
			}
			grid.appendChild(head);
			
		}	
		
	}

	

	public void addRow(String[] values){
		Listbox grid = getListBox();
		
		if(grid!=null){
			Listitem row = new Listitem();
			
						
			for (int i = 0; i<values.length; i++){				
				Listcell col = new Listcell();
				col.setLabel(values[i]);
				row.appendChild(col);		
				
			}
			grid.appendChild(row);


		}
		
		
	}
	public void setShowClearAll(boolean show){
		this.getClearTableButton().setVisible(show);
	}
	public void setShowRemoveSelected(boolean show){
		this.getRemoveSelectedButton().setVisible(show);
	}
	public void setShowSelectAll(boolean show){
		this.getSelectAllButton().setVisible(show);
	}
	public void setShowClearSelection(boolean show){
		this.getClearSelectionButton().setVisible(show);
	}
	public void setShowInvertSelection(boolean show){
		this.getInvertSelectionButton().setVisible(show);
	}
	public void setShowSelectionButtons(boolean show){
		this.setShowSelectAll(show);
		this.setShowInvertSelection(show);
		this.setShowClearSelection(show);
	}
	public void setShowFilter(boolean show){
		if (show && this.getListBox().getMold().equals("paging")){
			throw new RuntimeException("no filter support in paging layout");
		}
		this.filterbox.setVisible(show);
		this.filterLabel.setVisible(show);
	}
	synchronized public void clear(){
		
		
		Listhead head = getListBox().getListhead();
		Util.removeChilds(getListBox());
		if(head!=null) getListBox().appendChild(head);
		
	}
	
	public void removeSelected(){
		Listbox listbox = this.getListBox();
		LinkedList<Listitem> removed = new LinkedList<Listitem>();
		  for (int i = listbox.getItemCount()-1; i>=0;i--){
			Listitem item = listbox.getItemAtIndex(i);
			if (item.isSelected()){
				listbox.removeChild(item);
				removed.add(item);
			}
	
		}
		
		listbox.clearSelection();
		Event evt = new Event(ONREMOVED_EVENT, this, removed);
		Events.postEvent(evt);
	}
	public void invertSelection(){
		Listbox listbox = this.getListBox();		
		  for (int i = listbox.getItemCount()-1; i>=0;i--){
			Listitem item = listbox.getItemAtIndex(i);
			if (item.isSelected() && item.isVisible()){
				item.setSelected(false);
				
			}else if(item.isVisible()){
				item.setSelected(true);
			}
	
		}
		
		Event evt = new Event(ONSELECTIONCHANGE_EVENT, this, null);
		Events.postEvent(evt);
		
	}
	public void selectAll(){
		Listbox listbox = this.getListBox();		
		  for (int i = listbox.getItemCount()-1; i>=0;i--){
			Listitem item = listbox.getItemAtIndex(i);
			if (!item.isSelected() && item.getFirstChild().isVisible()){
				item.setSelected(true);
				
			}
	
		}
		
		Event evt = new Event(ONSELECTIONCHANGE_EVENT, this, null);
		Events.postEvent(evt);
		
		
		
		
	}
	public void clearSelection(){
		
		Listbox listbox = this.getListBox();		
		  for (int i = listbox.getItemCount()-1; i>=0;i--){
			Listitem item = listbox.getItemAtIndex(i);
			if (item.isSelected() && item.getFirstChild().isVisible()){
				item.setSelected(false);
				
			}
	
		}
		
		
		
		Event evt = new Event(ONSELECTIONCHANGE_EVENT, this, null);
		Events.postEvent(evt);
		
	}
}
