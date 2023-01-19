package com.expert.fire.Common;

import com.expert.fire.Models.Dishes;
import com.expert.fire.Models.PersonalInfo;
import com.expert.fire.Models.Translation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Common {


    public static List<String> getMap(List<String> slice, String keyword) {
        boolean isExisting = false;
        List<String> strList = new ArrayList<>();
        int currentIndex = 0;

        String[] strSlice = keyword.split(",");

        for (String str : slice) {

            for (String str2 : strSlice) {
                currentIndex = 0;
                if (str.contains(str2)) {
                    if (currentIndex > 0) {
                        if (str.contains(str2) && str.contains(strSlice[currentIndex - 1])) {
                            strList.add(str);
                        }
                    } else {
                        strList.add(str);
                    }
                    currentIndex++;
                }

            }

        }

        return strList;
    }

    public static Map<String, Object> getYoutubeBody(String watchPath, String watchID) {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> contextMap = new HashMap<>();
        Map<String, Object> clientMap = new HashMap<>();
        Map<String, Object> mainAppWebInfo = new HashMap<>();
        clientMap.put("hl", "en");
        clientMap.put("clientName", "WEB");
        clientMap.put("clientVersion", "2.20210721.00.00");
        mainAppWebInfo.put("graftUrl", watchPath);
        clientMap.put("mainAppWebInfo", mainAppWebInfo);

        contextMap.put("client", clientMap);
        map.put("context", contextMap);
        map.put("videoId", watchID);
        return map;
    }

    public static Map<String, Object> convertDishToMap(Dishes dishes) {
        Map<String, Object> map = new HashMap<>();

        map.put("description", dishes.getDescription());
        map.put("dish", dishes.getDish());
        map.put("ingredients", dishes.getIngredients());
        map.put("instructions", dishes.getInstructions());
        map.put("origIngredients", dishes.getOrigIngredients());
        map.put("videoURL", dishes.getVideoURL());

        return map;
    }

    public static Map<String, Object> convertPersonalInfoToMap(PersonalInfo info) {
        Map<String, Object> map = new HashMap<>();
        map.put("name", info.getName());
        map.put("email", info.getEmail());
        map.put("phoneNum", info.getPhoneNum());

        return map;
    }

    public static Map<String, Object> convertTranslationToMap(Translation translation){
        Map<String, Object> map = new HashMap<>();
        map.put("sourceLang",translation.getSourceLang());
        map.put("targetLang",translation.getTargetLang());
        map.put("text",translation.getText());

        return map;
    }

    public static Map<String,String> systemTranslations(){
        Map<String,String> map = new HashMap<>();
        map.put("instructions","mga tagubilin");
        map.put("ingredients","mga sangkap");
        map.put("description","paglalarawan");
        map.put("favorites","mga paborito");
        map.put("user","gumagamit");
        map.put("generate","bumuo");

        return map;
    }
}
