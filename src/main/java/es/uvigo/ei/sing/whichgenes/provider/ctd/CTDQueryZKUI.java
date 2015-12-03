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

package es.uvigo.ei.sing.whichgenes.provider.ctd;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Html;
import org.zkoss.zul.Label;
import org.zkoss.zul.Vbox;

import es.uvigo.ei.sing.whichgenes.query.FreeQuery;
import es.uvigo.ei.sing.whichgenes.query.Query;
import es.uvigo.ei.sing.whichgenes.zkgui.FreeQueryBox;

public class CTDQueryZKUI extends FreeQueryBox {
	
	private Combobox interactionType;
	public CTDQueryZKUI(Query q){
		
		Vbox vbox = new Vbox();
		
		vbox.appendChild(new Html("you can copy a chemical from <a href='http://ctd.mdibl.org/voc.go?action=start&type=chem&browser=s' target='_blank'>here</a>"));
		
		
		vbox.appendChild(new Hbox(new Component[]{new Label("interaction type"), getInteractionType()}));
		
		this.appendChild(vbox);
	}
		
	private Combobox getInteractionType() {
		if (interactionType == null){
			interactionType = new Combobox();
			interactionType.appendItem("ANY");
			interactionType.appendItem("abundance");
			interactionType.appendItem("activity");
			interactionType.appendItem("binding");
			interactionType.appendItem("cotreatment");
			interactionType.appendItem("expression");
			interactionType.appendItem("folding");
			interactionType.appendItem("localization");
			interactionType.appendItem("metabolic processing");
			interactionType.appendItem("- acetylation");
			interactionType.appendItem("- acylation");
			interactionType.appendItem("- alkylation");
			interactionType.appendItem("- amination");
			interactionType.appendItem("- carbamoylation");
			interactionType.appendItem("- carboxylation");
			interactionType.appendItem("- chemical synthesis");
			interactionType.appendItem("- degradation");
			interactionType.appendItem("-- cleavage");
			interactionType.appendItem("--- hydrolysis");
			interactionType.appendItem("- ethylation");
			interactionType.appendItem("- glutathionylation");
			interactionType.appendItem("- glycation");
			interactionType.appendItem("- glycosylation");
			interactionType.appendItem("-- glucuronidation");
			interactionType.appendItem("-- N-linked glycosylation");
			interactionType.appendItem("-- O-linked glycosylation");
			interactionType.appendItem("- hydroxylation");
			interactionType.appendItem("- lipidation");
			interactionType.appendItem("-- farnesylation");
			interactionType.appendItem("-- geranoylation");
			interactionType.appendItem("-- myristoylation");
			interactionType.appendItem("-- palmitoylation");
			interactionType.appendItem("-- prenylation");
			interactionType.appendItem("- methylation");
			interactionType.appendItem("- nitrosation");
			interactionType.appendItem("- nucleotidylation");
			interactionType.appendItem("- oxidation");
			interactionType.appendItem("- phosphorylation");
			interactionType.appendItem("- reduction");
			interactionType.appendItem("- ribosylation");
			interactionType.appendItem("-- ADP-ribosylation");
			interactionType.appendItem("- sulfation");
			interactionType.appendItem("- sumoylation");
			interactionType.appendItem("- ubiquitination");
			interactionType.appendItem("mutagenesis");
			interactionType.appendItem("reaction");
			interactionType.appendItem("response to substance");
			interactionType.appendItem("splicing");
			interactionType.appendItem("stability");
			interactionType.appendItem("transport");
			interactionType.appendItem("- secretion");
			interactionType.appendItem("-- export");
			interactionType.appendItem("- uptake");
			interactionType.appendItem("-- import");    
			interactionType.setSelectedIndex(0);
		}
		return this.interactionType;
	}

	public void setQuery(FreeQuery q) {
		super.setQuery(q);
	}
	
	@Override
	public String getQuery() {
		try {
			return URLEncoder.encode(super.getQuery(), "UTF-8")+":"+ URLEncoder.encode(this.getInteractionType().getSelectedItem().getLabel().replaceAll("-", "").trim(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			
			throw new RuntimeException(e);
		}
	}
	

}
