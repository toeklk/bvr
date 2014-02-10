package no.sintef.bvr.constraints.bcl;

import bvr.BCLExpression;
import bvr.ConfigurableUnit;
import bvr.IntegerLiteralExp;
import bvr.OperationCallExp;
import bvr.RealLiteralExp;
import bvr.StringLiteralExp;
import bvr.VSpecRef;

public class BCLPrettyPrinter{
	public String prettyPrint(BCLExpression e, ConfigurableUnit cu) {
		return prettyPrint(e, cu, 0);
	}
	
	private String prettyPrint(BCLExpression e, ConfigurableUnit cu, int depth) {
		if(e instanceof VSpecRef){
			if(((VSpecRef) e).getVSpec() == null)
				return "[null]";
			else
				return ((VSpecRef) e).getVSpec().getName();
		}else if(e instanceof StringLiteralExp){
			return "\"" + ((StringLiteralExp)e).getString() + "\"";
		}else if(e instanceof IntegerLiteralExp){
			return ""+((IntegerLiteralExp)e).getInteger();
		}else if(e instanceof RealLiteralExp){
			return ""+((RealLiteralExp)e).getReal();
		}else if(e instanceof OperationCallExp){
			OperationCallExp o = (OperationCallExp) e;
			String opSym = "";
			if(o.getOperation() == bvr.Operation.getByName("logImplies")) opSym = "implies";
			if(o.getOperation() == bvr.Operation.getByName("logAnd")) opSym = "and"; 
			if(o.getOperation() == bvr.Operation.getByName("logOr")) opSym = "or"; 
			if(o.getOperation() == bvr.Operation.getByName("logXor")) opSym = "xor"; 
			if(o.getOperation() == bvr.Operation.getByName("eq")) opSym = "="; 
			if(o.getOperation() == bvr.Operation.getByName("lte")) opSym = "<="; 
			if(o.getOperation() == bvr.Operation.getByName("gte")) opSym = ">="; 
			if(o.getOperation() == bvr.Operation.getByName("lt")) opSym = "<"; 
			if(o.getOperation() == bvr.Operation.getByName("gt")) opSym = ">"; 
			if(o.getOperation() == bvr.Operation.getByName("arithMult")) opSym = "*";
			if(o.getOperation() == bvr.Operation.getByName("arithDev")) opSym = "/";
			if(o.getOperation() == bvr.Operation.getByName("arithPlus")) opSym = "+";
			if(o.getOperation() == bvr.Operation.getByName("arithMinus")) opSym = "-";
			
			if(o.getOperation() == bvr.Operation.getByName("arithNeg")) opSym = "-";
			if(o.getOperation() == bvr.Operation.getByName("logNot")) opSym = "not";
			if(o.getOperation() == bvr.Operation.getByName("isDefined")) opSym = "isDefined";
			if(o.getOperation() == bvr.Operation.getByName("isUndefined")) opSym = "isUndefined";
			
			String in = "";
			if(o.getArgument().size() == 1){
				String a1 = prettyPrint(o.getArgument().get(0), cu, depth+1);
				in = opSym + " " + a1;
			}else if(o.getArgument().size() == 2){
				String a1 = prettyPrint(o.getArgument().get(0), cu, depth+1);
				String a2 = prettyPrint(o.getArgument().get(1), cu, depth+1);
				in = a1 + " " + opSym + " " + a2;
			}else{
				throw new UnsupportedOperationException();
			}
			

			if(depth != 0)
				return "(" + in + ")";
			else return in;
		}else{
			throw new UnsupportedOperationException("Unsupported type: " + e.getClass().getName());
		}
	}
}