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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Vbox;

import es.uvigo.ei.sing.whichgenes.geneset.Basket;
import es.uvigo.ei.sing.whichgenes.query.Query;

public class MultipleQueryManager extends Vbox {
	private static final long serialVersionUID = 1L;

	//Components
	private Tabs tabs = new Tabs();
	private Tabpanels tabpanels = new Tabpanels();
	private Tabbox tabbox = new Tabbox();
	
	private void initialize(){
		
		tabbox.appendChild(tabs);
		tabbox.appendChild(tabpanels);
		tabbox.setWidth("100%");
		this.appendChild(tabbox);
	}
	public MultipleQueryManager(){
		
		initialize();
	}
	
	private Basket basket;
	public void setBasket(Basket b){
		for (QueryManager qmanager : this.getQueryManagers()){
			qmanager.setBasket(b);
		}
		this.basket = b;
	}
	
//	private HashMap<Query, Integer> querytabs= new HashMap<Query, Integer>();
	
	String listHeight="200px";
	public void setListHeight(String height){
		this.listHeight=height;
	}
	
	
	public QueryManager newQueryPack(Query[] qs, String basename){
		GlobalTimer.getOrCreateGlobalTimer(this); 
		final QueryManager manager = new QueryManager(basename);
		manager.setBasket(this.basket);
		manager.setWidth("100%");
		manager.getData().setWidth("100%");
		manager.getData().getListBox().setWidth("95%");
		manager.getData().getListBox().setHeight(this.listHeight);
	//	querytabs.put(q,tabs.getChildren().size());
		Tab tab = new Tab();
		tab.setClosable(true);
		tab.addEventListener("onClose", new EventListener(){

			public void onEvent(Event arg0)  {
				manager.stop();
				
			}
			
		});
		tab.setLabel(basename);
		
		tabs.appendChild(tab);
		
		Tabpanel panel = new Tabpanel();
		
		panel.appendChild(manager);
		
		tabpanels.appendChild(panel);
		
		manager.runQueries(qs);
		
		this.tabbox.setSelectedIndex(this.tabbox.getTabs().getChildren().size()-1);
		
		return manager;
	}
	public QueryManager newQuery(Query q, String setname){
		return this.newQueryPack(new Query[]{q}, setname);
		
	}
	public List<QueryManager> getQueryManagers(){
		LinkedList<QueryManager> toret = new LinkedList<QueryManager>();
		for (Object o: tabpanels.getChildren()){
			Tabpanel panel = (Tabpanel) o;
			toret.add((QueryManager) panel.getChildren().get(0));
		}
		return toret;
	}
	
	
	
}
