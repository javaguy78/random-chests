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

class MaxItemPrompt extends NumericPrompt {

    @Override
    protected Prompt acceptValidatedInput(ConversationContext context, Number number) {
        context.setSessionData(AddConversation.MAX_ITEMS, number.intValue());
        return new AllowDuplicatesPrompt();
    }

    @Override
    public String getPromptText(ConversationContext conversationContext) {
        return "What is the maximum number of items chest should ever give?";
    }

    @Override
    protected boolean isNumberValid(ConversationContext context, Number input) {
        int min = (Integer) context.getSessionData(AddConversation.MIN_ITEMS);
        return (input.intValue() >= min && input.intValue() <= 27);
    }

    @Override
    protected String getFailedValidationText(ConversationContext context, Number failedInput) {
        int min = (Integer) context.getSessionData(AddConversation.MIN_ITEMS);
        if (failedInput.intValue() < min) {
            return "Max items number must be larger than min items number";
        }

        return "Maximum number of items cannot exceed 27";
    }

    @Override
    protected String getInputNotNumericText(ConversationContext context, String invalidInput) {
        return "Please enter a valid number";
    }
}
