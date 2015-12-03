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

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.zkoss.zhtml.Messagebox;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zul.Button;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;

import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Html;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treecell;
import org.zkoss.zul.Treechildren;
import org.zkoss.zul.Treecol;
import org.zkoss.zul.Treecols;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.Treerow;
import org.zkoss.zul.Vbox;

import es.uvigo.ei.sing.whichgenes.provider.TreeNode;
import es.uvigo.ei.sing.whichgenes.query.ConstrainedQuery;

public class ConstrainedQueryBox extends Groupbox{
	private static final long serialVersionUID = 1L;
	protected static final String ONSEARCH_EVENT = "onSearch";
	private Label descriptionLabel;
	private ConstrainedQuery query;
	
	private Button searchButton;
	private Tree tree;
	
	private Label filterLabel;
	private Textbox filterbox;
	private String filter;
	
	private Checkbox oneSetPerItem;
	
	private Button clearSelectionButton;
	private Button selectAllButton;
	private Button invertSelectionButton;
	
	public void setQuery(ConstrainedQuery q) {
		this.query = q;
		queryChanged();
	}
	public List<TreeNode> getConstrainedValues() {
		List<TreeNode> toret = new LinkedList<TreeNode>();
		Set<Treeitem> selected = getQueryTree().getSelectedItems();
		for (Treeitem item : selected){
			
			toret.add((TreeNode)item.getAttribute("node"));
		}
		return toret;
		
	}
	public void clearQuery(){
		getQueryTree().clearSelection();
	}
	public ConstrainedQueryBox() {
		initialize();
	}
	private void initialize() {

		this.setMold("3d");
		
		Caption caption = new Caption();
		caption.setLabel("Query");
		
		
		Vbox vbox = new Vbox(new Component[]{
//						getDescriptionLabel(), 
						getQueryTree(),						
						new Hbox(new Component[]{getFilterLabel(),getFilterBox()}){{this.setWidth("100%");}}
						
						
			}); 
				
		vbox.appendChild(new Hbox(new Component[]{getSelectAllButton(),getClearSelectionButton(), getInvertSelectionButton()}));
		vbox.setWidth("100%");
		
		this.appendChild(caption);
		this.appendChild(vbox);
		this.appendChild(new Html("<hr>"));
		this.appendChild(this.getOneSetPerItem());
		
		
		
	}
	
	private void queryChanged() {
		
		updateTree();
		this.getDescriptionLabel().setValue(this.query.getParameterDescription());
		if (this.query.isMultiple()){
			this.setShowSelectionButtons(true);
		}else{
			this.setShowSelectionButtons(false);
		}
	}
	
	private class OnLoadListener implements EventListener{

