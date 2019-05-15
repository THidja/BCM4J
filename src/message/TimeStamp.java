package message;

import java.io.Serializable;
import java.sql.Timestamp;

public class TimeStamp implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private long time;
	private String timestamper;
	
	public TimeStamp(long time, String timestamper) {
		this.time = time;
		this.timestamper = timestamper;
	}
	
	public TimeStamp() {
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		this.time = timestamp.getTime();
		this.timestamper = timestamp.toString();
	}
	
	/**
	 * @author Felix, Tahar, Christian, Jonathan
	 * @return vrai si time different de 0 et timestamper n'est pas vide sinon faux
	 */
	public boolean isInitialised() {
		return (this.time != 0)  && (this.timestamper != null);
	}
	
	/**
	 * @author Felix, Tahar, Christian, Jonathan
	 * @return long time 
	 */
	public long getTime() {
		return time;
	}
	
	/**
	 * @author Felix, Tahar, Christian, Jonathan
	 * @param long time 
	 * Modifier le temps
	 */
	public void setTime(long time) {
		this.time = time;
	}
	
	/**
	 * @author Felix, Tahar, Christian, Jonathan
	 * @return String timestamper
	 */
	public String getTimestamper() {
		return timestamper;
	}
	
	/**
	 * @author Felix, Tahar, Christian, Jonathan
	 * @param String timestamper
	 * Modifier le timestamper
	 */
	public void setTimestamper(String timestamper) {
		this.timestamper = timestamper;
	}

}
