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
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Html;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vbox;

import es.uvigo.ei.sing.whichgenes.query.FreeQuery;

public class FreeQueryBox extends Groupbox {

	private static final long serialVersionUID = 1L;

	private static final String ONSEARCH_EVENT = "onSearch";

	private Button searchButton;

	protected Textbox queryField;

	protected Label queryLabel;

	protected Label descriptionLabel;

	private FreeQuery query;
	
	private Checkbox oneSetPerItem;
	private Html line = new Html("<hr>");

	
	public void setQueryText(String text){
		getQueryField().setText(text);
	}
	public void setQuery(FreeQuery q) {
		this.query = q;
		if (this.query.isMultiline()){
			this.getQueryField().setRows(10);
		}else{
			this.line.setVisible(false);
			this.getOneSetPerItem().setVisible(false);
		}
		
		queryChanged();
	}

	public FreeQueryBox() {
		initialize();
	}
	private void initialize() {

		this.setMold("3d");
		
		Caption caption = new Caption();
		caption.setLabel("Query");
		
		
		Vbox vbox = new Vbox(new Component[]{
				new Hbox(new Component[]{
					getQueryLabel(), 
					new Vbox(new Component[]{
						getQueryField(),
						getDescriptionLabel()}) 
				})});
		
		this.appendChild(caption);
		this.appendChild(vbox);
		this.appendChild(line);
		this.appendChild(this.getOneSetPerItem());
		
	}

	private Label getDescriptionLabel() {
		if (this.descriptionLabel == null){
			this.descriptionLabel = new Label();
			this.setStyle("font-size:0.8em");
			
		}
		return this.descriptionLabel;
	}

	private void queryChanged() {
		this.getQueryLabel().setValue(this.query.getParameterName());
		this.getQueryField().setFocus(true);
		this.getDescriptionLabel().setValue(this.query.getParameterDescription());
	}

	private Label getQueryLabel() {
		if (this.queryLabel == null){
			this.queryLabel = new Label();
		}
		return this.queryLabel;
	}
	protected Checkbox getOneSetPerItem(){
		if (this.oneSetPerItem == null){
			this.oneSetPerItem = new Checkbox();
			this.oneSetPerItem.setLabel("create one set per item");
			this.oneSetPerItem.setChecked(false);
		}
		return this.oneSetPerItem;
	}
	public String getQuery() {
		return getQueryField().getValue();
	}
	public void clearQuery(){
		getQueryField().setValue("");
	}

	private Textbox getQueryField() {
		if (this.queryField == null) {
			this.queryField = new Textbox();
			
		}
		return this.queryField;
	}

	private Button getSearchButton() {
		if (this.searchButton == null) {
			this.searchButton = new Button();
			this.searchButton.setLabel("Start!");
			this.searchButton.setImage("search.png");
			this.searchButton.addEventListener("onClick", new EventListener() {

				public void onEvent(Event arg0) throws Exception {
					Event evt = new Event(ONSEARCH_EVENT, FreeQueryBox.this);
					Events.postEvent(evt);
				}

			});
		}
		return this.searchButton;
	}
	public boolean isOneSetPerSelectionSelected(){
		return this.getOneSetPerItem().isChecked();
	}
	public void setOneSetPerSelectionSelected(boolean checked){
		this.getOneSetPerItem().setChecked(checked);
	}

}
