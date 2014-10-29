package no.sintef.bvr.ui.editor.mvc.resolutionV2.event;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

import javax.swing.JComponent;

import no.sintef.bvr.tool.controller.BVRResolutionToolView;
import no.sintef.bvr.tool.model.ResolutionModelIterator;
import no.sintef.bvr.ui.editor.mvc.resolutionV2.commands.UnstripCommand;
import bvr.NamedElement;
import bvr.VSpec;
import bvr.VSpecResolution;

public class UnstrippedEvent implements ActionListener {
	private Object cp;
	private Map<JComponent, NamedElement> vmMap;
	private BVRResolutionToolView  view;
	private VSpec x;

	public UnstrippedEvent(Object cp, Map<JComponent, NamedElement> vmMap, BVRResolutionToolView  view, VSpec x) {
		this.cp = cp;
		this.vmMap = vmMap;
		this.view = view;
		this.x = x;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		Object v = vmMap.get(cp);
	/*	do{
			System.out.println();
			Iterators.getInstance().iterateExisting(view, new UnstripCommand(), x, (VSpecResolution) v, true);
		}while(view.getStripped().contains(v));*/
		view.refresh();
		
	}
}