package com.bhaskarshashwath.Ziplink.controller;


import com.bhaskarshashwath.Ziplink.controller.common.ControllerHelper;
import com.bhaskarshashwath.Ziplink.domain.User;
import com.bhaskarshashwath.Ziplink.model.UrlMappingDTO;
import com.bhaskarshashwath.Ziplink.response.ApiResponseDTO;
import com.bhaskarshashwath.Ziplink.request.UrlMappingRequest;
import com.bhaskarshashwath.Ziplink.service.UrlMappingService;
import com.bhaskarshashwath.Ziplink.service.impl.UserServiceImpl;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/urls")
@AllArgsConstructor
public class UrlMappingController {

    private UrlMappingService mappingService;

    private UserServiceImpl userService;

    private ControllerHelper controllerHelper;

    @PostMapping("/shorten")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponseDTO<UrlMappingDTO>> createShortUrl(
            @RequestBody @NonNull UrlMappingRequest request,
            Principal principal
            ){
        User user = userService.getByUsername(principal.getName());
        return controllerHelper.createOkResponse( mappingService.createMapping(request, user), "Mapping created successfully");
    }


}
