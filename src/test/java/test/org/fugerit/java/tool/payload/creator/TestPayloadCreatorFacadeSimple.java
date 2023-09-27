package test.org.fugerit.java.tool.payload.creator;

import static org.junit.Assert.fail;

import org.fugerit.java.doc.base.config.DocConfig;
import org.fugerit.java.tool.payload.creator.PayloadCreatorFacade;
import org.fugerit.java.tool.payload.creator.PayloadResult;
import org.junit.Assert;
import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestPayloadCreatorFacadeSimple {
		
	@Test
	public void testCreatePdfAMax() {
		try {
			PayloadResult result = PayloadCreatorFacade.create( 100*1024 , DocConfig.TYPE_PDF );
			log.info( "requestedSize : {}, size : {}", result.getRequestedSize(), result.getPayloadData().length );
			Assert.assertNotEquals( 0, result.getPayloadData().length );
		} catch (Exception | ExceptionInInitializerError e) {
			String message  = "Error : "+e.getMessage();
			log.error( message, e );
			fail( message );
		}
	}
	
}
