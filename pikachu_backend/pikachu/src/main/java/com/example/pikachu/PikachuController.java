package com.example.pikachu;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@RestController
@CrossOrigin
public class PikachuController {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

   @GetMapping("/getpikachu")
   public List<Notes> getPikachu() {
    String json = redisTemplate.opsForValue().get("notes").toString();
    List<Notes> pikachu = new Gson().fromJson(json, new TypeToken<List<Notes>>(){}.getType());
    System.out.println("げっと"+pikachu);
    return pikachu;
}

    @PostMapping("/pikachu")
    public void addPikachu(@RequestBody List<Notes> pikachu) {
        System.out.println(pikachu);
        String json = new Gson().toJson(pikachu);
       redisTemplate.opsForValue().set("notes", json);
       System.out.println(pikachu);
    }
    
    // @PutMapping("/pikachu/{id}")
    // public void updatePikachu(@RequestBody Pikachu pikachu, @PathVariable String id) {
    //     pikachuService.updatePikachu(id, pikachu);
    // }
    
    // @DeleteMapping("/pikachu/{id}")
    // public void deletePikachu(@PathVariable String id) {
    //     pikachuService.deletePikachu(id);
    // }

}
