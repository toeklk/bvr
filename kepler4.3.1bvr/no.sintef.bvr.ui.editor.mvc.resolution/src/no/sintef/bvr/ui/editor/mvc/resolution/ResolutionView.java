package no.sintef.bvr.ui.editor.mvc.resolution;

import java.awt.Point;
import java.awt.geom.Rectangle2D.Double;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import org.abego.treelayout.TreeLayout;
import org.abego.treelayout.demo.TextInBox;
import org.abego.treelayout.demo.TextInBoxNodeExtentProvider;
import org.abego.treelayout.util.DefaultConfiguration;
import org.abego.treelayout.util.DefaultTreeForTreeLayout;

import no.sintef.bvr.tool.exception.BVRModelException;
import no.sintef.bvr.tool.subject.ConfigurableUnitSubject;
import no.sintef.bvr.tool.ui.command.AddChoiceResolutuion;
import no.sintef.bvr.tool.ui.command.AddVInstance;
import no.sintef.bvr.tool.ui.command.AddVariableValueAssignment;
import no.sintef.bvr.tool.ui.dropdown.VSpecResDropDownListener;
import no.sintef.bvr.tool.ui.editor.BVRUIKernel;
import no.sintef.bvr.tool.ui.loader.BVRNotifier;
import no.sintef.bvr.tool.ui.loader.BVRModel;
import no.sintef.bvr.tool.ui.loader.BVRView;
import no.sintef.bvr.tool.ui.loader.Pair;
import no.sintef.bvr.ui.framework.TitledElement;
import no.sintef.bvr.ui.framework.elements.EditableModelPanel;
import bvr.ChoiceResolutuion;
import bvr.ConfigurableUnit;
import bvr.NamedElement;
import bvr.VInstance;
import bvr.VSpec;
import bvr.VSpecResolution;
import bvr.VariableValueAssignment;

public class ResolutionView extends BVRView{
	private BVRModel m;
	
	public JTabbedPane modelPane;
	
	// VSpec
	public JScrollPane vspecScrollPane;
	public EditableModelPanel vspecEpanel;
	private Map<JComponent, NamedElement> vspecvmMap;
	private BVRUIKernel vSpecbvruikernel;
	
	// Resolutions
	public JTabbedPane resPane;
	private List<JScrollPane> resolutionPanes;
	private List<EditableModelPanel> resolutionEpanels;
	private List<BVRUIKernel> resolutionkernels;
	private List<Map<JComponent, NamedElement>> resolutionvmMaps;
	private List<List<JComponent>> resolutionNodes;
	private List<List<Pair<JComponent, JComponent>>> resolutionBindings;
	
	private ConfigurableUnitSubject configurableUnitSubject;

	public BVRUIKernel getKernel() {
		return vSpecbvruikernel;
	}
	
