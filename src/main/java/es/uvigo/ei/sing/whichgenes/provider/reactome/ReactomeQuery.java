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

import java.net.URL;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

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
	
	public static void main(String[] args) {
		new ReactomeQuery(new ReactomeProvider("hsa"), "hsa").getConstraints();
	}
	
	@Override
	public List<TreeNode> getConstraints() {
	    try {
	    	final List<TreeNode> nodes = new LinkedList<TreeNode>();
	    	final Stack<DefaultTreeNode> nodeStack = new Stack<DefaultTreeNode>();
	    	SAXParserFactory spf = SAXParserFactory.newInstance();
	    	spf.setNamespaceAware(true);
			SAXParser saxParser = spf.newSAXParser();
			
			String specie = "";
			if (this.specie.equalsIgnoreCase("hsa")) {
				specie = "homo+sapiens";
			} else if (this.specie.equalsIgnoreCase("mmu")) {
				specie = "mus+musculus";
			}
			saxParser.parse(new URL("http://reactome.org/ReactomeRESTfulAPI/RESTfulWS/pathwayHierarchy/"+specie).openStream(), new DefaultHandler(){
				
				@Override
				public void startElement(String uri, String localName, String qName, Attributes attributes)
						throws SAXException {
					if (localName.equalsIgnoreCase("pathway")) {
						DefaultTreeNode node = new DefaultTreeNode(attributes.getValue("dbId"), attributes.getValue("displayName"));
						if (nodeStack.size()>0) {
							nodeStack.peek().addChild(node);
						}
						nodeStack.push(node);
					}
				}
				
				@Override
				public void endElement(String uri, String localName, String qName) throws SAXException {
					if (localName.equalsIgnoreCase("pathway")) {
						DefaultTreeNode node = nodeStack.pop();
						if (nodeStack.size()==0) {
							nodes.add(node);
						}
					}
				}
			}, "");
			
			return nodes;
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	    
		/*for (String path:ReactomeManager.getPathways(this.specie)){
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
			
		});*/
		
	}

	public String getParameterDescription() {
		return "Pathways";
	}

	public String getParameterName() {
		return "Reactome Pathway";
	}

	public void runQuery(final QueryHandler handler)  {
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
					if (specie.equalsIgnoreCase("hsa")){
						names = IDConverter.convert("hsapiens_gene_ensembl", new String[]{"uniprot_swissprot", "uniprot_sptrembl"}, "hgnc_symbol", genes);
						
					}else if (specie.equalsIgnoreCase("mmu")){
						names = IDConverter.convert("mmusculus_gene_ensembl", new String[]{"uniprot_swissprot", "uniprot_sptrembl"}, "mgi_symbol", genes);
						
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
