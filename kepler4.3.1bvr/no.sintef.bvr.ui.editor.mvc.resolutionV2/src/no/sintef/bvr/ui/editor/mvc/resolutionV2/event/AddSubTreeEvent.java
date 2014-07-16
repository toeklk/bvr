package no.sintef.bvr.ui.editor.mvc.resolutionV2.event;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import no.sintef.bvr.tool.context.Context;
import no.sintef.bvr.tool.ui.loader.BVRView;
import no.sintef.bvr.ui.editor.mvc.resolutionV2.commands.AddResolution;
import no.sintef.bvr.ui.editor.mvc.resolutionV2.tools.CloneRes;
import no.sintef.bvr.ui.editor.mvc.resolutionV2.tools.Iterators;
import bvr.BvrFactory;
import bvr.Choice;
import bvr.ChoiceResolutuion;
import bvr.ConfigurableUnit;
import bvr.VInstance;
import bvr.VSpec;
import bvr.VSpecResolution;
import bvr.VariableValueAssignment;

public class AddSubTreeEvent implements ActionListener {

	private VSpecResolution parent;
	private VSpec target;
	private BVRView view;

	public AddSubTreeEvent(VSpecResolution parent, BVRView view) {
		super();
		this.parent = parent;
		this.target = parent.getResolvedVSpec();
		this.view = view;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		VSpecResolution grandParent = getParent(this.view.getCU(), parent);
		VSpecResolution root = null;
		if (grandParent != null) {
			if (parent instanceof ChoiceResolutuion) {
				root = BvrFactory.eINSTANCE.createChoiceResolutuion();

			} else if (parent instanceof VariableValueAssignment) {
				root = BvrFactory.eINSTANCE.createVariableValueAssignment();

			} else if (parent instanceof VInstance) {
				root = BvrFactory.eINSTANCE.createVInstance();
			}
			root.setResolvedVSpec(target);
			root.setName(root.getResolvedVSpec().getName());
			Iterators.getInstance().iterateEmptyOnChildren(this.view, new AddResolution(), target, root, false);
			Context.eINSTANCE.getEditorCommands().removeNamedElementVSpecResolution(grandParent, parent);
			if (parent instanceof ChoiceResolutuion) {
				Context.eINSTANCE.getEditorCommands().addChoiceResolved((Choice) root.getResolvedVSpec(), grandParent, (ChoiceResolutuion) root);

			} else if (parent instanceof VariableValueAssignment) {
				Context.eINSTANCE.getEditorCommands().addVariableValueAssignment(grandParent, (VariableValueAssignment) root);

			} else if (parent instanceof VInstance) {
				Context.eINSTANCE.getEditorCommands().addVInstance(grandParent, (VInstance) root);
			}

		}

	}

	private VSpecResolution getParent(ConfigurableUnit cu, VSpecResolution child) {
		for (VSpecResolution c : cu.getOwnedVSpecResolution())
			if (c == child) {
				VSpecResolution root = null;
				if (parent instanceof ChoiceResolutuion) {
					root = BvrFactory.eINSTANCE.createChoiceResolutuion();

				} else if (parent instanceof VariableValueAssignment) {
					System.err.println("root must be Choice");
					throw new UnsupportedOperationException();

				} else if (parent instanceof VInstance) {
					System.err.println("root must be Choice");
					throw new UnsupportedOperationException();
				}
				root.setResolvedVSpec(target);
				Iterators.getInstance().iterateEmptyOnChildren(this.view, new AddResolution(), target, root, false);

				Context.eINSTANCE.getEditorCommands().removeOwnedVSpecResolutionConfigurableUnit(view.getCU(), child);
				Context.eINSTANCE.getEditorCommands().createNewResolution((ChoiceResolutuion) root, view.getCU());
				Context.eINSTANCE.getEditorCommands().addChoiceResolved((Choice) root.getResolvedVSpec(), root, (ChoiceResolutuion) root);

				return null;
			}
		for (VSpecResolution r : cu.getOwnedVSpecResolution()) {
			VSpecResolution found = getParent(r, child);
			if (found != null)
				return found;
		}
		return null;
	}

	private VSpecResolution getParent(VSpecResolution root, VSpecResolution child) {
		for (VSpecResolution r : root.getChild())
			if (r == child)
				return root;
		for (VSpecResolution r : root.getChild()) {
			VSpecResolution found = getParent(r, child);
			if (found != null)
				return found;
		}
		return null;
	}
}
