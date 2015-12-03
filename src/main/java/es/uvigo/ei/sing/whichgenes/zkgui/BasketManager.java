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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Html;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Vbox;

import es.uvigo.ei.sing.whichgenes.geneset.Basket;
import es.uvigo.ei.sing.whichgenes.geneset.Gene;
import es.uvigo.ei.sing.whichgenes.geneset.GeneSet;
import es.uvigo.ei.sing.whichgenes.provider.idconverter.IDConverter;
import es.uvigo.ei.sing.whichgenes.user.User;

public class BasketManager extends Vbox{

	private static Log log = LogFactory.getLog(BasketManager.class);
	private final String NO_CONVERT="*no_convert*";
	private static final long serialVersionUID = 1L;
	private EnhancedGrid setGrid;
	private GeneSetEditor genesEditor;
	private Basket basket;
	private Iframe report;
	private Button exportButton;
	private Combobox exportNamespaceBox;
	private Combobox exportFormatBox;
	private Button editSetButton;
	private Button copyButton;
	private Button unionButton;
	private Button intersectionButton;
	private Button diffButton;
	private Button renameButton;
	
	private Button createCustomSet;
	
	private Label noItemsLabel = new Label("You have no items in the basket");
	private Vbox allItems = new Vbox();
	
	private Html downloadLink;
	private EnhancedGrid toExportArea;
	public BasketManager(){
		initialize();
	}

	
	private EnhancedGrid getSetGrid() {
		
		if (this.setGrid == null){
			this.setGrid = new EnhancedGrid();
			//this.setGrid.setShowClearAll(false);
			//this.setGrid.setShowClearSelected(false);
			this.setGrid.setWidth("100%");
			this.setGrid.getListBox().setWidth("100%");
			this.setGrid.setRowTitles(new String[]{"Set name","size"});
			this.setGrid.setMultiple(true);
			this.setGrid.setShowClearAll(false);
			this.setGrid.getListBox().setRows(15);
			this.setGrid.getListBox().setHeight("400px");
			this.setGrid.setShowRemoveSelected(false);	
			
		}
		 
		
		return this.setGrid;
	}

	private EnhancedGrid getToExportArea(){
		if (this.toExportArea == null){
			this.toExportArea = new EnhancedGrid();
			this.toExportArea.setRowTitles(new String[]{"set"});
			this.toExportArea.getListBox().setDisabled(true);
			this.toExportArea.getListBox().setRows(10);
			this.toExportArea.setShowClearAll(false);
			this.toExportArea.setMultiple(false);
			this.toExportArea.getListBox().setCheckmark(false);
			this.toExportArea.setShowRemoveSelected(false);
			this.toExportArea.setWidth("100%");
			this.toExportArea.getListBox().setWidth("100%");
			this.toExportArea.getListBox().setHeight("300px");
			this.toExportArea.setShowFilter(false);
			this.toExportArea.setShowSelectionButtons(false);
			
			
		}
		return this.toExportArea;
	}
	
	private GeneSetEditor getGenesEditor() {
		if (this.genesEditor == null){
			this.genesEditor = new GeneSetEditor();
			this.genesEditor.setWidth("100%");
			this.genesEditor.setListHeight("600px");
			this.genesEditor.setVisible(false);
			
		}
		return this.genesEditor;
		
	}
	public void setBasket(Basket b){
		this.basket = b;
		b.addObserver(basketObserver);
		this.setEmptyMode(b.getSets().size()==0);
	}
	private class BasketObserver implements Observer{

		public void update(Observable o, Object arg) {
			
			setEmptyMode(basket.getSets().size()==0);
			getSetGrid().clear();
			
			for (GeneSet set: basket.getSets()){
				
				getSetGrid().addRow(new String[]{set.getName(), ""+set.getGenes().size() });
				set.deleteObserver(setChangedObserver);
				set.addObserver(setChangedObserver);
			}
			
			if (getGenesEditor().getGeneSet()!=null){
			
				if (basket.getByName(getGenesEditor().getGeneSet().getName())==null){
					genesEditor.setGeneSet(null);
					genesEditor.setVisible(false);
					
				}
			}
			updateExportArea();

			
		}
		
	}
	private BasketObserver basketObserver = new BasketObserver();
	private class SetChangedObserver implements Observer{

