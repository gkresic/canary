package com.steatoda.canary.server.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Payload {

	@JsonProperty
	public String text;

	@JsonProperty
	public Integer number;

}
