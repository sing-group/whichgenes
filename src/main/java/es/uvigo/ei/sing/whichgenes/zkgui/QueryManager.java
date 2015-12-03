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

package es.uvigo.ei.sing.whichgenes.zkgui;

import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.logging.Logger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.zkoss.zhtml.Messagebox;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Box;
import org.zkoss.zul.Button;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;

import es.uvigo.ei.sing.whichgenes.geneset.Basket;
import es.uvigo.ei.sing.whichgenes.geneset.Gene;
import es.uvigo.ei.sing.whichgenes.geneset.GeneSet;
import es.uvigo.ei.sing.whichgenes.provider.GeneSetProvider;
import es.uvigo.ei.sing.whichgenes.provider.TreeNode;
import es.uvigo.ei.sing.whichgenes.query.ConstrainedQuery;
import es.uvigo.ei.sing.whichgenes.query.FreeQuery;
import es.uvigo.ei.sing.whichgenes.query.Query;
import es.uvigo.ei.sing.whichgenes.query.QueryHandler;
import es.uvigo.ei.sing.whichgenes.user.Emailer;
import es.uvigo.ei.sing.whichgenes.user.User;

public class QueryManager extends Box {
	private static Log log = LogFactory.getLog(QueryManager.class);
	private static final long serialVersionUID = 1L;

	// Events
	public static final String ONFINISH_EVENT = "onFinish";
	
	// Components

	private Button stopButton;
	private Label statusLabel = new Label("Retrieving genes ");
	private Image loadingImage;
	private EnhancedGrid enhancedGrid;
	private Hbox runningControls = new Hbox();

	// Fields
	
	
	private int currentQuery = 0; //we are populating this query
	private int currentViewingQuery = 0; //we are refreshing on this query 1 before currentQuery sometimes...
	private Query[] q = null;	
	private String setName;
	private LinkedList<String>[] currentRes;
	private boolean[] finished;
	private Object lock = new Object();
	private boolean[] hasResults;
	
	private boolean stopRequested;	
	protected boolean[] setCreated;	
	private HashSet<String>[] allResults;
	private Throwable[] error;
	
	
	private Button getStopButton() {
		
		if (this.stopButton == null) {
			this.stopButton = new Button();
			this.stopButton.setLabel("Stop");
			this.stopButton.setVisible(false);
			this.stopButton.addEventListener("onClick", new EventListener(){

				public void onEvent(Event arg0) throws Exception {
					stop();
					
				}
				
				
			});
		}
		return this.stopButton;
	}

	private Image getLoadingImage() {
		if (this.loadingImage == null) {
			this.loadingImage = new Image();
			this.loadingImage.setSrc("loading.gif");
		}
		return this.loadingImage;
	}

	
	private synchronized void createSet(String name, int pos) {

		if (this.setCreated[pos]){
			System.out.println("Set "+name+" yet created, skipping");
			return; 
		}
		System.out.println("Creating set "+name);

		GeneSet set = new GeneSet(name);
		for (String result : this.allResults[pos]) {

			set.addGene(new Gene(result));
		}
		basket.addGeneSet(set);
		this.setCreated[pos] = true;
	}

	private void onFinish() {

		GlobalTimer timer = GlobalTimer.getOrCreateGlobalTimer(this);
		
		
		
		if (!this.setCreated[currentViewingQuery]&& this.error[currentViewingQuery]==null){
			String _setName = setName;
			if (q.length>1){
				if (q[currentViewingQuery] instanceof ConstrainedQuery){
					_setName+="_"+((ConstrainedQuery) q[currentViewingQuery]).getConstrainedValues().get(0).getName();
				}else{
					_setName+="_"+((FreeQuery) q[currentViewingQuery]).getQuery();
				}
			}
			
			if (!stopRequested) {
				createSet(_setName, currentViewingQuery);
				statusLabel.setValue("Added "+(currentViewingQuery+1)+" set(s) to your basket");
			}
			
		}
		if (currentViewingQuery == this.q.length-1){
			timer.removeTask(timerName);
			getLoadingImage().setVisible(false);
			statusLabel.setValue("Finished");
			if (this.error[currentViewingQuery]!=null){
				this.error[currentViewingQuery].printStackTrace();
				statusLabel.setValue("Finished with error: "+this.error[currentViewingQuery].getMessage());
				statusLabel.setStyle("color:red");
			}else{
				statusLabel.setStyle("color:green");
				
			}
			
			getStopButton().setVisible(false);
			Event evt = new Event(ONFINISH_EVENT, this, this.error[currentViewingQuery]);
			Events.postEvent(evt);
		}else{
			currentViewingQuery ++;
		}
		

	}

