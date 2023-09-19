package com.example.pikachu;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class PikachuController {
    // @Autowired
    // private RedisTemplate<String, Object> redisTemplate;

    // @GetMapping("/pikachu")
    // public List<Pikachu> getAllPikachu() {
    //     return pikachuService.getAllPikachu();
    // }
    
    // @GetMapping("/pikachu/{id}")
    // public Pikachu getPikachu(@PathVariable String id) {
    //     return pikachuService.getPikachu(id);
    // }
    
    @PostMapping("/pikachu")
    public void addPikachu(@RequestBody String pikachu) {
    //    redisTemplate.opsForValue().set("1", pikachu);
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
