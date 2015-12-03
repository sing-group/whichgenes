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

package es.uvigo.ei.sing.whichgenes.provider.aautomator;

import es.uvigo.ei.sing.jarvest.core.OutputHandler;
import es.uvigo.ei.sing.jarvest.core.Transformer;
import es.uvigo.ei.sing.jarvest.core.XMLInputOutput;
import es.uvigo.ei.sing.whichgenes.provider.idconverter.IDConverter;
import es.uvigo.ei.sing.whichgenes.query.QueryHandler;

public class AautomatorQueryRunner {

	private Transformer robot=null;
	
	
	public void stop(){
		if (this.robot!=null){
			robot.stop();
		}
		
	}
	
	public void runQuery(String resourcePath, final QueryHandler handler, String[] input) {
		try{
			this.robot = XMLInputOutput.loadTransformer(AautomatorQueryRunner.class.getResourceAsStream(resourcePath));
			
			
			class MyOutputHandler implements OutputHandler{
	
				public void allFinished() {
					handler.finished();
					
				}
	
				public void outputFinished() {
					handler.newResult(this.currentResult);
					this.currentResult = "";
						
					
				}
	
				private String currentResult = "";
				public void pushOutput(String arg0) {
					currentResult+=arg0;
					
				}
							
			};
			MyOutputHandler automatorHandler = new MyOutputHandler();
			this.robot.setOutputHandler(automatorHandler);
			
			for (String string : input){			
				this.robot.pushString(string);
				this.robot.closeOneInput();
			}
			
			
			this.robot.closeAllInputs();
			
		}catch(Exception e){
			handler.error(e);
			handler.finished();
		}

	}
	
	public void runQuery(String resourcePath, final QueryHandler handler, String[] input, final String outputspecie, final String convertInput, final String convertOutput, final int buffersize) {
		try{
			this.robot = XMLInputOutput.loadTransformer(AautomatorQueryRunner.class.getResourceAsStream(resourcePath));
			class MyOutputHandler implements OutputHandler{
				int BUFFER_SIZE = buffersize;
				String[] geneBuffer = new String[BUFFER_SIZE];
				int currentPos = 0;
				
				private void dropBuffer(){
					if (currentPos > 0){
						String[] genes = new String[currentPos];
						System.arraycopy(geneBuffer, 0, genes, 0, currentPos);
						String[] names=null;					
						names = IDConverter.convert(outputspecie,convertInput, convertOutput, genes);
						
						for (String name: names){
							handler.newResult(name);
						}
						currentPos=0;
					}
				}
	
				private String currentResult = "";
				public void allFinished() {
					dropBuffer();
					handler.finished();
	
				}
	
				public void outputFinished() {				
					geneBuffer[currentPos++] = this.currentResult;
					this.currentResult="";
					if (currentPos == BUFFER_SIZE){
						dropBuffer();
					}
						
					
				}
	
				public void pushOutput(String arg0) {
					currentResult+=arg0;
					
				}						
			};
			
			Util.runRobot(this.robot, new MyOutputHandler(), input);
		
		
		}catch(Exception e){
			handler.error(e);
			handler.finished();
		}
		
	}
	public static void main(String[] args){
		new AautomatorQueryRunner().runQuery("/es/uvigo/ei/sing/whichgenes/provider/ensemblcytobands/exon.xml", new QueryHandler(){

			public void error(Throwable t) {
				System.err.println(t);
				
			}

			public void finished() {
				// TODO Auto-generated method stub
				
			}

			public void newResult(String result) {
				System.out.println(result);
				
			}
			
		},new String[]{" "});
	}
	
	
	
}
