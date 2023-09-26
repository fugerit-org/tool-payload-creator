package org.fugerit.java.tool.payload.creator;

import java.util.Base64;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class PayloadResult {

	@Getter private byte[] payloadData;
	
	@Getter private int requestedSize;
	
	public String getBase64() {
		return Base64.getEncoder().encodeToString( this.getPayloadData() );
	}

}
