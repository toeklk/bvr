package no.sintef.bvr.tool.ui.command.event;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;

import no.sintef.bvr.tool.ui.loader.BVRView;
import no.sintef.bvr.tool.ui.loader.Pair;
import no.sintef.bvr.ui.framework.elements.ChoicePanel;
import no.sintef.bvr.ui.framework.elements.VSpecPanel;
import bvr.BvrFactory;
import bvr.MultiplicityInterval;
import bvr.NamedElement;
import bvr.VSpec;

public class MinimizeEvent implements ActionListener {

	private Object cp;
	private Map<JComponent, NamedElement> vmMap;
	private BVRView view;

	public MinimizeEvent(Object cp, Map<JComponent, NamedElement> vmMap,List<JComponent> nodes, List<Pair<JComponent, JComponent>> bindings, BVRView view) {
		this.cp = cp;
		this.vmMap = vmMap;
		this.view = view;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		Object v = vmMap.get(cp);
		
		// Modify model
		view.setMinimized(v);
		
		// Regenerate view
		view.notifyVspecViewUpdate();
		view.notifyResolutionViewUpdate();
	}

}