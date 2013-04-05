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

import com.themaskedcrusader.bukkit.chest.SmartChest;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class CreateConversation {
    private final ConversationFactory factory;

    public CreateConversation(JavaPlugin plugin) {
        factory = new ConversationFactory(plugin)
            .withModality(true)
            .withPrefix(new CreateRandomChestPrefix())
            .withFirstPrompt(new IsThisAKitPrompt())
            .withEscapeSequence("/cancel")
            .withTimeout(30);
    }

    public void doConversation(Player player, SmartChest chest) {
        Conversation conversation = factory.buildConversation(player);
        ConversationContext context = conversation.getContext();
        context.setSessionData(AddConversation.CHEST, chest);
        context.setSessionData(AddConversation.PLAYER, player);
        conversation.begin();
    }
}
