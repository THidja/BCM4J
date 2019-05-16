package bcm.extend;

public abstract class AbstractComponent
	   extends fr.sorbonne_u.components.AbstractComponent
	   implements ComponentI
{
	
	private ComponentScenario componentBehavior;
	protected final int	executorIndex = -1 ;

	public AbstractComponent(int nbThreads, int nbSchedulableThreads) {
		super(nbThreads, nbSchedulableThreads);
		// TODO Auto-generated constructor stub
	}


	public AbstractComponent(String reflectionInboundPortURI, int nbThreads, int nbSchedulableThreads) {
		super(reflectionInboundPortURI, nbThreads, nbSchedulableThreads);
		// TODO Auto-generated constructor stub
	}


	@Override
	public <T> T handleRequestSync(SFunctionalComponentService<T> request) throws Exception {

		return this.handleRequestSync(executorIndex,
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
