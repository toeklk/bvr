package no.sintef.bvr.tool.ui.command.event;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;

import no.sintef.bvr.tool.ui.loader.BVRView;
import no.sintef.bvr.tool.ui.loader.Pair;
import bvr.BCLConstraint;
import bvr.ConfigurableUnit;
import bvr.BvrFactory;
import bvr.NamedElement;
import bvr.PrimitiveTypeEnum;
import bvr.PrimitveType;
import bvr.VSpec;
import bvr.VSpecRef;
import bvr.Variable;
import bvr.Variabletype;

public class AddVariableEvent implements ActionListener {
	
	private JComponent p;
	private Map<JComponent, NamedElement> vmMap;
	private BVRView view;

	public AddVariableEvent(JComponent p, Map<JComponent, NamedElement> vmMap, List<JComponent> nodes, List<Pair<JComponent, JComponent>> bindings, BVRView view) {
		this.p = p;
		this.vmMap = vmMap;
		this.view = view;
	}
	
	static int x = 1;

	@Override
	public void actionPerformed(ActionEvent e) {
		VSpec v = (VSpec)vmMap.get(p);
		
		//System.out.println("Adding variable to " + v.getName());
		
		Variable var = BvrFactory.eINSTANCE.createVariable();
		PrimitveType vt = BvrFactory.eINSTANCE.createPrimitveType();
		
		vt.setType(PrimitiveTypeEnum.INTEGER);
		vt.setName("xx");
		
		view.getCU().getOwnedVariabletype().add(vt);
		
		var.setName("Var" + x);
		var.setType(vt);
		x++;
		
		v.getChild().add(var);
		
		// Regenerate view
		view.notifyVspecViewUpdate();
	}

}