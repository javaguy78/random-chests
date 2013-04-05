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

import org.bukkit.conversations.BooleanPrompt;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;

class RandomStaticContentsPrompt extends BooleanPrompt {
    @Override
    protected Prompt acceptValidatedInput(ConversationContext context, boolean b) {
        context.setSessionData(AddConversation.CONTENTS, b);
        if (b) {
            return new MinItemPrompt();
        } else {
            AddChestWorker.addChest(context);
            return END_OF_CONVERSATION;
        }
    }

    @Override
    public String getPromptText(ConversationContext conversationContext) {
        return "Are chest contents random?";
    }
}
