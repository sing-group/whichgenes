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

package es.uvigo.ei.sing.whichgenes.tomcat;

import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import es.uvigo.ei.sing.whichgenes.Util;
import es.uvigo.ei.sing.whichgenes.provider.ProvidersFacade;
import es.uvigo.ei.sing.whichgenes.provider.go.GOManager;
import es.uvigo.ei.sing.whichgenes.provider.kegg.KEGGManager;
import es.uvigo.ei.sing.whichgenes.provider.reactome.ReactomeManager;
import es.uvigo.ei.sing.whichgenes.zkgui.QueryManager;

public class ContextListener implements ServletContextListener {
	private static Log log = LogFactory.getLog(ContextListener.class);
	public void contextDestroyed(ServletContextEvent arg0) {
		System.out.println("removing session due to app undeploy");
		GOManager.closeSession();
		KEGGManager.releaseResources();
		ReactomeManager.releaseResources();
		QueryManager.noGUIDispatchingPool.shutdownNow();
		
		log.info("Shutting down watchdog timer, due to app stop");
		timer.cancel();
	}

	
	
	private static Timer timer;
	public synchronized void contextInitialized(ServletContextEvent arg0) {
		String watchdog = Util.getConfigParam("whichgenes.watchdog");
		if(watchdog.equalsIgnoreCase("true")){
			log.info("WhichGenes context init. Starting watchdog...."+this);
			
			
			//sometimes this is called twice, argggg, so check it!
			if (timer!=null){
				log.info("Cancelling previous watchdogs.");
				timer.cancel();
				
			}
			timer = new Timer();
			
			timer.schedule(new TimerTask(){
	
				@Override
				public void run() {
					ProvidersFacade.testProviders();				
				}
				
			}, 10000, 14400000);
		}

	}

}
