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

package es.uvigo.ei.sing.whichgenes.provider.kegg;

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
import es.uvigo.ei.sing.whichgenes.provider.idconverter.IDConverter;
import es.uvigo.ei.sing.whichgenes.query.ConstrainedQuery;
import es.uvigo.ei.sing.whichgenes.query.QueryHandler;

public class KEGGQuery extends ConstrainedQuery {

	private String specie;
	public KEGGQuery(GeneSetProvider provider, String specie){
		super(provider);
		this.specie = specie;
	}
	
	@Override
	public List<TreeNode> getConstraints() {
		List<TreeNode> nodes = new LinkedList<TreeNode>();
		try {
			for (String path:KEGGManager.getPathways(this.specie)){
				String name = path.replaceAll(" - Homo sapiens \\(human\\) ", "");
				String id = name.substring(name.indexOf("[")+1,name.length()-1);
				name = name.substring(0,name.indexOf("["));
				nodes.add(new DefaultTreeNode(id,name));
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return nodes;
	}

	public String getParameterDescription() {
		return "Pathways";
	}

	public String getParameterName() {
		return "KEGG Pathway";
	}

	public void runQuery(final QueryHandler handler)  {
		HashSet<String> norepeat = new HashSet<String>();
		AautomatorQueryRunner runner  = new AautomatorQueryRunner();
		for (TreeNode value : this.getConstrainedValues()){
			String pathid = value.getID();
			Collection<String> pathway_genes;
			try {
				pathway_genes = KEGGManager.retrieveGenesFromPathay(pathid);
				String[] genes = new String[pathway_genes.size()];
				
				
				int i = 0;
				for (String name : pathway_genes){
					genes[i++] = name.substring(4); //removing "hsa:"					
				}
				
				String[] names = null;
				if (specie.equalsIgnoreCase("hsa")){
					
					names = IDConverter.convert("entrezgene", "hgnc_symbol", genes);
				}else if (specie.equalsIgnoreCase("mmu")){
					names = IDConverter.convert("mmusculus_gene_ensembl","entrezgene", "mgi_symbol", genes);
				}
				
				for (String name : names){
					handler.newResult(name);					
				}
				
			} catch (RemoteException e) {
				handler.error(e);
				handler.finished();
				return;
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
