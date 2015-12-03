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

package es.uvigo.ei.sing.whichgenes.provider.reactome;

import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;

import es.uvigo.ei.sing.whichgenes.provider.DefaultTreeNode;
import es.uvigo.ei.sing.whichgenes.provider.GeneSetProvider;
import es.uvigo.ei.sing.whichgenes.provider.TreeNode;
import es.uvigo.ei.sing.whichgenes.provider.aautomator.AautomatorQueryRunner;
import es.uvigo.ei.sing.whichgenes.provider.idconverter.IDConverter;
import es.uvigo.ei.sing.whichgenes.query.ConstrainedQuery;
import es.uvigo.ei.sing.whichgenes.query.QueryHandler;

public class ReactomeQuery extends ConstrainedQuery {

	private String specie;
	public ReactomeQuery(GeneSetProvider provider, String specie){
		super(provider);
		this.specie = specie;
	}
	
	@Override
	public List<TreeNode> getConstraints() {
		List<TreeNode> nodes = new LinkedList<TreeNode>();
		for (String path:ReactomeManager.getPathways(this.specie)){
			String[] pair = path.split("\t");
			if (pair == null || pair.length<2) continue;
			String name = pair[1];
			String id = pair[0];
			System.out.println("adding pair: "+id+" "+name);
			nodes.add(new DefaultTreeNode(id,name));
		}
		Collections.sort(nodes, new Comparator<TreeNode>(){

			public int compare(TreeNode arg0, TreeNode arg1) {
				return arg0.getName().compareTo(arg1.getName());
			}
			
		});
		return nodes;
	}

	public String getParameterDescription() {
		return "Pathways";
	}

	public String getParameterName() {
		return "Reactome Pathway";
	}

	public void runQuery(final QueryHandler handler)  {
		HashSet<String> norepeat = new HashSet<String>();
		AautomatorQueryRunner runner  = new AautomatorQueryRunner();
		for (TreeNode node : this.getConstrainedValues()){
			String pathid = node.getID();
			Collection<String> pathway_genes;
			try {
				pathway_genes = ReactomeManager.retrieveGenesFromPathay(pathid, this.specie); //uniprots...
				
				if (pathway_genes.size()>0){
					String[] genes = new String[pathway_genes.size()];
					
					int i = 0;
					for (String gene:pathway_genes){
						genes[i++] =gene;
					}
					
					
					
					
					String[] names = null;
					if (specie.equalsIgnoreCase("homo sapiens")){
						names = IDConverter.convert("hsapiens_gene_ensembl", "uniprot_swissprot", "hgnc_symbol", genes);
						
					}else if (specie.equalsIgnoreCase("mus musculus")){
						names = IDConverter.convert("mmusculus_gene_ensembl", "uniprot_swissprot", "mgi_symbol", genes);
						
					}
					
					for (String name : names){
						handler.newResult(name);					
					}
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
