package org.fugerit.java.tool.payload.creator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.fugerit.java.doc.base.config.DocException;
import org.fugerit.java.doc.base.config.DocOutput;
import org.fugerit.java.doc.base.config.DocTypeHandler;
import org.fugerit.java.doc.base.process.DocProcessContext;
import org.fugerit.java.doc.freemarker.process.FreemarkerDocProcessConfig;
import org.fugerit.java.doc.freemarker.process.FreemarkerDocProcessConfigFacade;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PayloadCreatorFacade {
	
	private PayloadCreatorFacade() {}

	// load the configuration or throw a ConfigRuntimeException
	private static final FreemarkerDocProcessConfig FACADE = 
			FreemarkerDocProcessConfigFacade.loadConfigSafe( "cl://fj-doc-tool-payload-creator/freemarker-doc-process.xml" );
	
	private static final String CHAIN_ID_PAYLOAD = "payload";
	
	private static final String ATT_FILLER_LIST = "fillerList";
	
	private static void createHelper( String type, ByteArrayOutputStream baos, List<String> listFiller ) throws DocException {
		DocTypeHandler handler = FACADE.getFacade().findHandler(type);
		log.info( "handlers {}", FACADE.getFacade().handlers() );
		if ( handler == null ) {
			throw new DocException( "No handler found : "+type );
		}
		// run code or raise DocException
		DocException.apply( () -> 
			FACADE.fullProcess( CHAIN_ID_PAYLOAD, DocProcessContext.newContext( ATT_FILLER_LIST, listFiller ), handler, DocOutput.newOutput(baos) )	
		 );
	}
	
	public static PayloadResult create( int requestedSize, String type ) throws IOException, DocException {
		byte[] currentData = new byte[0];
		boolean goOn = true;
		List<String> currentFiller = new ArrayList<>();
		int count = 1;
		while ( goOn ) {
			try ( ByteArrayOutputStream buffer = new ByteArrayOutputStream() ) {
				createHelper(type, buffer, currentFiller);
				byte[] currentBuffer = buffer.toByteArray();
				log.info( "current size : {} - requested size : {} - count : {}", currentBuffer.length, requestedSize, count );
				if ( currentBuffer.length > requestedSize ) {
					goOn = false;
				} else {
					currentData = currentBuffer;
					int calcIncrement = ((requestedSize-currentBuffer.length)/20 );
					if ( calcIncrement < 1 ) {
						calcIncrement = 1;
					}
					log.info( "calc incremet {}", calcIncrement );
					StringBuilder append = new StringBuilder();
					for ( int k=0; k<calcIncrement; k++ ) {
						append.append( " filler, filler," );
						if ( k%10000==0 ) {
							currentFiller.add( append.toString() );
							append = new StringBuilder();
							log.info( "cut {}", k );
						}
					}
					currentFiller.add( append.toString() );
					count++;
				}
			}		
		}
		return new PayloadResult(currentData, requestedSize);
	}
	
}
