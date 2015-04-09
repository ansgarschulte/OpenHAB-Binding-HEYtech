package org.openhab.binding.heytech.config;

import org.openhab.core.binding.BindingConfig;

/**
 * This is a helper class holding binding specific configuration details
 * 
 * @author Ansgar@Schulte.com.de
 * @since 1.6.2
 */
public class HEYtechBindingConfig  implements BindingConfig {
	// put member fields here which holds the parsed values
	
	private int kanal;

	public int getKanal() {
		return kanal;
	}

	public void setKanal(int kanal) {
		this.kanal = kanal;
	}

}
