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

import java.util.HashMap;
import java.util.HashSet;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Timer;

public class GlobalTimer {
	private Timer timer = new Timer();
	private HashMap<String, Runnable> tasks = new HashMap<String, Runnable>();
	
	private static String GLOBAL_TIMER_NAME= "____globaltimer____";
	public GlobalTimer(){
		timer.setDelay(1000);
		timer.setRepeats(true);
		timer.stop();
		timer.addEventListener("onTimer", new EventListener(){

			public void onEvent(Event arg0) throws Exception {
				doTasks();
				
			}
			
		});
	}
	
	public void setGlobalTimerDelay(int delay){
		timer.setDelay(delay);
		
	}
	public synchronized String addTask(Runnable run){
		String name = ""+Math.random();
		System.out.println("gtimer: adding task "+name);
		
		tasks.put(name, run);
		if (!timer.isRunning()){
			System.out.println("gtimer: starting timer");
			timer.start();
		}
		return name;
	}
	
	public synchronized void removeTask(String name){
		if (doingTasks){
			// some tasks removes thier counter in their runnable, so
			// a concurrent exception may occur in the doTasks() loop
			// If we are in the loop, we will remove the task later
			toRemove.add(name);
		}else{
			tasks.remove(name);
			if (tasks.size() == 0 && timer.isRunning()){
				System.out.println("gtimer: stopping timer");timer.stop();
			}
		}
	}
	
	private HashSet<String> toRemove= new HashSet<String>();
	private boolean doingTasks = false;
	private synchronized void doTasks(){
		doingTasks = true;
		System.out.println("gtimer: dispatching tasks");
		for (String taskName: tasks.keySet()){
			System.out.println("gtimer: dispatching "+taskName);
			try{
				tasks.get(taskName).run();
			}catch(Exception e){
				//Exception during e
				System.out.println("Uncaught exception during timer dispatch. Removing this timer");				
				e.printStackTrace();
				removeTask(taskName);
			}
		}
		doingTasks = false;
		doRemoves();
		
	}
	
	private void doRemoves() {
		for (String task : toRemove){
			removeTask(task);
		}
		toRemove.clear();
		
	}

	public static GlobalTimer getOrCreateGlobalTimer(Component parent){
		GlobalTimer gtimer = (GlobalTimer) parent.getPage().getVariable(GLOBAL_TIMER_NAME); 
		if (gtimer == null){
			System.out.println("timer was null, creating one");
			gtimer = new GlobalTimer();
			parent.getPage().setVariable(GLOBAL_TIMER_NAME, gtimer);
			parent.appendChild(gtimer.timer);
		}else{
			System.out.println("there is a timer yet, not creating");
		}
		return gtimer;
	}
}
