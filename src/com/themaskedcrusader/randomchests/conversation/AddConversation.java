/*
 * Copyright 2013 Topher Donovan (themaskedcrusader.com)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.themaskedcrusader.randomchests.conversation;

public class AddConversation {

    public static final String CHEST = "original-chest";
    public static final String IS_KIT = "is-kit";
    public static final String KIT_NAME = "kit-name";
    public static final String PART_OF_KIT = "part-kit";
    public static final String CONTENTS = "contents";
    public static final String MIN_ITEMS = "min-items";
    public static final String EMPTY_PERCENT = "empty-percent";
    public static final String MAX_ITEMS = "max-items";
    public static final String DUPLICATES = "allow-duplicates";
    public static final String RADIUS = "player-radius";
    public static final String RESPAWN_SECONDS = "respawn-seconds";

    public static final String PLAYER = "player-object";
}

// Ask player a bunch of questions (Use conversation api)
// 1. is chest the definition of a kit
//   1.1 if yes, name kit and goto 3
// 2. Are contents part of a kit
//   2.1 if so, assign kit name and exit
// 3. Are contents random or static
// 4. If Random - Minimum number of items in chest
//   4.1 If Min number == 0, percent of the time that chest is empty
// 5. If Random - Maximum number of items in chest
// 6. If Random - allow duplicates in chest?
// 7. Minimum radius to check for players on respawn
// 8. Seconds after which to respawn //

