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

package es.uvigo.ei.sing.whichgenes.user;

import java.io.Serializable;
import java.util.Observable;
import java.util.Observer;

import es.uvigo.ei.sing.whichgenes.geneset.Basket;

public class User implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	private Basket basket = new Basket();
	private String email;
	private String name;
	private String institution;
	private String password;
	private UserNotifier notifier = new UserNotifier();
	private boolean blocked;
	
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public User(String email){
		this.email = email;
	}
	public Basket getBasket() {
		return basket;
	}
	public void setBasket(Basket basket) {
		
		this.basket = basket;
		
		/*this.basket.deleteObserver(notifier);
		this.basket.addObserver(notifier);*/
	}
	public boolean isBlocked() {
		return blocked;
	}
	public void setBlocked(boolean blocked) {
		this.blocked = blocked;
	}
	public String getInstitution() {
		return institution;
	}
	public void setInstitution(String institution) {
		this.institution = institution;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	
	private boolean mailNotificationsEnabled=false;
	
	public void setMailNotificationsEnabled(boolean enabled){
		this.mailNotificationsEnabled = enabled;
	}
	
	class UserNotifier implements Observer, Serializable{
		
			public void update(Observable arg0, Object arg1) {
				if (mailNotificationsEnabled){
					
					try{
						Emailer.sendEmail("no-reply@www.whichgenes.org", User.this.getEmail(), "WhichGenes // Your Basket has been updated", "Hi, Your basket in WhichGenes has been updated");
						System.out.println("which genes: mail sent to: "+User.this.getEmail());
					}catch(Exception e){
						e.printStackTrace();
					}
				
				}
			}
		
	}
	
	
	
	
}
