/**
 * Copyright 2013 작은광명
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dvdprime.mobile.android.provider;

import de.greenrobot.event.EventBus;

/**
 * Event Bus Provider
 * 
 * @author 작은광명
 * 
 */
public class EventBusProvider {
    private static EventBus eventBus = null;

    public static EventBus getInstance() {
        if (eventBus == null) {
            EventBus.clearCaches();
            EventBus.clearSkipMethodNameVerifications();

            eventBus = new EventBus();
        }

        return eventBus;
    }

    private EventBusProvider() {
        // No instances.
    }
}
