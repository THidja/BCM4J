package message;

import java.io.Serializable;


public class Message implements MessageI {
	
	private static int i = 0;
	private static final long serialVersionUID = 1L;
	
	private String URI;
	private TimeStamp timeStamp;
	private Properties properties;
	private Serializable content;
	
	
	public Message(Serializable obj) {
		i++;
		this.URI = String.format("Message n°%d", i);
		this.timeStamp = new TimeStamp();
		this.properties = new Properties();
		this.content = obj;
	}
	
	/**
	 * @author Felix, Tahar, Christian, Jonathan
	 * @return URI
	 */
	@Override
	public String getURI() {
		return URI;
	}
	
	/**
	 * @author Felix, Tahar, Christian, Jonathan
	 * @return timeStamp
	 */
	@Override
	public TimeStamp getTimeStamp() {
		return timeStamp;
	}
	
	/**
	 * @author Felix, Tahar, Christian, Jonathan
	 * @return properties
	 */
	@Override
	public Properties getProperties() {
		return properties;
	}

	/**
	 * @author Felix, Tahar, Christian, Jonathan
	 * @return content
	 */
	@Override
	public Serializable getPayload() {
		return content;
	}
	
	/**
	 * @author Felix, Tahar, Christian, Jonathan
	 * @return l'uri et le content
	 */
	@Override
	public String toString() {
		return String.format("message(uri:%s, content:%s)",
							  this.URI,
							  this.content.toString());
	}
	
	/**
	 * @author Felix, Tahar, Christian, Jonathan
	 * @param Object o
	 * @return faux si o est null ou du type subscriber sinon on cree un abonné
	 * @return faux si l'abonné n'existe pas sinon vrai
	 */
	@Override
	public boolean equals(Object o) {
		
		if(o == null) return false;
		if(!(o instanceof MessageI)) return false;
		MessageI m = (MessageI) o;
		if(m.getURI().equals(this.getURI())) return true;
		
		return false;
	}
	
	/**
	 * @author Felix, Tahar, Christian, Jonathan
	 * @return Integer URI
	 */
	@Override
	public int hashCode() {
		return this.getURI().hashCode();
	}

}