		public void update(Observable arg0, Object arg1) {
			basketObserver.update(basket, null);
			
		}
		
	};
	private SetChangedObserver setChangedObserver = new SetChangedObserver();
	private Button removeButton;
	private void updateExportArea(){
		getToExportArea().clear();
		clearDownloadLink();
		for (GeneSet set : getSelectedSets()){
			
			getToExportArea().addRow(new String[]{set.getName()});
		}
	}
	private void clearDownloadLink(){
		this.getDownloadLink().setContent("");
	}
	private void initialize() {
		
		Hbox hbox1 = new Hbox();
		
		hbox1.appendChild(getEditSetButton());
		hbox1.appendChild(getRenameButton());
		hbox1.appendChild(getCopyButton());
		hbox1.appendChild(getRemoveButton());

		
		Hbox hbox2 = new Hbox();
		hbox2.appendChild(getIntersectionButton());
		hbox2.appendChild(getUnionButton());
		hbox2.appendChild(getDiffButton());
		
		
		Vbox vbox = new Vbox();
		vbox.setWidth("100%");
		vbox.appendChild(hbox1);
		vbox.appendChild(getSetGrid());
		vbox.appendChild(hbox2);
		
		Groupbox group = new Groupbox();
		group.setMold("3d");
		group.appendChild(new Caption("Export"));
		
		group.appendChild(new Label("attempting to export:"));
		group.appendChild(this.getToExportArea());
		
		this.getSetGrid().getListBox().addEventListener("onSelect", new EventListener(){

			public void onEvent(Event arg0) throws Exception {
			
				updateExportArea();
				
			}
			
		});
		this.getSetGrid().addEventListener(EnhancedGrid.ONSELECTIONCHANGE_EVENT, new EventListener(){

			public void onEvent(Event arg0) throws Exception {
			
				updateExportArea();
				
			}
			
		});
		group.appendChild(this.getExportButton());
		group.appendChild(getDownloadLink());
		group.appendChild(new Grid(){{
			this.appendChild(new Rows(){{
			this.appendChild(
				new Row(){{this.appendChild(new Label("Format: "));this.appendChild(getExportFormatCombo());}});
			this.appendChild(
				new Row(){{this.appendChild(new Label("Namespace: "));this.appendChild(getExportNamespaceCombo());}});
			}});
		}});
			
		
		group.appendChild(new Hbox(new Component[]{ 
									new Image(){{this.setSrc("ensembl_logo.gif");}},new Label("Powered by Ensembl"){{this.setStyle("font-size:0.8em");}}}));
		
		//vbox.appendChild(group);
		
		allItems.appendChild(hbox1);
		allItems.setWidth("97%");
		
		Div div = new Div();
		div.setStyle("border-left:2px solid #d2d2d2;padding-left:5px;align:left; margin-left:10px");
		div.setWidth("100%");
		
		div.appendChild(getGenesEditor());
		
		Div div2 = new Div();
		div2.setStyle("border-left:2px solid #d2d2d2;padding-left:5px;align:left; margin-left:20px");
		//div2.setWidth("100%");
		
		div2.appendChild(group);
		
		Hbox hbox = new Hbox(new Component[]{vbox, div, div2});
		hbox.setWidth("100%");
		hbox.setWidths("310px,280px,300px");
		allItems.appendChild(hbox);
		allItems.setStyle("margin: 0 auto; align:center");
		
		
		
		this.appendChild(noItemsLabel);
		this.appendChild(new Hbox(new Component[]{getCreateCustomSetButton()}){{this.setStyle("margin: 0 auto; align:center"); this.setWidth("97%");}});
		this.appendChild(allItems);
		
		
		
	}

	private Button getCreateCustomSetButton() {
		if (this.createCustomSet == null){
			
			this.createCustomSet = new Button("Create a custom gene set");
			this.createCustomSet.setImage("add.png");
			
			this.createCustomSet.addEventListener("onClick", new EventListener(){
				public void onEvent(Event arg0) throws Exception {
					CreateCustomSetWindow win = new CreateCustomSetWindow(BasketManager.this);
					
					win.doModal();
					
					GeneSet set = win.getSet();
					
					if (set !=null){
						BasketManager.this.basket.addGeneSet(set);
					}
				}
			});
		}
		return this.createCustomSet;
	}


