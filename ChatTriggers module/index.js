const API_calls = Java.type("com.rtd.dupechecker.ChatTriggers");
import axios from "axios";

// Note: the ONLY part accessed from the .jar file is the getItemData function.
// All other functions are not being run by this module.

function API(argument) {
    return "https://dupechecker.pythonanywhere.com/api"+argument;
};

function verify(item_uuid) {
    axios.get(API("/verify?uuid="+item_uuid), {
        headers: {
          "User-Agent": "Mozilla/5.0 (ChatTriggers)"
        }
      })
    .then(response => {
        item_data = API_calls.getItemData(`${response.data}`);
        update_database(item_data);
        return 'false';
    })
    .catch(error => {
        if (error.isAxiosError) {
        print("Verify error:"+error.code + ": " + error.response.data);
        } else {
        print("Verify error:"+error.message);
        return 'true';
        }
    })
};

function update_database(item_data) {
    axios.post(API("/data"), {
        headers: {
            "User-Agent": "Mozilla/5.0 (ChatTriggers)"
        },
        body: {
            content: item_data,
        },
        JSON: false
    })
    .then(response => {})
    .catch(error => {
        if (error.isAxiosError) {
        print(error.code + ": " + error.response.data);
        } else {
        print(error.message);
        }
    })
}

function check_hand(silent, item_uuid) {
    //silent mode will scan your api & add item uuids to database (to expand accuracy)
    if (!silent) {
        if (item_uuid == null) {
            ChatLib.chat("§3Item does not contain a uuid §a(Not duped)");
            verify('');
            return;
        }
    }
    
    duped = (verify(item_uuid) == true);
    if (!silent) {
        if (!duped) {
            ChatLib.chat("§aThis item does not appear to be duped.")
        } else {
            ChatLib.chat("§4This item appears to be duped.")
        }
    }
};

register("command", (event) => {
    try {
        const item_uuid = Player.getHeldItem().getItemNBT().getTag("tag").getTag("ExtraAttributes").getTag("uuid");
        check_hand(false, item_uuid) //checks hand item
    } catch (error) {
        ChatLib.chat("§aThis item does not appear to be duped.");
        verify('');
    }
    
  }).setName("dupecheck").setAliases("checkdupe", "checkduped");

register("worldLoad", () => {
    ChatLib.chat("§aDupechecker loaded! verifying hand...")
    check_hand(false, `${Player.getHeldItem()}`)
});
