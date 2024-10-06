package com.example.oauthjwt.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {

    @Operation(summary = "회원가입", description = "")
    @PostMapping("/join")
    public ResponseEntity<Void> join() {

        return ResponseEntity.ok().build();
    }
}
