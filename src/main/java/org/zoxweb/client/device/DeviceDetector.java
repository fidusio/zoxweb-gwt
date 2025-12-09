/*
 * Copyright (c) 2012-2014 ZoxWeb.com LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.zoxweb.client.device;

import com.google.gwt.user.client.Window;

/**
 * Utility class for detecting device type at runtime.
 * Uses user agent string and screen dimensions.
 */
public class DeviceDetector {

    private static DeviceType cachedDeviceType = null;
    private static final int MOBILE_MAX_WIDTH = 768;
    private static final int TABLET_MAX_WIDTH = 1024;

    private DeviceDetector() {
    }

    /**
     * Detect the current device type.
     * Results are cached for performance.
     */
    public static DeviceType detect() {
        if (cachedDeviceType == null) {
            cachedDeviceType = detectInternal();
        }
        return cachedDeviceType;
    }

    /**
     * Force re-detection of device type.
     * Useful after orientation changes.
     */
    public static DeviceType reDetect() {
        cachedDeviceType = null;
        return detect();
    }

    /**
     * Check if current device is mobile or tablet.
     */
    public static boolean isMobile() {
        return detect().isMobileOrTablet();
    }

    /**
     * Check if current device is desktop.
     */
    public static boolean isDesktop() {
        return detect().isDesktop();
    }

    /**
     * Get the current screen width.
     */
    public static int getScreenWidth() {
        return Window.getClientWidth();
    }

    /**
     * Get the current screen height.
     */
    public static int getScreenHeight() {
        return Window.getClientHeight();
    }

    private static DeviceType detectInternal() {
        String userAgent = getUserAgent().toLowerCase();

        // Check for mobile devices first
        if (isMobileUserAgent(userAgent)) {
            // Distinguish between phone and tablet
            if (isTabletUserAgent(userAgent) || getScreenWidth() > MOBILE_MAX_WIDTH) {
                return DeviceType.TABLET;
            }
            return DeviceType.MOBILE;
        }

        // Check screen size as fallback
        int width = getScreenWidth();
        if (width <= MOBILE_MAX_WIDTH) {
            return DeviceType.MOBILE;
        } else if (width <= TABLET_MAX_WIDTH && hasTouchSupport()) {
            return DeviceType.TABLET;
        }

        return DeviceType.DESKTOP;
    }

    private static boolean isMobileUserAgent(String userAgent) {
        return userAgent.contains("mobile") ||
               userAgent.contains("android") ||
               userAgent.contains("iphone") ||
               userAgent.contains("ipod") ||
               userAgent.contains("blackberry") ||
               userAgent.contains("windows phone") ||
               userAgent.contains("opera mini") ||
               userAgent.contains("opera mobi");
    }

    private static boolean isTabletUserAgent(String userAgent) {
        return userAgent.contains("ipad") ||
               (userAgent.contains("android") && !userAgent.contains("mobile")) ||
               userAgent.contains("tablet");
    }

    /**
     * Get the browser user agent string.
     */
    private static native String getUserAgent() /*-{
        return $wnd.navigator.userAgent || '';
    }-*/;

    /**
     * Check if the device supports touch events.
     */
    private static native boolean hasTouchSupport() /*-{
        return 'ontouchstart' in $wnd ||
               $wnd.navigator.maxTouchPoints > 0 ||
               $wnd.navigator.msMaxTouchPoints > 0;
    }-*/;
}
