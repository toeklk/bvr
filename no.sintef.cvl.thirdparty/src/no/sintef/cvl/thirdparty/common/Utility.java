package no.sintef.cvl.thirdparty.common;

import java.io.File;
import java.net.URI;
import java.util.HashMap;

import org.eclipse.core.filesystem.URIUtil;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.transaction.Transaction;

public final class Utility {
	
	public final static String statusCode = "statusCode";
	public final static String message = "message";
	public final static String isOk = "isOk";

	public static String getWorkspaceRowLocation(){
		IPath path = ResourcesPlugin.getWorkspace().getRoot().getRawLocation();
		return path.toString();
	}
	
	public static String findFileInWorkspace(File file){
		IWorkspaceRoot myWorkspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		URI uri = URIUtil.toURI(file.getAbsolutePath());
		IFile[] files = myWorkspaceRoot.findFilesForLocationURI(uri);
		return (files.length == 1) ? files[0].getFullPath().toString() : null;
	}
	
	public static HashMap<String, Object> parseTransaction(Transaction transaction){
		HashMap<String, Object> result = new HashMap<String, Object>();
		result.put(isOk, transaction.getStatus().isOK());
		result.put(statusCode, transaction.getStatus().getSeverity());
		result.put(message, transaction.getStatus().getMessage());
		return result;
	}

}
