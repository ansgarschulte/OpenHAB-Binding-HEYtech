/**
 * Copyright (c) 2010-2015, openHAB.org and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.heytech;

import org.openhab.binding.heytech.config.HEYtechBindingConfig;
import org.openhab.core.binding.BindingProvider;

/**
 * @author Ansgar@Schulte.com.de
 * @since 1.6.2
 */
public interface HEYtechBindingProvider extends BindingProvider {

	public HEYtechBindingConfig getItemConfig(String itemName);
}
