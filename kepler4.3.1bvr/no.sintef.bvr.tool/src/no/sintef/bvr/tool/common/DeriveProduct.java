package no.sintef.bvr.tool.common;

import java.util.HashMap;

import no.sintef.bvr.tool.chain.ExecutionHandler;
import no.sintef.bvr.tool.chain.impl.ExecutionRealizationHandler;
import no.sintef.bvr.tool.chain.impl.IntersactionResolverExecutionHandler;
import no.sintef.bvr.tool.chain.impl.ParserExecutionHandler;
import no.sintef.bvr.tool.chain.impl.ResetExecutionHandler;
import no.sintef.bvr.tool.chain.impl.SaveProductExecutionHandler;
import no.sintef.bvr.tool.chain.impl.ScopeResolverExecutionHandler;
import no.sintef.bvr.tool.exception.AbstractError;
import no.sintef.bvr.tool.primitive.impl.SingleExecutionRequest;


public class DeriveProduct {

	private SingleExecutionRequest request;
	private ExecutionHandler starter;

	public DeriveProduct(HashMap<String, Object> keywords) throws AbstractError{
		request = new SingleExecutionRequest(keywords);
		
		ResetExecutionHandler resetModel = new ResetExecutionHandler(null);
		SaveProductExecutionHandler saveProduct = new SaveProductExecutionHandler(resetModel);
		ExecutionRealizationHandler realization = new ExecutionRealizationHandler(saveProduct);
		ScopeResolverExecutionHandler scopeResolving = new ScopeResolverExecutionHandler(realization);
		IntersactionResolverExecutionHandler intersectionResolver = new IntersactionResolverExecutionHandler(scopeResolving);
		starter = new ParserExecutionHandler(intersectionResolver);
	}
	
	public void run() throws AbstractError{
		starter.handleRequest(request);
	}
}