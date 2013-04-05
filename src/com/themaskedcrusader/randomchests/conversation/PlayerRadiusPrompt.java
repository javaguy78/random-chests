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

import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.NumericPrompt;
import org.bukkit.conversations.Prompt;

class PlayerRadiusPrompt extends NumericPrompt{
    @Override
    protected Prompt acceptValidatedInput(ConversationContext context, Number number) {
        context.setSessionData(AddConversation.RADIUS, number.intValue());
        AddChestWorker.addChest(context);
        return END_OF_CONVERSATION;
    }

    @Override
    public String getPromptText(ConversationContext conversationContext) {
        return "Enter Radius to check for players when respawning:";
    }

    @Override
    protected boolean isNumberValid(ConversationContext context, Number input) {
        return (input.intValue() > 0 && input.intValue() <= 50);
    }

    @Override
    protected String getFailedValidationText(ConversationContext context, String invalidInput) {
        return "Please choose a distance less than 50 blocks";
    }
}
