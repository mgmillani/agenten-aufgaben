package de.dailab.aot.sose2012.user;

import java.io.Serializable;

import de.dailab.aot.sose2012.effectors.Heating;
import de.dailab.aot.sose2012.sensors.TemperatureSensor;
import de.dailab.aot.sose2012.sensors.Window;
import de.dailab.jiactng.agentcore.AbstractAgentBean;
import de.dailab.jiactng.agentcore.action.Action;
import de.dailab.jiactng.agentcore.action.ActionResult;
import de.dailab.jiactng.agentcore.environment.ResultReceiver;

public final class HeatingControl extends AbstractAgentBean implements ResultReceiver {

	/*@Override
	public void doStart() throws Exception {
		super.doStart();
	}

	@Override
	public void doStop() throws Exception {
	}*/
	
	protected Double temperature   = null;
	protected Integer stateHeating = Heating.INITIAL;
	protected Boolean stateWindow  = Window.INITIAL;
	
	@Override
	public void receiveResult(final ActionResult result) {

		final String name = result.getAction().getName();
		if (Heating.ACTION_GET_HEATING_STATE.equals(name)) {
			try {
				this.stateHeating = (Integer) result.getResults()[0];
			}
			catch (final Exception e) {
				this.log.error("could not update heating state: " + e.getMessage());
			}
		}
		else if (Window.ACTION_GET_WINDOW_STATE.equals(name)) {
			try {
				this.stateWindow = (Boolean) result.getResults()[0];
			}
			catch (final Exception e) {
				this.log.error("could not update window state: " + e.getMessage());
			}
		}
		else if (CurrentTemperature.ACTION_GET_TEMPERATURE.equals(name)){
			try {
				this.temperature = (Double) result.getResults()[0];
			}
			catch (final Exception e) {
				this.log.error("could not update temperature: " + e.getMessage());
			}
		}
	}
	
	protected void updateVariables(){
		final Action heating = this.retrieveAction(Heating.ACTION_GET_HEATING_STATE);
		if (heating != null) {
			this.invoke(heating, new Serializable[] {}, this);
		}
		final Action window = this.retrieveAction(Window.ACTION_GET_WINDOW_STATE);
		if (window != null) {
			this.invoke(window, new Serializable[] {}, this);
		}
		final Action temperature = this.retrieveAction(CurrentTemperature.ACTION_GET_TEMPERATURE);
		if (temperature != null) {
			this.invoke(temperature, new Serializable[] {}, this);
		}
	}
	
	@Override
	public void execute(){
		this.updateVariables();
		
		this.log.info("Temperature: " + this.temperature);
		this.log.info("Window:      " + this.stateWindow);
		this.log.info("Heating:     " + this.stateHeating);
		
		//Burn, baby, burn!
		final Action heating = this.retrieveAction(Heating.ACTION_UPDATE_STATE);
		if (heating != null) {
			
			// default heating setting
			int adjustedHeating = 3;
			
			if(this.temperature != null) {
				
				final Double TMIN = 0.0;
				final Double TMAX = 30.0;
				
				// calculates the next probable temperature
				Integer w = this.stateWindow ? 2 : 1;
				Double l = -0.07 * w * (this.temperature - TMIN);
				Double g = 0.11 * this.stateHeating * (TMAX - this.temperature);
				Double nextTemperature = l + g + this.temperature;
				
				// calculates the heating setting in order to achieve 21 degrees
				Double targetHeating;
				
				targetHeating = 21 + 0.07*w*(nextTemperature - TMIN) - nextTemperature;
				targetHeating = targetHeating / (0.11 * (TMAX-nextTemperature));
				adjustedHeating = targetHeating.intValue();
				
				if(adjustedHeating < 0 || adjustedHeating > 5) {
					adjustedHeating = adjustedHeating > 5 ? 5 : 0;
				}
			}
			
			this.invoke(heating, new Serializable[] {adjustedHeating}, this);
		}
	}

}
