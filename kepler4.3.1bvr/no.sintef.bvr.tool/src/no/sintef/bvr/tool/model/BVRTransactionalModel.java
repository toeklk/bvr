package no.sintef.bvr.tool.model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import no.sintef.bvr.common.CommonUtility;
import no.sintef.bvr.thirdparty.editor.IBVREnabledEditor;
import no.sintef.bvr.tool.checker.ModelChecker;
import no.sintef.bvr.tool.common.Constants;
import no.sintef.bvr.tool.common.LoaderUtility;
import no.sintef.bvr.tool.context.Context;
import no.sintef.bvr.tool.controller.command.AddResolution;
import no.sintef.bvr.tool.strategy.impl.BindingCalculatorContext;
import no.sintef.bvr.tool.exception.IllegalOperationException;
import no.sintef.bvr.tool.exception.RethrownException;
import no.sintef.bvr.tool.exception.UnexpectedException;
import no.sintef.bvr.tool.exception.UserInputError;
import no.sintef.bvr.tool.observer.ResourceObserver;
import no.sintef.bvr.tool.observer.ResourceSetEditedSubject;
import no.sintef.bvr.tool.observer.ResourceSubject;
import no.sintef.ict.splcatool.BVRException;
import no.sintef.ict.splcatool.CALib;
import no.sintef.ict.splcatool.CNF;
import no.sintef.ict.splcatool.CSVException;
import no.sintef.ict.splcatool.CoveringArray;
import no.sintef.ict.splcatool.CoveringArrayComplete;
import no.sintef.ict.splcatool.GUIDSL;
import no.sintef.ict.splcatool.GraphMLFM;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.resource.impl.ExtensibleURIConverterImpl;
import org.eclipse.emf.ecore.util.EObjectEList;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.transaction.TransactionalEditingDomain;

import splar.core.fm.FeatureModelException;
import de.ovgu.featureide.fm.core.io.UnsupportedModelException;
import bvr.BCLConstraint;
import bvr.BVRModel;
import bvr.BoundaryElementBinding;
import bvr.BvrFactory;
import bvr.Choice;
import bvr.CompoundNode;
import bvr.CompoundResolution;
import bvr.Constraint;
import bvr.FragmentSubstitution;
import bvr.FromBinding;
import bvr.FromPlacement;
import bvr.FromReplacement;
import bvr.MultiplicityInterval;
import bvr.NamedElement;
import bvr.NegResolution;
import bvr.Note;
import bvr.ObjectHandle;
import bvr.PlacementBoundaryElement;
import bvr.PlacementFragment;
import bvr.PosResolution;
import bvr.PrimitiveTypeEnum;
import bvr.PrimitveType;
import bvr.ReplacementBoundaryElement;
import bvr.ReplacementFragmentType;
import bvr.Target;
import bvr.ToBinding;
import bvr.ToPlacement;
import bvr.ToReplacement;
import bvr.VClassifier;
import bvr.VNode;
import bvr.VSpec;
import bvr.VSpecResolution;
import bvr.Variable;
import bvr.Variabletype;
import bvr.VariationPoint;

public class BVRTransactionalModel extends BVRToolModel implements ResourceObserver {
	private Resource resource;
	
	static private int choicCounter = 0;
	static private int variableCount = 0;
	static private int classifierCount = 0;
	private NamedElement cutNamedElement = null;
	static private int instanceCount = 0;
	List<String> satValidationMessage;
	
	
	public BVRTransactionalModel(File sf, no.sintef.ict.splcatool.SPLCABVRModel x) {
		bvrm = x;
		f = sf;
		loadFilename = sf.getAbsolutePath();
		init();
	}

	public BVRTransactionalModel(File sf) {
		f = sf;
		bvrm = new BVRInnerModel(f);
		loadFilename = sf.getAbsolutePath();
		init();
	}
	
	private void init(){
		minimizedVSpec = new ArrayList<VSpec>();
		minimizedVSpecResolution = new ArrayList<VSpecResolution>();
		checkModel();
	}

	@Override
	public no.sintef.ict.splcatool.SPLCABVRModel getBVRM() {
		return bvrm;
	}

	@Override
	public BVRModel getBVRModel() {
		return bvrm.getRootBVRModel();
	}

	@Override
	public File getFile() {
		return f;
	}
	
	public Resource getResource(){
		return resource;
	}

	private class BVRInnerModel extends no.sintef.ict.splcatool.SPLCABVRModel {

		public BVRInnerModel(File f) {
			model = loadFromFile(f);
		}

		@Override
		public BVRModel getRootBVRModel() {
			return model;
		}

