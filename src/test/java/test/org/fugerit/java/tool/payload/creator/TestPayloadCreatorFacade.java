package test.org.fugerit.java.tool.payload.creator;

import static org.junit.Assert.fail;

import java.io.File;

import org.fugerit.java.core.io.FileIO;
import org.fugerit.java.doc.base.config.DocConfig;
import org.fugerit.java.tool.payload.creator.PayloadCreatorFacade;
import org.fugerit.java.tool.payload.creator.PayloadResult;
import org.junit.Assert;
import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestPayloadCreatorFacade {

	private boolean testCreateWorker( String type, String baseName, int requestedSize ) {
		boolean ok = false;
		try {
			File outputFileNormal = new File( "target/"+baseName+"."+type );
			File outputFileBase64 = new File( "target/"+baseName+".txt" );
			int max = requestedSize;
			int min = max-1024;
			PayloadResult result = PayloadCreatorFacade.create( max , DocConfig.TYPE_PDF );
			int actualSize = result.getPayloadData().length;
			log.info( "min : {}, max : {}, actual size : {}", min, max, actualSize );
			FileIO.writeBytes( result.getPayloadData() , outputFileNormal );
			FileIO.writeString( result.getBase64() , outputFileBase64 );
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
		boolean ok = this.testCreateWorker( DocConfig.FORMAT_PDF_A_1B , "test_pdf_a_100kb", 100*1024 );
		Assert.assertTrue( ok );
	}
	
}
