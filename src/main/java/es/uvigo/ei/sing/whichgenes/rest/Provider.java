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

package es.uvigo.ei.sing.whichgenes.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.ProduceMime;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import es.uvigo.ei.sing.whichgenes.geneset.Gene;
import es.uvigo.ei.sing.whichgenes.geneset.GeneSet;
import es.uvigo.ei.sing.whichgenes.provider.GeneSetProvider;
import es.uvigo.ei.sing.whichgenes.provider.ProvidersFacade;
import es.uvigo.ei.sing.whichgenes.provider.TreeNode;
import es.uvigo.ei.sing.whichgenes.provider.idconverter.IDConverter;
import es.uvigo.ei.sing.whichgenes.query.ConstrainedQuery;
import es.uvigo.ei.sing.whichgenes.query.FreeQuery;
import es.uvigo.ei.sing.whichgenes.query.Query;
import es.uvigo.ei.sing.whichgenes.query.QueryHandler;
import es.uvigo.ei.sing.whichgenes.user.User;
import es.uvigo.ei.sing.whichgenes.user.UserManager;


@ProduceMime("text/plain")
public class Provider {

	private static Log log = LogFactory.getLog(Provider.class);
	private final String LOG_SEP="\t";
	
	private HashMap<String, GeneSetProvider> providers = new HashMap<String, GeneSetProvider>();
	public Provider() {
		for (GeneSetProvider provider: ProvidersFacade.getProviders()){
			providers.put((provider.getOrganism()+"_"+provider.getName()).replaceAll(" ", ""), provider);
		}
	}
	
	@GET
	@Path("/sources/{provider}/{query}")	 
	public StreamingOutput runProvider(@PathParam("provider") final String providerkey, @PathParam("query") final String query){
		final StringBuffer queryLog = new StringBuffer();
		System.out.println("API_QUERY"+LOG_SEP+"RUN_PROVIDER"+LOG_SEP+providerkey+LOG_SEP+query);
		queryLog.append("API_QUERY"+LOG_SEP+"RUN_PROVIDER"+LOG_SEP+providerkey+LOG_SEP+query);
		return new StreamingOutput(){
			public void write(OutputStream arg0) throws IOException, WebApplicationException {
				GeneSetProvider provider = providers.get(providerkey);
				final PrintStream out = new PrintStream(arg0);
				if (provider==null){
					out.println("no provider with name "+providerkey);
				}
				Query q = provider.newQuery();
				
				
				
				class MyQueryHandler implements QueryHandler{
					int results = 0;
					public void error(Throwable t) {
						StringWriter writer = new StringWriter();
						t.printStackTrace(new PrintWriter(writer));					
						out.println("***error in whichgenes service***\n"+writer.getBuffer().toString());
						queryLog.append(LOG_SEP+"error: "+t.getMessage());
						log.info(queryLog.toString());
					}
		
					public void finished() {
						queryLog.append(LOG_SEP+"success"+LOG_SEP+results+" genes");
						log.info(queryLog.toString());
						
					}
		
					private boolean first = true;
					public void newResult(String result) {
						if(!first) out.println("");
						else first = false;
						results++;
						out.print(result);
						
					}
					
				
				}
				if (q instanceof FreeQuery){
					FreeQuery fq = (FreeQuery) q;
					fq.setQuery(query);
					fq.runQuery(new MyQueryHandler());
				}
				if (q instanceof ConstrainedQuery){
					ConstrainedQuery cq = (ConstrainedQuery) q;
					String[] ids = query.split(",");
					List<TreeNode> nodes = new ArrayList<TreeNode>(ids.length);
					for (final String id: ids){
						nodes.add(new TreeNode(){
							
							public List<TreeNode> getChilds() {
								return null;
							}
		
							public String getID() {
								return id;
							}
		
							public String getName() {
								return null;
							}
							
						});
					}
					cq.setConstrainedValues(nodes);
					cq.runQuery(new MyQueryHandler());
				}
					// TODO Auto-generated method stub
			
		
		
		
			}
		};
		
		
	}
	@GET
	@Path("/sources/")	 
	public String getSources(){
		String toret = "";
		for (String prov : providers.keySet()){
			toret+=prov+"\n";
		}
		return toret;
		
	}
	