		private BVRModel loadFromFile(File file) {
			TransactionalEditingDomain editingDomain = Context.eINSTANCE.getEditorCommands().testTransactionalEditingDomain();

			URIConverter converter = new ExtensibleURIConverterImpl();
			URI emptyFileURI = URI.createFileURI(ResourcesPlugin.getWorkspace()
					.getRoot().getLocation().toOSString() + File.separator);
			URI emptyPlatformURI = URI.createPlatformResourceURI("/", false);
			converter.getURIMap().put(emptyFileURI, emptyPlatformURI);
			URI platformURI = converter.normalize(URI.createFileURI(file
					.getAbsolutePath()));

			resource = editingDomain.getResourceSet().getResource(
					platformURI, true);
			resource.setTrackingModification(true);
			
			return (BVRModel) resource.getContents().get(0);
		}

		public void writeToFile(String filename) throws IOException {

			TransactionalEditingDomain editingDomain = Context.eINSTANCE.getEditorCommands().testTransactionalEditingDomain();
			
			URIConverter converter = new ExtensibleURIConverterImpl();
			URI emptyFileURI = URI.createFileURI(ResourcesPlugin.getWorkspace()
					.getRoot().getLocation().toOSString() + File.separator);
			URI emptyPlatformURI = URI.createPlatformResourceURI("/", false);
			converter.getURIMap().put(emptyFileURI, emptyPlatformURI);
			URI platformURI = converter.normalize(URI.createFileURI(filename));

			resource = editingDomain.getResourceSet().getResource(
					platformURI, true);
			resource.setTrackingModification(true);

			Map<Object, Object> options = new HashMap<Object, Object>();
			options.put(XMIResource.OPTION_ENCODING, "UTF-8");
			resource.save(options);
		}

	}
	
	@Override
	public void update(ResourceSubject subject) {
		if(subject instanceof ResourceSetEditedSubject){
			checkModel();
		}
	}
	