	public ResolutionView(BVRModel m, BVRNotifier ep) {
		super();
		
		this.ep = ep;
		
		// Alloc
		
        resolutionPanes = new ArrayList<JScrollPane>();
        resolutionEpanels = new ArrayList<EditableModelPanel>();
        resolutionkernels = new ArrayList<BVRUIKernel>();
    	resolutionvmMaps = new ArrayList<Map<JComponent,NamedElement>>();
    	resolutionNodes = new ArrayList<List<JComponent>>();
    	resolutionBindings = new ArrayList<List<Pair<JComponent,JComponent>>>();
    	
    	//bvrViewSubject = new BVRViewSubject(this);
		
		this.m = m;
		
    	configurableUnitSubject = new ConfigurableUnitSubject(this.getCU());
	
    	vSpecbvruikernel = new BVRUIKernel(vspecvmMap, this, resolutionvmMaps);
		

		vspecScrollPane = new JScrollPane(vSpecbvruikernel.getModelPanel(), JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        vspecEpanel = new EditableModelPanel(vspecScrollPane);        
        
        // Resolution panes
        resPane = new JTabbedPane();
        
        try {
			loadBVRResolutionView(m.getBVRM().getCU(), resolutionkernels, resPane);
		} catch (BVRModelException e) {
			e.printStackTrace();
		}
        
        autoLayoutResolutions();

	}
	
	public boolean isDirty() {
		return m.isNotSaved();
	}

	public ConfigurableUnitSubject getConfigurableUnitSubject(){
		return configurableUnitSubject;
	}

	public ConfigurableUnit getCU() {
		return m.getCU();
	}

	int choiceCount = 1;

	List<VSpecResolution> minimized = new ArrayList<VSpecResolution>();

	private BVRNotifier ep;

	private void autoLayoutResolutions() {
		for(int i = 0; i < resolutionPanes.size(); i++){
			Map<JComponent, TextInBox> nodemap = new HashMap<JComponent, TextInBox>();
			Map<TextInBox, JComponent> nodemapr = new HashMap<TextInBox, JComponent>();
			
			for(JComponent c : resolutionNodes.get(i)){
				String title = ((TitledElement)c).getTitle();
				//System.out.println(title);
				TextInBox t = new TextInBox(title, c.getWidth(), c.getHeight());
				nodemap.put(c, t);
				nodemapr.put(t, c);
			}
			
			TextInBox root = nodemap.get(resolutionNodes.get(i).get(0));
	
			DefaultTreeForTreeLayout<TextInBox> tree = new DefaultTreeForTreeLayout<TextInBox>(root);
			
			for(Pair<JComponent, JComponent> p : resolutionBindings.get(i)){
				TextInBox a = nodemap.get(p.a);
				TextInBox b = nodemap.get(p.b);
				tree.addChild(a, b);
			}
			
			// setup the tree layout configuration
			double gapBetweenLevels = 30;
			double gapBetweenNodes = 10;
			DefaultConfiguration<TextInBox> configuration = new DefaultConfiguration<TextInBox>(gapBetweenLevels, gapBetweenNodes);
	
			// create the NodeExtentProvider for TextInBox nodes
			TextInBoxNodeExtentProvider nodeExtentProvider = new TextInBoxNodeExtentProvider();
			TreeLayout<TextInBox> treeLayout = new TreeLayout<TextInBox>(tree, nodeExtentProvider, configuration);
			
			// Set positions
			for(JComponent c : resolutionNodes.get(i)){
				TextInBox t = nodemap.get(c);
				Map<TextInBox, Double> x = treeLayout.getNodeBounds();
				Double z = x.get(t);
				c.setBounds((int)z.getX(), (int)z.getY(), (int)z.getWidth(), (int)z.getHeight());
			}
		}
	}

	public void notifyResolutionViewUpdate() {
		// Save
		boolean isEmpty = resPane.getTabCount() == 0;
		int resmodels = getCU().getOwnedVSpecResolution().size();
		boolean modelIsEmpty = getCU().getOwnedVSpecResolution().size() == 0;
		
		int selected = 0;
		Point pos = null;
		if(!isEmpty){
			selected = resPane.getSelectedIndex();
			pos = resolutionPanes.get(selected).getViewport().getViewPosition();
		}
		
		// Clean up
		resPane.removeAll();
	    resolutionPanes = new ArrayList<JScrollPane>();
	    resolutionEpanels = new ArrayList<EditableModelPanel>();
	    resolutionkernels = new ArrayList<BVRUIKernel>();
		resolutionvmMaps = new ArrayList<Map<JComponent,NamedElement>>();
		resolutionNodes = new ArrayList<List<JComponent>>();
		resolutionBindings = new ArrayList<List<Pair<JComponent,JComponent>>>();
		
		choiceCount = 1;
	    
	    try {
			loadBVRResolutionView(m.getBVRM().getCU(), resolutionkernels, resPane);
		} catch (BVRModelException e) {
			e.printStackTrace();
		}
	    
	    autoLayoutResolutions();
	    
	    // Restore positions
	    if(!isEmpty && !modelIsEmpty && selected <= resmodels){
		    resPane.setSelectedIndex(selected);
		    resolutionPanes.get(selected).getViewport().setViewPosition(pos);
	    }
	    
	    // Mark dirty
	    m.markNotSaved();
	    ep.notifyProbeDirty();
	}

	private void loadBVRResolutionView(ConfigurableUnit cu, List<BVRUIKernel> resolutionkernels, JTabbedPane resPane) throws BVRModelException{
		resPane.addMouseListener(new VSpecResDropDownListener(m, cu, this));
		
		if(cu.getOwnedVSpecResolution().size() == 0) return;
		
		for(VSpecResolution v : cu.getOwnedVSpecResolution()){
			BVRUIKernel resKernel = new BVRUIKernel(vspecvmMap, this, resolutionvmMaps);
			resolutionkernels.add(resKernel);
	        JScrollPane scrollPane = new JScrollPane(resKernel.getModelPanel(), JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
	        EditableModelPanel epanel = new EditableModelPanel(scrollPane);
	        
	        resolutionPanes.add(scrollPane);
	        resolutionEpanels.add(epanel);
	        Map<JComponent, NamedElement> vmMap = new HashMap<JComponent, NamedElement>();
	    	resolutionvmMaps.add(vmMap);
	        List<JComponent> nodes = new ArrayList<JComponent>();
	    	resolutionNodes.add(nodes);
	    	List<Pair<JComponent, JComponent>> bindings = new ArrayList<Pair<JComponent,JComponent>>();
	    	resolutionBindings.add(bindings);
			
			loadBVRResolutionView(v, resKernel, null, cu, vmMap, nodes, bindings);
			String tabtitle = "";
			if(v instanceof ChoiceResolutuion){
				ChoiceResolutuion cr = (ChoiceResolutuion) v;
				String choicename = "null";
				if(cr.getResolvedVSpec() != null){
					choicename = cr.getResolvedVSpec().getName();
				}
				tabtitle = choicename + " " + choiceCount;
				choiceCount++;
			}else if(v instanceof VInstance){
				VInstance vi = (VInstance) v;
				tabtitle = vi.getName() + ":" + vi.getResolvedVSpec().getName();
			}

			resPane.addTab(tabtitle, null, epanel, "");
		}
	}

	private void loadBVRResolutionView(VSpecResolution v, BVRUIKernel bvruikernel, JComponent parent, ConfigurableUnit cu, Map<JComponent, NamedElement> vmMap, List<JComponent> nodes, List<Pair<JComponent, JComponent>> bindings) throws BVRModelException {
		JComponent nextParent = null;
		
		// Add view
		//System.out.println(v.getClass().getSimpleName());
		if(v instanceof VInstance){
			//System.out.println(v + ", " + bvruikernel);
			
			nextParent = new AddVInstance(minimized.contains(v)).init(bvruikernel, v, parent, vmMap, nodes, bindings, this).execute();
			
			vmMap.put(nextParent, v);
			
		}else if(v instanceof ChoiceResolutuion){
			//System.out.println(v);
			
			nextParent = new AddChoiceResolutuion(minimized.contains(v)).init(bvruikernel, v, parent, vmMap, nodes, bindings, this).execute();
			
			vmMap.put(nextParent, v);
			
		}else if(v instanceof VariableValueAssignment){
			//System.out.println(v);
			
			nextParent = new AddVariableValueAssignment(minimized.contains(v)).init(bvruikernel, v, parent, vmMap, nodes, bindings, this).execute();
			
			vmMap.put(nextParent, v);
			
		}else{
			throw new BVRModelException("Unknown element: " + v.getClass());
		}
		
		// Recursive step
		//System.out.println();
		for(VSpecResolution vs : v.getChild()){
			//System.out.println("Treating " + vs.getResolvedVSpec().getName());
			if(!minimized.contains(v))
				loadBVRResolutionView(vs, bvruikernel, nextParent, cu, vmMap, nodes, bindings);
		}
	}

	@Override
	public void notifyVspecViewUpdate() {
	}

	@Override
	public void notifyRelalizationViewReset() {
		throw new UnsupportedOperationException();		
	}

	@Override
	public void notifyAllViews() {
		throw new UnsupportedOperationException();		
	}

	@Override
	public void setMaximized(Object v) {
		minimized.remove(v);
	}

	@Override
	public void setMinimized(Object v) {
		minimized.add((VSpecResolution)v);
	}
}