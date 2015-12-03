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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import es.uvigo.ei.sing.whichgenes.Util;

public class UserManager {

	private static String FILE_PATH = (UserManager.class.getProtectionDomain().getCodeSource().getLocation()+"/../users").substring(5);
	
	static{
		FILE_PATH = Util.getConfigParam("whichgenes.usermanagerdirectory");
		
	}
	private static File getUserFile(String username){
		return new File(FILE_PATH+"/"+username);
	}
	public static User retrieveUser(String id, String password){
		
			User u;
			try {
				u = (User) new ObjectInputStream(new FileInputStream(getUserFile(id))).readObject();
				
				
				
				if (u.getPassword().equals(password)){
					return u;
				}else{
					throw new RuntimeException("Incorrect password");
				}
			} catch (FileNotFoundException e) {
				throw new RuntimeException("There is no username with email "+id);
			} catch (IOException e) {
				throw new RuntimeException(e);
			} catch (ClassNotFoundException e) {
				throw new RuntimeException(e);
			}
		
	}
	
	public static void saveUser(User user){
		try {
			new ObjectOutputStream(new FileOutputStream(getUserFile(user.getEmail()))).writeObject(user);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	public static boolean userExists(String id){
		return (getUserFile(id).exists());
	}
}
