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

	private static final String UNIT_K = "0123456789ABCDE 0123456789ABCDE 0123456789ABCDE 0123456789ABCDE 0123456789ABCDE 0123456789ABCDE 0123456789ABCDE 0123456789ABCDE 0123456789ABCDE 0123456789ABCDE 0123456789ABCDE 0123456789ABCDE 0123456789ABCDE 0123456789ABCDE 0123456789ABCDE 0123456789ABCDE 0123456789ABCDE 0123456789ABCDE 0123456789ABCDE 0123456789ABCDE 0123456789ABCDE 0123456789ABCDE 0123456789ABCDE 0123456789ABCDE 0123456789ABCDE 0123456789ABCDE 0123456789ABCDE 0123456789ABCDE 0123456789ABCDE 0123456789ABCDE 0123456789ABCDE 0123456789ABCDE 0123456789ABCDE 0123456789ABCDE 0123456789ABCDE 0123456789ABCDE 0123456789ABCDE 0123456789ABCDE 0123456789ABCDE 0123456789ABCDE 0123456789ABCDE 0123456789ABCDE 0123456789ABCDE 0123456789ABCDE 0123456789ABCDE 0123456789ABCDE 0123456789ABCDE 0123456789ABCDE 0123456789ABCDE 0123456789ABCDE 0123456789ABCDE 0123456789ABCDE 0123456789ABCDE 0123456789ABCDE 0123456789ABCDE 0123456789ABCDE 0123456789ABCDE 0123456789ABCDE 0123456789ABCDE 0123456789ABCDE 0123456789ABCDE 0123456789ABCDE 0123456789ABCDE 0123456789ABCDE ";
	
	private static final String FILLER = "0123456789ABCDE 0123456789ABCDE 0123456789ABCDE 0123456789ABCDE";
	
	// load the configuration or throw a ConfigRuntimeException
	private static final FreemarkerDocProcessConfig FACADE = 
			FreemarkerDocProcessConfigFacade.loadConfigSafe( "cl://fj-doc-tool-payload-creator/freemarker-doc-process.xml" );
	
	private static final String CHAIN_ID_PAYLOAD = "payload";
	
	private static final String ATT_FILLER_LIST = "fillerList";
	
	private static void createHelper( String type, ByteArrayOutputStream baos, List<String> listFiller ) throws DocException {
		DocTypeHandler handler = FACADE.getFacade().findHandler(type);
		log.info( "type : {}, listFiller.size() : {}", type, listFiller.size() );
		FACADE.getFacade().handlers().forEach( h -> log.info( "handler : {} -> {}", h.getKey(), h ) );
		if ( handler == null ) {
			throw new DocException( "No handler found : "+type );
		}
		// run code or raise DocException
		DocException.apply( () -> 
			FACADE.fullProcess( CHAIN_ID_PAYLOAD, DocProcessContext.newContext( ATT_FILLER_LIST, listFiller ), handler, DocOutput.newOutput(baos) )	
		 );
	}
	
	private static int itFiller( int requestedSize, List<String> currentFiller, byte[] currentBuffer ) {
		int calcDiff = (requestedSize-currentBuffer.length);
		int itCount = 1;
		while ( calcDiff > UNIT_K.length()*4 ) {
			currentFiller.add( UNIT_K );
			calcDiff = (requestedSize-currentBuffer.length-(UNIT_K.length()*itCount));
			itCount++;
		}
		return calcDiff;
	}
	
	public static PayloadResult create( int requestedSize, String type ) throws IOException, DocException {
		byte[] currentData = new byte[0];
		boolean goOn = true;
		List<String> currentFiller = new ArrayList<>();
		int count = 1;
		for ( int a=0; a<((requestedSize/1024)-40); a++ ) {
			currentFiller.add( UNIT_K );
		}
		while ( goOn ) {
			try ( ByteArrayOutputStream buffer = new ByteArrayOutputStream() ) {
				createHelper(type, buffer, currentFiller);
				byte[] currentBuffer = buffer.toByteArray();
				log.info( "current size : {} - requested size : {} - count : {}", currentBuffer.length, requestedSize, count );
				if ( currentBuffer.length > requestedSize ) {
					goOn = false;
				} else {
					currentData = currentBuffer;
					int calcDiff = itFiller(requestedSize, currentFiller, currentBuffer);
					int calcIncrement = (calcDiff/80 );
					if ( calcIncrement < 1 ) {
						calcIncrement = 1;
					}
					log.info( "calc incremet {}", calcIncrement );
					StringBuilder append = new StringBuilder();
					for ( int k=0; k<calcIncrement; k++ ) {
						append.append( FILLER );
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
