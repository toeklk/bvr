package no.sintef.bvr.tool.observer;

public interface ResourceSubject {

	public void attach(ResourceObserver observer);
	
	public void detach(ResourceObserver observer);
	
	public void notifyObservers();
}