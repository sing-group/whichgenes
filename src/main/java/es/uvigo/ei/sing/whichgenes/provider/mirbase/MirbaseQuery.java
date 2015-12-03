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

package es.uvigo.ei.sing.whichgenes.provider.mirbase;

import es.uvigo.ei.sing.jarvest.core.Decorator;
import es.uvigo.ei.sing.jarvest.core.PatternMatcher;
import es.uvigo.ei.sing.jarvest.core.SimpleTransformer;
import es.uvigo.ei.sing.jarvest.core.Transformer;
import es.uvigo.ei.sing.jarvest.core.URLRetriever;
import es.uvigo.ei.sing.whichgenes.provider.GeneSetProvider;
import es.uvigo.ei.sing.whichgenes.provider.aautomator.Util;
import es.uvigo.ei.sing.whichgenes.provider.idconverter.IDConverter;
import es.uvigo.ei.sing.whichgenes.query.FreeQuery;
import es.uvigo.ei.sing.whichgenes.query.QueryHandler;

public class MirbaseQuery extends FreeQuery {

	private String specie;
	public MirbaseQuery(GeneSetProvider provider, String specie){
		super(provider);
		this.specie = specie;
	}
	public String getParameterName() {
		return "miRNAs (one per line) ";
	}

	Transformer robot = null;
	
	public void runQuery(final QueryHandler handler) {
		try{
			// create a robot
			
			String lines[] = this.getQuery().split("\n");
			for (String mirna: lines){
				Transformer urlRet = new URLRetriever();
			
				PatternMatcher matcher = new PatternMatcher();
				matcher.setPattern(getEnsemblPrefix()+"([0-9]+?)\\t");
			
				Decorator decorator = new Decorator();
				decorator.setHead(getEnsemblPrefix());
				
				robot = new SimpleTransformer();
			
				robot.add(urlRet);
				robot.add(matcher);
				robot.add(decorator);
				
				String[] genes = Util.runRobot(robot, new String[]{"http://microrna.sanger.ac.uk/cgi-bin/targets/v5/download_formatter.pl?format=txt&genome_id="+getGenomeID()+"&mirna_id="+mirna});
				
				if (genes.length>0){
					if (this.specie.equals("homo sapiens")){
						genes = IDConverter.convert("hsapiens_gene_ensembl", "ensembl_transcript_id", "hgnc_symbol", genes);
					}else if (this.specie.equals("mus musculus")){
						genes = IDConverter.convert("mmusculus_gene_ensembl", "ensembl_transcript_id", "mgi_symbol", genes);
					}
					for (String gene: genes){
						handler.newResult(gene);
					}
				}
			}
			handler.finished();
		}catch(Exception e){
			handler.error(e);
			handler.finished();
		}

	}
	private String getEnsemblPrefix() {
		if (this.specie.equals("homo sapiens")){
			return "ENST";
		}else if (this.specie.equals("mus musculus")){
			return "ENSMUST";
		}
		return "ENST";//human by default?
	}
	private String getGenomeID() {
		if (this.specie.equals("homo sapiens")){
			return "2964";
		}else if (this.specie.equals("mus musculus")){
			return "3876";
		}
		return "2964";//human by default?
	}
	@Override
	public void requestStop() {
		this.robot.stop();
	}
	public String getParameterDescription() {
		if (this.specie.equals("homo sapiens")){
			return "ex: hsa-miR-181a";
		}else if (this.specie.equals("mus musculus")){
			return "ex: mmu-miR-182";
		}
		return "";//human by default?
		
	}
	@Override
	public boolean isMultiline() {
		return true;
	}
	@Override
	public String sampleQuery() {
		if (this.specie.equals("homo sapiens")){
			return "hsa-miR-181a";
		}else if (this.specie.equals("mus musculus")){
			return "mmu-miR-182";
		}
		return "";//human by default?
	}

}
