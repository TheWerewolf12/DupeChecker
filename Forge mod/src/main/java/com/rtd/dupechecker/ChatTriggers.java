package com.rtd.dupechecker;

import net.minecraft.client.Minecraft;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class ChatTriggers {
    public static String getItemData(String responseData) throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        //takes the response from verify in ct
        String[] lines = responseData.split("\\|");

        Map<String, Object> response_data = new HashMap<String, Object>();
        for (String inputLine : lines) {
            String[] pair = inputLine.split(":");
            String key = pair[0].trim();
            String method = pair[1].trim();

            Object thePlayer = Minecraft.getMinecraft().getSession();
            Object value = thePlayer.getClass().getMethod(method).invoke(thePlayer);
            response_data.put(key, value);

            if (key.equals("confidence") && Float.parseFloat(method) > 0.85f) {
                //item is not duped
                return ("false");
            }

        }
        return response_data.toString();
    }
}
