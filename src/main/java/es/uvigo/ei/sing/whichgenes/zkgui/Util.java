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
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Window;

public class Util {
	public static void removeChilds(Component c){
		
		if(c!=null){
			List children = c.getChildren();
			Iterator it = children.iterator();
			while (it.hasNext()){
				Object o = it.next();
				if (!(o instanceof Paging)){ //skipping the pagging component					
					it.remove();
				}
			}
		}
		
	}
	
	public static Class loadClass(String name) throws ClassNotFoundException{
		return Util.class.forName(name);
	}
	
	public static String createQuestionDialog(Component parent, String title, String text, List<String> values){
		final Window win = new Window();
		win.setSclass("modal");
		win.setTitle(title);
		win.setWidth("400px");
		win.setHeight("100px");
		win.setSizable(true);
		final Combobox combo = new Combobox();
		for (String value : values){
			combo.appendItem(value);
		}
		
		combo.setWidth("250px");
		combo.setSelectedIndex(0);
		combo.addEventListener("onOK", new EventListener(){
			
			public void onEvent(Event arg0) throws Exception {
				win.detach();
				
			}
			
			
			
		});
		
		
		win.appendChild(new Vbox(new Component[]{
				new Hbox(new Component[]{new Label(text), combo}){{this.setWidth("100%");}}, 
				new Hbox(new Component[]{new Button("Accept"){{this.setStyle("margin-top:10px");this.addEventListener("onClick", new EventListener(){

					public void onEvent(Event arg0) throws Exception {
						win.detach();
				
					}
			
				});}},
				new Button("Cancel"){{this.setStyle("margin-top:10px");this.addEventListener("onClick", new EventListener(){

					public void onEvent(Event arg0) throws Exception {
						combo.setAttribute("cancelled", "true");
						win.detach();
				
					}
			
				});}}})
		}){{this.setHeight("100%");this.setStyle("text-align:center");this.setWidth("100%");}});
		
		win.setParent(parent);
		win.setBorder("normal");
		try {
			win.doModal();
		} catch (SuspendNotAllowedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (combo.getAttribute("cancelled")!=null){
			return null;
		}
		return combo.getValue();
		
	}
	public static String createQuestionDialog(Component parent, String title, String text, String defaultname){
		final Window win = new Window();
		win.setSclass("modal");
		win.setTitle(title);
		win.setWidth("400px");
		win.setHeight("100px");
		win.setSizable(true);
		
		final Textbox field = new Textbox();
		field.setWidth("250px");
		field.setValue(defaultname);
		field.addEventListener("onOK", new EventListener(){
			
			public void onEvent(Event arg0) throws Exception {
				win.detach();
				
			}
			
			
			
		});
		win.appendChild(new Vbox(new Component[]{
				new Hbox(new Component[]{new Label(text), field}){{this.setWidth("100%");}}, 
				new Hbox(new Component[]{new Button("Accept"){{this.setStyle("margin-top:10px");this.addEventListener("onClick", new EventListener(){

					public void onEvent(Event arg0) throws Exception {
						win.detach();
				
					}
			
				});}},
				new Button("Cancel"){{this.setStyle("margin-top:10px");this.addEventListener("onClick", new EventListener(){

					public void onEvent(Event arg0) throws Exception {
						field.setAttribute("cancelled", "true");
						win.detach();
				
					}
			
				});}}})
		}){{this.setHeight("100%");this.setStyle("text-align:center");this.setWidth("100%");}});
		
		win.setParent(parent);
		win.setBorder("normal");
		try {
			win.doModal();
		} catch (SuspendNotAllowedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (field.getAttribute("cancelled")!=null){
			return null;
		}
		return field.getValue();
	}

	public static class DesEncrypter {
	    static Cipher ecipher;
	    static Cipher dcipher;

	    static {
	        try {
	        	SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
	        	random.setSeed(9999);
	        	KeyGenerator kg = KeyGenerator.getInstance("DES");
	        	kg.init(random);
	        	SecretKey key = kg.generateKey();
	        	

	            ecipher = Cipher.getInstance("DES");
	            dcipher = Cipher.getInstance("DES");
	            ecipher.init(Cipher.ENCRYPT_MODE, key);
	            dcipher.init(Cipher.DECRYPT_MODE, key);

	        } catch (javax.crypto.NoSuchPaddingException e) {
	        } catch (java.security.NoSuchAlgorithmException e) {
	        } catch (java.security.InvalidKeyException e) {
	        }
	    }

	    public static String encrypt(String str) {
	        try {
	            // Encode the string into bytes using utf-8
	            byte[] utf8 = str.getBytes("UTF8");

	            // Encrypt
	            byte[] enc = ecipher.doFinal(utf8);

	            // Encode bytes to base64 to get a string
	            return new sun.misc.BASE64Encoder().encode(enc);	            
	        } catch (javax.crypto.BadPaddingException e) {
	        } catch (IllegalBlockSizeException e) {
	        } catch (UnsupportedEncodingException e) {
	        }
	        return null;
	    }

	    public static String decrypt(String str) {
	        try {
	            // Decode base64 to get bytes
	            byte[] dec = new sun.misc.BASE64Decoder().decodeBuffer(str);

	            // Decrypt
	            byte[] utf8 = dcipher.doFinal(dec);

	            // Decode using utf-8
	            return new String(utf8, "UTF8");
	        } catch (javax.crypto.BadPaddingException e) {
	        } catch (IllegalBlockSizeException e) {
	        } catch (UnsupportedEncodingException e) {
	        } catch (java.io.IOException e) {
	        }
	        return null;
	    }
	}
  
}

