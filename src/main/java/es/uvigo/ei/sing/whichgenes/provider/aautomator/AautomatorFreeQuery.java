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

package es.uvigo.ei.sing.whichgenes.provider.aautomator;

import java.util.Arrays;

import es.uvigo.ei.sing.whichgenes.provider.GeneSetProvider;
import es.uvigo.ei.sing.whichgenes.query.FreeQuery;
import es.uvigo.ei.sing.whichgenes.query.QueryHandler;

public abstract class AautomatorFreeQuery extends FreeQuery {
	protected String resourcePath;	
	protected String specie = "hsa";
	protected String resourceNamespace = null;
	protected String outputNamespace = null;
	protected int convertBuffer = 50;
	
	public AautomatorFreeQuery(GeneSetProvider provider, String specie, String resourcePath){
		this(provider, resourcePath);
		this.specie = specie;
		
	}
	public AautomatorFreeQuery(GeneSetProvider provider, String resourcePath){
		super(provider);
		this.resourcePath = resourcePath;
	}
	

	protected AautomatorQueryRunner runner = new AautomatorQueryRunner();
	
	@Override
	public void requestStop() {
		if (runner!=null) runner.stop();
	}
	
	protected String[] preprocessInput(){
		String[] toret = new String[]{this.query};
		System.out.println("Query: "+Arrays.toString(toret));
		return toret;
	}
	public void runQuery(QueryHandler handler) {
		
		
		if (outputNamespace!=null){ //has to convert
			runner.runQuery(resourcePath, handler, preprocessInput(), specie, resourceNamespace, outputNamespace,convertBuffer);
		}else{
			runner.runQuery(resourcePath, handler, preprocessInput());
		}
	}

}