	private Html getDownloadLink() {
		if (this.downloadLink == null){
			this.downloadLink = new Html();
			getDownloadLink().setContent("<a href='#' id='downlink'></a>");
			
		}
		return this.downloadLink;
	}



	private Combobox getExportNamespaceCombo() {
		if (this.exportNamespaceBox == null){
			this.exportNamespaceBox = new Combobox();			
			this.exportNamespaceBox.appendItem(NO_CONVERT);
			
			for (String humanID: IDConverter.getHumanIDs()){
				this.exportNamespaceBox.appendItem(humanID+" [homo]");
			}
			for (String mouseID: IDConverter.getMouseIDs()){
				this.exportNamespaceBox.appendItem(mouseID+" [mus musculus]");
			}

			this.exportNamespaceBox.addEventListener(Events.ON_CHANGE , new EventListener(){

				public void onEvent(Event arg0) throws Exception {
					clearDownloadLink();
					
				}
				
			});

			this.exportNamespaceBox.setSelectedIndex(0);	
				
		}
		return this.exportNamespaceBox;
	}
	private Combobox getExportFormatCombo() {
		if (this.exportFormatBox == null){
			this.exportFormatBox = new Combobox();			
			this.exportFormatBox.appendItem("CSV (sets in columns)");
			this.exportFormatBox.appendItem("CSV (sets in rows)");
			this.exportFormatBox.appendItem("GMT");
			this.exportFormatBox.appendItem("GMX");
			this.exportFormatBox.addEventListener(Events.ON_CHANGE , new EventListener(){

				public void onEvent(Event arg0) throws Exception {
					clearDownloadLink();
					
				}
				
			});
			this.exportFormatBox.setSelectedIndex(0);
	
				
		}
		return this.exportFormatBox;
	}


	private void setEmptyMode(boolean empty){
		allItems.setVisible(!empty);
		noItemsLabel.setVisible(empty);
	}
	


	


