package com.example.pikachu;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
// import com.google.gson.reflect.TypeToken;
import com.google.gson.reflect.TypeToken;

@RestController
@CrossOrigin(origins = "http://localhost:8080")
public class PikachuController {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @CrossOrigin(origins = "http://localhost:8080")
    @GetMapping("/getPikachu")
   public List<Notes> getPikachu() {
    System.out.println("getPikachuReceiveCheck");
    String json = redisTemplate.opsForValue().get("notes").toString();
    System.out.println("getPikachuReceiveCheck2");
    List<Notes> pikachu = new Gson().fromJson(json, new TypeToken<List<Notes>>(){}.getType());
    System.out.println("getPikachuReceiveCheck3" + pikachu);
    // System.out.println("GET成功"+pikachu);
    return pikachu;
    }

    @MessageMapping("/postPikachu")
    @SendTo("/topic/greetings")
    public String addPikachu(String pikachu) {
        System.out.println("POST成功" + pikachu);
        // Thread.sleep(1000);
        String json = new Gson().toJson(pikachu);
       redisTemplate.opsForValue().set("notes", json);
       return pikachu;
    }

    @MessageMapping("/postNoteContents")
    @SendTo("/topic/inputNotes")
    public String addNoteContents(String noteContent) throws InterruptedException {
        try {
            // String json = new Gson().toJson(noteContent);
            // redisTemplate.opsForValue().set("notes", json);
            return noteContent;
        } catch (Exception e) {
            return "error";
        }
    }
    

}
