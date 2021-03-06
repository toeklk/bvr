/*******************************************************************************
 * Copyright (c) 2011, 2012 SINTEF, Martin F. Johansen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     SINTEF, Martin F. Johansen - the implementation
 *******************************************************************************/

package no.sintef.ict.splcatool;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.sat4j.core.VecInt;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IVecInt;

import splar.core.constraints.BooleanVariableInterface;

class FMThread implements Runnable, ProgressReporter{
	private CNF cnf;
	private List<BooleanVariableInterface> vars;
	private int progress = 0;
	private Set<BooleanVariableInterface> mandatory = new HashSet<BooleanVariableInterface>();
	SAT4JSolver satSolver;

	public FMThread(CNF cnf, List<BooleanVariableInterface> vars){
		this.cnf = cnf;
		this.vars = vars;
		try {
			satSolver = cnf.getSAT4JSolver();
		} catch (ContradictionException e) {
		}
	}
	
	private void findMandatory() {
		for(BooleanVariableInterface v : vars){
			progress++;
			boolean man = isMandatory(v);
			if(man){
				mandatory.add(v);
			}
		}
	}
	
	private boolean isMandatory(BooleanVariableInterface v){
		int [] clause = new int [1];
		clause[0] = -cnf.getNr(v.getID());
		IVecInt assumps = new VecInt(clause);
		try {
			if(!satSolver.solver.isSatisfiable(assumps)){
				return true;
			}
		} catch (org.sat4j.specs.TimeoutException e1) {
		}
		
		return false;
	}

	@Override
	public void run() {
		findMandatory();
	}
	
	public long getProgress(){
		return progress;
	}
	
	public Set<BooleanVariableInterface> getMandatory(){
		return mandatory;
	}
}