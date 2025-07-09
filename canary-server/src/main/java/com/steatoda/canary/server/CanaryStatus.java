package com.steatoda.canary.server;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class CanaryStatus {

	@JsonProperty
	public OffsetDateTime getNow() { return now; }
	public void setNow(OffsetDateTime now) { this.now = now; }

	@JsonProperty
	public String getVersion() { return version; }
	public void setVersion(String version) { this.version = version; }

	private OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
	private String version;
	
}
