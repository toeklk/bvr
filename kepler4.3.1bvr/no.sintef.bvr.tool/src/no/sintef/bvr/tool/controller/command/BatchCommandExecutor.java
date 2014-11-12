package no.sintef.bvr.tool.controller.command;

import no.sintef.bvr.common.command.SimpleExeCommandInterface;
import no.sintef.bvr.tool.context.Context;


public class BatchCommandExecutor implements SimpleExeCommandInterface, CommandBatchInterface {

	protected SimpleExeCommandInterface command;

	public BatchCommandExecutor(SimpleExeCommandInterface _command){
		command = _command;
	}
	
	@Override
	public void execute() {
		preExecute();
		command.execute();
		postExecute();
	}

	@Override
	public void preExecute() {
		Context.eINSTANCE.getEditorCommands().enableBatchProcessing();
	}

	@Override
	public void postExecute() {
		Context.eINSTANCE.getEditorCommands().executeBatch();
		Context.eINSTANCE.getEditorCommands().disableBatchProcessing();
	}
}
