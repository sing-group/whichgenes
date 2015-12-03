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

import java.util.Observable;
import java.util.Observer;

import org.zkoss.zul.Label;
import org.zkoss.zul.Window;

import es.uvigo.ei.sing.whichgenes.geneset.Basket;

public class BasketMiniView extends Window implements Observer{

	private static final long serialVersionUID = 1L;
	private Basket basket;	
	
	private Label label;
	public BasketMiniView(){		
		initialize();
	}
	public BasketMiniView(Basket b){
		this();
		this.setBasket(b);
	}

	public void setBasket(Basket b){
		this.basket = b;
		b.addObserver(this);
	}
	private void initialize() {
		this.setTitle("Your Gene Sets");
		this.setWidth("200px");
		this.setBorder("normal");
		this.appendChild(getLabel());
	}

	private Label getLabel() {
		if (this.label == null){
			this.label = new Label();
			this.label.setValue("No Gene Sets added yet");
		}
		return this.label;
	}


	public void update(Observable arg0, Object arg1) {				
			label.setValue("the basket has "+basket.getSets().size()+" sets");		
	}
}
