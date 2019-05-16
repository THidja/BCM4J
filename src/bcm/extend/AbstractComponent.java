package bcm.extend;

public abstract class AbstractComponent
	   extends fr.sorbonne_u.components.AbstractComponent
	   implements ComponentI
{
	
	private ComponentBehavior componentBehavior = () -> {};

	public AbstractComponent(int nbThreads, int nbSchedulableThreads) {
		super(nbThreads, nbSchedulableThreads);
	}


	public AbstractComponent(String reflectionInboundPortURI, int nbThreads, int nbSchedulableThreads) {
		super(reflectionInboundPortURI, nbThreads, nbSchedulableThreads);
	}
	
	@Override
	public <T> T handleRequestSync(int executorServiceIndex, SFunctionalComponentService<T> request) throws Exception {
		
		return this.handleRequestSync(
					executorServiceIndex,
					new AbstractComponent.AbstractService<T>() {
						@Override
						public T call() throws Exception {
							return request.service();
						}
					}
				);
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
	public <T> void handleRequestAsync(int executorServiceIndex, AFunctionalComponentService request) throws Exception {

		this.handleRequestAsync(
				executorServiceIndex,
				new AbstractComponent.AbstractService<T>() {
					@Override
					public T call() throws Exception {
						request.service();
						return null;
					}
				});
	}

	@Override
	public void setComponentBehavior(ComponentBehavior behavior) throws Exception {
		this.componentBehavior = behavior;
	}

	@Override
	public void execute() throws Exception {
		super.execute();
		this.componentBehavior.run();
	}

}
