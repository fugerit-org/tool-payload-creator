package test.org.fugerit.java.tool.payload.creator;

import static org.junit.Assert.fail;

import java.io.File;
import java.util.Properties;

import org.fugerit.java.core.cfg.ConfigRuntimeException;
import org.fugerit.java.core.cli.ArgUtils;
import org.fugerit.java.core.io.FileIO;
import org.fugerit.java.doc.base.config.DocConfig;
import org.fugerit.java.tool.payload.creator.PayloadCreatorTool;
import org.fugerit.java.tool.payload.creator.PayloadResult;
import org.junit.Assert;
import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestPayloadCreatorTool {

	private PayloadResult helper( int requestedSize, String targetFormat, String outputFile, String createBase64, String createJson ) {
		Properties params = new Properties();
		params.setProperty( PayloadCreatorTool.ARG_TARGET_SIZE_BYTE , String.valueOf( requestedSize ) );
		params.setProperty( PayloadCreatorTool.ARG_TARGET_FORMAT , targetFormat );
		params.setProperty( PayloadCreatorTool.ARG_OUTPUT_FILE , outputFile );
		if ( createBase64 != null ) {
			params.setProperty( PayloadCreatorTool.ARG_CREATE_BASE64 , createBase64 );
		}
		if ( createJson != null ) {
			params.setProperty( PayloadCreatorTool.ARG_CREATE_JSON ,createJson );	
		}
		return PayloadCreatorTool.handle(params);
	}
	
	private boolean testCreateWorker( String type, String baseName, int requestedSize ) {
		boolean ok = false;
		try {
			File outputFileNormal = new File( "target/"+baseName+"."+type );
			File outputFileBase64 = new File( "target/"+baseName+".txt" );
			File outputFileBaseJson = new File( "target/"+baseName+".json" );
			int max = requestedSize;
			int min = max-1024;
			// try tool
			PayloadResult result = this.helper( max, type, outputFileNormal.getCanonicalPath(), outputFileBase64.getCanonicalPath(), outputFileBaseJson.getCanonicalPath() );
			int actualSize = result.getPayloadData().length;
			log.info( "min : {}, max : {}, actual size : {}", min, max, actualSize );
			FileIO.writeBytes( result.getPayloadData() , outputFileNormal );
			String base64 = result.getBase64();
			FileIO.writeString( base64 , outputFileBase64 );
			FileIO.writeString( "{\"content\":\""+base64+"\"}", outputFileBaseJson );
			ok = ( min < actualSize && max > actualSize );
		} catch (Exception | ExceptionInInitializerError e) {
			String message  = "Error : "+e.getMessage();
			log.error( message, e );
			fail( message );
		}
		return ok;
	}
		
	@Test
	public void testCreatePdfAMax() {
		boolean ok = this.testCreateWorker( DocConfig.TYPE_PDF , "test_pdf_a_100kb", 100*1024 );
		Assert.assertTrue( ok );
	}
	
	@Test
	public void testCreatePdfOnlyOutputFile() {
		PayloadResult result = this.helper( 1024 ,DocConfig.TYPE_PDF, "target/only_pdf.pdf", null, null);
		Assert.assertNotNull(result);
	}

	@Test
	public void testCreatePdfFail() {
		// test no params
		Assert.assertThrows( ConfigRuntimeException.class , () -> PayloadCreatorTool.main( new String[0] ) ) ;
		// test tool
		String[] args = { ArgUtils.getArgString( PayloadCreatorTool.ARG_TARGET_SIZE_BYTE ), "2048", 
							ArgUtils.getArgString( PayloadCreatorTool.ARG_TARGET_FORMAT ), "not_exists",
							ArgUtils.getArgString( PayloadCreatorTool.ARG_OUTPUT_FILE ), "target/not_exists.pdf" };
		PayloadCreatorTool.main( args );
	}
	
}
