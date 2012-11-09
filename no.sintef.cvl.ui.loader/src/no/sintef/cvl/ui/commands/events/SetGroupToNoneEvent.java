package no.sintef.cvl.ui.commands.events;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;

import no.sintef.cvl.ui.loader.CVLView;
import no.sintef.cvl.ui.loader.Main;
import no.sintef.cvl.ui.loader.Pair;
import cvl.VSpec;

public class SetGroupToNoneEvent implements ActionListener {
	private JComponent p;
	private Map<JComponent, VSpec> vmMap;
	private CVLView view;

	public SetGroupToNoneEvent(JComponent p, Map<JComponent, VSpec> vmMap, List<JComponent> nodes, List<Pair<JComponent, JComponent>> bindings, CVLView view) {
		this.p = p;
		this.vmMap = vmMap;
		this.view = view;
	}
	
	public void actionPerformed(ActionEvent arg0) {
		VSpec v = vmMap.get(p);
		
		// Modify model
		v.setGroupMultiplicity(null);
		
		// Regenerate view
		view.notifyViewUpdate();
	}
}
