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



import java.io.UnsupportedEncodingException;
import java.util.Observable;
import java.util.Observer;

import org.zkoss.zhtml.Messagebox;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vbox;

import es.uvigo.ei.sing.whichgenes.geneset.Basket;
import es.uvigo.ei.sing.whichgenes.user.Emailer;
import es.uvigo.ei.sing.whichgenes.user.User;
import es.uvigo.ei.sing.whichgenes.user.UserManager;


public class LoginBox extends Vbox {
	private Vbox nologinBox;
	
	private RegistrationForm registrationForm = new RegistrationForm();
	private Groupbox registrationGroup;
	
	public class RegistrationForm extends Vbox{
		public Textbox email, password, name, institution;
		public Button registerButton;
		public Grid grid;
		public RegistrationForm(){
			initialize();
		}

		
		public Button getRegisterButton(){
			if (this.registerButton == null){
				this.registerButton = new Button("Register");
			
				this.registerButton.addEventListener("onClick", new EventListener(){
	
					public void onEvent(Event arg0) throws Exception {
						try{
							if (UserManager.userExists(RegistrationForm.this.email.getValue())){
								throw new RuntimeException("This username is yet used");
								
							}
							
							// check fields
							if (RegistrationForm.this.email.getValue().length() == 0){
								throw new RuntimeException("The email is manadatory");
							}
							if (RegistrationForm.this.name.getValue().length() == 0){
								throw new RuntimeException("The name is manadatory");
							}
							if (RegistrationForm.this.password.getValue().length() == 0){
								throw new RuntimeException("The password is manadatory");
							}
							
							User u = new User(RegistrationForm.this.email.getValue());
							u.setPassword(RegistrationForm.this.password.getValue());
							u.setName(RegistrationForm.this.name.getValue());
							u.setInstitution(RegistrationForm.this.institution.getValue());
							u.setBlocked(true);
							
							UserManager.saveUser(u);
							
							//doLogin(RegistrationForm.this.email.getValue(), RegistrationForm.this.password.getValue());
							sendActivation(u);
							
							try {
								Messagebox.show("Welcome "+u.getName()+". Please check your email in order to finish the registration process.", "Welcome", Messagebox.OK, Messagebox.INFORMATION);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
						}catch(Exception e){
							e.printStackTrace();
							Messagebox.show("Can't do login: "+e.getMessage(), "Error", Messagebox.OK, Messagebox.ERROR);
						}
						
					}
					
				});
			}
			return this.registerButton;
		}
		private void initialize() {
			
			
			this.appendChild(getGrid());
			this.appendChild(getRegisterButton());
			
			
		}
		
		private Grid getGrid(){
			if (this.grid == null){
				this.email = new Textbox();
				this.email.setConstraint("/.+@.+\\.[a-z]+/: Please enter a valid e-mail address");				
				this.password = new Textbox();
				this.password.setType("password");				
				this.name = new Textbox();
				
				this.institution = new Textbox();
				
				this.grid = new Grid();
				Rows rows = new Rows();
				
				Row row=null;
				row = new Row(); row.appendChild(new Label("email*")); row.appendChild(this.email); rows.appendChild(row);
				row = new Row(); row.appendChild(new Label("password*")); row.appendChild(this.password); rows.appendChild(row);
				row = new Row(); row.appendChild(new Label("name*")); row.appendChild(this.name); rows.appendChild(row);
				row = new Row(); row.appendChild(new Label("institution")); row.appendChild(this.institution); rows.appendChild(row);
				
				this.grid.appendChild(rows);
			}
			return this.grid;
		}
	
	}
	
	private Hbox loggedBox;
	private Label welcomeLabel = new Label();
	private User user;
	
	private static final String ONLOGIN= "onLogin";
	private static final String ONLOGOUT= "onLogout";
	
