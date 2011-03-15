/*
 * Copyright (C) 2008-2011 University of Deusto
 * 
 * All rights reserved.
 *
 * This software is licensed as described in the file COPYING, which
 * you should have received as part of this distribution.
 * 
 * This software consists of contributions made by many individuals, 
 * listed below:
 *
 * Author: FILLME
 *
 */
package otsopack.commons.data.impl;

import java.util.Vector;

import otsopack.commons.data.Graph;
import otsopack.commons.data.ISemanticFormatConversor;

public class SemanticFormatsManager implements ISemanticFormatConversor {

	private static final Vector/*<ISemanticFormatConversor>*/ conversors = new Vector/*<ISemanticFormatConversor>*/();
	
	public static void initialize(ISemanticFormatConversor [] conversors){
		SemanticFormatsManager.conversors.removeAllElements();
		addSemanticFormatConversors(conversors);
	}
	
	public static void addSemanticFormatConversors(ISemanticFormatConversor [] conversors){
		for(int i = 0; i < conversors.length; ++i)
			SemanticFormatsManager.conversors.addElement(conversors[i]);
	}
	
	public static void clear(){
		conversors.removeAllElements();
	}
	
	private ISemanticFormatConversor getConversor(String inputFormat, String outputFormat){
		for(int i = 0; i < conversors.size(); ++i)
			if(((ISemanticFormatConversor)conversors.elementAt(i)).canConvert(inputFormat, outputFormat))
				return (ISemanticFormatConversor)conversors.elementAt(i);
		return null;
	}
	
	public boolean canConvert(String inputFormat, String outputFormat){
		if(inputFormat.equals(outputFormat))
			return true;
		final ISemanticFormatConversor conversor = getConversor(inputFormat, outputFormat);
		if(conversor == null)
			return false;
		return true;
	}

	public String convert(String inputFormat, String originalText, String outputFormat) {
		if(inputFormat.equals(outputFormat))
			return originalText;
		return getConversor(inputFormat, outputFormat).convert(inputFormat, originalText, outputFormat);
	}
	
	public Graph convert(Graph input, String outputFormat){
		final String outputText = convert(input.getFormat(), input.getData(), outputFormat);
		return new Graph(outputText, outputFormat);
	}
	
	public boolean isInputSupported(String inputFormat){
		final SemanticFactory sf = new SemanticFactory();
		final String [] supportedInputFormats = sf.getSupportedInputFormats();
		
		for(int i = 0; i < supportedInputFormats.length; ++i)
			if(canConvert(inputFormat, supportedInputFormats[i]))
				return true;
		
		return false;
	}

	public boolean isOutputSupported(String outputFormat) {
		final SemanticFactory sf = new SemanticFactory();
		final String [] supportedOutputFormats = sf.getSupportedOutputFormats();
		
		for(int i = 0; i < supportedOutputFormats.length; ++i)
			if(canConvert(supportedOutputFormats[i], outputFormat))
				return true;
		
		return false;
	}
	
	public String retrieveProperOutput(String [] acceptedOutputFormats){
		for(int i = 0; i < acceptedOutputFormats.length; ++i)
			if(isOutputSupported(acceptedOutputFormats[i]))
				return acceptedOutputFormats[i];
		return null;
	}
}
