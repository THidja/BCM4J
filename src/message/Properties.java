package message;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Properties implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Map<String,Object> Prop = new HashMap<>();
	
//setter
	/**
	 * @author Felix, Tahar, Christian, Jonathan
	 * @param String name
	 * @param boolean v
	 */
	public void putProp(String name, boolean v) {
		this.Prop.put(name, v);
	}
	
	/**
	 * @author Felix, Tahar, Christian, Jonathan
	 * @param String name
	 * @param byte v
	 */
	public void putProp(String name, byte v) {
		this.Prop.put(name, v);
	}
	
	/**
	 * @author Felix, Tahar, Christian, Jonathan
	 * @param String name
	 * @param char v
	 */
	public void putProp(String name, char v) {
		this.Prop.put(name, v);
	} 
	
	/**
	 * @author Felix, Tahar, Christian, Jonathan
	 * @param String name
	 * @param double v
	 */
	public void putProp(String name, double v) {
		this.Prop.put(name, v);
	}
	 
	/**
	 * @author Felix, Tahar, Christian, Jonathan
	 * @param String name
	 * @param float v
	 */
	public void putProp(String name, float v) {
		this.Prop.put(name, v);
	}
	 
	/**
	 * @author Felix, Tahar, Christian, Jonathan
	 * @param String name
	 * @param int v
	 */
	public void putProp(String name, int v) {
		this.Prop.put(name, v);
	}
	
	/**
	 * @author Felix, Tahar, Christian, Jonathan
	 * @param String name
	 * @param long v
	 */
	public void putProp(String name, long v) {
		this.Prop.put(name, v);
	}
	
	/**
	 * @author Felix, Tahar, Christian, Jonathan
	 * @param String name
	 * @param short v
	 */
	public void putProp(String name, short v) {
		this.Prop.put(name, v);
	}
	
	/**
	 * @author Felix, Tahar, Christian, Jonathan
	 * @param String name
	 * @param String v
	 */
	public void putProp(String name, String v) {
		this.Prop.put(name, v);
	}

// getters
	
	/**
	 * @author Felix, Tahar, Christian, Jonathan
	 * @param String name
	 * @return value boolean
	 */
	public boolean getBooleanProp(String name) {
		return (boolean) this.Prop.get(name); 
	}
	
	/**
	 * @author Felix, Tahar, Christian, Jonathan
	 * @param String name
	 * @return value Byte
	 */
	public Byte getByteProp(String name) {
		return (Byte) this.Prop.get(name);
	}

	/**
	 * @author Felix, Tahar, Christian, Jonathan
	 * @param String name
	 * @return value char
	 */
	public char getCharProp(String name) {
		return (char) this.Prop.get(name);
	}

	/**
	 * @author Felix, Tahar, Christian, Jonathan
	 * @param String name
	 * @return value double
	 */
	public double getDoubleProp(String name) {
		return (double) this.Prop.get(name);
	}

	/**
	 * @author Felix, Tahar, Christian, Jonathan
	 * @param String name
	 * @return value float
	 */
	public float getFloatProp(String name) {
		return (float) this.Prop.get(name);
	}

	/**
	 * @author Felix, Tahar, Christian, Jonathan
	 * @param String name
	 * @return value int
	 */
	public int getIntProp(String name) {
		return (int) this.Prop.get(name);
	}

	/**
	 * @author Felix, Tahar, Christian, Jonathan
	 * @param String name
	 * @return value long
	 */
	public long getLongProp(String name) {
		return (long) this.Prop.get(name);
	}

	/**
	 * @author Felix, Tahar, Christian, Jonathan
	 * @param String name
	 * @return value short
	 */
	public short getShortProp(String name) {
		return (short) this.Prop.get(name);
	}

	/**
	 * @author Felix, Tahar, Christian, Jonathan
	 * @param String name
	 * @return value String
	 */
	public String getStringProp(String name) {
		return (String) this.Prop.get(name);
	}

}
