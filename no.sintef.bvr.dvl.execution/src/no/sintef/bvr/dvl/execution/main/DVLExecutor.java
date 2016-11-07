package no.sintef.bvr.dvl.execution.main;


import java.util.ArrayList;
import java.util.List;

import no.sintef.bvr.dvl.execution.interfaces.errors.ConfigError;
import no.sintef.bvr.dvl.execution.interfaces.errors.PlannerError;
import no.sintef.bvr.dvl.execution.interfaces.errors.RealisationError;
import no.sintef.bvr.dvl.execution.interfaces.main.IDVLExecutor;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import dvlconfig.Config;
import dvlconfig.DvlconfigPackage;


import dvlconfig.Original;
import dvlconfig.Realop;
import no.sintef.autorealspl.converter.main.BVREcoreVarModelToOperatorConverter;
import no.sintef.autorealspl.converter.main.IConverter;
import no.sintef.bvr.planner.Operators;
import no.sintef.bvr.planner.Plan;
import no.sintef.bvr.planner.PlanningProblem;
import no.sintef.bvr.planner.Settings;
import no.sintef.bvr.planner.State;
import no.sintef.bvr.planner.repository.FileSystem;
import no.sintef.bvr.planner.repository.PlanWriter;
import no.sintef.bvr.planner.repository.PropertiesStateReader;
import no.sintef.bvr.planner.repository.ReaderException;
import no.sintef.bvr.planner.repository.Repository;
import no.sintef.bvr.planner.repository.WriterException;
import no.sintef.bvr.planner.repository.ecore.EcoreOperatorReader;
import no.sintef.bvr.planner.repository.interfaces.IOperatorsReader;

public class DVLExecutor implements IDVLExecutor {
	
	private Repository repository;
	private FileSystem fileSystem;
	private IFile config_file;
		
	public DVLExecutor(IFile _config_file) {
		config_file = _config_file;
		fileSystem = new FileSystem(); 
	}

	@Override
	public List<String> getOperators(IFile realisation) throws PlannerError, ConfigError {
		List<String> operators = new ArrayList<String>();
		Settings settings = Settings.defaultSettings();
		
		settings.setGoalLocation(realisation.getLocation().toOSString());
			
		String file_plan_name = realisation.getLocation().lastSegment() + "plan";
		IFile ifile_plan = config_file.getProject().getFile(file_plan_name);
		settings.setPlanLocation(ifile_plan.getLocation().toOSString());
		
		configureSettingsFromConfig(settings);
		repository = configureRepositoryWith(settings);
		
		try {
			PlanningProblem problem = new PlanningProblem(operators(), origin(), goal());
			Plan solution = problem.solve();
			store(solution);
		} catch (ReaderException e) {
			e.printStackTrace();
			throw new PlannerError(e.getMessage(), e);
		} catch (WriterException e) {
			e.printStackTrace();
			throw new PlannerError(e.getMessage(), e);
		}
		
		return operators;
	}


	@Override
	public void deriveProduct(List<String> operators) throws RealisationError {

	}
	
	private Operators operators() throws ReaderException {
		return repository.getOperators();
	}
	
    private State origin() throws ReaderException {
        return repository.getOrigin();
    }

    private State goal() throws ReaderException {
        return repository.getGoal();
    }
    
    private void store(Plan plan) throws WriterException {
        repository.store(plan);
    }
    
    private Repository configureRepositoryWith(Settings settings) {
        return new Repository(
                new PropertiesStateReader(settings.getOriginLocation(), fileSystem),
                new PropertiesStateReader(settings.getGoalLocation(), fileSystem),
                operatorsReader(settings),
                new PlanWriter(settings.getPlanLocation(), fileSystem));	
    }
    
    private IOperatorsReader operatorsReader(Settings settings) {
        IConverter ecore_converter = new BVREcoreVarModelToOperatorConverter();
        return new EcoreOperatorReader(ecore_converter, settings.getOperatorsLocation());
    }
  
	private void configureSettingsFromConfig(Settings settings) throws ConfigError {
		IProject project = config_file.getProject();
		Config config_model = getDVLConfig();
		
		
		Realop realop = config_model.getRealop();
		if(realop == null || realop.getLocation() == null)
			throw new ConfigError("Realop is not set in config");
		
		String operator_file = config_model.getRealop().getLocation();
		IFile operator_ifile = project.getFile(operator_file);
		settings.setOperatorsLocation(operator_ifile.getFullPath().toOSString());
		
		Original original = config_model.getOriginal();
		if(original == null || original.getLocation() == null)
			throw new ConfigError("Original state is not set in config");
		
		String original_file = original.getLocation();
		IFile original_ifile = project.getFile(original_file);
		settings.setOriginLocation(original_ifile.getLocation().toOSString());
	}
	
	private Config getDVLConfig() {
		DvlconfigPackage.eINSTANCE.eClass();
		Resource.Factory.Registry registry = Resource.Factory.Registry.INSTANCE;
		registry.getExtensionToFactoryMap().put("dvlconfig", new XMIResourceFactoryImpl());
		
		ResourceSet resourceSet = new ResourceSetImpl();
		Resource resource = resourceSet.getResource(URI.createPlatformResourceURI(config_file.getFullPath().toOSString(), true), true);
		
		return (Config) resource.getContents().get(0);		
	}
}
