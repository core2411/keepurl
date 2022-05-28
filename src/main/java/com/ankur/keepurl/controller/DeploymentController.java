package com.ankur.keepurl.controller;

import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ankur.keepurl.app.exception.KeepUrlServiceException;

@RestController
@RequestMapping("api/deploy")
public class DeploymentController {

    private static Logger logger = LoggerFactory.getLogger(DeploymentController.class);

    private static final String BRANCH_KEY = "ref";
    private static final String BRANCH = "/main";
    private static final String DEPLOYMENT_SCRIPT = "devop/deploy.sh fileout";

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public String deploy(@RequestBody Map<String, Object> payload) throws IOException {
	logger.info("Github Webhook Invoked");
	if (!payload.containsKey(BRANCH_KEY)) {
	    logger.error("Property {} missing", BRANCH_KEY);
	    throw new KeepUrlServiceException("Property " + BRANCH_KEY + " missing. Cannot determine branch");
	}
	if (payload.get(BRANCH_KEY).toString().contains(BRANCH)) {
	    logger.info("Calling Deployment Script: {}", DEPLOYMENT_SCRIPT);
	    Runtime.getRuntime().exec(DEPLOYMENT_SCRIPT);
	    return "Deployment Triggered";
	}
	logger.info("Skipping Deployment as non main branch pushed");
	return "Deployment Skipped";
    }
}
