/**
 * 
 */
package com.example.MoodyUtilities;

import android.os.Build;

/**
 * @author S�rgio Andaluz Ramos
 *
 */
public final class AndroidCheckUtils {
	
	public static final Boolean IsJellyBeanOrLater(){ return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN); }
	
}
