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

class MinItemPrompt extends NumericPrompt {
    @Override
    protected Prompt acceptValidatedInput(ConversationContext context, Number number) {
        context.setSessionData(AddConversation.MIN_ITEMS, number.intValue());
        if (number.intValue() == 0) {
            return new ChanceOfEmptyPrompt();
        } else {
            return new MaxItemPrompt();
        }
    }

    @Override
    public String getPromptText(ConversationContext conversationContext) {
        return "Enter the minimum number of items the chest will ever give:";
    }

    @Override
    protected boolean isNumberValid(ConversationContext context, Number input) {
        int number = input.intValue();
        return (number >= 0 && number < 27);
    }

    @Override
    protected String getFailedValidationText(ConversationContext context, Number invalidInput) {
        return "Please enter a number between 0 and 27";
    }
}
