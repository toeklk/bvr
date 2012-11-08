package no.sintef.cvl.ui.commands;

import java.util.Map;

import javax.swing.JComponent;

import no.sintef.cvl.ui.editor.CVLUIKernel;
import no.sintef.cvl.ui.framework.OptionalElement.OPTION_STATE;
import no.sintef.cvl.ui.framework.elements.GroupPanel;
import no.sintef.cvl.ui.framework.elements.VClassifierPanel;
import no.sintef.cvl.ui.loader.Main;
import cvl.MultiplicityInterval;
import cvl.VClassifier;
import cvl.VSpec;

public class AddVClassifier implements Command {
	
	CVLUIKernel rootPanel;
	VClassifier vc;
	JComponent parent;
	
	public Command init(CVLUIKernel rootPanel, Object p, JComponent parent, Map<JComponent, VSpec> vmMap) {
		if(p instanceof VClassifier){
			this.rootPanel = rootPanel;
			this.vc = (VClassifier) p;
			this.parent = parent;
		}		
		
		return this;
	}
	
	public JComponent execute() {
		VClassifierPanel c = new VClassifierPanel(rootPanel.getModelPanel());
		Main.nodes.add(c);
		
		CommandMouseListener listener = new CommandMouseListener();
        SelectInstanceCommand command = new SelectInstanceCommand();
        command.init(rootPanel, c, parent, Main.vmMap);
        listener.setLeftClickCommand(command);
        c.addMouseListener(new ClassifierDropDownListener(c));
        c.addMouseListener(listener);
		
        MultiplicityInterval m = vc.getInstanceMultiplicity();

        //I would prefer not to mix concerns (here validation. We should assume a valid CVL model as input).
        /*if(m == null){
        	throw new CVLModelException("VClassifier instance must have InstanceMultiplicity");
        }*/
        int l = m.getLower();
        int u = m.getUpper();
        c.setNameAndCardinality(vc.getName(), l, u);
        
        rootPanel.getModelPanel().addNode(c);
        
        Helper.bind(parent, c, rootPanel.getModelPanel(), (parent instanceof GroupPanel) ? OPTION_STATE.OPTIONAL : OPTION_STATE.MANDATORY);
        
        return c;
	}

}
