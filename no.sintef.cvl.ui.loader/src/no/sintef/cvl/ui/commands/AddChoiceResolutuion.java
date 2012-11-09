package no.sintef.cvl.ui.commands;

import java.util.List;
import java.util.Map;

import javax.swing.JComponent;

import no.sintef.cvl.ui.editor.CVLUIKernel;
import no.sintef.cvl.ui.framework.OptionalElement.OPTION_STATE;
import no.sintef.cvl.ui.framework.elements.ChoiceResolutionPanel;
import no.sintef.cvl.ui.loader.CVLView;
import no.sintef.cvl.ui.loader.Pair;
import cvl.Choice;
import cvl.ChoiceResolutuion;
import cvl.VSpec;

public class AddChoiceResolutuion implements Command {
	private Map<JComponent, VSpec> vmMap;
	private List<JComponent> nodes;
	private List<Pair<JComponent, JComponent>> bindings;
	private CVLView view;
	private JComponent parent;
	private CVLUIKernel rootPanel;
	private ChoiceResolutuion c;

	public Command init(CVLUIKernel rootPanel, Object p, JComponent parent, Map<JComponent, VSpec> vmMap, List<JComponent> nodes, List<Pair<JComponent, JComponent>> bindings, CVLView view) {
		if(p instanceof ChoiceResolutuion){
			this.rootPanel = rootPanel;
			this.c = (ChoiceResolutuion) p;
			this.parent = parent;
		}	
		
		this.vmMap = vmMap;
		this.nodes = nodes;
		this.bindings = bindings;
		this.view = view;
		this.parent = parent;
		
		return this;  
	}

	public JComponent execute() {
		ChoiceResolutionPanel cp = new ChoiceResolutionPanel();
		nodes.add(cp);
		
        /*listener = new CommandMouseListener();
        cp.addMouseListener(new ChoiceDropDownListener(cp, vmMap, nodes, bindings, view));
        cp.addMouseListener(listener);*/
		
        cp.setTitle(c.getResolvedChoice().getName() + " = " + c.isDecision());
		rootPanel.getModelPanel().addNode(cp);
        Helper.bind(parent, cp, rootPanel.getModelPanel(), OPTION_STATE.MANDATORY, bindings);
        return cp;
	}

}