	private EnhancedGrid getEnhancedGrid() {
		if (this.enhancedGrid == null) {
			this.enhancedGrid = new EnhancedGrid();
			this.enhancedGrid.setMultiple(true);

			this.enhancedGrid.setRowTitles(new String[] { "Gene name" });
			this.enhancedGrid.getListBox().setMold("paging");
			this.enhancedGrid.getListBox().setPageSize(100);
			this.enhancedGrid.setShowClearAll(false);
			this.enhancedGrid.setShowRemoveSelected(false);
			this.enhancedGrid.setShowSelectionButtons(false);
			this.enhancedGrid.getListBox().setCheckmark(false);
			this.enhancedGrid.getListBox().setMultiple(false);
		}
		return this.enhancedGrid;
	}

	private void initialize() {

		runningControls = new Hbox();
		runningControls.appendChild(getStopButton());
		
		runningControls.appendChild(statusLabel);
		runningControls.appendChild(getLoadingImage());
		this.appendChild(runningControls);

		this.appendChild(getEnhancedGrid());

	}

	public QueryManager(String setName) {
		this.setName = setName;
		initialize();

	}

	private Basket basket;

	private User user;

	public void refresh() {
			if ((User) getPage().getDesktop().getSession().getAttribute("user") != null) {
				this.user = (User) getPage().getDesktop().getSession().getAttribute("user");
			}
	
			if (error[currentViewingQuery]==null){
				List mycurrentRes = popResults();
				for (int i = 0; i < mycurrentRes.size(); i++) {
					String _setName = setName;
					if (q.length>1){
						if (q[currentViewingQuery] instanceof ConstrainedQuery){
							_setName+="_"+((ConstrainedQuery) q[currentViewingQuery]).getConstrainedValues().get(0).getName();
						}else{
							_setName+="_"+((FreeQuery) q[currentViewingQuery]).getQuery();
						}
					}
					String[] row = new String[] { mycurrentRes.get(i).toString(), _setName};
					if (this.q.length==1){
						row = new String[] { mycurrentRes.get(i).toString()};
					}
					getEnhancedGrid().addRow(row);
				}
			}
			if (finished[currentViewingQuery]) {
				onFinish();
				
			}
		
			// commented lines, cause the message box cannot be closed, i dont know why since it is in the event dispatch thread... only it is in a timer?
			/*if (error[currentViewingQuery] != null){
				try {
					Messagebox.show("There were an error during the query: "+error[currentViewingQuery].getMessage(), "Error", Messagebox.OK, Messagebox.ERROR);
					
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}*/
		

	}

	public void setBasket(Basket b) {
		this.basket = b;

	}

	String timerName = "";

	class Refresher implements Runnable {
		QueryManager manager;

		public Refresher(QueryManager manager) {
			this.manager = manager;
		}

		public void run() {
			this.manager.refresh();
		}

	}

	Refresher refresher = new Refresher(this);

	

	

	public void addResult(String result) {
		
		currentRes[currentQuery].add(result);
	}

	public void setHasResults(boolean has) {
		hasResults[currentQuery] = has;
	}

	public void setFinished(boolean finished) {
		this.finished[currentQuery] = finished;
	}

	public static ExecutorService noGUIDispatchingPool = java.util.concurrent.Executors.newFixedThreadPool(2);
	class Handler implements QueryHandler {
		QueryManager manager;
		QueryLog log;
		public Handler(QueryManager manager, QueryLog log) {
			this.manager = manager;
			this.log = log;
		}

