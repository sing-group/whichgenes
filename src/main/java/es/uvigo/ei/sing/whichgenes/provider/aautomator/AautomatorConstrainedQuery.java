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
import es.uvigo.ei.sing.whichgenes.query.ConstrainedQuery;
import es.uvigo.ei.sing.whichgenes.query.QueryHandler;

public abstract class AautomatorConstrainedQuery extends ConstrainedQuery {

	protected String resourcePath;	
	protected String specie = "hsa";
	protected String resourceNamespace = null;
	protected String outputNamespace = null;
	protected int convertBuffer = 50;
	
	public AautomatorConstrainedQuery(GeneSetProvider provider, String resourcePath){
		super (provider);
		this.resourcePath = resourcePath;
		
	}
	public AautomatorConstrainedQuery(GeneSetProvider provider, String specie, String resourcePath){
		this(provider, resourcePath);
		this.specie = specie;
		
	}
	private AautomatorQueryRunner runner = new AautomatorQueryRunner();

	
	protected String[] preprocessInput(){
		String[] ids = new String[this.getConstrainedValues().size()];
		for (int i = 0; i< ids.length; i++){
			ids[i] = this.getConstrainedValues().get(i).getID();
		}
		System.out.println(Arrays.toString(ids));
		return ids;
	}
	public void runQuery(QueryHandler handler) {
		
		if (outputNamespace!=null){
			runner.runQuery(resourcePath, handler, preprocessInput(), specie, resourceNamespace, outputNamespace, convertBuffer);
		}else{
			runner.runQuery(resourcePath, handler, preprocessInput());
		}

	}
	@Override
	public void requestStop() {
		if (runner!=null) runner.stop();
	}

}
