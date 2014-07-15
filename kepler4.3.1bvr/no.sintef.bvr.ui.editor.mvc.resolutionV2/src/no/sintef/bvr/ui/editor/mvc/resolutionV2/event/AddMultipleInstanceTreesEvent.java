package no.sintef.bvr.ui.editor.mvc.resolutionV2.event;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import no.sintef.bvr.tool.context.Context;
import no.sintef.bvr.tool.ui.loader.BVRView;
import no.sintef.bvr.ui.editor.mvc.resolutionV2.commands.AddResolution;
import no.sintef.bvr.ui.editor.mvc.resolutionV2.tools.Iterators;
import bvr.BCLExpression;
import bvr.BooleanLiteralExp;
import bvr.BvrFactory;
import bvr.Choice;
import bvr.ChoiceResolutuion;
import bvr.ConfigurableUnit;
import bvr.IntegerLiteralExp;
import bvr.PrimitiveTypeEnum;
import bvr.PrimitiveValueSpecification;
import bvr.PrimitveType;
import bvr.RealLiteralExp;
import bvr.StringLiteralExp;
import bvr.UnlimitedLiteralExp;
import bvr.VInstance;
import bvr.VSpec;
import bvr.VSpecResolution;
import bvr.ValueSpecification;
import bvr.Variable;
import bvr.VariableValueAssignment;
import bvr.common.PrimitiveTypeGenerator;
import bvr.common.PrimitiveValueGenerator;

public class AddMultipleInstanceTreesEvent implements ActionListener {
	int currentInstances;
	int instancesRequested;
	VSpecResolution parent;
	VSpec target;
	BVRView view;

