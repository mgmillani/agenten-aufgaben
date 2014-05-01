package de.dailab.aot.sose2012.user;

import de.dailab.aot.sose2012.ontology.Temperature;
import de.dailab.jiactng.agentcore.action.AbstractMethodExposingBean;
import de.dailab.jiactng.agentcore.action.scope.ActionScope;

public final class CurrentTemperature extends AbstractMethodExposingBean {
	
	private Double temperature = null;
	
	/**
	 * Returns current temperature, which is a Double
	 */
	public static final String ACTION_GET_TEMPERATURE = "CurrentTemperature.getTemperature";

	@Expose(name = ACTION_GET_TEMPERATURE, scope = ActionScope.AGENT)
	public Double getTemperature(){
		return this.temperature;
	}
	
	/**
	 * Sets current temperature, which is a Double
	 */
	public static final String ACTION_SET_TEMPERATURE = "CurrentTemperature.setTemperature";

	@Expose(name = ACTION_SET_TEMPERATURE, scope = ActionScope.AGENT)
	public void setTemperature(Temperature temperature){
		this.temperature = temperature.getValue();
	}
}