	private void checkModel() {
		Job job = new Job("Checking model"){

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				Status status = new Status(Status.OK, Constants.PLUGIN_ID, "OK!");
				try {
					Context.eINSTANCE.problemLogger.resetLogger();
					ModelChecker.eINSTANCE.execute(getBVRModel());
				} catch (Exception error) {
					Context.eINSTANCE.logger.error("Model check failed", error);
					status = new Status(Status.ERROR, Constants.PLUGIN_ID, "Model check failed (see log for more details): " + error.getMessage(), error);
				}
				return status;
			}
		};
		job.schedule();
	}
	
	private EList<EObject> calculateInnerFragmentElements(EList<EObject> outsideInside, EList<EObject> outsideOutside, EList<EObject> inside, EList<EObject> visited){
		for(EObject eObject : inside){
			EList<EReference> links = new BasicEList<EReference>(eObject.eClass().getEAllReferences());
			EList<EObject> refobjects = getReferencedEObjects(eObject, links);
			refobjects.addAll(eObject.eContents());
			if(!outsideInside.contains(eObject) && !outsideOutside.contains(eObject) && !visited.contains(eObject)){
				visited.add(eObject);
				visited = calculateInnerFragmentElements(outsideInside, outsideOutside, refobjects, visited);
			}
		}
		return visited;
	}
	
	private EList<EObject> getReferencedEObjects(EObject source, EList<EReference> links){
		EList<EObject> eObjects = new BasicEList<EObject>();
		for(EReference link : links){
			if(CommonUtility.isDerived(link) == 0){
				Object value = source.eGet(link);
				if(value instanceof EObject){
					eObjects.add((EObject) value);
				}else if (value instanceof EObjectEList){
					eObjects.addAll((EList<? extends EObject>) value);
				}else if(value != null){
					Context.eINSTANCE.logger.debug("reference " + link + " does not point to EObject nor EObjectList :" + value);
				}
			}
		}
		return eObjects;
	}
	
	private EList<HashMap<EObject, Integer>> markObjects(EList<EObject> outsideInsideElements, EList<EObject> outsideOutsideElements, EList<EObject> insideElements, boolean isPlacement){
		EList<HashMap<EObject, Integer>> objectsToHL = new BasicEList<HashMap<EObject,Integer>>();
		int fragment = (isPlacement) ? IBVREnabledEditor.HL_PLACEMENT : IBVREnabledEditor.HL_REPLACEMENT;
		int fragment_in = (isPlacement) ? IBVREnabledEditor.HL_PLACEMENT_IN : IBVREnabledEditor.HL_REPLACEMENT_IN;
		int fragment_out = (isPlacement) ? IBVREnabledEditor.HL_PLACEMENT_OUT : IBVREnabledEditor.HL_REPLACEMENT_OUT;
		for(EObject eObject : outsideInsideElements){
			HashMap<EObject, Integer> objectsToH = new HashMap<EObject, Integer>();
			objectsToH.put(eObject, fragment_in);
			objectsToHL.add(objectsToH);
		}
		for(EObject eObject : insideElements){
			HashMap<EObject, Integer> objectsToH = new HashMap<EObject, Integer>();
			objectsToH.put(eObject, fragment);
			objectsToHL.add(objectsToH);
		}
		for(EObject eObject : outsideOutsideElements){
			HashMap<EObject, Integer> objectsToH = new HashMap<EObject, Integer>();
			objectsToH.put(eObject, fragment_out);
			objectsToHL.add(objectsToH);
		}
		return objectsToHL;
	}
	
	private EList<HashMap<EObject, Integer>> getBoundaryObjectsToHighlight(NamedElement boundary) throws IllegalOperationException{
		EList<HashMap<EObject, Integer>> list = new BasicEList<HashMap<EObject, Integer>>();
		if(boundary instanceof ToPlacement || boundary instanceof ToReplacement){
			boolean isToPlacement = (boundary instanceof ToPlacement) ? true : false;
			if(!isToPlacement && LoaderUtility.isNullBoundary(boundary))
				return list;
			
			EObject eObject = (isToPlacement) ? ((ToPlacement) boundary).getOutsideBoundaryElement().getMOFRef() : ((ToReplacement) boundary).getOutsideBoundaryElement().getMOFRef();
			if(eObject != null){
				HashMap<EObject, Integer> map = new HashMap<EObject, Integer>();
				map.put(eObject, (isToPlacement) ? IBVREnabledEditor.HL_PLACEMENT_IN : IBVREnabledEditor.HL_REPLACEMENT_IN);
				list.add(map);
			}else{
				Context.eINSTANCE.logger.error("outside boundary element reference is null for toBoundary" + boundary);
			}
			
			EList<ObjectHandle> objectHandles = (isToPlacement) ? ((ToPlacement) boundary).getInsideBoundaryElement() : ((ToReplacement) boundary).getInsideBoundaryElement();
			for(ObjectHandle oh : objectHandles){
				eObject = oh.getMOFRef();
				if(eObject != null){
					HashMap<EObject, Integer> map = new HashMap<EObject, Integer>();
					map.put(eObject, (isToPlacement) ? IBVREnabledEditor.HL_PLACEMENT : IBVREnabledEditor.HL_REPLACEMENT);
					list.add(map);
				}else{
					Context.eINSTANCE.logger.error("inside boundary element reference is null for toBoundary" + boundary);
				}
			}
		}else if(boundary instanceof FromPlacement || boundary instanceof FromReplacement){
			boolean isFromPlacement = (boundary instanceof FromPlacement) ? true : false;
			if(isFromPlacement && LoaderUtility.isNullBoundary(boundary))
				return list;
			
			EObject eObject = (isFromPlacement) ? ((FromPlacement) boundary).getInsideBoundaryElement().getMOFRef() : ((FromReplacement) boundary).getInsideBoundaryElement().getMOFRef();
			if(eObject != null){
				HashMap<EObject, Integer> map = new HashMap<EObject, Integer>();
				map.put(eObject, (isFromPlacement) ? IBVREnabledEditor.HL_PLACEMENT : IBVREnabledEditor.HL_REPLACEMENT);
				list.add(map);
			}else{
				Context.eINSTANCE.logger.error("inside boundary element reference is null for fromBoundary" + boundary);
			}
			
			EList<ObjectHandle> objectHandles = (isFromPlacement) ? ((FromPlacement) boundary).getOutsideBoundaryElement() : ((FromReplacement) boundary).getOutsideBoundaryElement();
			for(ObjectHandle oh : objectHandles){
				eObject = oh.getMOFRef();
				if(eObject != null){
					HashMap<EObject, Integer> map = new HashMap<EObject, Integer>();
					map.put(eObject, (isFromPlacement) ? IBVREnabledEditor.HL_PLACEMENT_OUT : IBVREnabledEditor.HL_REPLACEMENT_OUT);
					list.add(map);
				}else{
					Context.eINSTANCE.logger.error("outside boundary element reference is null for fromBoundary" + boundary);
				}
			}
		}
		return list;
	}
	
	@Override
	public void addChoice(NamedElement parent) {
		Choice c = BvrFactory.eINSTANCE.createChoice();
		c.setName("Choice"+choicCounter);
		
		//each vspec has to have target
		Target target = BvrFactory.eINSTANCE.createTarget();
		target.setName(c.getName());
		((CompoundNode) c).getOwnedTargets().add(target);
		c.setTarget(target);
		
		if(parent instanceof CompoundNode){
			Context.eINSTANCE.getEditorCommands().addChoice(c, (CompoundNode) parent);
		}else if (parent instanceof BVRModel){
			BVRModel model = (BVRModel) parent;
			if(model.getVariabilityModel() == null){
				Context.eINSTANCE.getEditorCommands().addChoice(c, model);
			}
		}
		choicCounter++;
	}
	@SuppressWarnings("unchecked")
	@Override
	public void addChoiceResolution(VSpec resolvedVspec, VSpecResolution parent){
		
		NegResolution ncr = BvrFactory.eINSTANCE.createNegResolution();
		CommonUtility.setResolved(ncr, resolvedVspec);
		 Context.eINSTANCE.getEditorCommands().addNegChoiceResoulution(parent, ncr);
	}
		
	

	@Override
	public void minimaizeVSpec(VSpec vspec) {
		minimizedVSpec.add(vspec);
	}

	@Override
	public void maximizeVSpec(VSpec vspec) {
		minimizedVSpec.remove(vspec);
	}

	@Override
	public boolean isVSpecMinimized(VSpec vspec) {
		return minimizedVSpec.contains(vspec);
	}
	
	@Override
	public void minimaizeVSpecResolution(VSpecResolution vspecRes) {
		minimizedVSpecResolution.add(vspecRes);
	}

	@Override
	public void maximizeVSpecResolution(VSpecResolution vspecRes) {
		minimizedVSpecResolution.remove(vspecRes);
	}

	@Override
	public boolean isVSpecResolutionMinimized(VSpecResolution vspecRes) {
		return minimizedVSpecResolution.contains(vspecRes);
	}
	
	@Override
	public void updateVariable(Variable variable, String name, String typeName){
		PrimitiveTypeFacade.getInstance().testModelsPrimitiveTypes(getBVRModel());
		
		if(name.equals("")){
			Context.eINSTANCE.getEditorCommands().removeVSpecVariable((VSpec)variable.eContainer(), variable);
			return;
		}
		
		Context.eINSTANCE.getEditorCommands().setName(variable, name);

	
		PrimitiveTypeEnum t = null;
        for(PrimitiveTypeEnum x : PrimitiveTypeEnum.VALUES){
        	if(x.getName().equals(typeName)){
        		t = x;
        	}
        }
        
        if(t == null)
        	throw new UnsupportedOperationException("Invalid primitive type name " + typeName);
        
        PrimitveType primitivType = PrimitiveTypeFacade.getInstance().testPrimitiveType(getBVRModel(), t);
        Context.eINSTANCE.getEditorCommands().setTypeForVariable(variable, primitivType);
	}
	
	@Override
	public void updateName(NamedElement namedElement, String name) {	
		//update corresponding target accordingly if namedElement is VClassifier or Choice
		if(namedElement instanceof VClassifier || namedElement instanceof Choice){
			Target target = TargetFacade.eINSTANCE.testVSpecTarget((VSpec) namedElement);
			Context.eINSTANCE.getEditorCommands().setName(target, name);
		}
		if(!namedElement.getName().equals(name)){
			Context.eINSTANCE.getEditorCommands().setName(namedElement, name);
		}
	}
	
	@Override
	public void updateComment(NamedElement namedElement, String text) {
		Note commentNote = NoteFacade.eINSTANCE.testCommentNote(namedElement);
		Context.eINSTANCE.getEditorCommands().updateNoteExp(commentNote, text);
	}
	
	@Override
	public String getNodesCommentText(NamedElement namedElement) {
		return NoteFacade.eINSTANCE.getCommentText(namedElement);
	}
	
	@Override
	public void addVariable(VNode parentNode) {
		Variable var = BvrFactory.eINSTANCE.createVariable();
		
		PrimitveType primitivType = PrimitiveTypeFacade.getInstance().testPrimitiveType(getBVRModel(), PrimitiveTypeEnum.INTEGER);
		var.setName("Var" + variableCount);
		variableCount++;
		var.setType(primitivType);
		
		Context.eINSTANCE.getEditorCommands().addVariable(parentNode, var);
	}
	
	@Override
	public void setVClassifierLowerBound(VClassifier vClassifier, int lowerBound) {
		MultiplicityInterval interval = vClassifier.getInstanceMultiplicity();
		Context.eINSTANCE.getEditorCommands().setGroupMultiplicityLowerBound(interval, lowerBound);
	}
	
	@Override
	public void setVClassifierUpperBound(VClassifier vClassifier, int upperBound) {
		MultiplicityInterval interval = vClassifier.getInstanceMultiplicity();
		Context.eINSTANCE.getEditorCommands().setGroupMultiplicityUpperBound(interval, upperBound);
	}
	
	@Override
	public void addVClassifier(NamedElement parent) {
		VClassifier c = BvrFactory.eINSTANCE.createVClassifier();
		c.setName("Classifier"+classifierCount);
		MultiplicityInterval mi = BvrFactory.eINSTANCE.createMultiplicityInterval();
		mi.setLower(1);
		mi.setUpper(1);
		c.setInstanceMultiplicity(mi);
		
		//each vspec has to have target
		Target target = BvrFactory.eINSTANCE.createTarget();
		target.setName(c.getName());
		((CompoundNode) c).getOwnedTargets().add(target);
		c.setTarget(target);
		
		if(parent instanceof CompoundNode){
			Context.eINSTANCE.getEditorCommands().addVClassifierToVSpec((CompoundNode) parent, c);
		}else if(parent instanceof BVRModel) {
			BVRModel model = (BVRModel) parent;
			if(model.getVariabilityModel() == null)
				Context.eINSTANCE.getEditorCommands().addVClassifierToBVRModel(model, c);
		}
		classifierCount++;
	}
	
	@Override
	public void addBCLConstraint(VNode parentVNode) {
		ConstraintFacade.eINSTANCE.createBCLConstraint(parentVNode);
	}
	
	@Override
	public void updateBCLConstraint(BCLConstraint constraint, String strConstr) {
		ConstraintFacade.eINSTANCE.updateBCLConstraint(bvrm.getRootBVRModel(), constraint, strConstr);
	}
	
	@Override
	public void toggleChoiceOptionalMandotary(Choice choice) {
		Context.eINSTANCE.getEditorCommands().setIsImpliedByParent(choice, !choice.isIsImpliedByParent());
	}
	
	@Override
	public void cutNamedElement(NamedElement namedElement) {
		EObject parent = namedElement.eContainer();
		if(namedElement instanceof VNode){
			if(parent instanceof CompoundNode && namedElement instanceof VNode){
				Context.eINSTANCE.getEditorCommands().removeVNodeCompoundNode((CompoundNode) parent, (VNode) namedElement);
			}else if (parent instanceof BVRModel && namedElement instanceof CompoundNode) {
				Context.eINSTANCE.getEditorCommands().removeVariabilityModelBVRModel((BVRModel) parent, (CompoundNode) namedElement);
			}else{
				throw new UnexpectedException("not supported operation");
			}
		}else {
			throw new UnsupportedOperationException("Cut is not implemented for anything other than VNode " + namedElement);
		}		
		cutNamedElement = namedElement;
	}
	
	@Override
	public void pastNamedElementAsChild(NamedElement parent) {
		if(cutNamedElement != null){
			if(parent instanceof CompoundNode && cutNamedElement instanceof VNode){
				Context.eINSTANCE.getEditorCommands().addVNodeToCompoundNode((CompoundNode) parent, (VNode) cutNamedElement);
			} else if (parent instanceof BVRModel && cutNamedElement instanceof CompoundNode) {
				Context.eINSTANCE.getEditorCommands().addVariabilityModelToBVRModel((BVRModel) parent, (CompoundNode) cutNamedElement);
			}else{
				throw new UnexpectedException("not supported operation");
			}
			cutNamedElement = null;
		}
	}
	
	@Override
	public void pastNamedElementAsSibling(NamedElement sibling) {
		if(cutNamedElement != null){
			EObject parent = sibling.eContainer();
			if(parent instanceof CompoundNode && cutNamedElement instanceof VNode){
				Context.eINSTANCE.getEditorCommands().addVNodeToCompoundNode((CompoundNode) parent, (VNode) cutNamedElement);
			} else{
				throw new UnexpectedException("not supported operation");
			}
			cutNamedElement = null;
		}		
	}
	
	@Override
	public void setGroupMultiplicity(VNode parent, int lowerBound, int upperBound) {
		MultiplicityInterval mi = BvrFactory.eINSTANCE.createMultiplicityInterval();
		mi.setLower(lowerBound);
		mi.setUpper(upperBound);
		Context.eINSTANCE.getEditorCommands().setVNodeGroupMultiplicity(parent, mi);
	}
	
	@Override
	public void removeGroupMultiplicity(VNode parent) {
		Context.eINSTANCE.getEditorCommands().setVNodeGroupMultiplicity(parent, null);
	}
	
	@Override
	public String getBCLConstraintString(BCLConstraint constraint) {
		return ConstraintFacade.eINSTANCE.getBCLConstraintString(bvrm.getRootBVRModel(), constraint);
	}
	
	@Override
	public void removeNamedElement(NamedElement element) {
		EObject parent = element.eContainer();
		if(parent != null){
			if(parent instanceof CompoundNode){
				if(element instanceof Constraint){
					Context.eINSTANCE.getEditorCommands().removeConstraintCompoundNode((CompoundNode) parent, (Constraint) element);
				} else if(element instanceof VNode){
					Context.eINSTANCE.getEditorCommands().removeVNodeCompoundNode((CompoundNode) parent, (VNode) element);
				}else {
					throw new UnexpectedException("can not remove " + element + " with parent " + parent);
				}
			}else if(parent instanceof BVRModel){
				Context.eINSTANCE.getEditorCommands().removeVariabilityModelBVRModel((BVRModel) parent, (CompoundNode) element);
			}else {
				throw new UnexpectedException("can not remove " + element + " with parent " + parent);
			}
		}else{
			throw new UnexpectedException("can not find parent element to remove " + element);
		}
	}
	
	@Override
	public void deletePlacements(EList<VariationPoint> placements) {
		if(placements.size() > 0)
			Context.eINSTANCE.getEditorCommands().removeOwenedVariationPoints(getBVRModel(), placements);
	}
	
	@Override
	public void deleteReplacements(EList<Variabletype> replacements) {
		if(replacements.size() > 0)
			Context.eINSTANCE.getEditorCommands().removeOwnedVariationTypes(getBVRModel(), replacements);
	}
	
	@Override
	public void deleteFragments(EList<VariationPoint> fslist) {
		if(fslist.size() > 0)
			Context.eINSTANCE.getEditorCommands().removeOwenedVariationPoints(getBVRModel(), fslist);
	}
	
	@Override
	public void createFragmentSubstitution(PlacementFragment placement, ReplacementFragmentType replacement) {
		FragmentSubstitution fs = SubstitutionFragmentFacade.eINSTANCE.createFragmentSubstitution();
		fs.setPlacement(placement);
		fs.setReplacement(replacement);
		Context.eINSTANCE.getEditorCommands().addRealizationVariationPoint(getBVRModel(), fs);
	}
	
	@Override
	public void generateBindings(FragmentSubstitution fragmentSubstitution) {
		BindingCalculatorContext bindingCalculator = new BindingCalculatorContext();
		bindingCalculator.generateBindings(fragmentSubstitution);
	}
	
	@Override
	public void updateFragmentSubstitutionBinding(VariationPoint vp, VSpec vSpec) {
		Context.eINSTANCE.getEditorCommands().setBindingVariationPoint(vp, vSpec);
	}
	
	@Override
	public EList<HashMap<EObject, Integer>> findFragmentElementsToHighlight(NamedElement fragment) {
		EList<HashMap<EObject, Integer>> objectsToHighlightList = new BasicEList<HashMap<EObject, Integer>>();
		if(!Context.eINSTANCE.getConfig().isHighlightingMode())
			return objectsToHighlightList;
		
    	if(fragment instanceof PlacementFragment){
    		PlacementFragment placement = (PlacementFragment) fragment;            		
    		EList<PlacementBoundaryElement> boundaries = placement.getPlacementBoundaryElement();
    		EList<EObject> outsideInsideElements = new BasicEList<EObject>();
    		EList<EObject> outsideOutsideElements = new BasicEList<EObject>();
    		EList<EObject> insideElements = new BasicEList<EObject>();
    		for(PlacementBoundaryElement boundary : boundaries){
    			if(boundary instanceof ToPlacement){
    				ToPlacement toPlacement = (ToPlacement) boundary;
    				if(!LoaderUtility.isNullBoundary(toPlacement)){
    					EObject eObject = toPlacement.getOutsideBoundaryElement().getMOFRef();
    					if(eObject == null){
    						Context.eINSTANCE.logger.error("outside boundary element refrence is null for toPlacement" + toPlacement);
    					}else{
    						outsideInsideElements.add(eObject);
    					}
    					EList<ObjectHandle> objectHandles = toPlacement.getInsideBoundaryElement();
    					for(ObjectHandle objectHandle : objectHandles){
    						eObject = objectHandle.getMOFRef();
    						if(eObject == null){
    							Context.eINSTANCE.logger.error("inside boundary element refrence is null for toPlacement" + toPlacement);
    						}else{
    							insideElements.add(eObject);
    						}
    					}
    				}else{
    					Context.eINSTANCE.logger.error("toPlacement can not be null boundary, placement " + placement);
    				}
    			}
    			if(boundary instanceof FromPlacement){
    				FromPlacement fromPlacement = (FromPlacement) boundary;
    				if(!LoaderUtility.isNullBoundary(fromPlacement)){
    					EObject eObject = fromPlacement.getInsideBoundaryElement().getMOFRef();
    					if(eObject == null){
    						Context.eINSTANCE.logger.error("inside boundary element refrence is null for fromPlacement" + fromPlacement);
    					}else{
    						insideElements.add(eObject);
    					}
    					EList<ObjectHandle> objectHandles = fromPlacement.getOutsideBoundaryElement();
    					for(ObjectHandle objectHandle : objectHandles){
    						eObject = objectHandle.getMOFRef();
    						if(eObject == null){
    							Context.eINSTANCE.logger.error("outside boundary element refrence is null for fromPlacement" + fromPlacement);
    						}else{
    							outsideOutsideElements.add(eObject);
    						}
    					}
    				}
    			}
    		}
    		insideElements = calculateInnerFragmentElements(outsideInsideElements, outsideOutsideElements, insideElements, new BasicEList<EObject>());
    		objectsToHighlightList.addAll(markObjects(outsideInsideElements, outsideOutsideElements, insideElements, true));
    	}
    	if(fragment instanceof ReplacementFragmentType){
    		ReplacementFragmentType replacement = (ReplacementFragmentType) fragment;
    		EList<ReplacementBoundaryElement> boundaries = replacement.getReplacementBoundaryElement();
    		EList<EObject> outsideInsideElements = new BasicEList<EObject>();
    		EList<EObject> outsideOutsideElements = new BasicEList<EObject>();
    		EList<EObject> insideElements = new BasicEList<EObject>();
    		for(ReplacementBoundaryElement boundary : boundaries){
    			if(boundary instanceof ToReplacement){
    				ToReplacement toReplacement = (ToReplacement) boundary;
    				if(!LoaderUtility.isNullBoundary(toReplacement)){
    					EObject eObject = toReplacement.getOutsideBoundaryElement().getMOFRef();
    					if(eObject == null){
    						Context.eINSTANCE.logger.debug("outside boundary element refrence is null for toReplacement" + toReplacement);
    					}else{
    						outsideInsideElements.add(eObject);
    					}
    					EList<ObjectHandle> objectHandles = toReplacement.getInsideBoundaryElement();
    					for(ObjectHandle objectHandle : objectHandles){
    						eObject = objectHandle.getMOFRef();
    						if(eObject == null){
    							Context.eINSTANCE.logger.debug("inside boundary element refrence is null for toReplacement" + toReplacement);
    						}else{
    							insideElements.add(eObject);
    						}
    					}
    				}
    			}
    			if(boundary instanceof FromReplacement){
    				FromReplacement fromReplacement = (FromReplacement) boundary;
    				if(!LoaderUtility.isNullBoundary(fromReplacement)){
    					EObject eObject = fromReplacement.getInsideBoundaryElement().getMOFRef();
    					if(eObject == null){
    						Context.eINSTANCE.logger.debug("inside boundary element refrence is null for fromReplacement" + fromReplacement);
    					}else{
    						insideElements.add(eObject);
    					}
    					EList<ObjectHandle> objectHandles = fromReplacement.getOutsideBoundaryElement();
    					for(ObjectHandle objectHandle : objectHandles){
    						eObject = objectHandle.getMOFRef();
    						if(eObject == null){
    							Context.eINSTANCE.logger.debug("outside boundary element refrence is null for fromReplacement" + fromReplacement);
    						}else{
    							outsideOutsideElements.add(eObject);
    						}
    					}
    				}else{
    					Context.eINSTANCE.logger.debug("fromPlacement can not be null boundary, replacement " + replacement);
    				}
    			}
    		}
    		insideElements = calculateInnerFragmentElements(outsideInsideElements, outsideOutsideElements, insideElements, new BasicEList<EObject>());
    		objectsToHighlightList.addAll(this.markObjects(outsideInsideElements, outsideOutsideElements, insideElements, false));
    	}
		return objectsToHighlightList;
	}
	
	@Override
	public void highlightElements(EList<HashMap<EObject, Integer>> objectsToHighlightList) {
		if(!Context.eINSTANCE.getConfig().isHighlightingMode())
			return;
		 Context.eINSTANCE.highlightObjects(objectsToHighlightList);
	}
	
	@Override
	public EList<HashMap<EObject, Integer>> findBoundaryElementsToHighlight(NamedElement binding) {
		EList<HashMap<EObject, Integer>> objectsToHighlightList = new BasicEList<HashMap<EObject, Integer>>();
		if(!Context.eINSTANCE.getConfig().isHighlightingMode())
			return objectsToHighlightList;
		
		if(binding instanceof ToBinding){
			ToBinding toBinding = (ToBinding) binding;
			ToPlacement toPlacement = toBinding.getToPlacement();
			ToReplacement toReplacement = toBinding.getToReplacement();
			objectsToHighlightList.addAll(getBoundaryObjectsToHighlight(toPlacement));
			objectsToHighlightList.addAll(getBoundaryObjectsToHighlight(toReplacement));
		}else{
			FromBinding fromBinding = (FromBinding) binding;
			FromPlacement fromPlacement = fromBinding.getFromPlacement();
			FromReplacement fromReplacement = fromBinding.getFromReplacement();
			objectsToHighlightList.addAll(getBoundaryObjectsToHighlight(fromPlacement));
			objectsToHighlightList.addAll(getBoundaryObjectsToHighlight(fromReplacement));
		}
		return objectsToHighlightList;
	}
	
	@Override
	public void updateBindingBoundary(BoundaryElementBinding binding, NamedElement boundary) {
		if(binding instanceof ToBinding && boundary instanceof ToReplacement){
			ToBinding toBinding = (ToBinding) binding;
			Context.eINSTANCE.getEditorCommands().setToBindingToReplacement(toBinding, (ToReplacement) boundary);
		}else if(binding instanceof FromBinding && boundary instanceof FromPlacement) {
			FromBinding fromBinding = (FromBinding) binding;
			Context.eINSTANCE.getEditorCommands().setFromBindingFromPlacement(fromBinding, (FromPlacement) boundary);
		}else{
			throw new UnexpectedException("binding or boundary is not of the right type");
		}
	}
	
	@Override
	public CompoundResolution createResolution(){
		BVRModel model = getBVRModel();
		if(model.getVariabilityModel() == null)
			throw new UserInputError("there is not variability model yet, nothing to resolve");
		
		PosResolution root = BvrFactory.eINSTANCE.createPosResolution();

		CompoundNode variablityModel = model.getVariabilityModel();
		
		if (variablityModel instanceof Choice) {
			CommonUtility.setResolved(root, (VSpec) variablityModel);
			root.setName(((NamedElement) variablityModel).getName());
			ResolutionModelIterator.getInstance().iterateEmptyOnChildren(this, new AddResolution(), (VSpec) root.getResolvedChoice(), root, false);
		} else {
			throw new UserInputError("model must start with a choice");
		}
		return root;
	}
	
	@Override
	public void addResolutionModel(CompoundResolution root) {
		BVRModel model = getBVRModel();
		Context.eINSTANCE.getEditorCommands().createNewResolution((PosResolution) root, model);
	}
	
	@Override
	public void removeRootResolution(int resolutionIndex) {
		BVRModel model = getBVRModel();
		CompoundResolution resolution = model.getResolutionModels().get(resolutionIndex);
		Context.eINSTANCE.getEditorCommands().removeOwnedVSpecResolution(model, resolution);
	}
	
	@Override
	public void removeAllResolutions() {
		BVRModel model = getBVRModel();
		EList<CompoundResolution> resolutions = new BasicEList<CompoundResolution>();
		for(CompoundResolution cr : model.getResolutionModels()){
			if(cr instanceof PosResolution)
				resolutions.add(cr);
		}
		Context.eINSTANCE.getEditorCommands().removeBVRModelCompoundResolutions(model, resolutions);
	}
	
	@Override
	public void generatAllProducts() {
		try {
			GUIDSL gdsl = getBVRM().getGUIDSL();
			CNF cnf = gdsl.getSXFM().getCNF();
			CoveringArray ca = new CoveringArrayComplete(cnf);
			ca.generate();
			GraphMLFM gfm = gdsl.getGraphMLFMConf(ca);
			EList<VSpecResolution> resolutions = getBVRM().getChoiceResolutions(gfm);
			for(VSpecResolution resolution : resolutions)
				addResolutionModel((CompoundResolution) resolution);
		} catch (Exception e) {
			throw new RethrownException("failed to generate products", e); 
		}	
	}
	
	@Override
	public boolean performSATValidation() {
		boolean valid = false;
		CoveringArray ca;
		satValidationMessage = new ArrayList<String>();
		try {
			ca = getBVRM().getCoveringArray();
		} catch (CSVException e) {
			throw new RethrownException("Getting CA failed:", e);
		} catch (BVRException e) {
			throw new RethrownException("Getting CA failed:", e);
		}
		CNF cnf;
		try {
			cnf = getBVRM().getGUIDSL().getSXFM().getCNF();
			valid = CALib.verifyCA(cnf, ca, true, satValidationMessage);
		} catch (Exception e) {
			throw new RethrownException("Validation failed:", e);
		}
		return valid;
	}
	
	@Override
	public List<String> getSATValidationMessage() {
		return satValidationMessage;
	}

	@Override
	public Integer calculateCoverage(int t) {
		int cov;
		try {
			// Get FM:
			GUIDSL gdsl = getBVRM().getGUIDSL();
			CNF cnf = gdsl.getSXFM().getCNF();
			// Get Covering Array
			CoveringArray ca = getBVRM().getCoveringArray();
			// Calculate
			cov = (int) Math.round(CALib.calc_coverage(cnf, t, ca));
		} catch (FeatureModelException | IOException | UnsupportedModelException | BVRException | CSVException e) {
			throw new RethrownException("Failed to calculate coverage", e);
		}
		return cov;
	}

	@Override
	public void generateCoveringArray(int xWise) {
		try {
			removeAllResolutions();
			GUIDSL gdsl = getBVRM().getGUIDSL();
			CNF cnf = gdsl.getSXFM().getCNF();
			CoveringArray ca = cnf.getCoveringArrayGenerator("J11", xWise, 1);
			
			/*EList<CompoundResolution> compoundResolutions = getBVRModel().getResolutionModels();
			EList<PosResolution> rootResolutions = new BasicEList<PosResolution>();
			for(CompoundResolution compoundResolution : compoundResolutions) {
				if(compoundResolution instanceof PosResolution)
					rootResolutions.add((PosResolution) compoundResolution);
			}
			
			 if(rootResolutions.size() > 0){
				 CoveringArray startFrom = getBVRM().getCoveringArray();
				 ca.startFrom(startFrom);
			 }*/
			 
			 ca.generate();
			 GraphMLFM gfm = gdsl.getGraphMLFMConf(ca);
			 EList<VSpecResolution> resolutions = getBVRM().getChoiceResolutions(gfm);
			 for(VSpecResolution resolution : resolutions)
				addResolutionModel((CompoundResolution) resolution);
		} catch (Exception e) {
			throw new RethrownException("Failed to generate covering array", e);
		}

	}
}
