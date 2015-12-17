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

package es.uvigo.ei.sing.whichgenes.provider;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import es.uvigo.ei.sing.whichgenes.Util;
import es.uvigo.ei.sing.whichgenes.query.ConstrainedQuery;
import es.uvigo.ei.sing.whichgenes.query.FreeQuery;
import es.uvigo.ei.sing.whichgenes.query.Query;
import es.uvigo.ei.sing.whichgenes.query.QueryHandler;


public abstract class AbstractGeneSetProvider implements GeneSetProvider {
	private Log log = LogFactory.getLog(AbstractGeneSetProvider.class);

	private String name;
	private String description="";
	private long testTimeout = 240000;
	public AbstractGeneSetProvider(String name){
		String timeoutParam = Util.getConfigParam("whichgenes.watchdog.timeout");
		if (timeoutParam != null) {
			try{
				this.testTimeout = Long.parseLong(timeoutParam);
			} catch(NumberFormatException e) {
				log.error("watchdog timeout is not a number, ignoring");
			}
		}
		this.name = name;
	}
	public String getDescription() {
		return this.description;
	}

	protected void setDescription(String description){
		this.description = description;
	}
	public String getName() {
		return this.name;
	}

	@Override
	public String toString() {
		return this.name;
	}
	
	
	public int testProvider() throws Exception {
		
		class Tester implements QueryHandler{

			Throwable error;
			int results = 0;
			boolean finished=false;
			
			private String name;
			public Tester(String name){
				this.name = name;
			}
			
			public void error(Throwable t) {
				finished=true;
				error = t;
				
			}

			public void finished() {
				log.info(this.name+" finished");
				if (results==0){
					error = new Exception("No results");
				}
				finished=true;
				
				
			}

			public void newResult(String result) {
				results++;
				//log.info("Test result: "+result);
				
			}
			
		}
		
		
		final Tester tester = new Tester(this.getName()+" ["+this.getOrganism()+"]");
		
		final Query q = this.newQuery();
		
		
		Thread testThread = new Thread(){
			public void run(){
				try{
					System.out.println("running test thread "+q);
					if (q instanceof ConstrainedQuery){
						ConstrainedQuery query = (ConstrainedQuery) q;
						
						TreeNode constraint = query.getSampleConstraint();
						if (constraint !=null){
							query.setConstrainedValues(java.util.Arrays.asList(new TreeNode[]{constraint}));
							query.runQuery(tester);
							
							
						}else{
							throw new RuntimeException("constrained values has 0 size");
						}
					}else if (q instanceof FreeQuery){
						FreeQuery query = (FreeQuery) q;
						query.setQuery(query.sampleQuery());
						query.runQuery(tester);
						
						
					}
				}catch(Exception e){
					tester.error = e;
					tester.finished = true;
				}
			}
		};
		
		testThread.start();
		
		testThread.join(testTimeout);
		
		
		// kill thread
		if (testThread.isAlive()){
			
			log.info("Forcing "+tester.name+" stop...");
			q.requestStop();			
			Thread.sleep(5000);
			if (testThread.isAlive()){
				log.info("Forcing "+tester.name+" kill...");
				testThread.interrupt();			
				testThread.stop();
				Thread.sleep(5000);
			}
			log.info(tester.name+" alive: "+testThread.isAlive());
		}
		
		
		if (tester.error!=null){
			throw new RuntimeException(tester.error);
		}
		if (tester.results == 0){
			throw new Exception("no results");
		}
		
		return tester.results;
		
		
		
		
	}

}
