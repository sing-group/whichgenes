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

package es.uvigo.ei.sing.whichgenes.query;

import es.uvigo.ei.sing.whichgenes.provider.GeneSetProvider;


public abstract class FreeQuery implements Query{
	
	protected String query="";
	
	public abstract String sampleQuery();
	
	private GeneSetProvider provider;
	public FreeQuery(GeneSetProvider provider){
		this.provider =provider;
	}
	
	public GeneSetProvider getProvider() {
		return this.provider;
	}
	public void setQuery(String query){
		this.query = query;
	}
	public String getQuery(){
		return this.query;
	}
	public void requestStop() {		
	}
	@Override
	public String toString() {
		return this.query;
	}
	
	boolean multiline = false;
	protected void setMultiline(boolean multiline){
		this.multiline = multiline;
	}
	public boolean isMultiline(){
		return this.multiline;
	}
}
