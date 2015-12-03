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

package es.uvigo.ei.sing.whichgenes.provider.ctddiseases;

import org.zkoss.zhtml.A;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Html;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Vbox;

import es.uvigo.ei.sing.whichgenes.query.FreeQuery;
import es.uvigo.ei.sing.whichgenes.query.Query;
import es.uvigo.ei.sing.whichgenes.zkgui.FreeQueryBox;

public class CTDDiseaseQueryZKUI extends FreeQueryBox {
	
	
	public CTDDiseaseQueryZKUI(Query q){
		this.appendChild(new Html("you can copy a disease from <a href='http://ctd.mdibl.org/voc.go?action=start&type=disease&browser=s' target='_blank'>here</a>"));
		
		
	}
		
	public void setQuery(FreeQuery q) {
		super.setQuery(q);
		
		
	}
	
	
	

}
