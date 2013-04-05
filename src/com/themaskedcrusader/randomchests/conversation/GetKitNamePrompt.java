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

import com.themaskedcrusader.randomchests.data.KitChests;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;

class GetKitNamePrompt extends StringPrompt {
    private static final String INVALID_KIT = "invalid-kit";
    private static final String ALREADY_USED = "already-used";

    @Override
    public String getPromptText(ConversationContext context) {
        String toReturn = "";
        if (context.getSessionData(INVALID_KIT) != null && (Boolean) context.getSessionData(INVALID_KIT)) {
            toReturn += "Invalid Kit Name, Please Try Again:\n";
            context.setSessionData(INVALID_KIT, false);
        }

        if (context.getSessionData(ALREADY_USED) != null && (Boolean) context.getSessionData(ALREADY_USED)) {
            toReturn += "Kit Name is already in use:\n";
            context.setSessionData(ALREADY_USED, false);
        }

        toReturn += "Please specify kit name:";
        return toReturn;
    }

    @Override
    public Prompt acceptInput(ConversationContext context, String s) {
        s = s.toUpperCase();

        // Validate kit name contains only valid characters
        if (!KitChests.isValidKitName(s)) {
            context.setSessionData(INVALID_KIT, true);
            return this;
        }

        // if it's a kit, save the kit name and proceed
        if (context.getSessionData(AddConversation.IS_KIT) != null &&
                (Boolean) context.getSessionData(AddConversation.IS_KIT)) {
            if (KitChests.isKit(s)) {
                context.setSessionData(ALREADY_USED, true);
                return this;
            } else {
                context.setSessionData(AddConversation.KIT_NAME, s);
                return new RandomStaticContentsPrompt();
            }
        }

        // if it's part of a kit, validate that the kit exists and proceed
        if (context.getSessionData(AddConversation.PART_OF_KIT) != null &&
                (Boolean) context.getSessionData(AddConversation.PART_OF_KIT)) {
            if (!KitChests.isKit(s)) {
                context.setSessionData(INVALID_KIT, true);
                return this;
            } else {
                context.setSessionData(AddConversation.KIT_NAME, s);
                AddChestWorker.addChest(context);
            }
        }

    return END_OF_CONVERSATION; // the conversation will never get to this point
    }
}