		public void newResult(String result) {
			synchronized (lock) {
				allResults[currentQuery].add(result);
				//System.out.println("Adding gene: " + result + " "	+ allResults[currentQuery].size() + " genes");
				manager.addResult(result);
				manager.setHasResults(true);
			}
		}

		
		public void finished() {
			System.out.println("which genes: Query finished");
			manager.setFinished(true);
			final int localcurrentQuery = currentQuery;
			
			log.setname_prefix = setName;
			log.sets++;
			log.genes+=allResults[localcurrentQuery].size();
			if (error[localcurrentQuery]==null){
				log.success = "success";
			}else{
				log.success= "fail";
				
			}
			
			noGUIDispatchingPool.submit(new Runnable(){
				public void run(){
					try {
						long sec = 10000*localcurrentQuery+10000;
						System.out.println("sleeping: "+sec+" secs");
						Thread.sleep(sec); //sleep to ensure the creation of the set, but if the client is online it will be created when the user updates the UI (onFinish) 
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
					if(!stopRequested && error[localcurrentQuery]==null){
						String _setName = setName;
						
						if (q.length>1){
							if (q[localcurrentQuery] instanceof ConstrainedQuery){
								_setName+="_"+((ConstrainedQuery) q[localcurrentQuery]).getConstrainedValues().get(0).getName();
							}else{
								_setName+="_"+((FreeQuery) q[localcurrentQuery]).getQuery();
							}
						}
						
						
						System.out.println("Thread awake: "+QueryManager.this.user.getName()); 
						createSet(_setName, localcurrentQuery);	//this creates only if not was created					
						
						
						
						if (QueryManager.this.user != null) {
							try {
								String host = es.uvigo.ei.sing.whichgenes.Util.getHost();
								if (q.length>1){
									if (localcurrentQuery == q.length-1){
										System.out.println("which genes: And also to send an PACK email");
										String link = "http://"+host+"/whichgenes.zul?t=sg&l="+QueryManager.this.user.getEmail()+"&p="+java.net.URLEncoder.encode(Util.DesEncrypter.encrypt(QueryManager.this.user.getPassword()),"utf-8");
										Emailer.sendEmail("no-reply@www.whichgenes.org",
												QueryManager.this.user.getEmail(),
												"WhichGenes // Your Basket has been updated",
												"Hi "+QueryManager.this.user.getName()+"\nYour WhichGenes basket has been updated.\nA new set of groups with prefix \""
												+ setName + "\" was added to your basket.\n\nPlease enter WhichGenes and see your basket at: "+link);
										System.out.println("which genes: mail sent to: "
												+ QueryManager.this.user.getEmail());
										}	
									
								}else{
									System.out.println("which genes: And also to send an email");
									String link = "http://"+host+"/whichgenes.zul?t=sg&l="+QueryManager.this.user.getEmail()+"&p="+java.net.URLEncoder.encode(Util.DesEncrypter.encrypt(QueryManager.this.user.getPassword()),"utf-8")+"&s="+java.net.URLEncoder.encode(setName,"utf-8");
									Emailer.sendEmail("no-reply@www.whichgenes.org",
											QueryManager.this.user.getEmail(),
											"WhichGenes // Your Basket has been updated",
											"Hi "+QueryManager.this.user.getName()+"\nYour WhichGenes basket has been updated.\nA new group called \""
											+ setName + "\" was added to your basket.\n\nPlease enter WhichGenes and see this group at: "+link);
									System.out.println("which genes: mail sent to: "
											+ QueryManager.this.user.getEmail());
									}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}
			});
			
			

		}
		
		public void error(Throwable t) {
			
			error[currentQuery] = t;
			

		}

	};

	public void runQueries(Query[] queries){
		
		currentQuery = 0;
		
		finished = new boolean[queries.length];
		hasResults = new boolean[queries.length];
		stopRequested = false;
		currentRes = new LinkedList[queries.length];
		setCreated = new boolean[queries.length];
		allResults = new HashSet[queries.length];
		error = new Throwable[queries.length];
		norepeat = new HashSet[queries.length];
		
		String[] titles = new String[]{"Gene name"};
		if (queries.length > 1){
			titles = new String[]{"Gene name", "Set"};
		}
		getEnhancedGrid().setRowTitles(titles);
		this.q = queries;
		
		for (int i = 0; i< queries.length; i++){
			finished[i] = false;
			hasResults[i] = false;		
			setCreated[i] = false;
			norepeat[i] = new HashSet<String>();
		}
		
		
		getLoadingImage().setVisible(true);
		if (queries.length>1) getStopButton().setVisible(true);
		
		
		
		GlobalTimer timer = GlobalTimer.getOrCreateGlobalTimer(this);
		timerName = timer.addTask(refresher);
 
		
		
		class MyThread extends Thread {
			public void run() {
				QueryLog queryLog = new QueryLog();
				long start = System.currentTimeMillis();
				
				
				queryLog.multiple = (q.length>1)?"multiple":"simple";
				
				
				queryLog.query="";
				for (int i = 0; i<q.length; i++){
					currentQuery = i;
					currentRes[currentQuery] = new LinkedList<String>();
					allResults[currentQuery] = new HashSet<String>();
					if (!stopRequested){
						queryLog.free_constraint = (q[currentQuery] instanceof FreeQuery)?"free":"constrained"; 
						q[currentQuery].runQuery(new Handler(QueryManager.this, queryLog));
					}
					
					Query query = q[currentQuery];
				
					GeneSetProvider provider = query.getProvider();
					if (provider !=null){
						queryLog.source = provider.getName();
						queryLog.organism = provider.getOrganism();						
					}
					if(query instanceof FreeQuery){
						if (i>0){
							queryLog.query+="#"; 
						}
						queryLog.query += ((FreeQuery)query).getQuery();
					}else if (query instanceof ConstrainedQuery){
						if (i==0){							
							boolean first = true;
							for (TreeNode node : ((ConstrainedQuery)query).getConstrainedValues()){
								if (!first){
									queryLog.query+=", ";
								}
								queryLog.query += node.getID()+":"+node.getName();
								if (first) first = false;
							}
						}
						
					}
					finished[currentQuery] = true;
				}
				
				queryLog.time = System.currentTimeMillis() - start;
				
				queryLog.user = (QueryManager.this.user!=null)?QueryManager.this.user.getEmail():"anonymous";
				log.info(queryLog.toString());
				
			}
		}
		;
		new MyThread().start();
		
	}
	public void runQuery(Query query) {
		
		runQueries(new Query[]{query});

	}

	HashSet<String>[] norepeat;

	private List<String> popResults() {
		LinkedList<String> toret = new LinkedList<String>();
		synchronized (lock) {
			for (String current : currentRes[currentViewingQuery]) {
				if (!norepeat[currentViewingQuery].contains(current)) {
					norepeat[currentViewingQuery].add(current);
					toret.add(current);
				}
			}
			currentRes[currentViewingQuery].clear();

		}
		return toret;
	}

	public boolean getFinished() {
		return finished[currentQuery];
	}

	public Basket getBasket() {
		return basket;
	}

	public void stop() {
		if (q != null) {
			stopRequested = true;
			q[currentQuery].requestStop();
		}
	}

	public EnhancedGrid getData() {
		return getEnhancedGrid();
	}



	private class QueryLog{
		String type="QUERY";
		Date date = new Date();
		String user;
		String source;
		String organism;
		String free_constraint; //free_query / constrained_query
		String query;
		String multiple; // simple / multiple
		String setname_prefix;
		int sets;
		int genes;
		long time;
		String success; //success/fail
		
		String SEP="\t";
		public String toString(){
			String toret="";
			toret+=type;
			toret+=SEP+date;
			toret+=SEP+user;
			toret+=SEP+source;
			toret+=SEP+organism;
			toret+=SEP+free_constraint;
			toret+=SEP+query;
			toret+=SEP+multiple;
			toret+=SEP+setname_prefix;
			toret+=SEP+sets+" set(s)";
			toret+=SEP+genes+" gene(s)";
			toret+=SEP+time+" millis.";
			toret+=SEP+success;
			
			
			return toret;
		}
	}
}
