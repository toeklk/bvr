package no.sintef.cvl.ui.strategy.impl;

import java.io.File;
import java.util.HashMap;

import javax.swing.JComponent;
import javax.swing.JFileChooser;

import no.sintef.cvl.ui.context.Context;
import no.sintef.cvl.ui.strategy.SaveProductStrategy;

public class SimpleSaveProductStrategy implements SaveProductStrategy {

	@Override
	public void saveProduct(HashMap<String, Object> keywords) {
		JComponent parent = (JComponent) keywords.get("parentComponent");
		
		final JFileChooser fc = Context.eINSTANCE.getFileChooser();
		
		int status = fc.showSaveDialog(parent);
		if(status == JFileChooser.CANCEL_OPTION)
			return;
		
		File sf = fc.getSelectedFile();
		if(sf == null) return;
		
		Context.eINSTANCE.writeProductsToFiles(Context.subEngine.getCopiedBaseModels(), sf);
	}
}