	@GET
	@ProduceMime("application/xml")
	@Path("/sources/{provider}/")
	public Object getCatalog(@PathParam("provider") String providerkey){
		GeneSetProvider provider = providers.get(providerkey);
		
		if (provider == null) throw new RuntimeException("Provider "+providerkey+" not found");
		
		Query q = provider.newQuery();

		if (q instanceof ConstrainedQuery){
			ConstrainedQuery cq = (ConstrainedQuery) q;
			List<JAXBTreeNode> toret = new LinkedList<JAXBTreeNode>();
			for (TreeNode child : cq.getConstraints()){
				toret.add(new JAXBTreeNode(child));
			}
			return new TreeNodeList(toret);
		}else{
			return "Error: This source has no catalog. Please enter a free query";
		}
		
	}
	@GET
	@ProduceMime("text/html")
	@Path("/")
	public String showHelp() throws IOException{
		BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/REST.html")));
		String line = null;
		StringBuffer out = new StringBuffer();
		while ((line = reader.readLine())!=null){
			out.append(line+"\n");
		}
		return out.toString();
		
		
	}
	
	@GET
	@Path("/basket/{username}/genesets")	 
	public String getGeneSets(@PathParam("username") String username, @QueryParam("password") String password){
		try{
			
			User user = UserManager.retrieveUser(username, password);
			StringBuilder result = new StringBuilder();
			for (GeneSet set: user.getBasket().getSets()){
				result.append(set.getName()+"\n");
			}
			return result.toString();
		}catch(Exception e){
			return "Error: "+e.getMessage();
		}
	}
	
	@GET
	@Path("/basket/namespaces")	 
	public String getNamespaces(){
		StringBuilder toret = new StringBuilder();
		
		for (String humanID : IDConverter.getHumanIDs()){
			toret.append("homo__"+humanID+"\n");
		}
		
		for (String mouseID : IDConverter.getMouseIDs()){
			toret.append("mus_musculus__"+mouseID+"\n");
		}
		return toret.toString();
	}
	
	@GET
	@Path("/basket/{username}/{geneset}/")	 
	public String getGeneSet(@PathParam("username") String username, @PathParam("geneset") String genesetname, @QueryParam("password") String password, @QueryParam("namespace") String namespace){
		try{
			User user = UserManager.retrieveUser(username, password);
			StringBuilder toret = new StringBuilder();
			
			GeneSet geneset = user.getBasket().getByName(genesetname); 
			if(geneset!=null){
				if (namespace!=null){
					HashSet<String> converted_ids = new HashSet<String>();

					String[] gene_ids = new String[geneset.getGenes().size()];
					int i = 0;
					for (Gene g: geneset.getGenes()){
						gene_ids[i++] = g.getName();
					}
					
					
							
					String[] converted_ids_repeat = null;
					String name = namespace.substring(namespace.indexOf("__")+2);
					if (namespace.contains("mus_musculus") && IDConverter.getMouseIDs().contains(name)){
						//mouse
						converted_ids_repeat=IDConverter.convert("mmusculus_gene_ensembl", "mgi_symbol",name ,gene_ids);
					}else if (namespace.contains("homo") && IDConverter.getHumanIDs().contains(name)){
						//human
						converted_ids_repeat=IDConverter.convert("hgnc_symbol",name ,gene_ids);
					}else{
						return "Error: unrecognized namespace "+namespace;
					}

					for (String id: converted_ids_repeat){
						converted_ids.add(id);
					}
					for (String id: converted_ids){
						toret.append(id+"\n");
					}
					
				}else{
					for (Gene gene : geneset.getGenes()){
						toret.append(gene.getName()+"\n");
					}
				}
			}else{
				return "Error: geneset "+geneset+" not found in basket";
			}
			return toret.toString();
		}catch(Exception e){
			return "Error: "+e.getMessage();
		}
		
	}
	
}
