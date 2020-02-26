package com.mps.insight.rest.service;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DaskboardController {
	@RequestMapping("hello")
	public String helloWorld(@RequestParam(value = "name", defaultValue = "World") String name) {
		return "Hello " + name + "!!";
	}

}
