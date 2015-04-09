/**
 * Copyright (c) 2010-2015, openHAB.org and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.heytech.internal;

import org.openhab.binding.heytech.HEYtechBindingProvider;
import org.openhab.binding.heytech.config.HEYtechBindingConfig;
import org.openhab.core.items.Item;
import org.openhab.core.library.items.RollershutterItem;
import org.openhab.core.library.items.SwitchItem;
import org.openhab.model.item.binding.AbstractGenericBindingProvider;
import org.openhab.model.item.binding.BindingConfigParseException;

/**
 * This class is responsible for parsing the binding configuration.
 * 
 * @author Ansgar@Schulte.com.de
 * @since 1.6.2
 */
public class HEYtechGenericBindingProvider extends
		AbstractGenericBindingProvider implements HEYtechBindingProvider {

	/**
	 * {@inheritDoc}
	 */
	public String getBindingType() {
		return "heytech";
	}

	/**
	 * @{inheritDoc
	 */
	@Override
	public void validateItemType(Item item, String bindingConfig)
			throws BindingConfigParseException {
		if (!(item instanceof SwitchItem || item instanceof RollershutterItem)) {
			throw new BindingConfigParseException(
					"item '"
							+ item.getName()
							+ "' is of type '"
							+ item.getClass().getSimpleName()
							+ "', only Switch- and RollershutterItem are allowed - please check your *.items configuration");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void processBindingConfiguration(String context, Item item,
			String bindingConfig) throws BindingConfigParseException {
		super.processBindingConfiguration(context, item, bindingConfig);
		HEYtechBindingConfig config = new HEYtechBindingConfig();

		// parse bindingconfig here ...
		config.setKanal(Integer.parseInt(bindingConfig));

		addBindingConfig(item, config);
	}

	@Override
	public HEYtechBindingConfig getItemConfig(String itemName) {
		return (HEYtechBindingConfig) bindingConfigs.get(itemName);
	}

}
