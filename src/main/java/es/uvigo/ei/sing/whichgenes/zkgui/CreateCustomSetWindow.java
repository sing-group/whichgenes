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

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Button;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Html;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vbox;


import org.zkoss.zul.Window;

import es.uvigo.ei.sing.whichgenes.geneset.Gene;
import es.uvigo.ei.sing.whichgenes.geneset.GeneSet;
import es.uvigo.ei.sing.whichgenes.user.User;

public class CreateCustomSetWindow extends Window {
	
	
	
	private Textbox contentBox;
	private Textbox nameBox;
	
	private boolean accept = false;
	
	
	public GeneSet getSet(){
		if (!accept){
			return null;
		}else{
			GeneSet res = new GeneSet(getNameBox().getText());
			String[] genes = getContentBox().getText().split("\n");
			for (String gene: genes){
				res.addGene(new Gene(gene));
			}
			return res;
		}
	}
	
	
	private void initialize(){
		this.setSclass("modal");
		this.setTitle("Create Custom Set");
		this.setWidth("300px");		
		this.setSizable(true);
		this.setClosable(true);
		//this.setHeight("400px");
		
		Groupbox box1 = new Groupbox();
		box1.setMold("3d");
		box1.appendChild(new Caption("Set name"));
		box1.appendChild(new Hbox(new Component[]{getNameBox()}));
		
		Groupbox box2 = new Groupbox();
		box2.setMold("3d");
		box2.appendChild(new Caption("Genes"));
		box2.appendChild(new Vbox(new Component[]{new Html("One gene per line.<br> <b>NOTE</b>: If you want to use the converting functions with this gene set, please use <i>HGNC symbols</i> for human and <i>MGI symbols</i> for mouse."), getContentBox()}));
		
		
		this.appendChild(new Vbox(new Component[]{
				box1,
				box2,
				
				new Hbox(new Component[]{new Button("Create"){{ this.addEventListener("onClick", new EventListener(){
					public void onEvent(Event arg0) throws Exception {
						if (getNameBox().getText().length()==0){
							Messagebox.show("Please provide a name for the gene set", "Error", Messagebox.OK, Messagebox.ERROR);
						}else{
							CreateCustomSetWindow.this.accept = true;
							CreateCustomSetWindow.this.detach();
						}
						
					}
				});}}, new Button("Cancel"){{ this.addEventListener("onClick", new EventListener(){
					public void onEvent(Event arg0) throws Exception {					
						CreateCustomSetWindow.this.detach();
						
					}
				});}}})
				
		}));
		
		
	}
	
	private Textbox getContentBox(){
		if (this.contentBox == null){
			this.contentBox = new Textbox();
			this.contentBox.setRows(15);
			this.contentBox.setWidth("280px");
			this.contentBox.setText("ABL1\nEGFR\nMKL1\nPML\nFTL3\n");
		}
		return this.contentBox;
	}
	private Textbox getNameBox(){
		if (this.nameBox == null){
			this.nameBox = new Textbox();
			
			
			User user = (User) getParent().getPage().getDesktop().getSession().getAttribute("user");
			String defaultname = "anonymous";
			if (user !=null ){
				defaultname=user.getEmail();
			}
			defaultname+="_";
			this.nameBox.setWidth("280px");
			this.nameBox.setText(defaultname);
			 
			
		}
		return this.nameBox;
	}
	public CreateCustomSetWindow(Component parent) {
		this.setParent(parent);
		initialize();
	}
}
