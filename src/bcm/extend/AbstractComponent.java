package bcm.extend;

public abstract class AbstractComponent 
	   extends fr.sorbonne_u.components.AbstractComponent
	   implements ComponentI
{
	
	private ComponentScenario componentBehavior;

	public AbstractComponent(int nbThreads, int nbSchedulableThreads) {
		super(nbThreads, nbSchedulableThreads);
	}
	

	@Override
	public <T> T handleRequestSync(SFunctionalComponentService<T> request) throws Exception {
		
		return this.handleRequestSync(
					new AbstractComponent.AbstractService<T>() {
						@Override
						public T call() throws Exception {
							return request.service();
						}
					});
	}


	@Override
	public <T> void handleRequestAsync(AFunctionalComponentService request) throws Exception {
		
		this.handleRequestAsync(
				new AbstractComponent.AbstractService<T>() {
					@Override
					public T call() throws Exception {
						request.service();
						return null;
					}
				});
	}
	
	@Override
	public void setComponentBehavior(ComponentScenario behavior) throws Exception {
		this.componentBehavior = behavior;
	}
	
	@Override
	public void execute() throws Exception {
		super.execute();
		this.componentBehavior.run();
	}
	
}