	private List<GeneSet> getSelectedSets(){
		List<GeneSet> toret = new LinkedList<GeneSet>();
		Set itemsSet = setGrid.getListBox().getSelectedItems();
		Iterator it = itemsSet.iterator();
		while (it.hasNext()){
			Listitem item = (Listitem) it.next();
			Listcell cell = (Listcell) item.getChildren().get(0);
			String geneSetName = cell.getLabel();
			GeneSet set = basket.getByName(geneSetName);
			System.out.println("set "+set.getName());
			toret.add(set);
		}
		return toret;
	}
	public void selectSet(GeneSet set){
		for (int i = 0; i<setGrid.getListBox().getItemCount(); i++){
			Listitem item = (Listitem) setGrid.getListBox().getItemAtIndex(i);
			Listcell cell = (Listcell) item.getChildren().get(0);
			String geneSetName = cell.getLabel();
			if (geneSetName.equals(set.getName())){
				setGrid.getListBox().selectItem(item);
				return;
			}
		}
		updateExportArea();
	}
	public void doEditSelected(){
		Set itemsSet = setGrid.getListBox().getSelectedItems();		
		
		if (itemsSet == null || itemsSet.size()!=1){
			getGenesEditor().setGeneSet(null);
			getGenesEditor().setVisible(false);
			
			throw new RuntimeException("Please select one set to edit");
		}else{
			Listitem item = (Listitem) itemsSet.iterator().next();
			Listcell cell = (Listcell) item.getChildren().get(0);
			String geneSetName = cell.getLabel();
			GeneSet set = basket.getByName(geneSetName);			
			getGenesEditor().setGeneSet(set);
			getGenesEditor().setVisible(true);
		}
		
	}
	private Button getEditSetButton() {
		if (this.editSetButton == null){
			this.editSetButton = new Button();
			this.editSetButton.setLabel("Edit");
			this.editSetButton.setImage("edit.png");
			this.editSetButton.setTooltiptext("edit selected set (first selected one)");
			this.editSetButton.addEventListener("onClick", new EventListener(){

				public void onEvent(Event arg0) throws Exception {
					doEditSelected();
					
				}
				
			});
		}
		return this.editSetButton;

	}
	private Button getUnionButton() {
		if (this.unionButton == null){
			this.unionButton = new Button();
			this.unionButton.setImage("union.png");
			this.unionButton.setLabel("OR");
			this.unionButton.setTooltiptext("union of selected sets");
			this.unionButton.addEventListener("onClick", new EventListener(){

				public void onEvent(Event arg0) throws Exception {
					
					List<GeneSet> selected = getSelectedSets();
					if (selected.size() < 2){
						throw new RuntimeException("Please select at least 2 sets");
					}
					String defaultname = "";
					boolean first = true;
					for(GeneSet set : selected){
						if (!first){
							defaultname +=" <or> ";
						}else{
							first = false;
						}
						defaultname += set.getName();
					}
					String name = Util.createQuestionDialog(BasketManager.this, "Give a set name", "new set name: ", defaultname);
					if (name == null){
						return;
					}
					GeneSet newset = new GeneSet(name);
					
					
					for (GeneSet set : selected){
						for (Gene g : set.getGenes()){
							newset.addGene(g);
						}
					}
					
					basket.addGeneSet(newset);										
					
					
				}
				
			});
		}
		return this.unionButton;

	}
	private Button getIntersectionButton() {
		if (this.intersectionButton == null){
			this.intersectionButton = new Button();
			this.intersectionButton.setLabel("AND");
			this.intersectionButton.setImage("intersection.png");
			this.intersectionButton.setTooltiptext("intersection of selected sets");
			this.intersectionButton.addEventListener("onClick", new EventListener(){

				public void onEvent(Event arg0) throws Exception {
					List<GeneSet> selected = getSelectedSets();
					if (selected.size() < 2){
						throw new RuntimeException("Please select at least 2 sets");
					}
					String defaultname = "";
					boolean first = true;
					for(GeneSet set : selected){
						if (!first){
							defaultname +=" <and> ";
						}else{
							first = false;
						}
						defaultname += set.getName();
					}
					
					String name = Util.createQuestionDialog(BasketManager.this, "Give a set name", "new set name: ", defaultname);
					if (name == null){
						return;
					}
					GeneSet newset = new GeneSet(name);
					
					
					for (Gene gene : selected.get(0).getGenes()){
						boolean inAll = true;
						for (int i = 1; i<selected.size(); i++){
							if (!selected.get(i).getGenes().contains(gene)){
								inAll = false;
								break;
							}
						}
						if (inAll){
							newset.addGene(gene);
						}
					}
					basket.addGeneSet(newset);
					
				}
				
			});
		}
		return this.intersectionButton;

	}
	
	
	private Button getDiffButton() {
		if (this.diffButton == null){
			this.diffButton = new Button();
			//this.intersectionButton.setLabel("Intersect.");
			this.diffButton.setImage("diff.png");
			this.diffButton.setLabel("A-B");
			this.diffButton.setTooltiptext("difference of selected sets");
			this.diffButton.addEventListener("onClick", new EventListener(){

				public void onEvent(Event arg0) throws Exception {
					List<GeneSet> selected = getSelectedSets();
					if (selected.size() != 2){
						if (selected.size()==0){
							throw new RuntimeException("Please, select two sets before pressing the button");
						}
						throw new RuntimeException("There are "+selected.size()+" selected sets. Please select EXACTLY 2 sets");
					}
					
					GeneSet setA = selected.get(0);
					GeneSet setB = selected.get(1);
					
					// select the order of the Diff
					
					List<String> values = new LinkedList<String>();
					
					String a_short = setA.getName();
					String b_short = setB.getName();
					if (a_short.length() > 50){
						a_short = a_short.substring(0,a_short.length()/2)+"..."+a_short.substring(a_short.length()/2);
					}
					if (b_short.length() > 50){
						b_short = b_short.substring(0,b_short.length()/2)+"..."+b_short.substring(b_short.length()/2);
					}
					String a_b =  "("+a_short+") AND NOT IN ("+b_short+")";
					String b_a = "("+b_short+") AND NOT IN ("+a_short+")";
					values.add(a_b);
					values.add(b_a);
					
					String order = Util.createQuestionDialog(BasketManager.this, "Select the order", "order: ", values);
					
					if (order == null) return;
					
					
					if (order.equals(b_a)){
						GeneSet temp = setA;
						setA = setB;
						setB = temp;
					}
					
					String defaultname = setA.getName()+" <and not in> "+setB.getName();
					
					String name = Util.createQuestionDialog(BasketManager.this, "Give a set name", "new set name: ", defaultname);
					if (name == null) return;
					
					GeneSet newset = new GeneSet(name);
					
					HashSet<Gene> setBfast = new HashSet<Gene>();
					setBfast.addAll(setB.getGenes());
					for (Gene gene : setA.getGenes()){
						if (!setBfast.contains(gene)){
							newset.addGene(gene);
						}
					}
					basket.addGeneSet(newset);
					
				}
				
			});
		}
		return this.diffButton;

	}
	private Button getRenameButton() {
		if (this.renameButton == null){
			this.renameButton = new Button();
			this.renameButton.setImage("rename.png");
			this.renameButton.setTooltiptext("Rename selected set");
			this.renameButton.setLabel("Rename");
			this.renameButton.addEventListener("onClick", new EventListener(){

				public void onEvent(Event arg0) throws Exception {
					List<GeneSet> selected = getSelectedSets();
					if (selected.size() != 1){
						throw new RuntimeException("Please select only 1 set");
					}
					
					String name = Util.createQuestionDialog(BasketManager.this, "Give a set name", "new set name: ", selected.get(0).getName());
					if (name == null) return;
					
					basket.renameSet(selected.get(0), name);
					
				}
				
			});
		}
		return this.renameButton;

	}
	
