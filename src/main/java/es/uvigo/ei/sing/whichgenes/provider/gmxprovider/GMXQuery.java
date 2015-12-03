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

package es.uvigo.ei.sing.whichgenes.provider.gmxprovider;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;


import es.uvigo.ei.sing.whichgenes.provider.DefaultTreeNode;
import es.uvigo.ei.sing.whichgenes.provider.GeneSetProvider;
import es.uvigo.ei.sing.whichgenes.provider.TreeNode;
import es.uvigo.ei.sing.whichgenes.provider.aautomator.AautomatorQueryRunner;
import es.uvigo.ei.sing.whichgenes.provider.aautomator.Util;
import es.uvigo.ei.sing.whichgenes.provider.idconverter.IDConverter;
import es.uvigo.ei.sing.whichgenes.query.ConstrainedQuery;
import es.uvigo.ei.sing.whichgenes.query.QueryHandler;

public abstract class GMXQuery extends ConstrainedQuery {

	private String specie;
	
	private GMXManager manager;
	private String namespace;
	public GMXQuery(GeneSetProvider provider, String specie, String gmxpath, String namespace){
		super(provider);
		this.specie = specie;
		this.namespace = namespace;
		try {
			this.manager = new GMXManager(gmxpath);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public List<TreeNode> getConstraints() {
		List<TreeNode> nodes = new LinkedList<TreeNode>();
		for (String path:manager.getSetNames()){
			String name = path;
			String id = name;			
			nodes.add(new DefaultTreeNode(id,name));
		}
		return nodes;
	}

	

	public void runQuery(final QueryHandler handler)  {
		
		
		for (TreeNode node : this.getConstrainedValues()){
			String pathid = node.getID();
			Collection<String> pathway_genes;
			try {
				pathway_genes = manager.getSetGenes(pathid);
				String[] genes = new String[pathway_genes.size()];
				pathway_genes.toArray(genes);
				
				String[] names = null;
				if (specie.equalsIgnoreCase("hsa")){
					
					names = IDConverter.convert(namespace, "hgnc_symbol", genes);
				}else if (specie.equalsIgnoreCase("mmu")){
					names = IDConverter.convert("mmusculus_gene_ensembl",namespace, "mgi_symbol", genes);
				}
				
				for (String name : names){
					handler.newResult(name);					
				}
				
			} 
			catch(Exception e){
				e.printStackTrace();
				handler.error(e);
				handler.finished();
			}
			
		
		}
		handler.finished();
		

	}

}
