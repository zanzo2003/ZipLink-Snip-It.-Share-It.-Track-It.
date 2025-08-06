package com.bhaskarshashwath.Ziplink.service;

import com.bhaskarshashwath.Ziplink.domain.User;
import com.bhaskarshashwath.Ziplink.model.UrlMappingDTO;
import com.bhaskarshashwath.Ziplink.request.UrlMappingRequest;

public interface UrlMappingService {

    UrlMappingDTO createMapping(UrlMappingRequest request, User User);

}
