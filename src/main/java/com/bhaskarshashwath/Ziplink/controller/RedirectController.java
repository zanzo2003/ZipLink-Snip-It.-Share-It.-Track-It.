package com.bhaskarshashwath.Ziplink.controller;



import com.bhaskarshashwath.Ziplink.controller.common.ControllerHelper;
import com.bhaskarshashwath.Ziplink.domain.UrlMapping;
import com.bhaskarshashwath.Ziplink.service.UrlMappingService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;


@AllArgsConstructor
@RestController
public class RedirectController {


    private UrlMappingService service;

    private ControllerHelper controllerHelper;

    @GetMapping("/{shortUrl}")
    public ResponseEntity<Void> redirect(@PathVariable String shortUrl){

        UrlMapping urlMapping = service.getOriginalMapping(shortUrl);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", urlMapping.getOriginalUrl());
        return controllerHelper
                .redirectResponse("Redirect Succesfull", HttpStatus.TEMPORARY_REDIRECT, headers);
    }
}