	private void sendActivation(User u){
		try {
			String host = es.uvigo.ei.sing.whichgenes.Util.getHost();
			String link = "http://"+host+"/whichgenes.zul?t=a&l="+u.getEmail()+"&p="+java.net.URLEncoder.encode(Util.DesEncrypter.encrypt(u.getPassword()),"utf-8");
			Emailer.sendEmail("no-reply@www.whichgenes.org", u.getEmail(),"WhichGenes // Welcome","Dear "+ u.getName()+",\nWelcome to WhichGenes. In order to complete your registration procedure, please follow the link:\n"+link);
			System.out.println("which genes: activation mail sent to: "	+ u.getEmail());
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
	}
	private Groupbox getRegistrationBox(){
		if (this.registrationGroup==null){
			this.registrationGroup = new Groupbox();
			this.registrationGroup.setMold("3d");
			
		}
		return this.registrationGroup;
	}
	private Vbox getNoLoginBox(){
		if (nologinBox == null){
			nologinBox = new Vbox();
			
			
			
			
			
			final Textbox userfield = new Textbox();			
			userfield.setFocus(true); //it doesnt work
			userfield.focus();  // it doesnt work
			final Textbox passwordfield = new Textbox();
			
			
			passwordfield.setType("password");
			
			
			
			
			
			Grid grid = new Grid();
			Rows rows = new Rows();
			
			Row row=null;
			row = new Row(); row.appendChild(new Label("email")); row.appendChild(userfield); rows.appendChild(row);
			row = new Row(); row.appendChild(new Label("password")); row.appendChild(passwordfield); rows.appendChild(row);
			
			grid.appendChild(rows);
			
			
			nologinBox.appendChild(grid);
			
			
			
			
			class LoginListener implements EventListener{
				public void onEvent(Event arg0) throws Exception {
					try{
						doLogin(userfield.getValue(), passwordfield.getValue(), false);						
					}catch(Exception e){
						Messagebox.show("Can't do login: "+e.getMessage(), "Error", Messagebox.OK, Messagebox.ERROR);
					}
					
				}
			};
			LoginListener listener = new LoginListener();
			passwordfield.addEventListener("onOK", listener);
			

			Button loginButton = new Button("Login");
			
			loginButton.addEventListener("onClick", listener);
			
			
			
			Button newUserButton = new Button("New user");
			
			newUserButton.addEventListener("onClick", new EventListener(){

				public void onEvent(Event arg0) throws Exception {
					getRegistrationBox().setVisible(true);
					
				}
				
			});
			
			
			nologinBox.appendChild(new Hbox(new Component[]{loginButton,newUserButton}));
			
		}
		return nologinBox;
	}
	public void doLogout(){
		LoginBox.this.getPage().getDesktop().getSession().removeAttribute("user");
		
		Event evt = new Event(ONLOGOUT, LoginBox.this, LoginBox.this.user);		
		Events.postEvent(evt);

		LoginBox.this.user = null;
	
		setLoginMode(false);
		Executions.sendRedirect(".");
		
	}
	private Hbox getLoggedBox(){
		if (loggedBox == null){
			loggedBox = new Hbox();
			loggedBox.appendChild(welcomeLabel);
			welcomeLabel.setValue("");
			
			
			Button logout = new Button("logout");
			
			logout.addEventListener("onClick", new EventListener(){

				public void onEvent(Event arg0) throws Exception {
					doLogout();					
				}
				
			});
			loggedBox.appendChild(logout);
		}
		return loggedBox;
	}
	public void doLogin(String user, String pass, boolean activate) {
		User u = UserManager.retrieveUser(user, pass);
		if (u.isBlocked() && !activate){
			sendActivation(u);
			throw new RuntimeException("User is not active. The activation email has been resent. Please check your mail");
		}else{
			u.setBlocked(false);
			UserManager.saveUser(u);
			
		}
		setSessionUser(u);
		u.setMailNotificationsEnabled(false);
		((Basket)LoginBox.this.getPage().getDesktop().getSession().getAttribute("basket")).copy(u.getBasket());
		u.setMailNotificationsEnabled(true);
		u.setBasket(((Basket)LoginBox.this.getPage().getDesktop().getSession().getAttribute("basket")));
		
		((Basket)LoginBox.this.getPage().getDesktop().getSession().getAttribute("basket")).deleteObserver(basketObserver);
		((Basket)LoginBox.this.getPage().getDesktop().getSession().getAttribute("basket")).addObserver(basketObserver);
		
		this.user = u;
		
		Event evt = new Event(ONLOGIN, this, this.user);		
		Events.postEvent(evt);
		
		setLoginMode(true);
		
		
		try {
			if(!activate){
				//Messagebox.show("Welcome "+u.getName()+". Your basket has been restored and has "+u.getBasket().getSets().size()+" sets.", "Welcome", Messagebox.OK, Messagebox.INFORMATION);
			}else{
				Messagebox.show("Welcome "+u.getName()+". The registration process has been finished. Enjoy!", "Welcome", Messagebox.OK, Messagebox.INFORMATION);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	private void setLoginMode(boolean logged){
		
		this.getNoLoginBox().setVisible(!logged);
		this.getLoggedBox().setVisible(logged);
		
		if (logged){
			updateLoggedBox();
		}
		
	}
	
	private User getSessionUser(){
		User u = null; 
		try{ 
			u = ((User)LoginBox.this.getPage().getDesktop().getSession().getAttribute("user"));
		
		}catch(NullPointerException e){
			// the user seems to be disconnected
			System.out.println("which genes: the user seems to be disconnected");
			u = this.user;
		}
		return u;
	}
	private void setSessionUser(User u){
		LoginBox.this.getPage().getDesktop().getSession().setAttribute("user",u);
	}
	private void updateLoggedBox() {
		this.welcomeLabel.setValue("Hello "+getSessionUser().getEmail());
		
		
	}
	public LoginBox(){
		
		Groupbox loginGroup = new Groupbox();
		loginGroup.setMold("3d");
		
		Caption captionLogin = new Caption();
		captionLogin.setLabel("Login");
		loginGroup.appendChild(captionLogin);
		loginGroup.appendChild(this.getNoLoginBox());
		this.appendChild(loginGroup);
		
		
		Caption captionReg = new Caption();
		captionReg.setLabel("Registration");
		getRegistrationBox().appendChild(captionReg);
		getRegistrationBox().setStyle("margin-top:15px; padding-top:15px");
		getRegistrationBox().appendChild(registrationForm);
		this.appendChild(getRegistrationBox());
		
		
		
		getRegistrationBox().setVisible(false);
		setLoginMode(false);
		
	}
	
	private BasketObserver basketObserver = new BasketObserver();
	class BasketObserver implements Observer{

		public void update(Observable arg0, Object arg1) {
			if (getSessionUser()!=null){
				System.out.println("saving user session");
				UserManager.saveUser(getSessionUser());
			}
			
		}
		
	}
	
}
