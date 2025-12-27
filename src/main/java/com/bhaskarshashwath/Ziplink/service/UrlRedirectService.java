package com.bhaskarshashwath.Ziplink.service;

import com.bhaskarshashwath.Ziplink.domain.UrlMapping;

public interface UrlRedirectService {

    UrlMapping getOriginalMapping(String shortUrl);

}

