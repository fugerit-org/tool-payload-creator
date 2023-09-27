package org.fugerit.java.tool.payload.creator;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.fugerit.java.core.cfg.ConfigRuntimeException;
import org.fugerit.java.core.cli.ArgUtils;
import org.fugerit.java.core.io.FileIO;
import org.fugerit.java.core.lang.helpers.StringUtils;
import org.fugerit.java.doc.base.config.DocException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PayloadCreatorTool {

	public static final String ARG_TARGET_SIZE_BYTE = "target-size-byte";
	
	public static final String ARG_TARGET_FORMAT = "target-format";
	
	public static final String ARG_OUTPUT_FILE = "output-file";
	
	public static final String ARG_CREATE_BASE64 = "create-base-64";
	
	public static final String ARG_CREATE_JSON = "create-json";
	
	private PayloadCreatorTool() {}

	private static void checkRequired( String ...params ) {
		for ( int k=0; k<params.length; k++ ) {
			if ( StringUtils.isEmpty( params[k] ) ) {
				throw new ConfigRuntimeException( "Required parameter "+params[k] );
			}
		}
	}
	
	public static PayloadResult handle( Properties params ) {
		PayloadResult result = null;
		log.info( "params : {}", params );
		String targetSize = params.getProperty( ARG_TARGET_SIZE_BYTE );
		String targetFormat = params.getProperty( ARG_TARGET_FORMAT );
		String outputFile = params.getProperty( ARG_OUTPUT_FILE );
		checkRequired( targetSize, targetFormat, outputFile );
		String createBase64 = params.getProperty( ARG_CREATE_BASE64 );
		String createJson = params.getProperty( ARG_CREATE_JSON );
		Integer reqSize = Integer.valueOf( targetSize );
		log.info( "targetSize : {}, targetFormat : {}", targetSize, targetFormat );
		try {
			result = PayloadCreatorFacade.create(  reqSize, targetFormat );
			FileIO.writeBytes( result.getPayloadData() , new File( outputFile ) );
			if ( StringUtils.isNotEmpty( createBase64 ) || StringUtils.isNotEmpty( createJson )  ) {
				String base64 = result.getBase64();
				if ( StringUtils.isNotEmpty( createBase64 ) ) {
					log.info( "createBase64 {}", createBase64 );
					FileIO.writeString( base64 , new File( createBase64 ) );
				}
				if ( StringUtils.isNotEmpty( createJson ) ) {
					log.info( "createJson {}", createJson );
					FileIO.writeString( "{\"content\":\""+base64+"\"}", new File( createJson ) );
				}
			}
		} catch (IOException | DocException e) {
			log.error( "Generation error : "+e, e );
		}
		return result;
	}
	
	public static void main( String[] args ) {
		handle( ArgUtils.getArgs( args ) );
	}
	
}
