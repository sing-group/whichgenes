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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import es.uvigo.ei.sing.whichgenes.provider.biocarta.BiocartaProvider;
import es.uvigo.ei.sing.whichgenes.provider.cancergenes.CancerGenesProvider;
import es.uvigo.ei.sing.whichgenes.provider.ctd.CTDProvider;
import es.uvigo.ei.sing.whichgenes.provider.ctddiseases.CTDDiseaseProvider;
import es.uvigo.ei.sing.whichgenes.provider.decipher.DecipherProvider;
import es.uvigo.ei.sing.whichgenes.provider.ensemblcytobands.EnsemblBandsProvider;
import es.uvigo.ei.sing.whichgenes.provider.genecards.GeneCardsProvider;
import es.uvigo.ei.sing.whichgenes.provider.go.GOProvider;
import es.uvigo.ei.sing.whichgenes.provider.intact.IntactProvider;
import es.uvigo.ei.sing.whichgenes.provider.kegg.KEGGProvider;
import es.uvigo.ei.sing.whichgenes.provider.mirbase.MirbaseProvider;
import es.uvigo.ei.sing.whichgenes.provider.msigdb.MSigDBPositionalProvider;
import es.uvigo.ei.sing.whichgenes.provider.reactome.ReactomeProvider;
import es.uvigo.ei.sing.whichgenes.provider.targetscan.TargetScanProvider;
import es.uvigo.ei.sing.whichgenes.user.Emailer;

public class ProvidersFacade {
	private static Log log = LogFactory.getLog(ProvidersFacade.class);
	
	static List<GeneSetProvider> providers = new LinkedList<GeneSetProvider>(); 
	static{
		//providers.add(new DummyProvider());
		
		//providers.add(new GeneCardsProvider()); //now requires CAPTCHA, disabled
		providers.add(new GOProvider());
		providers.add(new KEGGProvider("hsa"));
		providers.add(new KEGGProvider("mmu"));
		providers.add(new BiocartaProvider("hsa", "hgnc_symbol"));
		providers.add(new BiocartaProvider("mmu","mgi_symbol"));
		//providers.add(new ReactomeProvider("Homo sapiens")); //reactome BioMart is maintained, disabled
		//providers.add(new ReactomeProvider("Mus musculus"));
		providers.add(new MSigDBPositionalProvider());
		//providers.add(new TargetScanProvider()); //migrating to mirgate
		providers.add(new EnsemblBandsProvider("mus musculus"));
		providers.add(new EnsemblBandsProvider("homo sapiens"));
		//providers.add(new MirbaseProvider("homo sapiens")); //migrating to mirgate
		//providers.add(new MirbaseProvider("mus musculus")); //migrating to mirgate
		providers.add(new CTDProvider("hsa"));
		providers.add(new CTDProvider("mmu"));
		providers.add(new CTDDiseaseProvider("hsa"));
		providers.add(new CTDDiseaseProvider("mmu"));
		//providers.add(new CancerGenesProvider()); //site down permanently, disabled
		providers.add(new IntactProvider());
		providers.add(new DecipherProvider());
	}
	public static List<GeneSetProvider> getProviders(){
		return providers;
		
	}
	
	
	// out of order service management
	
	
	
	public static void testProviders(){
		String email="";
		String line="";
		
		line = "Testing providers...";
		log.info(line); email+="\n"+line;
		
		int ok = 0;
		int fail = 0;
		
		for (GeneSetProvider provider : providers){
			
			line = "Testing "+provider.getName()+" ["+provider.getOrganism()+"] ...";
			log.info(line); email+="\n"+line;
			try{
				
				int results = provider.testProvider();
				line = provider.getName()+" ["+provider.getOrganism()+"] seems OK ["+results+"]";
				
				log.info(line);	email+="\n"+line;
				
				ok++;
				setInService(provider);
			}catch(Exception e){
				fail++;
				setOutOfService(provider);
				StringWriter w = new StringWriter();
				PrintWriter pw = new PrintWriter(w);
				
				e.printStackTrace(pw);
				
				line = provider.getName()+ " throws an error... dumping stack \n"+w.toString();
				log.error(line);
				email +="\n"+line;
				
			}
		}
		line = "Providers tested OK: "+ok+" FAIL: "+fail;
		log.info(line); email+="\n"+line;
		Emailer.sendEmail("no-reply@whichgenes.org", "lipido@sing.ei.uvigo.es", "Whichgenes: "+line, email);
		
	}
	private static HashSet<GeneSetProvider> outOfOrder = new HashSet<GeneSetProvider>(); 
	public static boolean isOutOfService(GeneSetProvider provider){		
		return outOfOrder.contains(provider);
		
	}
	public static void setOutOfService(GeneSetProvider provider){
		outOfOrder.add(provider);
	}
	
	public static void setInService(GeneSetProvider provider){
		outOfOrder.remove(provider);
	}
}
