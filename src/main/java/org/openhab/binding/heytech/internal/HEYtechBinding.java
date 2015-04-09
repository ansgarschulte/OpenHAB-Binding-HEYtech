/**
 * Copyright (c) 2010-2015, openHAB.org and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.heytech.internal;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.openhab.binding.heytech.HEYtechBindingProvider;
import org.openhab.binding.heytech.config.HEYtechBindingConfig;
import org.openhab.core.binding.AbstractActiveBinding;
import org.openhab.core.library.types.OnOffType;
import org.openhab.core.library.types.StopMoveType;
import org.openhab.core.library.types.UpDownType;
import org.openhab.core.types.Command;
import org.openhab.core.types.State;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implement this class if you are going create an actively polling service like
 * querying a Website/Device.
 * 
 * @author Ansgar@Schulte.com.de
 * @since 1.6.2
 */
public class HEYtechBinding extends
		AbstractActiveBinding<HEYtechBindingProvider> {

	private static final Logger logger = LoggerFactory
			.getLogger(HEYtechBinding.class);

	/**
	 * The BundleContext. This is only valid when the bundle is ACTIVE. It is
	 * set in the activate() method and must not be accessed anymore once the
	 * deactivate() method was called or before activate() was called.
	 */
	private BundleContext bundleContext;

	/**
	 * the refresh interval which is used to poll values from the heytech server
	 * (optional, defaults to 60000ms)
	 */
	private long refreshInterval = 60000;

	/**
	 * the Ip of the HEYtech Lanadapter
	 */
	private String lanAdapterIp = null;

	private HEYtechTelNetHelper telNetHelper = null;

	public HEYtechBinding() {
	}

	/**
	 * Called by the SCR to activate the component with its configuration read
	 * from CAS
	 * 
	 * @param bundleContext
	 *            BundleContext of the Bundle that defines this component
	 * @param configuration
	 *            Configuration properties for this component obtained from the
	 *            ConfigAdmin service
	 */
	public void activate(final BundleContext bundleContext,
			final Map<String, Object> configuration) {
		this.bundleContext = bundleContext;

		updateConfig(configuration);
	}

	private void updateConfig(final Map<String, Object> configuration) {
		String refreshIntervalString = (String) configuration.get("refresh");
		if (StringUtils.isNotBlank(refreshIntervalString)) {
			refreshInterval = Long.parseLong(refreshIntervalString);
		}
		String host = (String) configuration.get("host");
		if (StringUtils.isNotBlank(host)) {
			lanAdapterIp = host;
			telNetHelper = new HEYtechTelNetHelper(lanAdapterIp);
		}

		setProperlyConfigured(true);
	}

	/**
	 * Called by the SCR when the configuration of a binding has been changed
	 * through the ConfigAdmin service.
	 * 
	 * @param configuration
	 *            Updated configuration properties
	 */
	public void modified(final Map<String, Object> configuration) {
		// update the internal configuration accordingly
		updateConfig(configuration);
	}

	/**
	 * Called by the SCR to deactivate the component when either the
	 * configuration is removed or mandatory references are no longer satisfied
	 * or the component has simply been stopped.
	 * 
	 * @param reason
	 *            Reason code for the deactivation:<br>
	 *            <ul>
	 *            <li>0 – Unspecified
	 *            <li>1 – The component was disabled
	 *            <li>2 – A reference became unsatisfied
	 *            <li>3 – A configuration was changed
	 *            <li>4 – A configuration was deleted
	 *            <li>5 – The component was disposed
	 *            <li>6 – The bundle was stopped
	 *            </ul>
	 */
	public void deactivate(final int reason) {
		this.bundleContext = null;
		// deallocate resources here that are no longer needed and
		// should be reset when activating this binding again
	}

	/**
	 * @{inheritDoc
	 */
	@Override
	protected long getRefreshInterval() {
		return refreshInterval;
	}

	/**
	 * @{inheritDoc
	 */
	@Override
	protected String getName() {
		return "heytech Refresh Service";
	}

	/**
	 * @{inheritDoc
	 */
	@Override
	protected void execute() {
		// the frequently executed code (polling) goes here ...
		logger.debug("execute() method is called!");
	}

	/**
	 * @{inheritDoc
	 */
	@Override
	protected void internalReceiveCommand(String itemName, Command command) {
		// the code being executed when a command was sent on the openHAB
		// event bus goes here. This method is only called if one of the
		// BindingProviders provide a binding for the given 'itemName'.
		logger.debug("internalReceiveCommand({},{}) is called!", itemName,
				command);

		HEYtechBindingConfig configForItemName = getConfigForItemName(itemName);
		int kanal = configForItemName.getKanal();

		if (command instanceof org.openhab.core.library.types.OnOffType) {

			if (((org.openhab.core.library.types.OnOffType) command)
					.equals(OnOffType.ON)) {

				logger.debug(
						"HEYtech ON internalReceiveCommand({},{}) is called!",
						itemName, command);

				telNetHelper.openShutter(kanal);

			} else if (((org.openhab.core.library.types.OnOffType) command)
					.equals(OnOffType.OFF)) {

				logger.debug(
						"HEYtech OFF internalReceiveCommand({},{}) is called!",
						itemName, command);

				telNetHelper.closeShutter(kanal);
			}

		} else if (command instanceof org.openhab.core.library.types.UpDownType) {
			if (command.equals(UpDownType.DOWN)) {
				logger.debug(
						"HEYtech DOWN internalReceiveCommand({},{}) is called!",
						itemName, command);
				telNetHelper.closeShutter(kanal);
			} else if (command.equals(UpDownType.UP)) {
				logger.debug(
						"HEYtech UP internalReceiveCommand({},{}) is called!",
						itemName, command);
				telNetHelper.openShutter(kanal);
			}
		} else if (command instanceof org.openhab.core.library.types.StopMoveType) {
			if (command.equals(StopMoveType.STOP)) {
				logger.debug(
						"HEYtech STOP internalReceiveCommand({},{}) is called!",
						itemName, command);
				telNetHelper.stopShutter(kanal);
			}
		}
	}

	private HEYtechBindingConfig getConfigForItemName(String itemName) {
		for (HEYtechBindingProvider provider : this.providers) {
			if (provider.getItemConfig(itemName) != null) {
				return provider.getItemConfig(itemName);
			}
		}
		return null;
	}

	/**
	 * @{inheritDoc
	 */
	@Override
	protected void internalReceiveUpdate(String itemName, State newState) {
		// the code being executed when a state was sent on the openHAB
		// event bus goes here. This method is only called if one of the
		// BindingProviders provide a binding for the given 'itemName'.
		logger.debug("internalReceiveUpdate({},{}) is called!", itemName,
				newState);
	}

}
