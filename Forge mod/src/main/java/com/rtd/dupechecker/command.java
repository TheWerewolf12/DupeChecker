package com.rtd.dupechecker;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import java.util.List;

public class command extends CommandBase {

    /* ---------------------------------------------------- */
    /*                   IMPORTANT NOTE:                    */
    /*                                                      */
    /* This module is not accessed while using ChatTriggers */
    /* ChatTriggers only runs the ChatTriggers.getItemData  */
    /* Function and does not touch any of the code over here*/
    /* ---------------------------------------------------- */

    private final List<String> aliases = Lists.newArrayList(loader.MODID, "dc", "isduped", "checkhand");

    private final String message1 = "§aThis item does not appear to be duped.";
    private final String message2 = "§4This item appears to be duped.";
    private final String message3 = "§4Your hand cannot be duped!";

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
    }


    @Override
    public String getCommandName() {
        return "dupecheck";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return null;
    }

    public List<String> getAliases() {
        return aliases;
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        EntityPlayer player = (EntityPlayer) sender;
        ItemStack heldItem = player.getHeldItem();

        try {
            if (heldItem != null && heldItem.getAttributeModifiers() != null) {
                if (API_calls.verify(heldItem.getAttributeModifiers().toString(), false)) {
                    sender.addChatMessage(new ChatComponentText(message1));
                } else {
                    sender.addChatMessage(new ChatComponentText(message2));
                }
            } else if ( heldItem == null) {
                sender.addChatMessage(new ChatComponentText(message3));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