	private boolean exported = false;
	private float completed = 0.0f;
	private class CheckExported implements Runnable{

		private long timestamp = System.currentTimeMillis();
		private String format;
		private String name;
		public CheckExported(String format) {
			this.format = format;
		}
		public void run() {
			System.out.println("running checker");
			if (exported){
				System.out.println("exported, enabling button");
				exported = false;
				completed = 0.0f;

				getDownloadLink().setContent("<a href='#' id='downlink' onclick=\"javascript:window.open('export?f="+format+"&t="+timestamp+"', 'mywindow')\">&nbsp; <img src='down.png' border='0'>Download now</a>");
				getExportButton().setLabel("Export sets");
				getExportButton().setDisabled(false);
				GlobalTimer.getOrCreateGlobalTimer(BasketManager.this).removeTask(this.name);
			}else{
				getExportButton().setLabel("Exporting... ("+(int)(completed*100)+"%)");
			}
			
		}
		public void setName(String taskname) {
			this.name = taskname;
			
		}
		
	}
	private Button getExportButton() {
		
		class ExportLog{
			String type ="EXPORT";
			String format;
			int size;
			String namespace;
			long time;
			String user;
			Date date = new Date();
		
			private String SEP = "\t";
			
			@Override
			public String toString() {
				String toret = "";
				
				User user = (User) getPage().getDesktop().getSession().getAttribute("user");
				if (user ==null){
					this.user = "anonymous";
				}else{
					this.user = user.getEmail();
				}
				toret += type;
				toret += SEP+date+SEP+this.user+SEP+size+" sets "+SEP+format+SEP+namespace+SEP+time+" millis.";
				return toret;
			}
		}
		if (this.exportButton == null){
			this.exportButton = new Button();
			this.exportButton.setImage("export.png");
			this.exportButton.setAction("onclick: document.getElementById('downlink').innerHTML=''");
			this.exportButton.setLabel("Export sets");
			this.exportButton.setStyle("margin-bottom:5px");
			this.exportButton.addEventListener("onClick", new EventListener(){
				public void onEvent(Event arg0) throws Exception {
					exported = false;
					final long start = System.currentTimeMillis(); 
					final ExportLog exportLog = new ExportLog();
					exportLog.format = getExportFormatCombo().getValue().toUpperCase();					
					exportLog.namespace = getExportNamespaceCombo().getSelectedItem().getLabel();
					exportLog.size = getSelectedSets().size();
					
					
					final Session session = Sessions.getCurrent();
					
					if (getExportFormatCombo().getValue().toUpperCase().indexOf("GMT")!=-1 || getExportFormatCombo().getValue().toUpperCase().indexOf("ROWS")!=-1){
						final boolean isGMT = getExportFormatCombo().getValue().toUpperCase().indexOf("GMT")!=-1 ;
						final boolean isCSV = !isGMT;
						
						
						String format = isCSV?"text/csv":"application/gmt";
						CheckExported checker = new CheckExported(format);
						String taskname = GlobalTimer.getOrCreateGlobalTimer(BasketManager.this).addTask(checker);
						checker.setName(taskname);
						getExportButton().setLabel("Exporting...");
						getExportButton().setDisabled(true);
						
						
						new Thread(){
							public void run(){
								String sep = "\t";
								if (isCSV){
									sep = ",";
								}
								StringBuffer buffer = new StringBuffer(1000);
								
								int total = 0;
								for (GeneSet set : getSelectedSets()){
									total++;
									completed = (float)total/(float)getSelectedSets().size();
									buffer.append(set.getName());
									if (isGMT){
										buffer.append(sep+"na"); //description
									}
									HashSet<String> converted_ids = new HashSet<String>();

									String[] gene_ids = new String[set.getGenes().size()];
									int i = 0;
									for (Gene g: set.getGenes()){
										gene_ids[i++] = g.getName();
									}
									
									
									if (!getExportNamespaceCombo().getSelectedItem().getLabel().equalsIgnoreCase(NO_CONVERT)){
											
										String[] converted_ids_repeat = null;
										String name = getExportNamespaceCombo().getSelectedItem().getLabel().substring(0, getExportNamespaceCombo().getSelectedItem().getLabel().indexOf(" "));
										if (getExportNamespaceCombo().getSelectedItem().getLabel().contains("mus musculus")){
											//mouse
											converted_ids_repeat=IDConverter.convert("mmusculus_gene_ensembl", "mgi_symbol",name ,gene_ids);
										}else{
											//human
											converted_ids_repeat=IDConverter.convert("hgnc_symbol",name ,gene_ids);
										}
			
										for (String id: converted_ids_repeat){
											converted_ids.add(id);
										}
										
									}else{
										for (String gene_id:gene_ids){
											converted_ids.add(gene_id);
										}
									}
									
									for (String g: converted_ids){
										buffer.append(sep+g);
										
									}
									buffer.append("\n");
								}
								//final InputStream mediais = new ByteArrayInputStream(buffer.toString().getBytes());
								//final AMedia amedia = new AMedia("genesets.gmt", "gmt", "application/gmt", mediais);
					                
					            //set iframe content
					            //getReport().setContent(amedia);
							
								session.setAttribute("exported", buffer.toString());
								exported = true;
								
								//getDownloadLink().setDynamicProperty("href", "export?id="+id+"&f=application/gmt");
								
								
								exportLog.time = System.currentTimeMillis() - start;
								log.info(exportLog.toString());

							}
						}.start();
												
						
						
						
						
					}else if(getExportFormatCombo().getValue().toUpperCase().indexOf("GMX")!=-1 || getExportFormatCombo().getValue().toUpperCase().indexOf("COLUMNS")!=-1){
						final boolean isGMX = getExportFormatCombo().getValue().toUpperCase().indexOf("GMX")!=-1;
						final boolean isCSV = !isGMX;
						
						
						
						String format = (isCSV?"text/csv":"application/gmx");
						CheckExported checker = new CheckExported(format);
						String taskname = GlobalTimer.getOrCreateGlobalTimer(BasketManager.this).addTask(checker);
						checker.setName(taskname);
						getExportButton().setLabel("Exporting...");
						getExportButton().setDisabled(true);
						
						new Thread(){
							public void run(){
								String sep ="\t";
								if (isCSV){
									sep = ",";
								}
								StringBuffer buffer = new StringBuffer(1000);
								List<GeneSet> selectedSets = getSelectedSets();
								int row = 0;
								
								//titles
								boolean first = true;
								for (GeneSet set : selectedSets){
									if (!first){
										buffer.append(sep);								 
									}else{
										first = false;
									}
									buffer.append(set.getName());
								}
								buffer.append("\n");
								
								if (isGMX){// na's
									first = true;
									for (int i = 0; i<selectedSets.size(); i++){
										if (!first){
											buffer.append(sep);								 
										}else{
											first = false;
										}
										buffer.append("na");
									}
									buffer.append("\n");
								}
								boolean allFinished = false;						
								// convert
								HashMap<GeneSet, List<String>> convertedGenes = new HashMap<GeneSet, List<String>>();
								
								int total = 0;
								for (GeneSet set : selectedSets){
									total++;
									completed = (float)total/(float)selectedSets.size();  
									Set<String> converted_ids = new HashSet<String>();
									String[] gene_ids = new String[set.getGenes().size()];
									int i = 0;
									for (Gene g: set.getGenes()){
										gene_ids[i++] = g.getName();
									}
									
									if (!getExportNamespaceCombo().getSelectedItem().getLabel().equalsIgnoreCase(NO_CONVERT)){
										
										String[] converted_ids_repeat = null;
										String name = getExportNamespaceCombo().getSelectedItem().getLabel().substring(0, getExportNamespaceCombo().getSelectedItem().getLabel().indexOf(" "));
										if (getExportNamespaceCombo().getSelectedItem().getLabel().contains("mus musculus")){
											//mouse
											converted_ids_repeat=IDConverter.convert("mmusculus_gene_ensembl", "mgi_symbol",name ,gene_ids);
										}else{
											//human
											converted_ids_repeat=IDConverter.convert("hgnc_symbol",name ,gene_ids);
										}
			
										for (String id: converted_ids_repeat){
											converted_ids.add(id);
										}
										
									}else{
										for (String gene_id:gene_ids){
											converted_ids.add(gene_id);
										}
									}
									List<String> geneList = new ArrayList<String>(converted_ids.size());
									geneList.addAll(converted_ids);
									convertedGenes.put(set, geneList);
								}
								
								while (!allFinished){
									allFinished = true;
									
									first = true;
									for (GeneSet set : selectedSets){
										if (!first){
											buffer.append(sep);								 
										}else{
											first = false;
										}
										if (convertedGenes.get(set).size()>row){
											buffer.append(convertedGenes.get(set).get(row));
											allFinished = false;
										
										}
									}
									buffer.append("\n");
									row++;
									
									
								}
								//final InputStream mediais = new ByteArrayInputStream(buffer.toString().getBytes());
								//final AMedia amedia = new AMedia("genesets.gmx", "gmx", "application/gmx", mediais);
								// set iframe content
					            //getReport().setContent(amedia);
								
								session.setAttribute("exported", buffer.toString());
								exported = true;
								
								exportLog.time = System.currentTimeMillis() - start;
								log.info(exportLog.toString());
							}
						}.start();
						
												
						
						
					}
					
				}
				
			});
			
		}
		return this.exportButton;
	}
	private Button getRemoveButton() {
	
		if (this.removeButton == null){
			this.removeButton = new Button();
			this.removeButton.setLabel("Remove");
			this.removeButton.setImage("remove.png");
			this.removeButton.setTooltiptext("remove selected sets");
			this.removeButton.addEventListener("onClick", new EventListener(){

				public void onEvent(Event arg0) throws Exception {
						for (GeneSet set :getSelectedSets()){
								
									basket.removeGeneSet(set);
								
						}
						updateExportArea();
				}
					
					
				
				
			});
		}
		return this.removeButton;
	}
	private Button getCopyButton() {
		
		if (this.copyButton == null){
			this.copyButton = new Button();
			this.copyButton.setLabel("Duplicate");
			this.copyButton.setImage("copy.png");
			this.copyButton.setTooltiptext("duplicate selected set");
			this.copyButton.addEventListener("onClick", new EventListener(){

				public void onEvent(Event arg0) throws Exception {
					List<GeneSet> selected = getSelectedSets();
					if (selected.size() != 1){
						if (selected.size()==0){
							throw new RuntimeException("Please, select one set before pressing the button");
						}
						throw new RuntimeException("There are "+selected.size()+" selected sets. Please select EXACTLY 1 set");
					}
					
					String defaultSet = "Copy of "+selected.get(0).getName();
				
					int count = 0;
					while (true){
						String candidate = defaultSet;
						
						if (count>0) candidate +="_"+(count++);
						else count ++;
						
						if (basket.getByName(candidate)==null){							
							defaultSet = candidate;
							break;
						}
					}
					String name = Util.createQuestionDialog(BasketManager.this, "Give a set name", "new set name: ", defaultSet);
					if (name == null) return;
					
					GeneSet newSet = new GeneSet(name);
					for (Gene gene : selected.get(0).getGenes()){
						newSet.addGene(gene);
					}
					basket.addGeneSet(newSet);
				}
					
					
				
				
			});
		}
		return this.copyButton;
	}
	
	
}
