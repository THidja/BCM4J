package bcm.extend;


public interface ComponentI extends fr.sorbonne_u.components.ComponentI {
	
	public <T> T handleRequestSync(SFunctionalComponentService<T> request) throws Exception;
	public <T> T handleRequestSync(int executorIndex, SFunctionalComponentService<T> request) throws Exception;
	public <T> void handleRequestAsync(AFunctionalComponentService request) throws Exception;
	public <T> void handleRequestAsync(int executorIndex, AFunctionalComponentService request) throws Exception;
	public void setComponentBehavior(ComponentBehavior behavior) throws Exception;
	
}