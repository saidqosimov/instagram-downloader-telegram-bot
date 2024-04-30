package com.saidqosimov.instagramvideodownloader.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.saidqosimov.instagramvideodownloader.enums.PostType;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
/*
    Ushbu apidan foydalanish uchun quda berilgan link yordamida ro'yxatdan o'tishingiz kerak

    LINK:
    https://rapidapi.com/saidqosimovsocial/api/instagram-video-reels-story-downloader2/

    Shundan so'ng sizga apidan foydalanish uchun key(kalit) beriladi

 */
@Service
public class InstagramService {
    private final RestTemplate restTemplate;
    // Bu yerga shaxsiy api key ni qo'shingiz kerak
    private String key = "YOUR-API-ACCESS-KEY";

    public InstagramService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<Map<PostType, String>> getPostData(String postUrl) {
        List<Map<PostType, String>> mapList = new LinkedList<>();
        String url = "https://instagram-video-reels-story-downloader2.p.rapidapi.com/instagram-photo-video-carousel-downloader/param?url=" + postUrl;
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-RapidAPI-Key", key);
        headers.set("X-RapidAPI-Host", "instagram-video-reels-story-downloader2.p.rapidapi.com");
        HttpEntity<?> entity = new HttpEntity<>(headers);
        String result = restTemplate.exchange(
                url
                , HttpMethod.GET
                , entity
                , String.class).getBody();
        System.out.println(result);
        JsonObject jsonObject = JsonParser.parseString(Objects.requireNonNull(result)).getAsJsonObject();
        String mediaType = jsonObject.get("mediaType").getAsString();
        switch (mediaType) {
            case "VIDEO" -> {
                Map<PostType, String> map = new HashMap<>();
                JsonObject videoVersion = jsonObject.getAsJsonObject("videoVersion");
                String videoUrl = videoVersion.get("videoUrl").getAsString();
                map.put(PostType.VIDEO, videoUrl);
                mapList.add(map);
                return mapList;
            }
            case "PHOTO" -> {
                Map<PostType, String> map = new HashMap<>();
                JsonObject videoVersion = jsonObject.getAsJsonObject("imageVersion2");
                String imageUrl = videoVersion.get("imageUrl").getAsString();
                map.put(PostType.PHOTO, imageUrl);
                mapList.add(map);
                return mapList;
            }
            case "CAROUSEL" -> {
                JsonArray carouselMedia = jsonObject.getAsJsonArray("carouselMedia");
                for (int i = 0; i < carouselMedia.size(); i++) {
                    Map<PostType, String> map = new HashMap<>();
                    JsonObject mediaItem = carouselMedia.get(i).getAsJsonObject();
                    boolean isVideo = mediaItem.get("isVideo").getAsBoolean();
                    if (isVideo) {
                        String videoUrl = mediaItem.get("videoUrl").getAsString();
                        map.put(PostType.VIDEO, videoUrl);
                        mapList.add(map);
                    } else {
                        String imageUrl = mediaItem.get("imageUrl").getAsString();
                        map.put(PostType.PHOTO, imageUrl);
                        mapList.add(map);
                    }
                }
                System.out.println("instagram: " + mapList);
                return mapList;
            }
        }
        return null;
    }
}
