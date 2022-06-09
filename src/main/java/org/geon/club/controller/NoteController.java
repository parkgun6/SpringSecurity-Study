package org.geon.club.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.geon.club.dto.NoteDTO;
import org.geon.club.service.NoteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Log4j2
@RequestMapping("/notes")
@RequiredArgsConstructor
public class NoteController {

    private final NoteService noteService;

    @PostMapping(value = "")
    public ResponseEntity<Long> register(@RequestBody NoteDTO noteDTO) {

        log.info("-------register-------");
        log.info(noteDTO);

        Long num = noteService.register(noteDTO);

        return new ResponseEntity<>(num, HttpStatus.OK);
    }

    @GetMapping(value = "/{num}")
    public ResponseEntity<NoteDTO> read(@PathVariable("num") Long num) {

        log.info("------read------");
        log.info(num);

        return new ResponseEntity<>(noteService.get(num), HttpStatus.OK);
    }

    @GetMapping(value = "/all")
    public ResponseEntity<List<NoteDTO>> getList(String email) {

        log.info("-------getList-------");
        log.info(email);

        return new ResponseEntity<>(noteService.getAllWithWriter(email), HttpStatus.OK);
    }


    @DeleteMapping(value = "/{num}")
    public ResponseEntity<String> remove(@PathVariable("num") Long num){

        log.info("-------remove--------");
        log.info(num);

        noteService.remove(num);

        return new ResponseEntity<>("removed", HttpStatus.OK);

    }

    @PutMapping(value = "/{num}")
    public ResponseEntity<String> modify(@RequestBody NoteDTO noteDTO){

        log.info("--------modify--------");
        log.info(noteDTO);

        noteService.modify(noteDTO);

        return new ResponseEntity<>("modified", HttpStatus.OK);

    }
}