	// parent er choice o.l. over
	public AddMultipleInstanceTreesEvent(int instancesRequested, VSpecResolution parent, VSpec target, BVRView view) {
		super();
		this.instancesRequested = instancesRequested;
		this.currentInstances = 0;
		this.parent = parent;
		this.target = target;
		this.view = view;
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (currentInstances < 0) {
			System.err.println("error current VInsgtance count <0");
			return;
		}
		for (VSpecResolution x : parent.getChild()) {
			if (x.getResolvedVSpec() == target) {
				if (x.getResolvedVSpec() == target) {
					currentInstances++;
				}
			}
		}
		if (currentInstances == instancesRequested) {
			return;
		}

		// System.out.println(parent);
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
			// populate top choice

			cloneItStart(root, parent);
			if (currentInstances > instancesRequested) {
				
				for (int i = root.getChild().size()-1; i >= 0; i--) {
					System.out.println("does : " + root.getChild().get(i) + " equal " + target);
					if ((currentInstances > instancesRequested) && (root.getChild().get(i).getResolvedVSpec() == target)) {
						System.out.println("removeing: " +root.getChild().get(i));
						root.getChild().remove(i);
						currentInstances--;
					}
				}
			}

			while (currentInstances < instancesRequested) {
				System.out.println("currentInstances < instancesRequested");
				VInstance newInstance = BvrFactory.eINSTANCE.createVInstance();
				newInstance.setResolvedVSpec(target);
				newInstance.setName(target.getName());
				new Iterators().iterateEmptyOnChildren(view, new AddResolution(), target, newInstance, false);
				root.getChild().add(newInstance);
				currentInstances++;
			}

			Context.eINSTANCE.getEditorCommands().removeNamedElementVSpecResolution(grandParent, parent);
			if (parent instanceof ChoiceResolutuion) {
				Context.eINSTANCE.getEditorCommands().addChoiceResolved((Choice) root.getResolvedVSpec(), grandParent, (ChoiceResolutuion) root);

			} else if (parent instanceof VariableValueAssignment) {
				Context.eINSTANCE.getEditorCommands().addVariableValueAssignment(grandParent, (VariableValueAssignment) root);
				;

			} else if (parent instanceof VInstance) {
				Context.eINSTANCE.getEditorCommands().addVInstance(grandParent, (VInstance) root);
			}
			/*
			 * } while(currentInstances < instancesRequested){ new AddVInstanceTreeEvent( parent, target, view).actionPerformed(e);
			 * currentInstances++; }
			 */
		}
	}

	private VSpecResolution getParent(ConfigurableUnit cu, VSpecResolution child) {
		for (VSpecResolution c : cu.getOwnedVSpecResolution())
			if (c == child) {
				VSpecResolution root = null;
				if (parent instanceof ChoiceResolutuion) {
					root = BvrFactory.eINSTANCE.createChoiceResolutuion();

				} else if (parent instanceof VariableValueAssignment) {
					root = BvrFactory.eINSTANCE.createVariableValueAssignment();

				} else if (parent instanceof VInstance) {
					root = BvrFactory.eINSTANCE.createVInstance();
				}
				cloneItStart(root, parent);
				if (currentInstances > instancesRequested) {
					
					for (int i = root.getChild().size()-1; i >= 0; i--) {
						System.out.println("does : " + root.getChild().get(i) + " equal " + target);
						if ((currentInstances > instancesRequested) && (root.getChild().get(i).getResolvedVSpec() == target)) {
							System.out.println("removeing: " +root.getChild().get(i));
							root.getChild().remove(i);
							currentInstances--;
						}
					}
				}
				while (currentInstances < instancesRequested) {
					System.out.println("currentInstances < instancesRequested");
					VInstance newInstance = BvrFactory.eINSTANCE.createVInstance();
					newInstance.setResolvedVSpec(target);
					System.out.println("target: " + target);
					newInstance.setName(target.getName());
					new Iterators().iterateEmptyOnChildren(view, new AddResolution(), target, newInstance, false);
					root.getChild().add(newInstance);
					currentInstances++;
				}
				System.out.println("root children: " + root.getChild());
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

	private void cloneRes(VSpecResolution copyTo, VSpecResolution copyFrom) {

		if (copyFrom instanceof ChoiceResolutuion) {

			((ChoiceResolutuion) copyTo).setDecision(((ChoiceResolutuion) copyFrom).isDecision());

		} else if (copyFrom instanceof VariableValueAssignment) {
			Variable vSpecFound = (Variable) copyFrom.getResolvedVSpec();
			String vString = getValueAsString((VariableValueAssignment) copyFrom);
			PrimitiveValueSpecification value = (new PrimitiveValueGenerator().make(vSpecFound, vString));
			PrimitiveTypeEnum type = ((PrimitveType) ((Variable) vSpecFound).getType()).getType();
			// Try searching for a type
			PrimitveType vt = (new PrimitiveTypeGenerator().make(view.getCU(), type));
			value.setType(vt);

			((VariableValueAssignment) copyTo).setValue(value);
			// ((VariableValueAssignment) copyFrom).getValue()
		} else if (copyFrom instanceof VInstance) {
			// copyTo = BvrFactory.eINSTANCE.createVInstance();

		}
		copyTo.setResolvedVSpec(copyFrom.getResolvedVSpec());
		copyTo.setName(copyFrom.getName());
	}

	private void cloneItStart(VSpecResolution parentTo, VSpecResolution parentFrom) {
		cloneRes(parentTo, parentFrom);
		cloneIterate(parentTo, parentFrom);
	}

	private void cloneIterate(VSpecResolution parentTo, VSpecResolution parentFrom) {
		if (parentFrom != null) {
			VSpecResolution newNode = null;
			for (VSpecResolution x : parentFrom.getChild()) {
				if (x instanceof ChoiceResolutuion) {
					newNode = BvrFactory.eINSTANCE.createChoiceResolutuion();

				} else if (x instanceof VariableValueAssignment) {
					newNode = BvrFactory.eINSTANCE.createVariableValueAssignment();

				} else if (x instanceof VInstance) {
					newNode = BvrFactory.eINSTANCE.createVInstance();
				}

				cloneRes(newNode, x);
				parentTo.getChild().add(newNode);
				cloneIterate(newNode, x);
			}
		}
	}

	private String getValueAsString(VariableValueAssignment elem) {
		String value = "";
		ValueSpecification vs = elem.getValue();
		if (vs instanceof PrimitiveValueSpecification) {
			PrimitiveValueSpecification pvs = (PrimitiveValueSpecification) vs;
			BCLExpression e = pvs.getExpression();
			if (e instanceof StringLiteralExp) {
				StringLiteralExp sle = (StringLiteralExp) e;
				value = sle.getString();
			} else if (e instanceof IntegerLiteralExp) {
				IntegerLiteralExp sle = (IntegerLiteralExp) e;
				value = "" + sle.getInteger();
			} else if (e instanceof RealLiteralExp) {
				RealLiteralExp sle = (RealLiteralExp) e;
				value = sle.getReal();
			} else if (e instanceof BooleanLiteralExp) {
				BooleanLiteralExp sle = (BooleanLiteralExp) e;
				value = "" + sle.isBool();
			} else if (e instanceof UnlimitedLiteralExp) {
				UnlimitedLiteralExp sle = (UnlimitedLiteralExp) e;
				value = "" + sle.getUnlimited();
			} else {
				throw new UnsupportedOperationException();
			}
		} else {
			throw new UnsupportedOperationException();
		}
		return value;
	}
}