		private Treeitem parentItem;
		private TreeNode parentNode;
		public OnLoadListener(Treeitem parentItem, TreeNode parentNode){
			this.parentItem = parentItem;
			this.parentNode = parentNode;
		}
		public void onEvent(Event arg0) throws Exception {
			Util.removeChilds(parentItem.getTreechildren());
			for (TreeNode child : parentNode.getChilds()){
				Treeitem childItem = createItem(child);
				childItem.setOpen(false);
				parentItem.getTreechildren().appendChild(childItem);
			}
			
			
		}
		
	}
	HashMap<String, Treeitem> itemsById = new HashMap<String, Treeitem>(); 
	private void updateTree() {
		Util.removeChilds(this.getQueryTree());
		
		//title
		Treecols titles = new Treecols();
		titles.setSizable(true);
		Treecol title = new Treecol(this.query.getParameterName());		
	
		title.setMaxlength(200);
		titles.appendChild(title);
		this.getQueryTree().appendChild(titles);
		

		boolean isList = true;
		
		//append childs
		itemsById.clear();
		Treechildren children = new Treechildren();
		
		try{
			for (TreeNode node: this.query.getConstraints()){
				if (node.getChilds() != null && node.getChilds().size()>0){
					isList = false;
				}
				Treeitem item = createItem(node);
				
				
				item.setOpen(false);
				children.appendChild(item);
				
			}
			//show the filter only for lists (a tree whith no childs)
			this.getFilterBox().setVisible(isList);	this.getFilterLabel().setVisible(isList); this.setShowSelectionButtons(isList);
			
			if (children.getChildren().size()>0){
				this.getQueryTree().appendChild(children);
				
			}

		}catch(Exception e){
			try {
				e.printStackTrace();
				Messagebox.show("There were an error retrieving the catalog of this service: "+e.getMessage(), "Error", Messagebox.OK, Messagebox.ERROR);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
		
		
		
		
		this.tree.setMultiple(this.query.isMultiple());
		
	}
	private void applyFilter(){
		List list = ((Treechildren)this.getQueryTree().getChildren().get(1)).getChildren();
		Iterator it = list.iterator();
		
		while (it.hasNext()){
			
				Treeitem item = (Treeitem) it.next();
				
				if (this.filter!=null && ((Treecell)((Treerow)item.getFirstChild()).getFirstChild()).getLabel().toUpperCase().indexOf(this.filter.toUpperCase())==-1){
					item.setVisible(false);
				}else{
					item.setVisible(true);
				}
			
			
		}
		
	}
	private Treeitem createItem(TreeNode node) {
		Treeitem item = new Treeitem();
		Treerow row = new Treerow();
		Treecell cell = new Treecell(node.getName());
		
		item.setAttribute("identifier",node.getID());
		item.setAttribute("node", node);
		
		itemsById.put(node.getID(), item);
		
		cell.setTooltiptext(node.getName()+" (id: "+node.getID()+")");
		//cell.setStyle("font-size: 0.85em");
		
		row.appendChild(cell);
		item.appendChild(row);
		
		//childs?
		if (node.getChilds().size()>0){
			item.appendChild(new Treechildren());
			item.addEventListener("onOpen", new OnLoadListener(item, node));
		}
		
		return item;
	}
	private Label getDescriptionLabel() {
		if (this.descriptionLabel == null){
			this.descriptionLabel = new Label();
			
		}
		return this.descriptionLabel;
	}
	
	private Checkbox getOneSetPerItem(){
		if (this.oneSetPerItem == null){
			this.oneSetPerItem = new Checkbox();
			this.oneSetPerItem.setLabel("create one set per item");
			this.oneSetPerItem.setChecked(false);
		}
		return this.oneSetPerItem;
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
	private Button getSearchButton() {
		if (this.searchButton == null) {
			this.searchButton = new Button();
			this.searchButton.setLabel("Start!");
			this.searchButton.setImage("search.png");
			this.searchButton.addEventListener("onClick", new EventListener() {

				public void onEvent(Event arg0) throws Exception {
					Event evt = new Event(ONSEARCH_EVENT, ConstrainedQueryBox.this);
					Events.postEvent(evt);
				}

			});
		}
		return this.searchButton;
	}
	private Tree getQueryTree() {
		if (this.tree == null){
			this.tree = new Tree();
			
			
			this.tree.setMultiple(true);
			
			
			this.tree.setPageSize(-1);
			this.tree.setRows(10);
			this.tree.setHeight("200px");
			this.tree.setRows(10);
			this.tree.setCheckmark(true);
			
			this.tree.setWidth("100%");
			
			
			
			
		}
		return this.tree;
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
	public void invertSelection(){
		List list = ((Treechildren)this.getQueryTree().getChildren().get(1)).getChildren();
		Iterator it = list.iterator();
		
		while (it.hasNext()){
			
				Treeitem item = (Treeitem) it.next();
				if (item.isVisible()){
					item.setSelected(!item.isSelected());
				}
		}
		
		
	}
	public void selectAll(){
		List list = ((Treechildren)this.getQueryTree().getChildren().get(1)).getChildren();
		Iterator it = list.iterator();
		
		while (it.hasNext()){
			
				Treeitem item = (Treeitem) it.next();
				if (item.isVisible()){
					item.setSelected(true);
				}
		}
		
		
		
		
		
	}
	public void clearSelection(){
		
		List list = ((Treechildren)this.getQueryTree().getChildren().get(1)).getChildren();
		Iterator it = list.iterator();
		
		while (it.hasNext()){
			
				Treeitem item = (Treeitem) it.next();
				if (item.isVisible()){
					item.setSelected(false);
				}
		}
		
		
		
		
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
	
	public boolean isOneSetPerSelectionSelected(){
		return this.getOneSetPerItem().isChecked();
	}
	
	public void setOneSetPerSelectionSelected(boolean selected){
		this.getOneSetPerItem().setChecked(selected);
	}
	
	public void setSelectedIds(Collection<String> ids, boolean selected){
		for (String id: ids){
			if (itemsById.containsKey(id)){
				itemsById.get(id).setSelected(selected);
			}
		}
	}

}
